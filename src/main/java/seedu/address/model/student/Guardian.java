package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/**
 * Immutable contact details of a student's guardian. Both fields are required.
 */
public final class Guardian {
    private final Name name;
    private final Phone phone;

    /** Creates a guardian with a required name and phone number. */
    public Guardian(Name name, Phone phone) {
        requireAllNonNull(name, phone);
        this.name = name;
        this.phone = phone;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Guardian guardian
                && name.equals(guardian.name) && phone.equals(guardian.phone));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone);
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
