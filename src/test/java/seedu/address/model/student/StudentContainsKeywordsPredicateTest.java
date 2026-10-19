package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class StudentContainsKeywordsPredicateTest {
    private final Student aiden = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private final Student ryan = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentContainsKeywordsPredicate(null, List.of()));
        assertThrows(NullPointerException.class, () -> new StudentContainsKeywordsPredicate(List.of(), null));
        assertThrows(NullPointerException.class, () ->
                new StudentContainsKeywordsPredicate(Arrays.asList("Aiden", null), List.of()));
    }

    @Test
    public void constructor_keywordListChangedAfterwards_predicateUnchanged() {
        List<String> nameKeywords = new ArrayList<>(List.of("Aiden"));
        StudentContainsKeywordsPredicate predicate =
                new StudentContainsKeywordsPredicate(nameKeywords, List.of());
        nameKeywords.set(0, "Ryan");
        assertTrue(predicate.test(aiden));
        assertFalse(predicate.test(ryan));
    }

    @Test
    public void hashCode_equalPredicates_sameHashCode() {
        assertEquals(new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Tan")).hashCode(),
                new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Tan")).hashCode());
    }

    @Test
    public void equals() {
        StudentContainsKeywordsPredicate firstPredicate =
                new StudentContainsKeywordsPredicate(List.of("first"), List.of());
        StudentContainsKeywordsPredicate secondPredicate =
                new StudentContainsKeywordsPredicate(List.of("first", "second"), List.of());

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new StudentContainsKeywordsPredicate(List.of("first"), List.of())));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different name keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // same keywords searched as guardian names instead of student names -> returns false
        assertFalse(firstPredicate.equals(new StudentContainsKeywordsPredicate(List.of(), List.of("first"))));

        // different guardian name keywords -> returns false
        assertFalse(new StudentContainsKeywordsPredicate(List.of("first"), List.of("Tan"))
                .equals(new StudentContainsKeywordsPredicate(List.of("first"), List.of("Lim"))));

        // extra guardian name keywords -> returns false
        assertFalse(firstPredicate.equals(new StudentContainsKeywordsPredicate(List.of("first"), List.of("Tan"))));
    }

    @Test
    public void test_studentNameContainsKeywords_returnsTrue() {
        // One keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of()).test(aiden));

        // Only one matching keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Bob", "Ryan"), List.of()).test(ryan));

        // Mixed-case keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of("aIDEN"), List.of()).test(aiden));

        // Partial keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Aid"), List.of()).test(aiden));

        // Multiple keywords, all matching
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Aiden", "Tan"), List.of()).test(aiden));

        // Keywords in a different order from the name
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Tan", "Aiden"), List.of()).test(aiden));

        // Repeated keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Aiden", "Aiden"), List.of()).test(aiden));

        // Upper-case name
        Student upperCase = new Student(new Name("AIDEN TAN"), new Phone("91234567"), null);
        assertTrue(new StudentContainsKeywordsPredicate(List.of("aiden"), List.of()).test(upperCase));

        // Name with repeated spaces between words
        Student extraSpaces = new Student(new Name("Aiden   Tan"), new Phone("91234567"), null);
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Tan"), List.of()).test(extraSpaces));

        // Numeric word in name
        Student withNumber = new Student(new Name("Aiden Tan 2"), new Phone("91234567"), null);
        assertTrue(new StudentContainsKeywordsPredicate(List.of("2"), List.of()).test(withNumber));
    }

    @Test
    public void test_studentNameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Chloe"), List.of()).test(aiden));

        // Keyword longer than a word in the name
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Aidens"), List.of()).test(aiden));

        // Keyword joining two words of the name
        assertFalse(new StudentContainsKeywordsPredicate(List.of("AidenTan"), List.of()).test(aiden));

        // Multiple non-matching keywords
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Chloe", "Lim", "Joel"), List.of()).test(aiden));

        // Keyword matches only the guardian's name
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Ling"), List.of()).test(aiden));

        // Keyword matches only a phone number
        assertFalse(new StudentContainsKeywordsPredicate(List.of("93210283"), List.of()).test(ryan));
    }

    @Test
    public void test_guardianNameContainsKeywords_returnsTrue() {
        // One keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("Ling")).test(aiden));

        // Only one matching keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("Lim", "Mei")).test(aiden));

        // Mixed-case keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("mEI")).test(aiden));

        // Partial keyword
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("Lin")).test(aiden));

        // Keyword matches the guardian's surname, which the student shares
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("Tan")).test(aiden));

        // Student without a phone number, reachable only through the guardian
        assertTrue(aiden.getPhone().isEmpty());
        assertTrue(new StudentContainsKeywordsPredicate(List.of(), List.of("Tan", "Mei", "Ling")).test(aiden));
    }

    @Test
    public void test_guardianNameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("Wei")).test(aiden));

        // Keyword joining two words of the guardian's name
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("MeiLing")).test(aiden));

        // Keyword matches only the student's own name
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("Aiden")).test(aiden));

        // Keyword matches only the guardian's phone number
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("91234567")).test(aiden));

        // Student has no guardian
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("Ryan", "Lim")).test(ryan));
    }

    @Test
    public void test_studentAndGuardianKeywords_mustBothMatch() {
        assertTrue(new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Ling")).test(aiden));

        // Student name matches, guardian name does not
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Wei")).test(aiden));

        // Guardian name matches, student name does not
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Chloe"), List.of("Ling")).test(aiden));

        // Student name matches, but the student has no guardian
        assertFalse(new StudentContainsKeywordsPredicate(List.of("Ryan"), List.of("Lim")).test(ryan));
    }

    @Test
    public void test_sameStudentTestedRepeatedly_sameResult() {
        StudentContainsKeywordsPredicate predicate =
                new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Ling"));
        assertTrue(predicate.test(aiden));
        assertTrue(predicate.test(aiden));
        assertFalse(predicate.test(ryan));
        assertFalse(predicate.test(ryan));
    }

    @Test
    public void test_zeroKeywords_returnsFalse() {
        StudentContainsKeywordsPredicate predicate = new StudentContainsKeywordsPredicate(List.of(), List.of());
        assertFalse(predicate.test(aiden));
        assertFalse(predicate.test(ryan));
    }

    @Test
    public void test_blankKeyword_returnsFalse() {
        assertFalse(new StudentContainsKeywordsPredicate(List.of("   "), List.of()).test(aiden));
        assertFalse(new StudentContainsKeywordsPredicate(List.of(), List.of("   ")).test(aiden));
    }

    @Test
    public void toStringMethod() {
        List<String> nameKeywords = List.of("keyword1", "keyword2");
        List<String> guardianNameKeywords = List.of("keyword3");
        StudentContainsKeywordsPredicate predicate =
                new StudentContainsKeywordsPredicate(nameKeywords, guardianNameKeywords);

        String expected = StudentContainsKeywordsPredicate.class.getCanonicalName() + "{nameKeywords="
                + nameKeywords + ", guardianNameKeywords=" + guardianNameKeywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
