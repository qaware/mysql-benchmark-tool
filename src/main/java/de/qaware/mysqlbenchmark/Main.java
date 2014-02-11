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
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class Main {
    private static QueryParser parser = new QueryParser();

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
            System.out.println(e.getMessage());
            return;
        }
        if (params.help) {
            commander.usage();
            return;
        }


        /**
         * parse the logfile and run queries
         */
        try {
            parser.parseLogFile(params.inputFile, params.connectionID, params.ignorePrefixes);
            System.out.println("Read " + parser.getQueries().size() + " queries from file '" + params.inputFile + "'.");

            SQLStatementExecutor executor = new SQLStatementExecutor();
            executor.initConnection(params.server + params.database, params.username, params.password);
            QueryBenchmark benchmark = new QueryBenchmark(executor);

            // process queries
            try {
                System.out.println("Executing benchmark...");
                benchmark.processQueries(parser.getQueries());
                System.out.println("Benchmark completed");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // get time measurements
            String result = benchmark.getResult(QueryBenchmark.Format.get(params.format));

            if (params.verbose)
                System.out.println(result);

            // write result to file if needed
            if (!Strings.isStringEmpty(params.resultfilename)) {
                FileWriter writer = new FileWriter(params.resultfilename);
                System.out.println("Writing result to " + params.resultfilename);
                writer.write(result);
                writer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
