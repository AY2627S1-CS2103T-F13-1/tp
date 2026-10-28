package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("-Peter")); // punctuation at the start
        assertFalse(Name.isValidName("Peter-")); // punctuation at the end
        assertFalse(Name.isValidName("Anne--Marie")); // consecutive punctuation
        assertFalse(Name.isValidName("O''Connor")); // consecutive apostrophes
        assertFalse(Name.isValidName("Kumar s/o")); // connector without a following name part
        assertFalse(Name.isValidName("s/o Ravi")); // connector without a preceding name part
        assertFalse(Name.isValidName("Kumar s/o d/o Ravi")); // consecutive connectors
        assertFalse(Name.isValidName("Peter a/b Paul")); // unsupported slash connector
        assertFalse(Name.isValidName("\u0301Jose")); // combining mark without a preceding letter
        assertFalse(Name.isValidName("1\u0301 Jose")); // combining mark after a digit

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("José Tan")); // accented letter
        assertTrue(Name.isValidName("Jose\u0301 Tan")); // decomposed accented letter
        assertTrue(Name.isValidName("का")); // combining spacing mark
        assertTrue(Name.isValidName("A⃝")); // enclosing combining mark
        assertTrue(Name.isValidName("李小龍")); // letters from another script
        assertTrue(Name.isValidName("Anne-Marie Lim")); // internal hyphen
        assertTrue(Name.isValidName("O'Connor")); // straight apostrophe
        assertTrue(Name.isValidName("O’Connor")); // curly apostrophe
        assertTrue(Name.isValidName("Kumar s/o Ravi")); // Singapore name connector
        assertTrue(Name.isValidName("Siti d/o Ahmad"));
        assertTrue(Name.isValidName("Muthu a/l Rajan"));
        assertTrue(Name.isValidName("Devi a/p Ramasamy"));
        assertTrue(Name.isValidName("Kumar  s/o  Ravi")); // extra spaces remain valid
    }

    @Test
    public void isValidSearchKeyword() {
        assertThrows(NullPointerException.class, () -> Name.isValidSearchKeyword(null));

        assertFalse(Name.isValidSearchKeyword(""));
        assertFalse(Name.isValidSearchKeyword("Anne Marie"));
        assertFalse(Name.isValidSearchKeyword("a/b"));

        assertTrue(Name.isValidSearchKeyword("José"));
        assertTrue(Name.isValidSearchKeyword("Anne-Marie"));
        assertTrue(Name.isValidSearchKeyword("O'Connor"));
        assertTrue(Name.isValidSearchKeyword("s/o"));
        assertTrue(Name.isValidSearchKeyword("D/O"));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
