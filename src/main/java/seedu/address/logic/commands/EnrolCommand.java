package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.student.Student;

/** Links a student and lesson selected from their displayed lists. */
public class EnrolCommand extends StudentLessonCommand {
    public static final String COMMAND_WORD = "enrol";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Enrols the student at the displayed student index in the lesson at the displayed lesson index.\n"
            + "Parameters: s/STUDENT_INDEX l/LESSON_INDEX (both must be positive integers)\n"
            + "Example: " + COMMAND_WORD + " s/1 l/2";
    public static final String MESSAGE_SUCCESS = "Enrolled %s in %s";
    public static final String MESSAGE_DUPLICATE = "%s is already enrolled in %s";

    /** Creates a command using displayed indices. */
    public EnrolCommand(Index studentIndex, Index lessonIndex) {
        super(studentIndex, lessonIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student student = getTargetStudent(model);
        Lesson lesson = getTargetLesson(model);
        if (model.getAddressBook().isEnrolled(student, lesson)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE, student.getName(), Messages.format(lesson)));
        }
        // the student's own lessons at an overlapping time, which are likely to be a double-booking
        List<Lesson> studentClashes = model.getAddressBook().getClashingLessons(lesson).stream()
                .filter(other -> model.getAddressBook().isEnrolled(student, other))
                .toList();
        model.enrolStudent(student, lesson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, student.getName(), Messages.format(lesson))
                + Messages.formatStudentClashWarning(student, studentClashes));
    }
}
