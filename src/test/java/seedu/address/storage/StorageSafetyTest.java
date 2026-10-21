package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;

public class StorageSafetyTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void read_invalidData_blocksOverwriteUntilSuccessfulReload() throws Exception {
        Path file = temporaryFolder.resolve("data.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        for (String invalid : List.of("null", "{", "{\"students\":{}}", "{\"lessons\":42}")) {
            Files.writeString(file, invalid);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));
            assertEquals(invalid, Files.readString(file));
            Files.writeString(file, "{}");
            storage.readAddressBook();
            storage.saveAddressBook(new AddressBook());
            assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void read_invalidRecords_recoversAndBacksUpBeforeSaving() throws Exception {
        for (String collection : List.of("persons", "students", "lessons", "enrolments")) {
            Path file = temporaryFolder.resolve(collection + ".json");
            String original = "{\"" + collection + "\":[null]}";
            Files.writeString(file, original);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
            assertEquals(original, Files.readString(file));
            try (var files = Files.list(temporaryFolder)) {
                List<Path> backups = files.filter(path -> path.getFileName().toString()
                        .startsWith(collection + ".json.recovery-")).toList();
                assertEquals(1, backups.size());
                assertEquals(original, Files.readString(backups.getFirst()));
            }
            storage.saveAddressBook(new AddressBook());
            assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        }
    }

    @Test
    public void save_failedReplacement_preservesExistingContentsAndCleansTemporaryFile() throws Exception {
        Path target = temporaryFolder.resolve("data.json");
        Files.createDirectory(target);
        Path existing = target.resolve("keep.txt");
        Files.writeString(existing, "original");
        assertThrows(IOException.class, () -> JsonUtil.saveJsonFile(List.of("new"), target));
        assertEquals("original", Files.readString(existing));
        try (var files = Files.list(temporaryFolder)) {
            assertEquals(List.of(target), files.toList());
        }
    }

    @Test
    public void save_atomicReplacement_roundTripsAndLeavesNoTemporaryFile() throws Exception {
        Path file = temporaryFolder.resolve("data.json");
        Files.writeString(file, "old contents");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(new AddressBook());
        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
        try (var files = Files.list(temporaryFolder)) {
            assertEquals(List.of(file), files.toList());
        }
    }
}
