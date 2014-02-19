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

package de.qaware.mysqlbenchmark.jetm;

import etm.core.aggregation.Aggregate;
import etm.core.monitor.EtmException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.*;

/**
 * Extending the JETM MeasurementRenderer for enabling CSV export of JETM measurements.
 *
 * @author felix.kelm (felix.kelm@qaware.de)
 *
 */
public class CsvRenderer implements etm.core.renderer.MeasurementRenderer {

    private static final char VALUE_SEPARATOR = ';';
    private final NumberFormat timeFormatter;
    private final Writer writer;
    private final String separator = System.getProperty("line.separator");

    /**
     * Constructs a CsvRenderer using the default locale
     * and STDOUT.
     */

    public CsvRenderer() {
        this(new OutputStreamWriter(System.out), Locale.getDefault());
    }

    /**
     * Constructs a CsvRenderer using the default locale
     * and the provided writer.
     *
     * @param aWriter The writer.
     */
    public CsvRenderer(Writer aWriter) {
        this(aWriter, Locale.getDefault());
    }

    /**
     * Constructs a CsvRenderer using the provided locale
     * and STDOUT.
     *
     * @param locale The locale to use.
     */
    public CsvRenderer(Locale locale) {
        this(new OutputStreamWriter(System.out), locale);
    }

    /**
     * Constructs a CsvRenderer using the provided locale
     * and provided writer.
     *
     * @param aWriter The writer to write to.
     * @param aLocale The locale to use.
     */
    public CsvRenderer(Writer aWriter, Locale aLocale) {
        writer = aWriter;
        timeFormatter = NumberFormat.getNumberInstance(aLocale);
        timeFormatter.setMaximumFractionDigits(3);
        timeFormatter.setMinimumFractionDigits(3);
        timeFormatter.setGroupingUsed(true);
    }

    /**
     * Constructs a CsvRenderer using the provided NumberFormat
     * instace to format the numbers and STDOUT.
     *
     * @param aTimeFormatter The number formatter.
     */

    public CsvRenderer(NumberFormat aTimeFormatter) {
        writer = new OutputStreamWriter(System.out);
        timeFormatter = aTimeFormatter;
    }

    /**
     * Constructs a CsvRenderer using the provided writer
     * and NumberFormat instance.
     *
     * @param aWriter        The writer.
     * @param aTimeFormatter The number formatter.
     */
    public CsvRenderer(Writer aWriter, NumberFormat aTimeFormatter) {
        writer = aWriter;
        timeFormatter = aTimeFormatter;
    }

    /**
     * Renders the map to the OutputStreamWriter.
     *
     * @param points The points.
     * @throws etm.core.monitor.EtmException Thrown to indicate that writing to the printer failed.
     */
    public void render(Map points) {

        Results results = new Results(points);

        try {
            results.render(writer);
            writer.flush();
        } catch (IOException e) {
            throw new EtmException("Unable to write to writer: " + e);
        }
    }

    class Results {
        private Column nameColumn = new Column("Measurement Point");
        private Column numberColumn = new Column("#");
        private Column avgColumn = new Column("Average");
        private Column minColumn = new Column("Min");
        private Column maxColumn = new Column("Max");
        private Column totalColumn = new Column("Total");

        public Results(Map points) {
            Map map = new TreeMap(points);
            for (Iterator iterator = map.values().iterator(); iterator.hasNext(); ) {
                Aggregate point = (Aggregate) iterator.next();
                addTopLevel(point);
            }
        }

        public void addTopLevel(Aggregate aAggregate) {
            addLine(0, aAggregate);

            if (aAggregate.hasChilds()) {
                addNested(1, aAggregate.getChilds());
            }
        }

        public void addNested(int nestingLevel, Map childs) {
            for (Iterator iterator = childs.values().iterator(); iterator.hasNext(); ) {
                Aggregate point = (Aggregate) iterator.next();
                addLine(nestingLevel, point);
                if (point.hasChilds()) {
                    addNested(nestingLevel + 1, point.getChilds());
                }
            }
        }

        public void addLine(int nestingLevel, Aggregate aAggregate) {
            nameColumn.addEntry(new NestedEntry(nestingLevel, aAggregate.getName()));
            numberColumn.addEntry(new RightAlignedEntry(String.valueOf(aAggregate.getMeasurements())));
            avgColumn.addEntry(new RightAlignedEntry(timeFormatter.format(aAggregate.getAverage())));
            minColumn.addEntry(new RightAlignedEntry(timeFormatter.format(aAggregate.getMin())));
            maxColumn.addEntry(new RightAlignedEntry(timeFormatter.format(aAggregate.getMax())));
            totalColumn.addEntry(new RightAlignedEntry(timeFormatter.format(aAggregate.getTotal())));
        }

