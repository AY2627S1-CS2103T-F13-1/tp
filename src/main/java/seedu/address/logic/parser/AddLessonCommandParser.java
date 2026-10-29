package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/**
 * Parses input arguments and creates a new {@code AddLessonCommand} object.
 */
public class AddLessonCommandParser implements Parser<AddLessonCommand> {

    public static final String MESSAGE_MISSING_FIELDS = "Missing %1$s. Each prefix must have a space before it.";

    /**
     * Parses the given {@code String} of arguments in the context of the {@code AddLessonCommand}
     * and returns an {@code AddLessonCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddLessonCommand parse(String args) throws ParseException {
        ArgumentTokenizer.verifyNoUnknownPrefixes(args, AddLessonCommand.COMMAND_WORD,
                PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLessonCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DAY, PREFIX_START_TIME, PREFIX_END_TIME);

        String name = argMultimap.getValue(PREFIX_NAME).orElseThrow();
        DayOfWeek day = ParserUtil.parseDay(argMultimap.getValue(PREFIX_DAY).orElseThrow());
        LocalTime startTime = ParserUtil.parseTime(argMultimap.getValue(PREFIX_START_TIME).orElseThrow());
        LocalTime endTime = ParserUtil.parseTime(argMultimap.getValue(PREFIX_END_TIME).orElseThrow());

        try {
            // Lesson owns the rules for a valid name and time range, and names the broken rule in its message.
            return new AddLessonCommand(new Lesson(name, day, startTime, endTime));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
