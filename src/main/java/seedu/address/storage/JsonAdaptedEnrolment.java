package seedu.address.storage;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Enrolment;

/** JSON enrolment referencing permanent record IDs. */
class JsonAdaptedEnrolment {
    private final String studentId;
    private final String lessonId;

    @JsonCreator
    public JsonAdaptedEnrolment(@JsonProperty("studentId") String studentId,
            @JsonProperty("lessonId") String lessonId) {
        this.studentId = studentId;
        this.lessonId = lessonId;
    }

    public JsonAdaptedEnrolment(Enrolment link) {
        studentId = link.studentId().toString();
        lessonId = link.lessonId().toString();
    }

    /** Restores IDs; the address book validates their references. */
    public Enrolment toModelType() throws IllegalValueException {
        try {
            return new Enrolment(UUID.fromString(studentId), UUID.fromString(lessonId));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalValueException("Invalid enrolment ID.");
        }
    }
}