        public void render(Writer writer) throws IOException {
            Iterator nameIt = nameColumn.iterator();
            Iterator numberIt = numberColumn.iterator();
            Iterator avgIt = avgColumn.iterator();
            Iterator minIt = minColumn.iterator();
            Iterator maxIt = maxColumn.iterator();
            Iterator totalIt = totalColumn.iterator();

            while (nameIt.hasNext()) {
                ((ColumnEntry) nameIt.next()).write(writer, nameColumn.currentMaxSize);
                writer.write(VALUE_SEPARATOR);
                ((ColumnEntry) numberIt.next()).write(writer, numberColumn.currentMaxSize);
                writer.write(VALUE_SEPARATOR);
                ((ColumnEntry) avgIt.next()).write(writer, avgColumn.currentMaxSize);
                writer.write(VALUE_SEPARATOR);
                ((ColumnEntry) minIt.next()).write(writer, minColumn.currentMaxSize);
                writer.write(VALUE_SEPARATOR);
                ((ColumnEntry) maxIt.next()).write(writer, maxColumn.currentMaxSize);
                writer.write(VALUE_SEPARATOR);
                ((ColumnEntry) totalIt.next()).write(writer, totalColumn.currentMaxSize);
                writer.write(separator);
            }
        }
    }

    class Column {
        private int currentMaxSize = 0;

        private List entries;


        public Column(String aHeadLine) {
            entries = new ArrayList();
            addEntry(new CenteredEntry(aHeadLine));
        }

        public void addEntry(ColumnEntry entry) {
            int i = entry.getCurrentLength();
            currentMaxSize = currentMaxSize > i ? currentMaxSize : entry.getCurrentLength();
            entries.add(entry);
        }

        public Iterator iterator() {
            return entries.iterator();
        }
    }

    interface ColumnEntry {
        public int getCurrentLength();

        public void write(Writer writer, int totalWidth) throws IOException;
    }

    class NestedEntry implements ColumnEntry {
        private int nestingLevel;
        private String text;


        public NestedEntry(int aNestingLevel, String aText) {
            nestingLevel = aNestingLevel;
            text = aText;
        }


        public int getCurrentLength() {
            return 2 * nestingLevel + text.length() + 2;
        }


        public void write(Writer writer, int totalWidth) throws IOException {
            writer.write(' ');
            for (int i = 0; i < nestingLevel * 2; i++) {
                writer.write(' ');
            }

            writer.write(text);

            for (int i = 0; i < totalWidth - nestingLevel * 2 - text.length() - 2; i++) {
                writer.write(' ');
            }
            writer.write(' ');
        }
    }

    class RightAlignedEntry implements ColumnEntry {
        private String text;

        public RightAlignedEntry(String aText) {
            text = aText;
        }


        public int getCurrentLength() {
            return text.length() + 2;
        }


        public void write(Writer writer, int totalWidth) throws IOException {
            writer.write(' ');
            if (text.length() == totalWidth) {
                writer.write(text);
            } else {
                for (int i = 0; i < totalWidth - text.length() - 2; i++) {
                    writer.write(' ');
                }
                writer.write(text);
            }
            writer.write(' ');
        }
    }

    class CenteredEntry implements ColumnEntry {
        private String text;

        public CenteredEntry(String aText) {
            text = aText;
        }


        public int getCurrentLength() {
            return text.length() + 2;
        }


        public void write(Writer writer, int totalWidth) throws IOException {
            if (totalWidth == getCurrentLength()) {
                writer.write(' ');
                writer.write(text);
                writer.write(' ');
            } else {
                int remaining = totalWidth - text.length();
                int prefix;
                int posfix;
                if (remaining % 2 == 1) {
                    remaining++;
                    prefix = remaining / 2;
                    posfix = prefix - 1;
                } else {
                    prefix = remaining / 2;
                    posfix = prefix;
                }


                for (int i = 0; i < prefix; i++) {
                    writer.write(' ');
                }
                writer.write(text);
                for (int i = 0; i < posfix; i++) {
                    writer.write(' ');
                }

            }

        }
    }
}
