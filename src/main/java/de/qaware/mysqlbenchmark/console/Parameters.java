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

package de.qaware.mysqlbenchmark.console;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Command line parmeter definitions via jcommander
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class Parameters {

    /**
     * mysql username
     */
    @Parameter(names = {"-u"},
            description = "mysql username",
            required = true)
    private String username;

    /**
     * mysql password
     */
    @Parameter(names = {"-p"},
            description = "mysql password",
            required = true)
    private String password;

    /**
     * The connection string to the mysql server (without database name)
     */
    @Parameter(names = {"-s"},
            description = "The connection string to the mysql server (without database name)."
                    + " eg.: -c jdbc:mysql://localhost:3306/",
            required = false)
    private String server = "jdbc:mysql://localhost:3306/";

    /**
     * The database name. eg.: -db test_db
     */
    @Parameter(names = {"-db"},
            description = "The database name. eg.: -db test_db",
            required = false)
    private String database = "test_db";

    /**
     * Location of the logfile which contains the mysql queries to execute
     */
    @Parameter(names = {"-log"},
            description = "Location of the logfile which contains the mysql queries to execute",
            required = false)
    private String inputFile = "benchmarking-queries.sql";

    /**
     * Location of the output file to write the results to
     */
    @Parameter(names = {"-o"},
            description = "Location of the output file to write the results to",
            required = false)
    private String resultfilename = "results.txt";

    /**
     * Execute mysql query benchmark based on mysql logs
     */
    @Parameter(names = {"-help", "-h"}, description = "Execute mysql query benchmark based on mysql logs",
            required = false)
    private boolean help = false;

    /**
     * Print all results to console
     */
    @Parameter(names = {"-verbose", "-v"}, description = "Print all results to console",
            required = false)
    private boolean verbose = false;

    /**
     * Ignore statements which start with these prefixes, case insensitive. Enter a comma separated list of prefixes
     */
    @Parameter(names = {"-ignore"}, description = "Ignore statements which start with these prefixes, case insensitive. Enter a comma separated list of prefixes.",
            required = false)
    private List<String> ignorePrefixes = new ArrayList<String>();

    /**
     * Only execute queries from the log with this connection id. This parameter is optional
     */
    @Parameter(names = {"-id"},
            description = "Only execute queries from the log with this connection id. This parameter is optional.",
            required = false)
    private String connectionID;

    /**
     * The result can be in default JETM style or CSV. JETM is default.
     */
    @Parameter(names = {"-f"},
            description = "The result can be in default JETM style or CSV. JETM is default.",
            required = false)
    private String format = "JETM";

    /**
     * mysql username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * mysql password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * The connection string to the mysql server (without database name)
     *
     * @return server
     */
    public String getServer() {
        return server;
    }

    /**
     * The database name. eg.: -db test_db
     *
     * @return database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Location of the logfile which contains the mysql queries to execute
     *
     * @return log file
     */
    public String getInputFile() {
        return inputFile;
    }

    /**
     * File to write the result to
     *
     * @return output filename
     */
    public String getResultfilename() {
        return resultfilename;
    }

    /**
     * display help
     *
     * @return true if help should be displayed
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * Print all results to console
     *
     * @return true if in verbose mode
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Ignore statements which start with these prefixes, case insensitive. Enter a comma separated list of prefixes
     *
     * @return prefixes to ignore
     */
    public List<String> getIgnorePrefixes() {
        return ignorePrefixes;
    }

    /**
     * Only execute queries from the log with this connection id. This parameter is optional
     *
     * @return connection id
     */
    public String getConnectionID() {
        return connectionID;
    }

    /**
     * Format for exporting results
     *
     * @return csv or jetm
     */
    public String getFormat() {
        return format;
    }
}
