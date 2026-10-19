package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Student}'s name and their guardian's name match the given keywords.
 * Matching is case-insensitive, and a keyword may match any part of a name. Within each list,
 * any one keyword may match.
 * When both lists are non-empty, the student must match both. An empty list places no condition
 * on that name, but a predicate with no keywords at all matches no student.
 */
public class StudentContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> nameKeywords;
    private final List<String> guardianNameKeywords;

    /**
     * Creates a predicate from keywords for the student's name and for the guardian's name.
     * Neither list, nor any keyword in them, may be null.
     */
    public StudentContainsKeywordsPredicate(List<String> nameKeywords, List<String> guardianNameKeywords) {
        requireAllNonNull(nameKeywords, guardianNameKeywords);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.guardianNameKeywords = List.copyOf(guardianNameKeywords);
    }

    @Override
    public boolean test(Student student) {
        if (nameKeywords.isEmpty() && guardianNameKeywords.isEmpty()) {
            return false;
        }
        boolean nameMatches = nameKeywords.isEmpty() || containsAnySubstring(student.getName().fullName, nameKeywords);
        boolean guardianNameMatches = guardianNameKeywords.isEmpty() || student.getGuardian()
                .map(guardian -> containsAnySubstring(guardian.getName().fullName, guardianNameKeywords))
                .orElse(false);
        return nameMatches && guardianNameMatches;
    }

    private static boolean containsAnySubstring(String name, List<String> keywords) {
        String normalizedName = name.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .map(keyword -> keyword.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedName::contains);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudentContainsKeywordsPredicate otherPredicate)) {
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
