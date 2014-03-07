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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Strings;
import de.qaware.mysqlbenchmark.console.Parameters;
import de.qaware.mysqlbenchmark.logfile.QueryParser;
import de.qaware.mysqlbenchmark.sql.SQLStatementExecutor;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Entry point for starting the benchmark tool
 * Parses the command line parameters, executes sql statements and writes results.
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
final class Main {
    private static QueryParser parser = new QueryParser();
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Main.class);

    private Main() {
        // Prevent instantiation
    }

    /**
     * Main entry point.
     * @param args args are described in the class {@link de.qaware.mysqlbenchmark.console.Parameters}
     */
    public static void main(String[] args) {

        /**
         * Read command line parameters using jcommander
         */
        final Parameters params = new Parameters();
        final JCommander commander = new JCommander(params);
        commander.setProgramName("MySQL Benckmark Tool");
        try {
            commander.parse(args);
        } catch (ParameterException e) {
            commander.usage();
            LOG.error("Wrong parameters.", e);
            return;
        }
        if (params.isHelp()) {
            commander.usage();
            return;
        }

        SQLStatementExecutor executor = new SQLStatementExecutor();

        /**
         * parse the logfile and run queries
         */
        try {
            parser.parseLogFile(params.getInputFile(), params.getConnectionID(), params.getIgnorePrefixes());
            LOG.info("Read " + parser.getQueries().size() + " queries from file '" + params.getInputFile() + "'.");

            executor.initConnection(params.getServer() + params.getDatabase(), params.getUsername(), params.getPassword());
            QueryBenchmark benchmark = new QueryBenchmark(executor);

            // process queries
            try {
                LOG.info("Executing benchmark...");
                benchmark.processQueries(parser.getQueries());
                LOG.info("Benchmark completed");
            } catch (Exception e) {
                LOG.error("Error processing queries.", e);
            }
            // get time measurements
            String result = benchmark.getResult(QueryBenchmark.Format.get(params.getFormat()));

            if (params.isVerbose()) {
                System.out.println(result);
            }

            // write result to file if needed
            if (!Strings.isStringEmpty(params.getResultfilename())) {
                FileWriter writer = new FileWriter(params.getResultfilename());
                System.out.println("Writing result to " + params.getResultfilename());
                writer.write(result);
                writer.close();
            }

        } catch (FileNotFoundException e) {
            LOG.error("File not found.", e);
        } catch (IOException e) {
            LOG.error("IO Exception.", e);
        } catch (SQLException e) {
            LOG.error("SQL Exception.", e);
        } finally {
            try {
                executor.closeConnection();
            } catch (Exception e) {
                /* Intentionally Swallow  Exception */
                LOG.error("Could not close sql connection.");
            }
        }
    }
}
