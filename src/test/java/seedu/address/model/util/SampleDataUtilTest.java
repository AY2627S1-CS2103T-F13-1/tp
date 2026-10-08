package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
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
        assertEquals(List.of(SampleDataUtil.getSampleStudents()), students);
    }
}
