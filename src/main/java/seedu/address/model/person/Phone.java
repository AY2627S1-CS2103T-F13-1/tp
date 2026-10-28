package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {

    public static final String MESSAGE_CONSTRAINTS =
            "Phone numbers should contain at least 3 digits, may use spaces or hyphens between digit groups, "
                    + "and may start with +";
    public static final String MESSAGE_STUDENT_PHONE_CONSTRAINTS =
            "Phone must be 8 local digits, +65 with 8 digits, or + with 3-15 digits.";
    private static final String SINGAPORE_COUNTRY_CODE = "65";
    private static final int SINGAPORE_PHONE_DIGIT_COUNT = 8;
    private static final int MINIMUM_PHONE_DIGIT_COUNT = 3;
    private static final int MAXIMUM_INTERNATIONAL_DIGIT_COUNT = 15;

    /** The normalized digits of a local or legacy number, or an international number starting with {@code +}. */
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = normalizePhone(phone);
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        requireNonNull(test);
        int currentIndex = test.startsWith("+") ? 1 : 0;
        int digitCount = 0;
        boolean previousCharacterIsDigit = false;

        while (currentIndex < test.length()) {
            char currentCharacter = test.charAt(currentIndex);
            if (isAsciiDigit(currentCharacter)) {
                digitCount++;
                previousCharacterIsDigit = true;
            } else if ((currentCharacter == ' ' || currentCharacter == '-') && previousCharacterIsDigit) {
                previousCharacterIsDigit = false;
            } else {
                return false;
            }
            currentIndex++;
        }

        return previousCharacterIsDigit && digitCount >= MINIMUM_PHONE_DIGIT_COUNT;
    }

    /**
     * Returns true if a given string is a valid student or guardian phone number.
     */
    public static boolean isValidStudentPhone(String test) {
        if (!isValidPhone(test)) {
            return false;
        }
        String digits = removeFormatting(test);
        if (!test.startsWith("+")) {
            return digits.length() == SINGAPORE_PHONE_DIGIT_COUNT;
        }
        if (digits.startsWith(SINGAPORE_COUNTRY_CODE)) {
            return digits.length() == SINGAPORE_COUNTRY_CODE.length() + SINGAPORE_PHONE_DIGIT_COUNT;
        }
        return digits.length() <= MAXIMUM_INTERNATIONAL_DIGIT_COUNT;
    }

    private static String normalizePhone(String phone) {
        String digits = removeFormatting(phone);
        if (!phone.startsWith("+")) {
            return digits;
        }
        if (digits.startsWith(SINGAPORE_COUNTRY_CODE)
                && digits.length() == SINGAPORE_COUNTRY_CODE.length() + SINGAPORE_PHONE_DIGIT_COUNT) {
            return digits.substring(SINGAPORE_COUNTRY_CODE.length());
        }
        return "+" + digits;
    }

    private static String removeFormatting(String phone) {
        StringBuilder digits = new StringBuilder(phone.length());
        for (int i = 0; i < phone.length(); i++) {
            char currentCharacter = phone.charAt(i);
            if (isAsciiDigit(currentCharacter)) {
                digits.append(currentCharacter);
            }
        }
        return digits.toString();
    }

    private static boolean isAsciiDigit(char character) {
        return character >= '0' && character <= '9';
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
