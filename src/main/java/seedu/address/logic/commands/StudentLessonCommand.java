package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/**
 * Represents a command that acts on a student and a lesson selected from their displayed lists.
 */
public abstract class StudentLessonCommand extends Command {

    private static final Logger logger = LogsCenter.getLogger(StudentLessonCommand.class);

    private final Index studentIndex;
    private final Index lessonIndex;

    /**
     * Creates a command for the student and lesson at the given displayed indices.
     */
    protected StudentLessonCommand(Index studentIndex, Index lessonIndex) {
        requireAllNonNull(studentIndex, lessonIndex);
        this.studentIndex = studentIndex;
        this.lessonIndex = lessonIndex;
    }

    /**
     * Returns the student at {@code studentIndex} in the displayed student list.
     *
     * @throws CommandException if the index is out of range.
     */
    protected Student getTargetStudent(Model model) throws CommandException {
        List<Student> students = model.getFilteredStudentList();
        if (studentIndex.getZeroBased() >= students.size()) {
            logger.fine(getClass().getSimpleName() + " rejected: student index "
                    + studentIndex.getOneBased() + " is out of range");
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        return students.get(studentIndex.getZeroBased());
    }

    /**
     * Returns the lesson at {@code lessonIndex} in the displayed lesson list.
     *
     * @throws CommandException if the index is out of range.
     */
    protected Lesson getTargetLesson(Model model) throws CommandException {
        List<Lesson> lessons = model.getFilteredLessonList();
        if (lessonIndex.getZeroBased() >= lessons.size()) {
            logger.fine(getClass().getSimpleName() + " rejected: lesson index "
                    + lessonIndex.getOneBased() + " is out of range");
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }
        return lessons.get(lessonIndex.getZeroBased());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // Commands of different types never match, even with the same indices
        if (other == null || other.getClass() != getClass()) {
            return false;
        }

        StudentLessonCommand otherCommand = (StudentLessonCommand) other;
        return studentIndex.equals(otherCommand.studentIndex) && lessonIndex.equals(otherCommand.lessonIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), studentIndex, lessonIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentIndex", studentIndex)
                .add("lessonIndex", lessonIndex)
                .toString();
    }
}
