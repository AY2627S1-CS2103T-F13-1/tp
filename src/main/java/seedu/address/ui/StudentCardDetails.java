package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Text displayed in a student contact card.
 */
record StudentCardDetails(String studentName, String studentPhone,
                          String guardianName, String guardianPhone, String lessons) {

    private static final String MISSING_DETAIL_PLACEHOLDER = "-";

    /**
     * Creates display-ready details from a student.
     */
    static StudentCardDetails from(Student student) {
        return from(student, List.of());
    }

    /**
     * Creates display-ready details from a student and the lessons they attend.
     */
    static StudentCardDetails from(Student student, List<Lesson> enrolledLessons) {
        requireNonNull(student);
        requireNonNull(enrolledLessons);
        String lessonMemberships = enrolledLessons.stream()
                .map(LessonUiFormatter::formatMembership)
                .collect(Collectors.joining("\n"));

        return new StudentCardDetails(
                student.getName().fullName,
                student.getPhone().map(phone -> phone.value).orElse(MISSING_DETAIL_PLACEHOLDER),
                student.getGuardian().map(guardian -> guardian.getName().fullName)
                        .orElse(MISSING_DETAIL_PLACEHOLDER),
                student.getGuardian().map(guardian -> guardian.getPhone().value)
                        .orElse(MISSING_DETAIL_PLACEHOLDER),
                lessonMemberships.isEmpty() ? MISSING_DETAIL_PLACEHOLDER : lessonMemberships);
    }
}
