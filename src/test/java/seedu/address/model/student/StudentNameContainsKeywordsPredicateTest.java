package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class StudentNameContainsKeywordsPredicateTest {
    private final Student aiden = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private final Student ryan = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    @Test
    public void equals() {
        StudentNameContainsKeywordsPredicate firstPredicate =
                new StudentNameContainsKeywordsPredicate(List.of("first"), List.of());
        StudentNameContainsKeywordsPredicate secondPredicate =
                new StudentNameContainsKeywordsPredicate(List.of("first", "second"), List.of());

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new StudentNameContainsKeywordsPredicate(List.of("first"), List.of())));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different name keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));

        // same keywords searched as guardian names instead of student names -> returns false
        assertFalse(firstPredicate.equals(new StudentNameContainsKeywordsPredicate(List.of(), List.of("first"))));
    }

    @Test
    public void test_studentNameContainsKeywords_returnsTrue() {
        // One keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of()).test(aiden));

        // Only one matching keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("Bob", "Ryan"), List.of()).test(ryan));

        // Mixed-case keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("aIDEN"), List.of()).test(aiden));
    }

    @Test
    public void test_studentNameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Chloe"), List.of()).test(aiden));

        // Partial word
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Aid"), List.of()).test(aiden));

        // Keyword matches only the guardian's name
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Ling"), List.of()).test(aiden));

        // Keyword matches only a phone number
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("93210283"), List.of()).test(ryan));
    }

    @Test
    public void test_guardianNameContainsKeywords_returnsTrue() {
        // One keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ling")).test(aiden));

        // Only one matching keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Lim", "Mei")).test(aiden));

        // Mixed-case keyword
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of(), List.of("mEI")).test(aiden));
    }

    @Test
    public void test_guardianNameDoesNotContainKeywords_returnsFalse() {
        // Non-matching keyword
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Wei")).test(aiden));

        // Partial word
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Lin")).test(aiden));

        // Keyword matches only the student's own name
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Aiden")).test(aiden));

        // Keyword matches only the guardian's phone number
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of(), List.of("91234567")).test(aiden));

        // Student has no guardian
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Ryan", "Lim")).test(ryan));
    }

    @Test
    public void test_studentAndGuardianKeywords_mustBothMatch() {
        assertTrue(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of("Ling")).test(aiden));

        // Student name matches, guardian name does not
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of("Wei")).test(aiden));

        // Guardian name matches, student name does not
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Chloe"), List.of("Ling")).test(aiden));

        // Student name matches, but the student has no guardian
        assertFalse(new StudentNameContainsKeywordsPredicate(List.of("Ryan"), List.of("Lim")).test(ryan));
    }

    @Test
    public void test_zeroKeywords_returnsFalse() {
        StudentNameContainsKeywordsPredicate predicate = new StudentNameContainsKeywordsPredicate(List.of(), List.of());
        assertFalse(predicate.test(aiden));
        assertFalse(predicate.test(ryan));
    }

    @Test
    public void toStringMethod() {
        List<String> nameKeywords = List.of("keyword1", "keyword2");
        List<String> guardianNameKeywords = List.of("keyword3");
        StudentNameContainsKeywordsPredicate predicate =
                new StudentNameContainsKeywordsPredicate(nameKeywords, guardianNameKeywords);

        String expected = StudentNameContainsKeywordsPredicate.class.getCanonicalName() + "{nameKeywords="
                + nameKeywords + ", guardianNameKeywords=" + guardianNameKeywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
