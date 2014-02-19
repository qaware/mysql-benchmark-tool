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

import java.sql.*;

/**
 * Wraps the SQL connection and enabled statement execution.
 * Can be passed to {@link de.qaware.mysqlbenchmark.QueryBenchmark} to execute statements.
 *
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class SQLStatementExecutor {
    private Connection connection = null;

    /**
     * Executes a sql statement. Make sure the connection is initialized first.
     *
     * @param name
     * @return
     * @throws SQLException
     */
    public ResultSet executeStatement(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(name);
        return ps.executeQuery();
    }

    /**
     * Close the connection if no longer needed
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
     */
    public void initConnection(String connectionString, String username, String password) throws SQLException {
        System.out.println("-------- Opening MySQL JDBC Connection ------------");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver found!");

        try {
            connection = DriverManager
                    .getConnection(connectionString, username, password);

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console.");
            e.printStackTrace();
            throw e;
        }

        if (connection != null) {
            System.out.println("Connection established.");
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
