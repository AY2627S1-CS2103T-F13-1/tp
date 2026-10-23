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
    public static final String MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX = "The student index provided is invalid.";
    public static final String MESSAGE_INVALID_LESSON_DISPLAYED_INDEX = "The lesson index provided is invalid.";
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

}
