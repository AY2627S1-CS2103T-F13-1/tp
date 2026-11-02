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
        assertFalse(Name.isValidName("José Tan")); // accented letter
        assertFalse(Name.isValidName("Jose\u0301 Tan")); // decomposed accented letter
        assertFalse(Name.isValidName("का")); // letters from another script
        assertFalse(Name.isValidName("李小龍"));
        assertFalse(Name.isValidName(". Peter")); // period without a preceding name character
        assertFalse(Name.isValidName(", Peter")); // comma without a preceding name character
        assertFalse(Name.isValidName("Peter.. Tan")); // consecutive periods
        assertFalse(Name.isValidName("Tan,David")); // comma within a name part
        assertFalse(Name.isValidName("@ Peter")); // alias separator without a preceding name part
        assertFalse(Name.isValidName("Peter @")); // alias separator without a following name part
        assertFalse(Name.isValidName("Peter @ @ David")); // consecutive alias separators
        assertFalse(Name.isValidName("Peter (David @ Smith)")); // alias separator inside parentheses
        assertFalse(Name.isValidName("Peter (David")); // unclosed parentheses
        assertFalse(Name.isValidName("Peter David)")); // closing parenthesis without an opening one
        assertFalse(Name.isValidName("Peter Da)vid")); // closing parenthesis inside a name part
        assertFalse(Name.isValidName("Peter () David")); // empty parentheses
        assertFalse(Name.isValidName("Peter ((David))")); // nested parentheses
        assertFalse(Name.isValidName("Peter (David (Junior))")); // nested parentheses across name parts

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Anne-Marie Lim")); // internal hyphen
        assertTrue(Name.isValidName("O'Connor")); // straight apostrophe
        assertTrue(Name.isValidName("O’Connor")); // curly apostrophe
        assertTrue(Name.isValidName("Kumar s/o Ravi")); // Singapore name connector
        assertTrue(Name.isValidName("Siti d/o Ahmad"));
        assertTrue(Name.isValidName("Muthu a/l Rajan"));
        assertTrue(Name.isValidName("Devi a/p Ramasamy"));
        assertTrue(Name.isValidName("Kumar  s/o  Ravi")); // extra spaces remain valid
        assertTrue(Name.isValidName("Ng Ah Kow @ Ng Kok Wee")); // alias separator
        assertTrue(Name.isValidName("S. Ramasamy")); // initial with a period
        assertTrue(Name.isValidName("Tan Ah Kow Jr.")); // suffix with a period
        assertTrue(Name.isValidName("J.R.R. Tolkien")); // several initials in one name part
        assertTrue(Name.isValidName("Tan, David")); // comma after a surname
        assertTrue(Name.isValidName("Tan Wei Ming (David)")); // parenthesized additional name
        assertTrue(Name.isValidName("TAN AH LIAN (TAN A LIAN) @ MARY TAN (MA LI DAN)"));
    }

    @Test
    public void isValidSearchKeyword() {
        assertThrows(NullPointerException.class, () -> Name.isValidSearchKeyword(null));

        assertFalse(Name.isValidSearchKeyword(""));
        assertFalse(Name.isValidSearchKeyword("Anne Marie"));
        assertFalse(Name.isValidSearchKeyword("a/b"));
        assertFalse(Name.isValidSearchKeyword("'")); // nothing but punctuation
        assertFalse(Name.isValidSearchKeyword("-"));
        assertFalse(Name.isValidSearchKeyword("''"));
        assertFalse(Name.isValidSearchKeyword("o''")); // repeated punctuation is a typo, even at an edge
        assertFalse(Name.isValidSearchKeyword("--ann"));
        assertFalse(Name.isValidSearchKeyword("Anne--Marie"));
        assertFalse(Name.isValidSearchKeyword("José"));

        assertTrue(Name.isValidSearchKeyword("Anne-Marie"));
        assertTrue(Name.isValidSearchKeyword("O'Connor"));
        assertTrue(Name.isValidSearchKeyword("s/o"));
        assertTrue(Name.isValidSearchKeyword("D/O"));
        assertTrue(Name.isValidSearchKeyword("@"));
        assertTrue(Name.isValidSearchKeyword("S."));
        assertTrue(Name.isValidSearchKeyword("Tan,"));
        assertTrue(Name.isValidSearchKeyword("(Tan"));
        assertTrue(Name.isValidSearchKeyword("Lian)"));
        assertTrue(Name.isValidSearchKeyword("o'")); // part of a name, cut at its punctuation
        assertTrue(Name.isValidSearchKeyword("o’"));
        assertTrue(Name.isValidSearchKeyword("'connor"));
        assertTrue(Name.isValidSearchKeyword("anne-"));
        assertTrue(Name.isValidSearchKeyword("-marie"));
        assertTrue(Name.isValidSearchKeyword("-ann-"));
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
