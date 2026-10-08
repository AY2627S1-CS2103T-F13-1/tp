package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

public class StudentCardDetailsTest {

    private static final Name STUDENT_NAME = new Name("Aiden Tan");
    private static final Phone STUDENT_PHONE = new Phone("92345678");
    private static final Name GUARDIAN_NAME = new Name("Tan Mei Ling");
    private static final Phone GUARDIAN_PHONE = new Phone("91234567");
    private static final Guardian GUARDIAN = new Guardian(GUARDIAN_NAME, GUARDIAN_PHONE);

    @Test
    public void from_allDetailsPresent_returnsAllDetails() {
        Student student = new Student(STUDENT_NAME, STUDENT_PHONE, GUARDIAN);

        StudentCardDetails details = StudentCardDetails.from(student);

        assertEquals("Aiden Tan", details.studentName());
        assertEquals("92345678", details.studentPhone());
        assertEquals("Tan Mei Ling", details.guardianName());
        assertEquals("91234567", details.guardianPhone());
    }

    @Test
    public void from_missingStudentPhone_usesPlaceholder() {
        Student student = new Student(STUDENT_NAME, null, GUARDIAN);

        StudentCardDetails details = StudentCardDetails.from(student);

        assertEquals("-", details.studentPhone());
        assertEquals("Tan Mei Ling", details.guardianName());
        assertEquals("91234567", details.guardianPhone());
    }

    @Test
    public void from_missingGuardian_usesPlaceholders() {
        Student student = new Student(STUDENT_NAME, STUDENT_PHONE, null);

        StudentCardDetails details = StudentCardDetails.from(student);

        assertEquals("-", details.guardianName());
        assertEquals("-", details.guardianPhone());
    }
}
