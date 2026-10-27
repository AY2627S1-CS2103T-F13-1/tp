package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

public class StudentStorageTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveAndReload_preservesAllContactCombinationsAndDeletions() throws Exception {
        AddressBook book = new AddressBook();
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student aiden = new Student(new Name("Aiden Tan"), null, guardian);
        book.addStudent(aiden);
        book.addStudent(new Student(new Name("Chloe Tan"), new Phone("88888888"), null));
        book.addStudent(new Student(new Name("Ben Tan"), new Phone("99999999"), guardian));
        book.addPerson(ALICE);
        Path file = testFolder.resolve("addressbook.json");
        new JsonAddressBookStorage(file).saveAddressBook(book);
        assertEquals(book, new AddressBook(new JsonAddressBookStorage(file).readAddressBook().orElseThrow()));
        book.removeStudent(aiden);
        new JsonAddressBookStorage(file).saveAddressBook(book);
        assertEquals(book, new AddressBook(new JsonAddressBookStorage(file).readAddressBook().orElseThrow()));
    }

    @Test
    public void read_legacyFile_keepsPersonsWithEmptyStudents() throws Exception {
        Path file = Path.of("src/test/data/JsonSerializableAddressBookTest/typicalPersonsAddressBook.json");
        var loaded = new JsonAddressBookStorage(file).readAddressBook().orElseThrow();
        assertTrue(loaded.getStudentList().isEmpty());
        assertTrue(loaded.getPersonList().contains(ALICE));
    }

    @Test
    public void read_invalidStudent_skipsRecord() throws Exception {
        String[] invalidRecords = {
            "null",
            "{\"name\":\"Aiden\"}",
            "{\"name\":\"Aiden!\",\"phone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"phone\":\"abc\"}",
            "{\"phone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"phone\":\"91234567\",\"guardianName\":\"Mei\"}",
            "{\"name\":\"Aiden\",\"guardianPhone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"guardianName\":\"\",\"guardianPhone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"guardianName\":\"Mei\",\"guardianPhone\":\"abc\"}",
            "{\"id\":\"not-a-uuid\",\"name\":\"Aiden\",\"phone\":\"91234567\"}"
        };
        Path file = testFolder.resolve("invalid.json");
        for (String record : invalidRecords) {
            Files.writeString(file, "{\"students\":[" + record + "]}");
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertTrue(storage.readAddressBook().isPresent());
            assertTrue(storage.getLoadWarning().contains("skipped"));
        }
    }

    @Test
    public void read_duplicateStudents_keepsFirst() throws Exception {
        Path file = testFolder.resolve("duplicates.json");
        Files.writeString(file, """
                {"students": [
                  {"name": "Aiden Tan", "phone": "91234567"},
                  {"name": "aiden  tan", "guardianName": "Mei", "guardianPhone": "91234567"}
                ]}
                """);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertEquals(1, storage.readAddressBook().orElseThrow().getStudentList().size());
        assertTrue(storage.getLoadWarning().contains("skipped 1"));
    }

    @Test
    public void readAndSave_formattedPhones_storesNormalizedDigits() throws Exception {
        Path sourceFile = testFolder.resolve("formatted.json");
        Files.writeString(sourceFile, """
                {"students": [
                  {"name": "Aiden Tan", "phone": "9123-4567", "guardianName": "Mei Tan",
                   "guardianPhone": "+65 9234-5678"},
                  {"name": "Alex Smith", "phone": "+44 7700-900123"}
                ]}
                """);
        JsonAddressBookStorage sourceStorage = new JsonAddressBookStorage(sourceFile);
        AddressBook loadedBook = new AddressBook(sourceStorage.readAddressBook().orElseThrow());
        Student student = loadedBook.getStudentList().getFirst();
        assertEquals(new Phone("91234567"), student.getPhone().orElseThrow());
        assertEquals(new Phone("92345678"), student.getGuardian().orElseThrow().getPhone());
        assertEquals(new Phone("+447700900123"), loadedBook.getStudentList().get(1).getPhone().orElseThrow());

        Path savedFile = testFolder.resolve("normalized.json");
        new JsonAddressBookStorage(savedFile).saveAddressBook(loadedBook);
        String savedJson = Files.readString(savedFile);
        assertTrue(savedJson.contains("\"phone\" : \"91234567\""));
        assertTrue(savedJson.contains("\"guardianPhone\" : \"92345678\""));
        assertTrue(savedJson.contains("\"phone\" : \"+447700900123\""));
    }
}
