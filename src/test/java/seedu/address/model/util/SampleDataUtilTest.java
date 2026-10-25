package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.StudentTestUtil.assertStudentDetailsEqual;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_containsRepresentativeStudents() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        List<Student> students = sampleAddressBook.getStudentList();

        assertFalse(students.isEmpty());
        assertTrue(students.stream().anyMatch(student -> student.getPhone().isEmpty()));
        assertTrue(students.stream().anyMatch(student -> student.getGuardian().isEmpty()));
        assertTrue(students.stream().anyMatch(student -> student.getPhone().isPresent()
                && student.getGuardian().isPresent()));
        Student[] expected = SampleDataUtil.getSampleStudents();
        assertEquals(expected.length, students.size());
        for (int i = 0; i < expected.length; i++) {
            assertStudentDetailsEqual(expected[i], students.get(i));
        }
    }

    @Test
    public void getSampleAddressBook_containsRepresentativeLessonsAndEnrolments() {
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        List<Student> students = sampleAddressBook.getStudentList();
        List<Lesson> lessons = sampleAddressBook.getLessonList();

        assertEquals(2, lessons.size());
        assertEquals("Sec 3 A-Math", lessons.get(0).getName());
        assertEquals("Late Physics", lessons.get(1).getName());

        List<Enrolment> enrolments = sampleAddressBook.getEnrolmentList();
        assertEquals(2, enrolments.size());

        UUID aidenId = students.get(0).getId();
        UUID chloeId = students.get(1).getId();
        assertTrue(enrolments.contains(new Enrolment(aidenId, lessons.get(0).getId())));
        assertTrue(enrolments.contains(new Enrolment(chloeId, lessons.get(1).getId())));
    }

    @Test
    public void getSamplePersons_suppliesLegacyPersonRecords() {
        assertEquals(6, SampleDataUtil.getSamplePersons().length);
        ReadOnlyAddressBook sampleAddressBook = SampleDataUtil.getSampleAddressBook();
        assertEquals(6, sampleAddressBook.getPersonList().size());
    }
}
