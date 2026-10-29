package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/**
 * Parses input arguments and creates a new {@code AddLessonCommand} object.
 */
public class AddLessonCommandParser implements Parser<AddLessonCommand> {

    public static final String MESSAGE_MISSING_FIELDS = "Missing %1$s. Each prefix must have a space before it.";

    // the fields in the order of the command format, with the placeholders that its usage message shows
    private static final List<Field> FIELDS = List.of(new Field(PREFIX_NAME, "NAME"), new Field(PREFIX_DAY, "DAY"),
            new Field(PREFIX_START_TIME, "START_TIME"), new Field(PREFIX_END_TIME, "END_TIME"));

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

        verifyAllFieldsPresent(argMultimap);
        if (!argMultimap.getPreamble().isEmpty()) {
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
     * Throws a {@code ParseException} if any field is missing. If some fields are given, the message names the
     * missing ones, since a prefix typed without a space before it, as in {@code st/1600et/1800}, is not read as
     * a prefix. If none are given, the input does not resemble the command, so the message shows only its usage.
     */
    private static void verifyAllFieldsPresent(ArgumentMultimap argMultimap) throws ParseException {
        List<Field> missingFields = FIELDS.stream()
                .filter(field -> argMultimap.getValue(field.prefix()).isEmpty())
                .toList();
        if (missingFields.isEmpty()) {
            return;
        }

        String usage = AddLessonCommand.MESSAGE_USAGE;
        if (missingFields.size() < FIELDS.size()) {
            usage = String.format(MESSAGE_MISSING_FIELDS, joinFields(missingFields)) + "\n" + usage;
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
    }

    /** Joins the fields as the usage message writes them, as in {@code d/DAY, st/START_TIME and et/END_TIME}. */
    private static String joinFields(List<Field> fields) {
        assert !fields.isEmpty() : "There must be a field to name";
        List<String> names = fields.stream().map(field -> field.prefix() + field.placeholder()).toList();
        if (names.size() == 1) {
            return names.getFirst();
        }
        return String.join(", ", names.subList(0, names.size() - 1)) + " and " + names.getLast();
    }

    /** A field of the command, with the placeholder that the usage message shows for its value. */
    private record Field(Prefix prefix, String placeholder) {
    }
}
