package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/** Jackson-friendly student data. Missing optional fields are stored as null. */
class JsonAdaptedStudent {
    private final String name;
    private final String phone;
    private final String guardianName;
    private final String guardianPhone;

    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("guardianName") String guardianName, @JsonProperty("guardianPhone") String guardianPhone) {
        this.name = name;
        this.phone = phone;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
    }

    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        phone = source.getPhone().map(value -> value.value).orElse(null);
        guardianName = source.getGuardian().map(value -> value.getName().fullName).orElse(null);
        guardianPhone = source.getGuardian().map(value -> value.getPhone().value).orElse(null);
    }

    /** Converts stored data to a validated student. */
    public Student toModelType() throws IllegalValueException {
        if (name == null || !Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if ((guardianName == null) != (guardianPhone == null)) {
            throw new IllegalValueException("Guardian name and phone must be given together.");
        }
        if (phone == null && guardianPhone == null) {
            throw new IllegalValueException(Student.MESSAGE_CONTACT_REQUIRED);
        }
        if ((phone != null && !Phone.isValidPhone(phone))
                || (guardianPhone != null && !Phone.isValidPhone(guardianPhone))) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        if (guardianName != null && !Name.isValidName(guardianName)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        Guardian guardian = guardianName == null ? null
                : new Guardian(new Name(guardianName), new Phone(guardianPhone));
        return new Student(new Name(name), phone == null ? null : new Phone(phone), guardian);
    }
}
