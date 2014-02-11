package de.qaware.mysqlbenchmark.console;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Command line parmeter definitions for jcommander
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class Parameters {

    @Parameter(names = {"-u"},
            description = "mysql username",
            required = true)
    public String username;

    @Parameter(names = {"-p"},
            description = "mysql password",
            required = true)
    public String password;

    @Parameter(names = {"-s"},
            description = "The connection string to the mysql server (without database name)."
                    + " eg.: -c jdbc:mysql://localhost:3306/",
            required = false)
    public String server = "jdbc:mysql://localhost:3306/";

    @Parameter(names = {"-db"},
            description = "The database name. eg.: -db test_db",
            required = false)
    public String database = "test_db";

    @Parameter(names = {"-log"},
            description = "Location of the logfile which contains the mysql queries to execute",
            required = false)
    public String inputFile = "benchmarking-queries.sql";

    @Parameter(names = {"-o"},
            description = "Location of the output file to write the results to",
            required = false)
    public String resultfilename = "results.txt";


    @Parameter(names = {"-help", "-h"}, description = "Execute mysql query benchmark based on mysql logs",
            required = false)
    public boolean help = false;

    @Parameter(names = {"-verbose", "-v"}, description = "Print all results to console",
            required = false)
    public boolean verbose = false;

    @Parameter(names = {"-ignore"}, description = "Ignore statements which start with these prefixes, case insensitive. Enter a comma separated list of prefixes.",
            required = false)
    public List<String> ignorePrefixes = new ArrayList<String>();

    @Parameter(names = {"-id"},
            description = "Only execute queries from the log with this connection id. This parameter is optional.",
            required = false)
    public String connectionID;

    @Parameter(names = {"-f"},
            description = "The result can be in default JETM style or CSV. JETM is default.",
            required = false)
    public String format = "JETM";

}
