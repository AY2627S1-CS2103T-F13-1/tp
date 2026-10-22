package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import seedu.address.model.student.Student;

/** Assertions for input that creates a new student with a generated ID. */
public class StudentTestUtil {
    /** Compares parsed contact details without expecting a predetermined generated ID. */
    public static void assertStudentDetailsEqual(Student expected, Student actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getGuardian(), actual.getGuardian());
    }
}
