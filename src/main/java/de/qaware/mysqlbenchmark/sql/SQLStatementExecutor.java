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

package de.qaware.mysqlbenchmark.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Wraps the SQL connection and enabled statement execution.
 * Can be passed to {@link de.qaware.mysqlbenchmark.QueryBenchmark} to execute statements.
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class SQLStatementExecutor {
    private Connection connection = null;

    private static final Logger LOG = LoggerFactory.getLogger(SQLStatementExecutor.class);

    /**
     * Executes a sql statement. Make sure the connection is initialized first.
     *
     * @param name statement string
     * @return the result set
     */
    public ResultSet executeStatement(String name) {
        try {
            // prepare statement for execution
            PreparedStatement ps = connection.prepareStatement(name);

            // execute the statement and return the result
            return ps.executeQuery();
        } catch (SQLException e) {
            LOG.error("Execution of statement {} failed.", name, e);
        }
        return null;
    }

    /**
     * Close the connection if no longer needed
     *
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        connection.close();
    }

    /**
     * open a mysql connection.
     *
     * @param connectionString where to connect to the mysql server
     * @param username         mysql username
     * @param password         mysql password
     * @throws SQLException
     */
    public void initConnection(String connectionString, String username, String password) throws SQLException {
        LOG.info("-------- Opening MySQL JDBC Connection ------------");

        try {
            // load jdbc driver
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("Where is your MySQL JDBC Driver?", e);
            return;
        }

        LOG.info("MySQL JDBC Driver found!");

        try {
            // open new connection
            connection = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException e) {
            LOG.error("SQL connection failed!", e);
            throw e;
        }

        LOG.info("Connection established.");
    }
}
