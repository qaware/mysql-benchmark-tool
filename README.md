# MySQL Benchmark Tool
MySQL Benchmark Tool is a lightweight commandline tool for repeatedly executing SQL-Statements from a previously written MySQL-Log.

## Download
You can find precompiled jars in the bin folder: https://github.com/qaware/mysql-benchmark-tool/tree/master/bin

## What is does and how to use it
There's a blog post with more detailed explanation at: http://blog.qaware.de/

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
