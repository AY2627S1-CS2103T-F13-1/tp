package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;

/**
 * Adds a weekly lesson to the address book, warning of any lessons whose times it overlaps.
 */
public class AddLessonCommand extends Command {

    public static final String COMMAND_WORD = "addlesson";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a weekly lesson.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_DAY + "DAY "
            + PREFIX_START_TIME + "START_TIME "
            + PREFIX_END_TIME + "END_TIME\n"
            + "DAY is a day's name or at least its first 3 letters, e.g. mon or monday.\n"
            + "Times are in 24-hour format, e.g. 1600 or 16:00.\n"
            + "An end time earlier than the start time means the lesson ends on the next day.\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Sec 3 A-Math "
            + PREFIX_DAY + "mon "
            + PREFIX_START_TIME + "1600 "
            + PREFIX_END_TIME + "1800";

    public static final String MESSAGE_SUCCESS = "New lesson added: %1$s";

    private static final Logger logger = LogsCenter.getLogger(AddLessonCommand.class);

    private final Lesson toAdd;

    /**
     * Creates an {@code AddLessonCommand} to add the specified {@code Lesson}.
     */
    public AddLessonCommand(Lesson lesson) {
        toAdd = requireNonNull(lesson);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Optional<Lesson> existingLesson = model.getAddressBook().findDuplicateOf(toAdd);
        if (existingLesson.isPresent()) {
            logger.fine("Rejected duplicate lesson: " + toAdd);
            throw new CommandException(Messages.formatDuplicateLesson(existingLesson.get(),
                    model.getFilteredLessonList().contains(existingLesson.get())));
        }

        List<Lesson> clashingLessons = model.getAddressBook().getClashingLessons(toAdd);
        model.addLesson(toAdd);
        assert model.getFilteredLessonList().contains(toAdd) : "Adding a lesson should clear the lesson filter";
        logger.info("Added lesson: " + toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd))
                + Messages.formatLongLessonWarning(toAdd) + Messages.formatClashWarning(clashingLessons));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddLessonCommand otherAddLessonCommand)) {
            return false;
        }

        // Lesson#equals includes the generated ID, which differs every time the same input is parsed.
        Lesson otherLesson = otherAddLessonCommand.toAdd;
        return toAdd.getName().equals(otherLesson.getName())
                && toAdd.getDay() == otherLesson.getDay()
                && toAdd.getStartTime().equals(otherLesson.getStartTime())
                && toAdd.getEndTime().equals(otherLesson.getEndTime());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
