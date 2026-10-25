package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_LESSONS;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

import seedu.address.model.Model;

/**
 * Lists all lessons or those that start on a specified day.
 */
public class ListLessonsCommand extends Command {

    public static final String COMMAND_WORD = "listlessons";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists lessons, optionally filtered by their starting day.\n"
            + "Parameters: [d/DAY]\n"
            + "DAY is a day's name or at least its first 3 letters, e.g. mon or monday.\n"
            + "Example: " + COMMAND_WORD + " d/mon";

    public static final String MESSAGE_SUCCESS = "Listed all lessons.";
    public static final String MESSAGE_SUCCESS_BY_DAY = "Listed lessons on %1$s.";

    private final DayOfWeek day;

    /** Creates a command that shows every lesson. */
    public ListLessonsCommand() {
        day = null;
    }

    /** Creates a command that shows lessons starting on {@code day}. */
    public ListLessonsCommand(DayOfWeek day) {
        this.day = requireNonNull(day);
    }

    @Override
    public boolean changesStoredData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (day == null) {
            model.updateFilteredLessonList(PREDICATE_SHOW_ALL_LESSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

        model.updateFilteredLessonList(lesson -> lesson.getDay() == day);
        String displayDay = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return new CommandResult(String.format(MESSAGE_SUCCESS_BY_DAY, displayDay));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListLessonsCommand command
                && Objects.equals(day, command.day));
    }

    @Override
    public int hashCode() {
        return Objects.hash(day);
    }
}
