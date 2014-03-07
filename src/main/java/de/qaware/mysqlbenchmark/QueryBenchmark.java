/*
 * Copyright (C) 2014 QAware GmbH (http://www.qaware.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qaware.mysqlbenchmark;

import de.qaware.mysqlbenchmark.jetm.CsvRenderer;
import de.qaware.mysqlbenchmark.sql.SQLStatementExecutor;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.renderer.MeasurementRenderer;
import etm.core.renderer.SimpleTextRenderer;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * Executes a list of SQL statements. Results can be retrieved in the formats: JETM-formatting and CSV.
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class QueryBenchmark {
    private EtmMonitor etmMonitor;
    private SQLStatementExecutor executor;

    /**
     * Constructor
     *
     * @param executor executor to execute the sql queries.
     */
    public QueryBenchmark(SQLStatementExecutor executor) {
        this.executor = executor;
    }

    /**
     * Run query list against the executor and measure timings with jetm
     *
     * @param queries queries to execute
     * @throws SQLException
     */
    public void processQueries(List<String> queries) throws SQLException {
        // start jetm for time measurements
        BasicEtmConfigurator.configure();
        etmMonitor = EtmManager.getEtmMonitor();
        etmMonitor.start();

        // one aggregation measurement point
        EtmPoint mpoint = etmMonitor.createPoint("Measurement");
        try {
            for (String query : queries) {
                // one measurement point for every query
                EtmPoint qpoint = etmMonitor.createPoint("Query: " + query);
                try {
                    executor.executeStatement(query);
                } finally {
                    qpoint.collect();
                }
            }
        } finally {
            mpoint.collect();
            etmMonitor.stop();
        }
    }

    /**
     * Export format for measurements. Currently supported formats are CSV and JETM-Style.
     */
    public enum Format {
        JETM("jetm"),
        CSV("csv");

        private Format(String format) {
        }

        /**
         * Get the format from string. If the string is "csv" (ignoring case), the Format.CSV is returned, else JETM.
         *
         * @param format string describing the format
         * @return a format for exporting the measurements
         */
        public static Format get(String format) {
            if (format != null && "csv".equals(format.toLowerCase())) {
                return CSV;
            } else {
                return JETM;
            }
        }
    }

    /**
     * Get results for printing to console or writing to files
     *
     * @return result
     */
    public String getResult(Format format) {
        StringWriter sw = new StringWriter();

        MeasurementRenderer renderer = null;
        switch (format) {
            case JETM:
                renderer = new SimpleTextRenderer(sw);
                break;
            case CSV:
                renderer = new CsvRenderer(sw);
                break;
        }
        if (etmMonitor == null) {
            return "No monitor initialized.";
        }

        etmMonitor.render(renderer);
        return sw.getBuffer().toString();
    }
}
