package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_LESSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditLessonCommand;
import seedu.address.logic.commands.EditLessonCommand.EditLessonDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.lesson.Lesson;

/**
 * Contains unit tests for {@code EditLessonCommandParser}.
 */
public class EditLessonCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditLessonCommand.MESSAGE_USAGE);

    private static final String NAME_DESC = " n/Advanced Mathematics";
    private static final String DAY_DESC = " d/tue";
    private static final String START_DESC = " st/1700";
    private static final String END_DESC = " et/1900";

    private final EditLessonCommandParser parser = new EditLessonCommandParser();

    @Test
    public void parse_allFieldsPresent_returnsEditLessonCommand() {
        assertParseSuccess(parser, "1" + NAME_DESC + DAY_DESC + START_DESC + END_DESC,
                command(descriptor("Advanced Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00")));
    }

    @Test
    public void parse_usageExample_returnsEditLessonCommand() {
        String usage = EditLessonCommand.MESSAGE_USAGE;
        String exampleArguments = usage.substring(
                usage.lastIndexOf(EditLessonCommand.COMMAND_WORD) + EditLessonCommand.COMMAND_WORD.length());
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setDay(DayOfWeek.TUESDAY);
        descriptor.setStartTime(LocalTime.parse("17:00"));
        descriptor.setEndTime(LocalTime.parse("19:00"));

        assertParseSuccess(parser, exampleArguments, command(descriptor));
    }

    @Test
    public void parse_eachFieldSeparately_returnsEditLessonCommand() {
        assertParseSuccess(parser, "1" + NAME_DESC, command(descriptorWithName("Advanced Mathematics")));

        EditLessonDescriptor dayDescriptor = new EditLessonDescriptor();
        dayDescriptor.setDay(DayOfWeek.TUESDAY);
        assertParseSuccess(parser, "1" + DAY_DESC, command(dayDescriptor));

        EditLessonDescriptor startDescriptor = new EditLessonDescriptor();
        startDescriptor.setStartTime(LocalTime.parse("17:00"));
        assertParseSuccess(parser, "1" + START_DESC, command(startDescriptor));

        EditLessonDescriptor endDescriptor = new EditLessonDescriptor();
        endDescriptor.setEndTime(LocalTime.parse("19:00"));
        assertParseSuccess(parser, "1" + END_DESC, command(endDescriptor));
    }

    @Test
    public void parse_fieldsInAnyOrder_returnsEditLessonCommand() {
        EditLessonCommand expectedCommand = command(
                descriptor("Advanced Mathematics", DayOfWeek.TUESDAY, "17:00", "19:00"));

        assertParseSuccess(parser, "1" + END_DESC + START_DESC + DAY_DESC + NAME_DESC, expectedCommand);
        assertParseSuccess(parser, "1" + DAY_DESC + NAME_DESC + END_DESC + START_DESC, expectedCommand);
    }

    @Test
    public void parse_extraWhitespace_normalizesName() {
        assertParseSuccess(parser, "  1   n/  Advanced   Mathematics  ",
                command(descriptorWithName("Advanced Mathematics")));
    }

    @Test
    public void parse_fullDayNameAndTimesWithColons_returnsEditLessonCommand() {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setDay(DayOfWeek.TUESDAY);
        descriptor.setStartTime(LocalTime.parse("17:00"));
        descriptor.setEndTime(LocalTime.parse("19:00"));

        assertParseSuccess(parser, "1 d/Tuesday st/17:00 et/1900", command(descriptor));
    }

    @Test
    public void parse_overnightTimes_returnsEditLessonCommand() {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setStartTime(LocalTime.parse("22:00"));
        descriptor.setEndTime(LocalTime.parse("01:00"));

        assertParseSuccess(parser, "1 st/2200 et/0100", command(descriptor));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, NAME_DESC, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   " + DAY_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        for (String index : List.of("0", "-1", "+1", "1.5", "one", "2147483648")) {
            assertParseFailure(parser, index + NAME_DESC, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_noFieldsToEdit_throwsParseException() {
        assertParseFailure(parser, "1", EditLessonCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "  1  ", EditLessonCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_repeatedField_throwsParseException() {
        assertParseFailure(parser, "1 n/Math" + NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "1 d/mon" + DAY_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY));
        assertParseFailure(parser, "1 st/1600" + START_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_START_TIME));
        assertParseFailure(parser, "1 et/1800" + END_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_END_TIME));
        assertParseFailure(parser, "1" + DAY_DESC + DAY_DESC + START_DESC + START_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_DAY, PREFIX_START_TIME));
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        String tooLongName = "A".repeat(51);
        for (String name : List.of("", "-Math", "(Math)", "Math/Science", "Math@Home", tooLongName)) {
            assertParseFailure(parser, "1 n/" + name, Lesson.MESSAGE_NAME_CONSTRAINTS);
        }
    }

    @Test
    public void parse_invalidDay_throwsParseException() {
        for (String day : List.of("", "funday", "mo", "m", "1")) {
            assertParseFailure(parser, "1 d/" + day, ParserUtil.MESSAGE_INVALID_DAY);
        }
    }

    @Test
    public void parse_validDayFollowedByText_throwsParseExceptionNamingText() {
        assertParseFailure(parser, "1 d/mon tue",
                String.format(ParserUtil.MESSAGE_UNEXPECTED_TEXT_AFTER_DAY, "mon", "tue"));
    }

    @Test
    public void parse_invalidTime_throwsParseException() {
        for (String time : List.of("", "4pm", "2400", "1660", "16:60", "900")) {
            assertParseFailure(parser, "1 st/" + time, ParserUtil.MESSAGE_INVALID_TIME);
            assertParseFailure(parser, "1 et/" + time, ParserUtil.MESSAGE_INVALID_TIME);
        }
    }

    @Test
    public void parse_equalStartAndEndTime_throwsParseException() {
        for (String time : List.of("0000", "1600", "2359")) {
            assertParseFailure(parser, "1 st/" + time + " et/" + time, Lesson.MESSAGE_TIME_CONSTRAINTS);
        }
    }

    @Test
    public void parse_unsupportedPrefixOrTrailingText_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("1 p/91234567"));
        assertThrows(ParseException.class, () -> parser.parse("1 n/Math p/91234567"));
        assertThrows(ParseException.class, () -> parser.parse("1 d/mon extra"));
        assertThrows(ParseException.class, () -> parser.parse("1 st/1600 l/2"));
    }

    @Test
    public void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    private static EditLessonCommand command(EditLessonDescriptor descriptor) {
        return new EditLessonCommand(INDEX_FIRST_LESSON, descriptor);
    }

    private static EditLessonDescriptor descriptorWithName(String name) {
        EditLessonDescriptor descriptor = new EditLessonDescriptor();
        descriptor.setName(name);
        return descriptor;
    }

    private static EditLessonDescriptor descriptor(
            String name, DayOfWeek day, String startTime, String endTime) {
        EditLessonDescriptor descriptor = descriptorWithName(name);
        descriptor.setDay(day);
        descriptor.setStartTime(LocalTime.parse(startTime));
        descriptor.setEndTime(LocalTime.parse(endTime));
        return descriptor;
    }
}
