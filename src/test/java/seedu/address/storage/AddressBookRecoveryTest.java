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
    public void recover_skippedRecords_warningNamesFirstFiveWithReasons() throws Exception {
        Path file = folder.resolve("addressbook.json");
        Files.writeString(file, MIXED_DATA);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.readAddressBook();
        String warning = storage.getLoadWarning();

        assertTrue(warning.contains("\n- student 2 (Ben): invalid phone number"));
        assertTrue(warning.contains("\n- student 3: a value has the wrong type or format"));
        assertTrue(warning.contains("\n- student 4 (aiden): duplicate student"));
        assertTrue(warning.contains("\n- lesson 2 (Science): "));
        assertTrue(warning.contains("\n- enrolment 2: refers to a missing student or lesson"));
        assertFalse(warning.contains("enrolment 3"));
        assertTrue(warning.contains("\n- and 1 more"));
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
    public void duplicateJsonKeys_blocksSavingAndPreservesOriginal() throws Exception {
        Path file = folder.resolve("addressbook.json");
        String student = "{\"name\":\"Aiden\",\"phone\":\"91234567\"}";
        for (String invalid : List.of(
                "{\"students\":[" + student + "],\"students\":[]}",
                "{\"students\":[{\"name\":\"Aiden\",\"name\":\"Ben\",\"phone\":\"91234567\"}]}")) {
            Files.writeString(file, invalid);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertTrue(storage.getLoadWarning().contains("Saving is disabled"));
            assertThrows(IOException.class, () -> storage.saveAddressBook(new AddressBook()));
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

    @Test
    public void recover_emptyInput_rejectedRatherThanTreatedAsEmptyBook() {
        for (String input : List.of("", "   ", "\n\t")) {
            assertThrows(IOException.class, () -> AddressBookRecovery.read(input));
        }
    }

    @Test
    public void recover_nullLists_acceptsLegacyDataWithoutSkippedRecords() throws Exception {
        var recovered = AddressBookRecovery.read(
                "{\"persons\":null,\"students\":null,\"lessons\":null,\"enrolments\":null}");
        assertEquals(new AddressBook(), recovered.getAddressBook());
        assertTrue(recovered.getSkippedRecords().isEmpty());
    }

    @Test
    public void recover_legacyPersons_keepsFirstDuplicateAndContinuesAfterInvalidRecord() throws Exception {
        String person = """
                {"name":"Alice","phone":"91234567","email":"alice@example.com","address":"Home"}
                """;
        String invalid = person.replace("\"Home\"", "\"Home\",\"tags\":[null]");
        String bob = person.replace("Alice", "Bob");
        var recovered = AddressBookRecovery.read(
                "{\"persons\":[" + person + "," + person + "," + invalid + "," + bob + "]}");
        assertEquals(List.of("Alice", "Bob"), recovered.getAddressBook().getPersonList().stream()
                .map(value -> value.getName().fullName).toList());
        assertEquals(List.of("person 2 (Alice): duplicate person", "person 3 (Alice): a value is missing"),
                recovered.getSkippedRecords());
        assertThrows(UnsupportedOperationException.class, () -> recovered.getSkippedRecords().clear());
    }

    @Test
    public void missingFileAfterFailedLoad_clearsWarningAndAllowsFreshSave() throws Exception {
        Path file = folder.resolve("addressbook.json");
        Files.writeString(file, "{");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        Files.delete(file);
        assertTrue(storage.readAddressBook().isEmpty());
        assertEquals("", storage.getLoadWarning());
        storage.checkSaveAllowed();
        storage.saveAddressBook(new AddressBook());
        assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void blockedPath_doesNotPreventSavingToAnotherFile() throws Exception {
        Path original = folder.resolve("addressbook.json");
        Path other = folder.resolve("other.json");
        Files.writeString(original, "{");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(original);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        storage.saveAddressBook(new AddressBook(), other);
        assertEquals(new AddressBook(), storage.readAddressBook(other).orElseThrow());
        assertThrows(IOException.class, storage::checkSaveAllowed);
        assertEquals("{", Files.readString(original));
    }

    private List<Path> backups() throws IOException {
        try (var paths = Files.list(folder)) {
            return paths.filter(path -> path.toString().endsWith(".bak")).toList();
        }
    }
}
