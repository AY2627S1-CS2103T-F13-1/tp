package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class StudentTest {
    private final Name name = new Name("Aiden Tan");
    private final Phone phone = new Phone("91234567");
    private final Guardian guardian = new Guardian(new Name("Tan Mei Ling"), phone);

    @Test
    public void constructor_optionalContacts_requiresOneNumber() {
        assertTrue(new Student(name, phone, null).getGuardian().isEmpty());
        assertTrue(new Student(name, null, guardian).getPhone().isEmpty());
        assertEquals(guardian, new Student(name, phone, guardian).getGuardian().orElseThrow());
        assertThrows(IllegalArgumentException.class, () -> new Student(name, null, null));
        assertThrows(NullPointerException.class, () -> new Student(null, phone, null));
        assertThrows(NullPointerException.class, () -> new Guardian(name, null));
        assertThrows(NullPointerException.class, () -> new Guardian(null, phone));
    }

    @Test
    public void isSameStudent_normalizedNameAndSharedNumber() {
        Student student = new Student(name, phone, null);
        Student guardianContact = new Student(new Name("aiden  tan "), null, guardian);
        assertTrue(student.isSameStudent(guardianContact));
        assertTrue(guardianContact.isSameStudent(student));
        assertFalse(student.isSameStudent(new Student(name, new Phone("88888888"), null)));
        assertFalse(student.isSameStudent(new Student(new Name("Chloe Tan"), null, guardian)));
        assertFalse(student.isSameStudent(null));
    }

    @Test
    public void equals_comparesAllFields() {
        Student student = new Student(name, phone, guardian);
        Student copy = new Student(new Name("Aiden Tan"), new Phone("91234567"),
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
        assertEquals(student, copy);
        assertEquals(student.hashCode(), copy.hashCode());
        assertNotEquals(student, new Student(name, phone, null));
        assertNotEquals(student, new Student(name, null, guardian));
        assertNotEquals(student, new Student(name, phone, new Guardian(new Name("Other Guardian"), phone)));
    }
}
