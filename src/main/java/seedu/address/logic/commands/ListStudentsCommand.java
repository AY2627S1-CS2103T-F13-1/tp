package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Lists all students or those enrolled in a lesson selected by its displayed index.
 */
public class ListStudentsCommand extends Command {

    public static final String COMMAND_WORD = "liststudents";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists students, optionally filtered by displayed lesson index.\n"
            + "Parameters: [l/LESSON_INDEX]\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all students.";

    private final Index lessonIndex;

    /** Creates a command to show all students. */
    public ListStudentsCommand() {
        lessonIndex = null;
    }

    /** Creates a command to show students enrolled in the displayed lesson. */
    public ListStudentsCommand(Index lessonIndex) {
        this.lessonIndex = requireNonNull(lessonIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (lessonIndex == null) {
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        } else {
            if (lessonIndex.getZeroBased() >= model.getFilteredLessonList().size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
            }
            model.filterStudentsByLesson(model.getFilteredLessonList().get(lessonIndex.getZeroBased()));
        }
        return new CommandResult(lessonIndex == null ? MESSAGE_SUCCESS : "Listed students enrolled in "
                + model.getFilteredLessonList().get(lessonIndex.getZeroBased()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListStudentsCommand command
                && java.util.Objects.equals(lessonIndex, command.lessonIndex));
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(lessonIndex);
    }
}
