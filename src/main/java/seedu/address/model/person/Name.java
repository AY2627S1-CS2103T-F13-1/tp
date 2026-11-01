package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should use letters from any language, digits, spaces and common name punctuation. "
                    + "Apostrophes and hyphens must appear between letters or digits; periods may appear after "
                    + "letters or digits; commas may end name parts; parentheses must enclose name parts; and @, "
                    + "s/o, d/o, a/l and a/p may appear between name parts. Names should not be blank.";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        requireNonNull(test);
        if (test.isEmpty() || test.charAt(0) == ' ') {
            return false;
        }

        int nameEnd = test.length();
        while (test.charAt(nameEnd - 1) == ' ') {
            nameEnd--;
        }

        int partStart = 0;
        boolean previousPartWasConnector = false;
        boolean isInsideParentheses = false;
        while (partStart < nameEnd) {
            int nextSpace = test.indexOf(' ', partStart);
            int partEnd = nextSpace == -1 ? nameEnd : nextSpace;
            String part = test.substring(partStart, partEnd);
            boolean isConnector = isNameConnector(part);
            if (isConnector && (partStart == 0 || previousPartWasConnector || isInsideParentheses)) {
                return false;
            }
            if (!isConnector) {
                NamePartValidation validation = validateNamePart(part, isInsideParentheses);
                if (!validation.isValid()) {
                    return false;
                }
                isInsideParentheses = validation.isInsideParentheses();
            }
            previousPartWasConnector = isConnector;

            partStart = partEnd;
            while (partStart < nameEnd && test.charAt(partStart) == ' ') {
                partStart++;
            }
        }
        return !previousPartWasConnector && !isInsideParentheses;
    }

    /**
     * Returns true if {@code test} is a name part or a supported connector that can be used as a search keyword.
     */
    public static boolean isValidSearchKeyword(String test) {
        requireNonNull(test);
        return isNameConnector(test)
                || validateNamePart(test, false).isValid()
                || validateNamePart(test, true).isValid();
    }

    private static NamePartValidation validateNamePart(String part, boolean isInsideParentheses) {
        String content = part;
        if (content.startsWith("(")) {
            if (isInsideParentheses) {
                return NamePartValidation.invalid();
            }
            isInsideParentheses = true;
            content = content.substring(1);
        }
        if (content.contains("(")) {
            return NamePartValidation.invalid();
        }

        if (content.endsWith(")")) {
            if (!isInsideParentheses) {
                return NamePartValidation.invalid();
            }
            isInsideParentheses = false;
            content = content.substring(0, content.length() - 1);
        }
        if (content.contains(")") || !isValidNamePartContent(content)) {
            return NamePartValidation.invalid();
        }
        return new NamePartValidation(true, isInsideParentheses);
    }

    private static boolean isValidNamePartContent(String part) {
        boolean canEndPart = false;
        boolean canTakeCombiningMark = false;
        boolean canTakePunctuation = false;
        for (int offset = 0; offset < part.length();) {
            int codePoint = part.codePointAt(offset);
            int codePointLength = Character.charCount(codePoint);
            if (Character.isLetter(codePoint)) {
                canEndPart = true;
                canTakeCombiningMark = true;
                canTakePunctuation = true;
            } else if (codePoint >= '0' && codePoint <= '9') {
                canEndPart = true;
                canTakeCombiningMark = false;
                canTakePunctuation = true;
            } else if (isCombiningMark(codePoint)) {
                if (!canTakeCombiningMark) {
                    return false;
                }
            } else if (codePoint == '\'' || codePoint == '’' || codePoint == '-') {
                if (!canTakePunctuation) {
                    return false;
                }
                canEndPart = false;
                canTakeCombiningMark = false;
                canTakePunctuation = false;
            } else if (codePoint == '.') {
                if (!canTakePunctuation) {
                    return false;
                }
                canEndPart = true;
                canTakeCombiningMark = false;
                canTakePunctuation = false;
            } else if (codePoint == ',') {
                if (!canTakePunctuation || offset + codePointLength != part.length()) {
                    return false;
                }
                canEndPart = true;
                canTakeCombiningMark = false;
                canTakePunctuation = false;
            } else {
                return false;
            }
            offset += codePointLength;
        }
        return canEndPart;
    }

    private static boolean isCombiningMark(int codePoint) {
        int type = Character.getType(codePoint);
        return type == Character.NON_SPACING_MARK
                || type == Character.COMBINING_SPACING_MARK
                || type == Character.ENCLOSING_MARK;
    }

    private static boolean isNameConnector(String part) {
        return switch (part.toLowerCase(Locale.ROOT)) {
            case "@", "s/o", "d/o", "a/l", "a/p" -> true;
            default -> false;
        };
    }

    /**
     * Represents the result of validating one whitespace-delimited name part.
     */
    private record NamePartValidation(boolean isValid, boolean isInsideParentheses) {
        private static NamePartValidation invalid() {
            return new NamePartValidation(false, false);
        }
    }

    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
