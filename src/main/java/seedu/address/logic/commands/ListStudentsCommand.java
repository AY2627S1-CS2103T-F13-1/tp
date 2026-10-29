package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;

/**
 * Lists all students or those enrolled in a lesson selected by its displayed index.
 */
public class ListStudentsCommand extends Command {

    public static final String COMMAND_WORD = "liststudents";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all students, or only those enrolled in the lesson at the displayed lesson index.\n"
            + "Parameters: [l/LESSON_INDEX]\n"
            + "Example: " + COMMAND_WORD + " l/1";

    public static final String MESSAGE_SUCCESS = "Listed all students.";
    public static final String MESSAGE_LESSON_SUCCESS = "Listed students enrolled in %1$s";

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
    public boolean changesStoredData() {
        return false;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (lessonIndex == null) {
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

        List<Lesson> lessons = model.getFilteredLessonList();
        if (lessonIndex.getZeroBased() >= lessons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }
        Lesson lesson = lessons.get(lessonIndex.getZeroBased());
        model.filterStudentsByLesson(lesson);
        return new CommandResult(String.format(MESSAGE_LESSON_SUCCESS, Messages.format(lesson)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof ListStudentsCommand command
                && Objects.equals(lessonIndex, command.lessonIndex));
    }

    @Override
    public int hashCode() {
        return Objects.hash(lessonIndex);
    }
}
