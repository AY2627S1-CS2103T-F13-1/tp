package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/** Removes the link between a student and lesson selected from their displayed lists. */
public class UnenrolCommand extends StudentLessonCommand {
    public static final String COMMAND_WORD = "unenrol";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the student at the displayed student index from the lesson at the displayed lesson index.\n"
            + "Parameters: s/STUDENT_INDEX l/LESSON_INDEX (both must be positive integers)\n"
            + "Example: " + COMMAND_WORD + " s/1 l/2";
    public static final String MESSAGE_SUCCESS = "Unenrolled %s from %s";
    public static final String MESSAGE_NOT_ENROLLED = "%s is not enrolled in %s";

    private static final Logger logger = LogsCenter.getLogger(UnenrolCommand.class);

    /** Creates a command using displayed indices. */
    public UnenrolCommand(Index studentIndex, Index lessonIndex) {
        super(studentIndex, lessonIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = getTargetStudent(model);
        Lesson lesson = getTargetLesson(model);
        if (!model.getAddressBook().isEnrolled(student, lesson)) {
            logger.fine("Rejected unenrol: " + student.getName() + " is not enrolled in " + lesson);
            throw new CommandException(String.format(MESSAGE_NOT_ENROLLED, student.getName(), Messages.format(lesson)));
        }
        model.unenrolStudent(student, lesson);
        assert !model.getAddressBook().isEnrolled(student, lesson) : "Unenrolled student should not be enrolled";
        logger.info("Unenrolled " + student.getName() + " from " + lesson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), Messages.format(lesson)));
    }
}
