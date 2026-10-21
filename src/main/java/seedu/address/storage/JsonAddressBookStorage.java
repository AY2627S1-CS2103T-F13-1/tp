package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a JSON file on the hard disk.
 */
public class JsonAddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private final Path filePath;
    private final Set<Path> blockedSavePaths = new HashSet<>();
    private String loadWarning = "";

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    /** Warning from the last load, for the existing startup message box. */
    public String getLoadWarning() {
        return loadWarning;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    /**
     * Returns AddressBook data as a {@link ReadOnlyAddressBook}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Path normalizedPath = filePath.toAbsolutePath().normalize();
        loadWarning = "";
        try {
            if (Files.notExists(filePath)) {
                blockedSavePaths.remove(normalizedPath);
                return Optional.empty();
            }
            byte[] original = Files.readAllBytes(filePath);
            AddressBookRecovery recovered = AddressBookRecovery.read(new String(original, StandardCharsets.UTF_8));
            if (!recovered.getSkippedRecords().isEmpty()) {
                Path backup = backupOriginal(normalizedPath, original);
                loadWarning = "Warning: skipped " + recovered.getSkippedRecords().size()
                        + " invalid or duplicate record(s), including any enrolments with missing references. "
                        + "Valid records were loaded. The original data, including skipped records, is saved in: "
                        + backup + "\nOpen this backup in a text editor to review the skipped information. "
                        + "The backup remains on disk after closing the app and is not automatically deleted.";
                logger.warning(loadWarning + " Skipped: " + recovered.getSkippedRecords());
            }
            blockedSavePaths.remove(normalizedPath);
            return Optional.of(recovered.getAddressBook());
        } catch (IOException e) {
            blockedSavePaths.add(normalizedPath);
            loadWarning = "Warning: the data file could not be safely loaded or backed up. "
                    + "Saving is disabled to protect the original file: " + normalizedPath
                    + ". Fix the file or restore a backup, then restart the app.";
            logger.warning(loadWarning + " " + e.getMessage());
            throw new DataLoadingException(e);
        }
    }

    /** Creates a unique backup of the exact bytes read, without replacing earlier backups. */
    protected Path backupOriginal(Path source, byte[] original) throws IOException {
        Path backup = Files.createTempFile(source.getParent(), source.getFileName() + ".recovery-", ".bak");
        try {
            Files.write(backup, original);
        } catch (IOException e) {
            Files.deleteIfExists(backup);
            throw e;
        }
        return backup;
    }

    /**
     * Saves the given {@link ReadOnlyAddressBook} to the storage.
     * @param addressBook cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        checkSaveAllowed(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);
    }

    /** Checks the configured file before a command changes any records. */
    public void checkSaveAllowed() throws IOException {
        checkSaveAllowed(filePath);
    }

    private void checkSaveAllowed(Path filePath) throws IOException {
        if (blockedSavePaths.contains(filePath.toAbsolutePath().normalize())) {
            throw new IOException("Saving is disabled because the original data could not be safely loaded. "
                    + "Fix the data file or restore a backup, then restart the app.");
        }
    }

}
