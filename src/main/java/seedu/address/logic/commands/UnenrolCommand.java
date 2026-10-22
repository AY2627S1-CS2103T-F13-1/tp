package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/** Removes the link between a student and lesson selected from their displayed lists. */
public class UnenrolCommand extends Command {
    public static final String COMMAND_WORD = "unenrol";
    public static final String MESSAGE_USAGE = "unenrol s/STUDENT_INDEX l/LESSON_INDEX\n"
            + "Both indices must be positive integers.\nExample: unenrol s/1 l/2";
    public static final String MESSAGE_SUCCESS = "Unenrolled %s from %s";
    public static final String MESSAGE_NOT_ENROLLED = "%s is not enrolled in %s";

    private static final Logger logger = LogsCenter.getLogger(UnenrolCommand.class);

    private final Index studentIndex;
    private final Index lessonIndex;

    /** Creates a command using displayed indices. */
    public UnenrolCommand(Index studentIndex, Index lessonIndex) {
        requireAllNonNull(studentIndex, lessonIndex);
        this.studentIndex = studentIndex;
        this.lessonIndex = lessonIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        if (studentIndex.getZeroBased() >= model.getFilteredStudentList().size()) {
            logger.fine("Rejected unenrol: student index " + studentIndex.getOneBased() + " is out of range");
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }
        if (lessonIndex.getZeroBased() >= model.getFilteredLessonList().size()) {
            logger.fine("Rejected unenrol: lesson index " + lessonIndex.getOneBased() + " is out of range");
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }
        Student student = model.getFilteredStudentList().get(studentIndex.getZeroBased());
        Lesson lesson = model.getFilteredLessonList().get(lessonIndex.getZeroBased());
        if (!model.getAddressBook().isEnrolled(student, lesson)) {
            logger.fine("Rejected unenrol: " + student.getName() + " is not enrolled in " + lesson);
            throw new CommandException(String.format(MESSAGE_NOT_ENROLLED, student.getName(), lesson));
        }
        model.unenrolStudent(student, lesson);
        assert !model.getAddressBook().isEnrolled(student, lesson) : "Unenrolled student should not be enrolled";
        logger.info("Unenrolled " + student.getName() + " from " + lesson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), lesson));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof UnenrolCommand command
                && studentIndex.equals(command.studentIndex) && lessonIndex.equals(command.lessonIndex));
    }
}
