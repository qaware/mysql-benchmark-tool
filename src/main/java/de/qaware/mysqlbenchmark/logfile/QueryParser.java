package de.qaware.mysqlbenchmark.logfile;

import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Felix Kelm felix.kelm@qaware.de
 */
public class QueryParser {
    private List<String> queries = new ArrayList<String>(1000);

    public List<String> getQueries() {
        return queries;
    }

    /**
     * Read ONE query from a string
     *
     * @param line           String to parse for ONE query.
     * @param restrictedID   only parse the query if this connection id matches
     * @param ignorePrefixes do not accept queries which start with these prefixes. May be null if not needed.
     * @throws IOException
     */
    public void parseLine(String line, String restrictedID, List<String> ignorePrefixes) {

        String prefixPattern = Strings.isNullOrEmpty(restrictedID) ? "\\d+" : restrictedID.toLowerCase();

        Pattern pattern = Pattern.compile("[\\s\\d:]*\\s+" + prefixPattern + "\\s+query\\s+(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {

            // ignore queries which start with special words
            for (String prefix : ignorePrefixes) {
                if (matcher.group(1).toLowerCase().startsWith(prefix.toLowerCase())) {
                    return;
                }
            }

            queries.add(matcher.group(1));
        }
    }

    /**
     * Read sql queries from the given logfile
     *
     * @param inputFilename
     * @param restrictedID
     * @param ignorePrefixes do not accept queries which start with these prefixes. May be null if not needed.
     * @throws IOException
     */
    public void parseLogFile(String inputFilename, String restrictedID, List<String> ignorePrefixes) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputFilename));
        String line;
        while ((line = br.readLine()) != null) {
            parseLine(line, restrictedID, ignorePrefixes);
        }
        br.close();
    }
}
