package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/** Links a student and lesson selected from their displayed lists. */
public class EnrolCommand extends Command {
    public static final String COMMAND_WORD = "enrol";
    public static final String MESSAGE_USAGE = "enrol s/STUDENT_INDEX l/LESSON_INDEX\n"
            + "Both indices must be positive integers.\nExample: enrol s/1 l/2";
    public static final String MESSAGE_SUCCESS = "Enrolled %s in %s";
    public static final String MESSAGE_DUPLICATE = "%s is already enrolled in %s";

    private final Index studentIndex;
    private final Index lessonIndex;

    /** Creates a command using displayed indices. */
    public EnrolCommand(Index studentIndex, Index lessonIndex) {
        requireAllNonNull(studentIndex, lessonIndex);
        this.studentIndex = studentIndex;
        this.lessonIndex = lessonIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        if (studentIndex.getZeroBased() >= model.getFilteredStudentList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        if (lessonIndex.getZeroBased() >= model.getFilteredLessonList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }
        Student student = model.getFilteredStudentList().get(studentIndex.getZeroBased());
        Lesson lesson = model.getFilteredLessonList().get(lessonIndex.getZeroBased());
        if (model.getAddressBook().isEnrolled(student, lesson)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE, student.getName(), lesson));
        }
        model.enrolStudent(student, lesson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), lesson));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof EnrolCommand command
                && studentIndex.equals(command.studentIndex) && lessonIndex.equals(command.lessonIndex));
    }
}
