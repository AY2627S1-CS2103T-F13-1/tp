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
            "Singapore student and guardian phone numbers should contain 8 digits and may start with +65. "
                    + "International phone numbers should start with + and contain 3 to 15 digits. "
                    + "Spaces or hyphens may separate digit groups";
    public static final String VALIDATION_REGEX = "\\+?[0-9]+(?:[ -][0-9]+)*";
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
        return test.matches(VALIDATION_REGEX)
                && removeFormatting(test).length() >= MINIMUM_PHONE_DIGIT_COUNT;
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
        return phone.replace("+", "").replace(" ", "").replace("-", "");
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
