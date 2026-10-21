package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;

public class AddressBookRecoveryTest {
    private static final String MIXED_DATA = """
            {
              "students": [
                {"id":"00000000-0000-0000-0000-000000000001","name":"Aiden","phone":"91234567"},
                {"id":"00000000-0000-0000-0000-000000000002","name":"Ben","phone":"invalid"},
                {"name":{},"phone":"91234567"},
                {"name":"aiden","phone":"91234567"}
              ],
              "lessons": [
                {"id":"00000000-0000-0000-0000-000000000003","name":"Math",
                 "day":"MONDAY","startTime":"1600","endTime":"1800"},
                {"id":"00000000-0000-0000-0000-000000000004","name":"Science",
                 "day":"MONDAY","startTime":"1800","endTime":"1800"}
              ],
              "enrolments": [
                {"studentId":"00000000-0000-0000-0000-000000000001",
                 "lessonId":"00000000-0000-0000-0000-000000000003"},
                {"studentId":"00000000-0000-0000-0000-000000000002",
                 "lessonId":"00000000-0000-0000-0000-000000000003"},
                {"studentId":"00000000-0000-0000-0000-000000000001",
                 "lessonId":"00000000-0000-0000-0000-000000000004"}
              ]
            }
            """;

    @TempDir
    public Path folder;

    @Test
    public void recover_keepsValidRecordsAndLinks_backsUpExactOriginalBeforeSaving() throws Exception {
        Path file = folder.resolve("addressbook.json");
        Files.writeString(file, MIXED_DATA);
        byte[] original = Files.readAllBytes(file);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        var recovered = storage.readAddressBook().orElseThrow();
        assertEquals(1, recovered.getStudentList().size());
        assertEquals(1, recovered.getLessonList().size());
        assertEquals(1, recovered.getEnrolmentList().size());
        assertTrue(storage.getLoadWarning().contains("skipped 6"));
        Path backup = backups().getFirst();
        assertTrue(storage.getLoadWarning().contains(backup.toString()));
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertArrayEquals(original, Files.readAllBytes(file));
        storage.saveAddressBook(recovered);
        assertArrayEquals(original, Files.readAllBytes(backup));
        assertEquals(new AddressBook(recovered), new AddressBook(storage.readAddressBook().orElseThrow()));
        assertEquals("", storage.getLoadWarning());
        assertEquals(1, backups().size());
    }

    @Test
    public void repeatedRecovery_neverOverwritesPreviousBackup() throws Exception {
        Path file = folder.resolve("addressbook.json");
        Files.writeString(file, MIXED_DATA);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.readAddressBook();
        storage.readAddressBook();
        assertEquals(2, backups().size());
        for (Path backup : backups()) {
            assertEquals(MIXED_DATA, Files.readString(backup));
        }
    }

    @Test
    public void unreadableStructure_blocksSavingAndPreservesOriginal() throws Exception {
        Path file = folder.resolve("addressbook.json");
        for (String invalid : List.of("{", "null", "[]", "{} {}", "{\"students\":{}}")) {
            Files.writeString(file, invalid);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertTrue(storage.getLoadWarning().contains("Saving is disabled"));
            assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));
            assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook(),
                    folder.resolve(".").resolve("addressbook.json")));
            assertEquals(invalid, Files.readString(file));
        }
        assertTrue(backups().isEmpty());
    }

    @Test
    public void failedBackup_blocksSaving() throws Exception {
        Path file = folder.resolve("addressbook.json");
        Files.writeString(file, MIXED_DATA);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file) {
            @Override
            protected Path backupOriginal(Path source, byte[] original) throws IOException {
                throw new IOException("Disk is full");
            }
        };
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));
        assertEquals(MIXED_DATA, Files.readString(file));
    }

    @Test
    public void validAndMissingFiles_needNoRecovery() throws Exception {
        Path file = folder.resolve("addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertFalse(storage.readAddressBook().isPresent());
        Files.writeString(file, "{\"students\":[{\"name\":\"Aiden\",\"phone\":\"91234567\"}]}");
        assertEquals(1, storage.readAddressBook().orElseThrow().getStudentList().size());
        assertEquals("", storage.getLoadWarning());
        assertTrue(backups().isEmpty());
    }

    private List<Path> backups() throws IOException {
        try (var paths = Files.list(folder)) {
            return paths.filter(path -> path.toString().endsWith(".bak")).toList();
        }
    }
}
