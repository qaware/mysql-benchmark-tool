# MySQL Benchmark Tool
MySQL Benchmark Tool is a lightweight commandline tool for repeatedly executing SQL-Statements from a previously written MySQL-Log.

## Download
You can find precompiled jars in the bin folder: https://github.com/qaware/mysql-benchmark-tool/tree/master/bin

## What is does and how to use it
There's a blog post with more detailed explanation at: http://blog.qaware.de/

Here's a small abstract:

This article describes a simple java tool for recording and replaying MySQL queries while measuring their execution times. The replay mechanism makes the measurements comparable across different environments.

Why do we need another benchmarking tool? What's so special about this one?
* It's capable of reading and replaying MySQL's 'general query logs', not 'slow query logs' and not tcpdumps. Percona Playback in version 0.3 doesn't supports 'general query logs'.
* It's measuring execution times of all queries and aggregating identical executions. pt-query-digest supports general query log, but only without measuring execution times.
* It's written in pure Java, making it the most realistic way of running queries against the server if you're developing a Java application.
* It's small and easy to use, not a full blown full featured benchmarking suite.
* It does not offer advanced filtering features, just restriction to connection ids and prefix matchings.
* Measurements can be exported in either CSV for easy excel import or in JETM-Style.

## Usage
    Usage: MySQL Benckmark Tool [options]
      Options:
        -db
           The database name. eg.: -db test_db
           Default: test_db
        -f
           The result can be in default JETM style or CSV. JETM is default.
           Default: JETM
        -help, -h
           Execute mysql query benchmark based on mysql logs
           Default: false
        -id
           Only execute queries from the log with this connection id. This parameter
           is optional.
        -ignore
           Ignore statements which start with these prefixes, case insensitive.
           Enter a comma separated list of prefixes.
           Default: []
        -log
           Location of the logfile which contains the mysql queries to execute
           Default: benchmarking-queries.sql
        -o
           Location of the output file to write the results to
           Default: results.txt
      * -p
           mysql password
        -s
           The connection string to the mysql server (without database name). eg.:
           -c jdbc:mysql://localhost:3306/
           Default: jdbc:mysql://localhost:3306/
      * -u
           mysql username
        -verbose, -v
           Print all results to console
           Default: false
