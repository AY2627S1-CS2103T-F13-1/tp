package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import seedu.address.model.student.Student;

/**
 * Text displayed in a student contact card.
 */
record StudentCardDetails(String studentName, String studentPhone,
                          String guardianName, String guardianPhone) {

    private static final String MISSING_DETAIL_PLACEHOLDER = "-";

    /**
     * Creates display-ready details from a student.
     */
    static StudentCardDetails from(Student student) {
        requireNonNull(student);
        return new StudentCardDetails(
                student.getName().fullName,
                student.getPhone().map(phone -> phone.value).orElse(MISSING_DETAIL_PLACEHOLDER),
                student.getGuardian().map(guardian -> guardian.getName().fullName)
                        .orElse(MISSING_DETAIL_PLACEHOLDER),
                student.getGuardian().map(guardian -> guardian.getPhone().value)
                        .orElse(MISSING_DETAIL_PLACEHOLDER));
    }
}
