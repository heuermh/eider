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

import static org.dishevelled.compress.Readers.reader;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.AutoComplete.GenerateCompletion;

import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
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

    @picocli.CommandLine.Option(names = { "--skip-history" })
    private boolean skipHistory;

    @picocli.CommandLine.Option(names = { "-q", "--query" })
    private String query;

    @picocli.CommandLine.Option(names = { "-i", "--query-path" })
    private Path queryPath;

    @picocli.CommandLine.Option(names = { "--verbose" })
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

        return 0;
    }


    /**
     * Read the query from the <code>queryPath</code> option.
     *
     * @return the query read from the <code>queryPath</code> option
     */
    String readQueryPath() throws IOException {
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
