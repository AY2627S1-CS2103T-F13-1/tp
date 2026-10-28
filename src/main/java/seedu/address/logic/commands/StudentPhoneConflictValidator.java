package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

/** Validates the role-specific phone-number constraints for a student record. */
final class StudentPhoneConflictValidator {

    static final String MESSAGE_PHONE_USED_BY_STUDENT =
            "This phone number is already used by another student.";
    static final String MESSAGE_PHONE_USED_BY_GUARDIAN =
            "This phone number is already used as a guardian contact.";
    static final String MESSAGE_SAME_STUDENT_GUARDIAN_PHONE =
            "A student and their guardian cannot use the same phone number.";

    private StudentPhoneConflictValidator() {
    }

    /**
     * Rejects phone numbers that conflict within {@code candidate} or with another student record. A record with
     * the same ID is excluded so unchanged numbers remain valid during edits.
     */
    static void validate(Student candidate, ReadOnlyAddressBook addressBook) throws CommandException {
        requireNonNull(candidate);
        requireNonNull(addressBook);

        Phone studentPhone = candidate.getPhone().orElse(null);
        Phone guardianPhone = candidate.getGuardian().map(guardian -> guardian.getPhone()).orElse(null);

        if (studentPhone != null && studentPhone.equals(guardianPhone)) {
            throw new CommandException(MESSAGE_SAME_STUDENT_GUARDIAN_PHONE);
        }

        for (Student existing : addressBook.getStudentList()) {
            if (existing.getId().equals(candidate.getId())) {
                continue;
            }

            if (matchesStudentPhone(studentPhone, existing) || matchesStudentPhone(guardianPhone, existing)) {
                throw new CommandException(MESSAGE_PHONE_USED_BY_STUDENT);
            }
        }

        if (studentPhone == null) {
            return;
        }

        boolean isUsedByGuardian = addressBook.getStudentList().stream()
                .filter(existing -> !existing.getId().equals(candidate.getId()))
                .map(Student::getGuardian)
                .flatMap(optionalGuardian -> optionalGuardian.stream())
                .anyMatch(guardian -> studentPhone.equals(guardian.getPhone()));
        if (isUsedByGuardian) {
            throw new CommandException(MESSAGE_PHONE_USED_BY_GUARDIAN);
        }
    }

    private static boolean matchesStudentPhone(Phone phone, Student existing) {
        return phone != null && existing.getPhone().filter(phone::equals).isPresent();
    }
}
