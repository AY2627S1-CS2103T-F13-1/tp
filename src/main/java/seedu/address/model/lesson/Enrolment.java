package seedu.address.model.lesson;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.UUID;

/** A student–lesson link using permanent IDs, never displayed indexes. */
public record Enrolment(UUID studentId, UUID lessonId) {
    public Enrolment {
        requireAllNonNull(studentId, lessonId);
    }
}
