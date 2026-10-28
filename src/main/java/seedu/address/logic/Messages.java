package seedu.address.logic;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/**
 * Container for user visible messages.
 */
public class Messages {
    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX =
            "Student index exceeds the number of students in the displayed list.";
    public static final String MESSAGE_INVALID_LESSON_DISPLAYED_INDEX =
            "Lesson index exceeds the number of lessons in the displayed list.";
    public static final String MESSAGE_CLASH_WARNING = "Warning: clashes with %1$s";
    public static final String MESSAGE_STUDENT_CLASH_WARNING = "Warning: %1$s already attends clashing lesson(s): %2$s";
    public static final String MESSAGE_LONG_LESSON_WARNING = "Warning: this lesson lasts %1$s and ends the next day."
            + " Check that the start and end times are not swapped.";
    public static final int LONG_OVERNIGHT_LESSON_MINUTES = 12 * 60;
    public static final String MESSAGE_DUPLICATE_LESSON = "This lesson already exists: %1$s";
    public static final String MESSAGE_LESSON_HIDDEN =
            "It is hidden by the lesson filter; run listlessons to show every lesson.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d person(s) listed!";
    public static final String MESSAGE_STUDENTS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_UNKNOWN_PREFIX = "%1$s takes only %2$s, not %3$s.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns an error message saying that {@code commandWord} does not take {@code unknownPrefix}, and listing
     * the {@code knownPrefixes} it takes, e.g. {@code findstudent takes only n/, gn/ and p/, not gp/.}
     */
    public static String getErrorMessageForUnknownPrefix(String commandWord, String unknownPrefix,
            Prefix... knownPrefixes) {
        assert knownPrefixes.length > 0;

        List<String> prefixes = Stream.of(knownPrefixes).map(Prefix::toString).toList();
        String knownPrefixList = prefixes.size() == 1
                ? prefixes.getFirst()
                : String.join(", ", prefixes.subList(0, prefixes.size() - 1)) + " and " + prefixes.getLast();
        return String.format(MESSAGE_UNKNOWN_PREFIX, commandWord, knownPrefixList, unknownPrefix);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code student} for display to the user. A missing phone or guardian is shown as {@code -}.
     */
    public static String format(Student student) {
        return student.getName()
                + "; Phone: " + student.getPhone().map(Phone::toString).orElse("-")
                + "; Guardian: " + student.getGuardian().map(Guardian::toString).orElse("-");
    }

    /**
     * Formats the {@code lesson} for display to the user, e.g. {@code Sec 3 A-Math (Mon 1600-1800)}.
     * A lesson that ends the next day is marked {@code (next day)}, as on lesson cards.
     */
    public static String format(Lesson lesson) {
        return lesson.endsNextDay() ? lesson + " (next day)" : lesson.toString();
    }

    /**
     * Returns a warning on a new line naming {@code clashingLessons}, or an empty string if there are none.
     * Clashes are allowed, as the tutor may teach overlapping lessons on purpose.
     */
    public static String formatClashWarning(List<Lesson> clashingLessons) {
        if (clashingLessons.isEmpty()) {
            return "";
        }
        return "\n" + String.format(MESSAGE_CLASH_WARNING, formatLessons(clashingLessons));
    }

    /**
     * Returns a warning on a new line naming the lessons of {@code student} that clash with a lesson they are
     * being enrolled in, or an empty string if there are none.
     */
    public static String formatStudentClashWarning(Student student, List<Lesson> clashingLessons) {
        if (clashingLessons.isEmpty()) {
            return "";
        }
        return "\n" + String.format(MESSAGE_STUDENT_CLASH_WARNING, student.getName(), formatLessons(clashingLessons));
    }

    /**
     * Returns a warning on a new line if {@code lesson} runs past midnight for more than 12 hours, which usually
     * means its start and end times were swapped, or an empty string otherwise.
     */
    public static String formatLongLessonWarning(Lesson lesson) {
        int minutes = lesson.getDurationInMinutes();
        if (!lesson.endsNextDay() || minutes <= LONG_OVERNIGHT_LESSON_MINUTES) {
            return "";
        }
        int hours = minutes / 60;
        int extraMinutes = minutes % 60;
        String duration = extraMinutes == 0 ? hours + " h" : hours + " h " + extraMinutes + " min";
        return "\n" + String.format(MESSAGE_LONG_LESSON_WARNING, duration);
    }

    /**
     * Returns the error for a lesson that duplicates {@code existingLesson}, which names it and, if the lesson
     * list does not show it, says how to show it.
     */
    public static String formatDuplicateLesson(Lesson existingLesson, boolean isShown) {
        String message = String.format(MESSAGE_DUPLICATE_LESSON, format(existingLesson));
        return isShown ? message : message + "\n" + MESSAGE_LESSON_HIDDEN;
    }

    private static String formatLessons(List<Lesson> lessons) {
        return lessons.stream().map(Messages::format).collect(Collectors.joining("; "));
    }

}
