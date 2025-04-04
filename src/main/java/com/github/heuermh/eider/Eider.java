/*
 * The authors of this file license it to you under the
 * Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You
 * may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.heuermh.eider;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Path;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.time.Duration;

import java.util.Arrays;
import java.util.List;

import java.util.concurrent.Callable;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.AutoComplete.GenerateCompletion;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.ScopeType;

/**
 * Eider.
 *
 * @author  Michael Heuer
 */
@Command(
  name = "eider",
  scope = ScopeType.INHERIT,
  subcommands = {
      HelpCommand.class,
      GenerateCompletion.class
  },
  mixinStandardHelpOptions = true,
  sortOptions = false,
  usageHelpAutoWidth = true,
  resourceBundle = "com.github.heuermh.eider.Messages",
  versionProvider = com.github.heuermh.eider.About.class
)
public final class Eider implements Callable<Integer> {

    @Option(names = { "-u", "--url" })
    private String url = "jdbc:duckdb:";

    @Option(names = { "-q", "--query" })
    private String query;

    @Option(names = { "-i", "--query-path" })
    private Path queryPath;

    @Option(names = { "--skip-history" })
    private boolean skipHistory;

    @Option(names = { "--verbose" })
    private boolean verbose;

    /** History file. */
    private final HistoryFile historyFile = new HistoryFile();

    /** Static logger. */
    static Logger logger;


    @Override
    public Integer call() throws Exception {

        // prepare query from inline or query path
        if (query == null) {
            logger.info("Reading SQL query from path {}", queryPath == null ? "<stdin>" : queryPath);
            query = readQueryPath();
        }

        // write query to history file
        if (!skipHistory) {
            historyFile.append(query);
        }

        // connect to DuckDB
        Class.forName("org.duckdb.DuckDBDriver");
        try (Connection connection = DriverManager.getConnection(url)) {
            Stopwatch total = Stopwatch.createUnstarted();
            try (Statement statement = connection.createStatement()) {

                // split query into subqueries by `;`
                Stopwatch perStatement = Stopwatch.createUnstarted();
                for (String sql : Splitter.on(';').trimResults().omitEmptyStrings().split(query)) {

                    logger.info("Executing SQL subquery \"{}\"...", abbreviate(sql));
                    total.start();
                    perStatement.start();

                    // execute statement
                    statement.execute(sql);

                    perStatement.stop();
                    total.stop();
                    logger.info("Executed in {}", format(perStatement.elapsed()));

                    perStatement.reset();
                }
            }
            logger.info("Total elapsed {}", format(total.elapsed()));
        }

        return 0;
    }


    /**
     * Read the query from the <code>queryPath</code> option.
     *
     * @return the query read from the <code>queryPath</code> option
     */
    private String readQueryPath() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = reader(queryPath)) {
            while (reader.ready()) {
                String line = reader.readLine();
                sb.append(line);
                sb.append(" ");
            }
        }
        catch (IOException e) {
            logger.error("Unable to read SQL from {}", queryPath == null ? "<stdin>" : queryPath);
            throw e;
        }
        return sb.toString().trim().replace("\\s+", " ");
    }


    /**
     * Abbreviate the specified value for log messages.
     *
     * @param value value to abbreviate for log messages
     * @return the specified value abbreviated for log messages
     */
    private static String abbreviate(final String value) {
        checkNotNull(value);
        if (value.length() < 35) {
            return value;
        }
        return value.substring(0, 32) + "...";
    }

    /**
     * Format the specified duration for log messages.
     *
     * @param duration duration to format, must not be null
     * @return the specified duration formatted for log messages
     */
    private static String format(final Duration duration) {
        checkNotNull(duration);
        StringBuilder sb = new StringBuilder();
        long h = duration.toHours();
        if (h > 0) {
            sb.append(h);
            sb.append("h ");
        }
        long m = duration.toMinutesPart();
        if (h > 0 || m > 0) {
            sb.append(m);
            sb.append("m ");
        }
        sb.append(duration.toSecondsPart());
        sb.append("."); // todo: i18n
        sb.append(String.format("%03ds", duration.toMillisPart()));
        return sb.toString();
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {

        // cheat to set system property before initializing logger
        if (Arrays.asList(args).contains("--verbose")) {
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        }
        logger = LoggerFactory.getLogger(Eider.class);

        System.exit(new CommandLine(new Eider()).execute(args));
    }
}
