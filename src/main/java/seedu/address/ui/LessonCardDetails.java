package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Display-ready details for a lesson card.
 */
record LessonCardDetails(String lessonName, String day, String time, String students) {

    private static final String MISSING_DETAIL_PLACEHOLDER = "-";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Creates display-ready details from a lesson and its enrolled students.
     */
    static LessonCardDetails from(Lesson lesson, List<Student> enrolledStudents) {
        requireNonNull(lesson);
        requireNonNull(enrolledStudents);

        String studentNames = enrolledStudents.stream()
                .map(student -> student.getName().fullName)
                .collect(Collectors.joining("\n"));
        studentNames = studentNames.isEmpty() ? MISSING_DETAIL_PLACEHOLDER : studentNames;
        String nextDaySuffix = lesson.getEndTime().isBefore(lesson.getStartTime()) ? " (next day)" : "";

        return new LessonCardDetails(
                lesson.getName(),
                lesson.getDay().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                lesson.getStartTime().format(TIME_FORMAT) + " – " + lesson.getEndTime().format(TIME_FORMAT)
                        + nextDaySuffix,
                studentNames);
    }
}
