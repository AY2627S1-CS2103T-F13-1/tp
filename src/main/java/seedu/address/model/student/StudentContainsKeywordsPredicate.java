package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Phone;

/**
 * Tests that a {@code Student}'s name, their guardian's name and their phone numbers match the given keywords.
 * Name matching is case-insensitive, and a keyword may match any part of a name. A phone keyword may match
 * any part of the student's own phone number or their guardian's phone number. Within each list,
 * any one keyword may match.
 * When several lists are non-empty, the student must match all of them. An empty list places no condition
 * on that detail, but a predicate with no keywords at all matches no student.
 */
public class StudentContainsKeywordsPredicate implements Predicate<Student> {
    private final List<String> nameKeywords;
    private final List<String> guardianNameKeywords;
    private final List<String> phoneKeywords;

    /**
     * Creates a predicate from keywords for the student's name and for the guardian's name, with no phone keywords.
     * Neither list, nor any keyword in them, may be null.
     */
    public StudentContainsKeywordsPredicate(List<String> nameKeywords, List<String> guardianNameKeywords) {
        this(nameKeywords, guardianNameKeywords, List.of());
    }

    /**
     * Creates a predicate from keywords for the student's name, the guardian's name and the phone numbers.
     * No list, nor any keyword in them, may be null.
     */
    public StudentContainsKeywordsPredicate(List<String> nameKeywords, List<String> guardianNameKeywords,
            List<String> phoneKeywords) {
        requireAllNonNull(nameKeywords, guardianNameKeywords, phoneKeywords);
        this.nameKeywords = List.copyOf(nameKeywords);
        this.guardianNameKeywords = List.copyOf(guardianNameKeywords);
        this.phoneKeywords = List.copyOf(phoneKeywords);
    }

    @Override
    public boolean test(Student student) {
        if (nameKeywords.isEmpty() && guardianNameKeywords.isEmpty() && phoneKeywords.isEmpty()) {
            return false;
        }
        boolean nameMatches = nameKeywords.isEmpty() || containsAnySubstring(student.getName().fullName, nameKeywords);
        boolean guardianNameMatches = guardianNameKeywords.isEmpty() || student.getGuardian()
                .map(guardian -> containsAnySubstring(guardian.getName().fullName, guardianNameKeywords))
                .orElse(false);
        boolean phoneMatches = phoneKeywords.isEmpty() || getPhoneNumbers(student)
                .anyMatch(number -> containsAnySubstring(number, phoneKeywords));
        return nameMatches && guardianNameMatches && phoneMatches;
    }

    /** Returns the student's own phone number, if any, followed by their guardian's, if any. */
    private static Stream<String> getPhoneNumbers(Student student) {
        Stream<Phone> guardianPhone = student.getGuardian().map(Guardian::getPhone).stream();
        return Stream.concat(student.getPhone().stream(), guardianPhone).map(phone -> phone.value);
    }

    private static boolean containsAnySubstring(String text, List<String> keywords) {
        String normalizedText = text.toLowerCase(Locale.ROOT);
        return keywords.stream()
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .map(keyword -> keyword.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedText::contains);
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
                && guardianNameKeywords.equals(otherPredicate.guardianNameKeywords)
                && phoneKeywords.equals(otherPredicate.phoneKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKeywords, guardianNameKeywords, phoneKeywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("nameKeywords", nameKeywords)
                .add("guardianNameKeywords", guardianNameKeywords)
                .add("phoneKeywords", phoneKeywords)
                .toString();
    }
}
