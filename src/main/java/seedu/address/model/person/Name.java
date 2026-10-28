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
            "Names should use letters from any language, digits, spaces, apostrophes or hyphens. "
                    + "Apostrophes and hyphens must appear between letters or digits, and s/o, d/o, a/l and a/p "
                    + "may appear between name parts. Names should not be blank.";

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
        while (partStart < nameEnd) {
            int nextSpace = test.indexOf(' ', partStart);
            int partEnd = nextSpace == -1 ? nameEnd : nextSpace;
            String part = test.substring(partStart, partEnd);
            boolean isConnector = isNameConnector(part);
            if (isConnector && (partStart == 0 || previousPartWasConnector)) {
                return false;
            }
            if (!isConnector && !isValidNamePart(part)) {
                return false;
            }
            previousPartWasConnector = isConnector;

            partStart = partEnd;
            while (partStart < nameEnd && test.charAt(partStart) == ' ') {
                partStart++;
            }
        }
        return !previousPartWasConnector;
    }

    /**
     * Returns true if {@code test} is a name part or a supported connector that can be used as a search keyword.
     */
    public static boolean isValidSearchKeyword(String test) {
        requireNonNull(test);
        return isValidNamePart(test) || isNameConnector(test);
    }

    private static boolean isValidNamePart(String part) {
        boolean canEndPart = false;
        boolean canTakeCombiningMark = false;
        for (int offset = 0; offset < part.length();) {
            int codePoint = part.codePointAt(offset);
            if (Character.isLetter(codePoint)) {
                canEndPart = true;
                canTakeCombiningMark = true;
            } else if (codePoint >= '0' && codePoint <= '9') {
                canEndPart = true;
                canTakeCombiningMark = false;
            } else if (isCombiningMark(codePoint)) {
                if (!canTakeCombiningMark) {
                    return false;
                }
            } else if (codePoint == '\'' || codePoint == '’' || codePoint == '-') {
                if (!canEndPart) {
                    return false;
                }
                canEndPart = false;
                canTakeCombiningMark = false;
            } else {
                return false;
            }
            offset += Character.charCount(codePoint);
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
            case "s/o", "d/o", "a/l", "a/p" -> true;
            default -> false;
        };
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
