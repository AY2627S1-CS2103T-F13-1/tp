package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class PhoneTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Phone(null));
    }

    @Test
    public void constructor_invalidPhone_throwsIllegalArgumentException() {
        String invalidPhone = "";
        assertThrows(IllegalArgumentException.class, () -> new Phone(invalidPhone));
    }

    @Test
    public void constructor_formattedPhone_removesFormatting() {
        assertEquals("91234567", new Phone("9123 4567").value);
        assertEquals("91234567", new Phone("9123-4567").value);
        assertEquals("91234567", new Phone("+65 9123-4567").value);
        assertEquals("+447700900123", new Phone("+44 7700-900123").value);
        assertEquals("+651234", new Phone("+65 1234").value);
    }

    @Test
    public void isValidPhone() {
        // null phone number
        assertThrows(NullPointerException.class, () -> Phone.isValidPhone(null));

        // invalid phone numbers
        assertFalse(Phone.isValidPhone("")); // empty string
        assertFalse(Phone.isValidPhone(" ")); // spaces only
        assertFalse(Phone.isValidPhone("91")); // less than 3 numbers
        assertFalse(Phone.isValidPhone("9-1")); // less than 3 numbers with formatting
        assertFalse(Phone.isValidPhone("phone")); // non-numeric
        assertFalse(Phone.isValidPhone("9011p041")); // alphabets within digits
        assertFalse(Phone.isValidPhone("(65) 91234567")); // unsupported punctuation
        assertFalse(Phone.isValidPhone("65.91234567")); // unsupported punctuation
        assertFalse(Phone.isValidPhone("65_91234567")); // unsupported punctuation
        assertFalse(Phone.isValidPhone("65/91234567")); // unsupported punctuation
        assertFalse(Phone.isValidPhone("65+91234567")); // plus sign within digits
        assertFalse(Phone.isValidPhone("++6591234567")); // repeated plus signs
        assertFalse(Phone.isValidPhone("-91234567")); // leading separator
        assertFalse(Phone.isValidPhone("91234567-")); // trailing separator
        assertFalse(Phone.isValidPhone("9123--4567")); // repeated separators
        assertFalse(Phone.isValidPhone("9123  4567")); // repeated separators
        assertFalse(Phone.isValidPhone("9123 -4567")); // adjacent separators
        assertFalse(Phone.isValidPhone("９１２３４５６７")); // non-ASCII digits

        // valid phone numbers
        assertTrue(Phone.isValidPhone("93121534"));
        assertTrue(Phone.isValidPhone("911"));
        assertTrue(Phone.isValidPhone("9312 1534"));
        assertTrue(Phone.isValidPhone("9312-1534"));
        assertTrue(Phone.isValidPhone("+65 9312-1534"));
        assertTrue(Phone.isValidPhone("+44 7700-900123")); // international number
        assertTrue(Phone.isValidPhone("+123")); // minimum international length
        assertTrue(Phone.isValidPhone("1".repeat(10_000))); // legacy phone value without a length limit
        assertTrue(Phone.isValidPhone("1 ".repeat(2_999) + "1")); // many formatted groups
    }

    @Test
    public void isValidStudentPhone() {
        // invalid Singapore numbers
        assertFalse(Phone.isValidStudentPhone("91"));
        assertFalse(Phone.isValidStudentPhone("911"));
        assertFalse(Phone.isValidStudentPhone("6591234567")); // a country code requires a leading plus sign
        assertFalse(Phone.isValidStudentPhone("447700900123"));
        assertFalse(Phone.isValidStudentPhone("+65 9123456"));
        assertFalse(Phone.isValidStudentPhone("+65 912345678"));

        // invalid international numbers
        assertFalse(Phone.isValidStudentPhone("+12"));
        assertFalse(Phone.isValidStudentPhone("+" + "1".repeat(16)));

        // valid Singapore numbers
        assertTrue(Phone.isValidStudentPhone("91234567"));
        assertTrue(Phone.isValidStudentPhone("9123-4567"));
        assertTrue(Phone.isValidStudentPhone("+65 9123-4567"));

        // valid international numbers
        assertTrue(Phone.isValidStudentPhone("+123"));
        assertTrue(Phone.isValidStudentPhone("+44 7700-900123"));
        assertTrue(Phone.isValidStudentPhone("+" + "1".repeat(15)));
    }

    @Test
    public void isValidStudentPhone_manyFormattedGroups_returnsFalseWithoutOverflow() {
        String longPhone = "1 ".repeat(2_999) + "1";

        assertFalse(Phone.isValidStudentPhone(longPhone));
    }

    @Test
    public void equals() {
        Phone phone = new Phone("99999999");

        // same values -> returns true
        assertTrue(phone.equals(new Phone("99999999")));
        assertTrue(new Phone("9123 4567").equals(new Phone("9123-4567")));
        assertTrue(new Phone("91234567").equals(new Phone("+65 9123-4567")));
        assertEquals(new Phone("9123 4567").hashCode(), new Phone("9123-4567").hashCode());

        // same object -> returns true
        assertTrue(phone.equals(phone));

        // null -> returns false
        assertFalse(phone.equals(null));

        // different types -> returns false
        assertFalse(phone.equals(5.0f));

        // different values -> returns false
        assertFalse(phone.equals(new Phone("99999995")));
    }
}
