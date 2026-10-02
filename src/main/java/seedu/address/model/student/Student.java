package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Immutable student contact. A student must be reachable through their own phone or a guardian.
 */
public final class Student {
    public static final String MESSAGE_CONTACT_REQUIRED =
            "At least one phone number is required: p/ for the student, or gn/ with gp/ for the guardian.";

    private final Name name;
    private final Phone phone;
    private final Guardian guardian;

    /**
     * Creates a student. {@code phone} and {@code guardian} may be null to indicate absence,
     * but at least one must be present. {@code name} must not be null.
     */
    public Student(Name name, Phone phone, Guardian guardian) {
        this.name = requireNonNull(name);
        checkArgument(phone != null || guardian != null, MESSAGE_CONTACT_REQUIRED);
        this.phone = phone;
        this.guardian = guardian;
    }

    public Name getName() {
        return name;
    }

    public Optional<Phone> getPhone() {
        return Optional.ofNullable(phone);
    }

    public Optional<Guardian> getGuardian() {
        return Optional.ofNullable(guardian);
    }

    /**
     * Returns whether the names match ignoring case and extra spaces, and at least one phone
     * matches across either student's own or guardian's number. This is a duplicate check,
     * not value equality; siblings with different names are allowed.
     */
    public boolean isSameStudent(Student other) {
        if (other == null || !normalizedName().equals(other.normalizedName())) {
            return false;
        }
        return (phone != null && other.hasPhone(phone))
                || (guardian != null && other.hasPhone(guardian.getPhone()));
    }

    private String normalizedName() {
        return name.fullName.trim().replaceAll(" +", " ").toLowerCase(Locale.ROOT);
    }

    private boolean hasPhone(Phone number) {
        return number.equals(phone) || (guardian != null && number.equals(guardian.getPhone()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Student student
                && name.equals(student.name) && Objects.equals(phone, student.phone)
                && Objects.equals(guardian, student.guardian));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, guardian);
    }

    @Override
    public String toString() {
        return name + "; Phone: " + getPhone().map(Phone::toString).orElse("-")
                + "; Guardian: " + getGuardian().map(Guardian::toString).orElse("-");
    }
}
