package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
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
    public void read_invalidStudent_rejectsFile() throws Exception {
        String[] invalidRecords = {
            "null",
            "{\"name\":\"Aiden\"}",
            "{\"name\":\"Aiden\",\"phone\":\"abc\"}",
            "{\"phone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"phone\":\"91234567\",\"guardianName\":\"Mei\"}",
            "{\"name\":\"Aiden\",\"guardianPhone\":\"91234567\"}",
            "{\"name\":\"Aiden\",\"guardianName\":\"\",\"guardianPhone\":\"91234567\"}"
        };
        Path file = testFolder.resolve("invalid.json");
        for (String record : invalidRecords) {
            Files.writeString(file, "{\"students\":[" + record + "]}");
            assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
        }
    }

    @Test
    public void read_duplicateStudents_rejectsFile() throws Exception {
        Path file = testFolder.resolve("duplicates.json");
        Files.writeString(file, """
                {"students": [
                  {"name": "Aiden Tan", "phone": "91234567"},
                  {"name": "aiden  tan", "guardianName": "Mei", "guardianPhone": "91234567"}
                ]}
                """);
        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
    }
}
