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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;

import java.nio.file.attribute.PosixFilePermission;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * History file.
 *
 * @author  Michael Heuer
 */
final class HistoryFile {
    /** File name. */
    private final String fileName;

    /** Default file name. */
    static final String DEFAULT_FILE_NAME = ".eider_history";

    /** Static logger. */
    static final Logger logger = LoggerFactory.getLogger(HistoryFile.class);


    /**
     * Create a new history file with the default file name.
     */
    HistoryFile() {
        this(DEFAULT_FILE_NAME);
    }

    /**
     * Create a new history file with the specified file name.
     *
     * @param fileName file name, must not be null
     */
    HistoryFile(final String fileName) {
	checkNotNull(fileName);
        this.fileName = fileName;
    }


    /**
     * Append the specified value to this history file.
     *
     * @param value value to append
     */
    void append(final String value) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(getHistoryFile(fileName), true)))) {
            writer.println(value);
        }
        catch (IOException e) {
            logger.warn("Could not write to history file, caught I/O exception", e);
        }
    }

    /**
     * Return the history file with the specified file name, creating it if necessary.
     *
     * @param fileName file name, must not be null
     * @return the history file with the specified file name, creating it if necessary
     */
    private static File getHistoryFile(final String fileName) throws IOException {
        File homeDirectory = new File(System.getProperty("user.home"));
        File historyFile = new File(homeDirectory, fileName);

        if (historyFile.exists()) {
            checkFilePermissions(historyFile);
        }
        else {
            createHistoryFile(historyFile);
        }
        logger.info("Opened history file {} for writing", historyFile);
        return historyFile;
    }

    /**
     * Create a history file with the correct file permissions.
     *
     * @param historyFile history file to create, must not be null
     * @throws IOException if an I/O error occurs
     */
    private static void createHistoryFile(final File historyFile) throws IOException {
        historyFile.createNewFile();
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        Files.setPosixFilePermissions(historyFile.toPath(), perms);
    }

    /**
     * Check file permissions for the specified history file. Incorrect file
     * permissions are logged at <code>WARN</code> level.
     *
     * @param historyFile history file to check, must not be null
     * @throws IOException if an I/O error occurs
     */
    private static void checkFilePermissions(final File historyFile) throws IOException {
        Set<PosixFilePermission> perms = Files.getPosixFilePermissions(historyFile.toPath());
        if (!perms.contains(PosixFilePermission.OWNER_READ)) {
            logger.warn("History file {} has incorrect posix file permissions, missing OWNER_READ", historyFile);
        }
        if (!perms.contains(PosixFilePermission.OWNER_WRITE)) {
            logger.warn("History file {} has incorrect posix file permissions, missing OWNER_WRITE", historyFile);
        }
        if (perms.contains(PosixFilePermission.GROUP_READ)) {
            logger.warn("History file {} has incorrect posix file permissions, should not have GROUP_READ", historyFile);
        }
        if (perms.contains(PosixFilePermission.GROUP_WRITE)) {
            logger.warn("History file {} has incorrect posix file permissions, should not have GROUP_WRITE", historyFile);
        }
        if (perms.contains(PosixFilePermission.OTHERS_READ)) {
            logger.warn("History file {} has incorrect posix file permissions, should not have OTHERS_READ", historyFile);
        }
        if (perms.contains(PosixFilePermission.OTHERS_WRITE)) {
            logger.warn("History file {} has incorrect posix file permissions, should not have OTHERS_READ", historyFile);
        }
    }
}
