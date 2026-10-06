package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s name and their guardian's name match the given keywords.
 * Matching is case-insensitive and by whole word. Within each list, any one keyword may match.
 * When both lists are non-empty, the student must match both. An empty list places no condition
 * on that name, but a predicate with no keywords at all matches no student.
 */
public class StudentNameContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> nameKeywords;
    private final List<String> guardianNameKeywords;

    /**
     * Creates a predicate from keywords for the student's name and for the guardian's name.
     * Neither list, nor any keyword in them, may be null.
     */
    public StudentNameContainsKeywordsPredicate(List<String> nameKeywords, List<String> guardianNameKeywords) {
        requireAllNonNull(nameKeywords, guardianNameKeywords);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.guardianNameKeywords = List.copyOf(guardianNameKeywords);
    }

    @Override
    public boolean test(Student student) {
        if (nameKeywords.isEmpty() && guardianNameKeywords.isEmpty()) {
            return false;
        }
        boolean nameMatches = nameKeywords.isEmpty() || containsAnyWord(student.getName().fullName, nameKeywords);
        boolean guardianNameMatches = guardianNameKeywords.isEmpty() || student.getGuardian()
                .map(guardian -> containsAnyWord(guardian.getName().fullName, guardianNameKeywords))
                .orElse(false);
        return nameMatches && guardianNameMatches;
    }

    private static boolean containsAnyWord(String name, List<String> keywords) {
        return keywords.stream().anyMatch(keyword -> StringUtil.containsWordIgnoreCase(name, keyword));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentNameContainsKeywordsPredicate otherPredicate)) {
            return false;
        }

        return nameKeywords.equals(otherPredicate.nameKeywords)
                && guardianNameKeywords.equals(otherPredicate.guardianNameKeywords);
    }

    @Override
    public int hashCode() {
        return nameKeywords.hashCode() * 31 + guardianNameKeywords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("guardianNameKeywords", guardianNameKeywords)
                .toString();
    }
}
