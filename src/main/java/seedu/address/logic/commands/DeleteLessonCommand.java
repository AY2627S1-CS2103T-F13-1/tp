package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Deletes a lesson identified using its displayed index in the lessons panel.
 */
public class DeleteLessonCommand extends Command {

    public static final String COMMAND_WORD = "deletelesson";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the lesson at the given index in the displayed lesson list.\n"
            + "Students enrolled in the lesson are kept.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_LESSON_SUCCESS = "Deleted lesson: %1$s";
    public static final String MESSAGE_UNENROLLED_STUDENTS = "Unenrolled %1$d student(s): %2$s";

    private final Index targetIndex;

    /**
     * Creates a DeleteLessonCommand to delete the lesson at the specified {@code targetIndex}.
     */
    public DeleteLessonCommand(Index targetIndex) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Lesson> lastShownList = model.getFilteredLessonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }

        Lesson lessonToDelete = lastShownList.get(targetIndex.getZeroBased());
        List<Student> unenrolledStudents = model.getAddressBook().getStudentsInLesson(lessonToDelete);
        model.deleteLesson(lessonToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_LESSON_SUCCESS, Messages.format(lessonToDelete))
                + formatUnenrolledStudents(unenrolledStudents));
    }

    /**
     * Returns a second line naming the {@code students} who were unenrolled, or an empty string if there were none.
     */
    private static String formatUnenrolledStudents(List<Student> students) {
        if (students.isEmpty()) {
            return "";
        }
        String names = students.stream()
                .map(student -> student.getName().fullName)
                .collect(Collectors.joining(", "));
        return "\n" + String.format(MESSAGE_UNENROLLED_STUDENTS, students.size(), names);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteLessonCommand otherDeleteLessonCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteLessonCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
