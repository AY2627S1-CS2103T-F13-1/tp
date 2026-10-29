package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Phone;

/**
 * Tests that a {@code Student}'s name, their guardian's name and their phone numbers match the given keywords.
 * Name matching ignores letter case, the accents of Latin letters and the style of apostrophe, and a keyword may
 * match any part of a name. A phone keyword may match
 * any part of the student's own phone number or their guardian's phone number, or be a whole number of at least
 * seven digits with a country code of up to three digits in front. Within each list, any one keyword may match.
 * When several lists are non-empty, the student must match all of them. An empty list places no condition
 * on that detail, but a predicate with no keywords at all matches no student.
 */
public class StudentContainsKeywordsPredicate implements Predicate<Student> {
    // a full number is at least this long, so that a short stored number does not match many keywords
    private static final int MIN_DIGITS_FOR_COUNTRY_CODE_MATCH = 7;
    private static final int MAX_COUNTRY_CODE_DIGITS = 3;
    // the accents that a Latin letter such as é decomposes into; marks of other scripts are part of their letters
    private static final String LATIN_ACCENT_REGEX = "[\\u0300-\\u036f]";

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
                .anyMatch(number -> matchesAnyPhoneKeyword(number, phoneKeywords));
        return nameMatches && guardianNameMatches && phoneMatches;
    }

    /** Returns the student's own phone number, if any, followed by their guardian's, if any. */
    private static Stream<String> getPhoneNumbers(Student student) {
        Stream<Phone> guardianPhone = student.getGuardian().map(Guardian::getPhone).stream();
        return Stream.concat(student.getPhone().stream(), guardianPhone)
                .map(phone -> phone.value.replace("+", ""));
    }

    private static boolean containsAnySubstring(String text, List<String> keywords) {
        String normalizedText = normalizeName(text);
        return toSearchTerms(keywords).map(StudentContainsKeywordsPredicate::normalizeName)
                .anyMatch(normalizedText::contains);
    }

    /**
     * Returns {@code name} in lower case, without the accents of Latin letters and with curly apostrophes made
     * straight, so that a keyword typed on any keyboard, or pasted from a document, finds the name as stored.
     */
    private static String normalizeName(String name) {
        String withoutAccents = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll(LATIN_ACCENT_REGEX, "");
        return withoutAccents.replace('’', '\'').toLowerCase(Locale.ROOT);
    }

    /**
     * Returns true if {@code number} contains any of {@code phoneKeywords}, or a keyword is the whole number with
     * a country code of up to three digits in front of it.
     */
    private static boolean matchesAnyPhoneKeyword(String number, List<String> phoneKeywords) {
        return toSearchTerms(phoneKeywords)
                .anyMatch(keyword -> number.contains(keyword) || hasCountryCodeBefore(keyword, number));
    }

    private static boolean hasCountryCodeBefore(String keyword, String number) {
        int countryCodeLength = keyword.length() - number.length();
        return number.length() >= MIN_DIGITS_FOR_COUNTRY_CODE_MATCH
                && countryCodeLength > 0 && countryCodeLength <= MAX_COUNTRY_CODE_DIGITS
                && keyword.endsWith(number);
    }

    /** Returns the keywords that are not blank, trimmed and in lower case. */
    private static Stream<String> toSearchTerms(List<String> keywords) {
        return keywords.stream()
                .map(String::trim)
                .filter(keyword -> !keyword.isEmpty())
                .map(keyword -> keyword.toLowerCase(Locale.ROOT));
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
