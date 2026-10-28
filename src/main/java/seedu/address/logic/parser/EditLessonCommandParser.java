package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.time.DayOfWeek;
import java.time.LocalTime;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.logic.commands.EditLessonCommand.EditLessonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/**
 * Parses input arguments and creates a new {@code EditLessonCommand} object.
 */
public class EditLessonCommandParser implements Parser<EditLessonCommand> {

    private static final LocalTime VALIDATION_START_TIME = LocalTime.MIDNIGHT;
    private static final LocalTime VALIDATION_END_TIME = LocalTime.of(0, 1);

    /**
     * Parses {@code args} in the context of {@code EditLessonCommand}.
     *
     * @throws ParseException If the user input does not conform to the expected format.
     */
    @Override
    public EditLessonCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE), e);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);

        EditLessonDescriptor editLessonDescriptor = new EditLessonDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editLessonDescriptor.setName(parseLessonName(argMultimap.getValue(PREFIX_NAME).orElseThrow()));
        }
        if (argMultimap.getValue(PREFIX_DAY).isPresent()) {
            editLessonDescriptor.setDay(ParserUtil.parseDay(argMultimap.getValue(PREFIX_DAY).orElseThrow()));
        }
        if (argMultimap.getValue(PREFIX_START_TIME).isPresent()) {
            editLessonDescriptor.setStartTime(
                    ParserUtil.parseTime(argMultimap.getValue(PREFIX_START_TIME).orElseThrow()));
        }
        if (argMultimap.getValue(PREFIX_END_TIME).isPresent()) {
            editLessonDescriptor.setEndTime(
                    ParserUtil.parseTime(argMultimap.getValue(PREFIX_END_TIME).orElseThrow()));
        }

        if (!editLessonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditLessonCommand.MESSAGE_NOT_EDITED);
        }
        ArgumentTokenizer.verifyNoUnknownPrefixes(args, EditLessonCommand.COMMAND_WORD,
                PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);
        if (editLessonDescriptor.getStartTime().isPresent()
                && editLessonDescriptor.getStartTime().equals(editLessonDescriptor.getEndTime())) {
            throw new ParseException(Lesson.MESSAGE_TIME_CONSTRAINTS);
        }

        return new EditLessonCommand(index, editLessonDescriptor);
    }

    /**
     * Validates and normalizes a lesson name using the rules owned by {@link Lesson}.
     */
    private static String parseLessonName(String name) throws ParseException {
        try {
            Lesson validationLesson = new Lesson(
                    name, DayOfWeek.MONDAY, VALIDATION_START_TIME, VALIDATION_END_TIME);
            return validationLesson.getName();
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
