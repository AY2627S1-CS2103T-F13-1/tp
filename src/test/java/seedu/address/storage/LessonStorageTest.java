package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class LessonStorageTest {
    @TempDir
    public Path folder;

    @Test
    public void roundTrip_preservesIdsMembershipsAndDeletion() throws Exception {
        Student student = new Student(new Name("Aiden"), new Phone("91234567"), null);
        Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
        AddressBook book = new AddressBook();
        book.addStudent(student);
        book.addLesson(lesson);
        book.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        Path file = folder.resolve("data.json");
        new JsonAddressBookStorage(file).saveAddressBook(book);
        var restored = new JsonAddressBookStorage(file).readAddressBook().orElseThrow();
        assertEquals(book, new AddressBook(restored));
        assertEquals(student.getId(), restored.getStudentList().getFirst().getId());
        assertEquals(lesson.getId(), restored.getLessonList().getFirst().getId());
        assertEquals(List.of(student), restored.getStudentsInLesson(lesson));
        book.removeLesson(lesson);
        new JsonAddressBookStorage(file).saveAddressBook(book);
        restored = new JsonAddressBookStorage(file).readAddressBook().orElseThrow();
        assertTrue(restored.getEnrolmentList().isEmpty());
        assertEquals(List.of(student), restored.getStudentList());
    }

    @Test
    public void legacyStudent_getsIdPersistedOnNextSave() throws Exception {
        Path file = folder.resolve("legacy.json");
        Files.writeString(file, """
                {"students":[{"name":"Aiden","phone":"91234567"}]}
                """);
        var storage = new JsonAddressBookStorage(file);
        var loaded = storage.readAddressBook().orElseThrow();
        assertTrue(loaded.getLessonList().isEmpty());
        assertTrue(loaded.getEnrolmentList().isEmpty());
        storage.saveAddressBook(loaded);
        assertEquals(loaded.getStudentList().getFirst().getId(),
                storage.readAddressBook().orElseThrow().getStudentList().getFirst().getId());
    }

    @Test
    public void invalidData_rejectsFile() throws Exception {
        String lesson = """
                {"id":"00000000-0000-0000-0000-000000000001","name":"Math",
                 "day":"MONDAY","startTime":"1600","endTime":"1800"}
                """;
        String student = """
                {"id":"00000000-0000-0000-0000-000000000002","name":"Aiden","phone":"91234567"}
                """;
        String link = """
                {"studentId":"00000000-0000-0000-0000-000000000002",
                 "lessonId":"00000000-0000-0000-0000-000000000001"}
                """;
        String[] invalid = {
            "{\"lessons\":[null]}",
            "{\"lessons\":[" + lesson.replace("1800", "1600") + "]}",
            "{\"lessons\":[" + lesson.replace("1600", "2400") + "]}",
            "{\"lessons\":[" + lesson.replace("MONDAY", "BAD") + "]}",
            "{\"lessons\":[" + lesson + "," + lesson + "]}",
            "{\"students\":[" + student + "," + student.replace("Aiden", "Chloe") + "]}",
            "{\"enrolments\":[" + link + "]}",
            "{\"students\":[" + student + "],\"lessons\":[" + lesson
                + "],\"enrolments\":[" + link + "," + link + "]}"
        };
        Path file = folder.resolve("invalid.json");
        for (String json : invalid) {
            Files.writeString(file, json);
            assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
        }
    }
}
