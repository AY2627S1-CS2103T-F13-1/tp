package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_DAY;

import java.time.DayOfWeek;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ListLessonsCommand;

public class ListLessonsCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListLessonsCommand.MESSAGE_USAGE);

    private final ListLessonsCommandParser parser = new ListLessonsCommandParser();

    @Test
    public void parse_noArguments_returnsUnfilteredCommand() {
        assertParseSuccess(parser, "", new ListLessonsCommand());
        assertParseSuccess(parser, " \t ", new ListLessonsCommand());
    }

    @Test
    public void parse_validDay_returnsFilteredCommand() {
        assertParseSuccess(parser, " d/mon", new ListLessonsCommand(DayOfWeek.MONDAY));
        assertParseSuccess(parser, " d/WED", new ListLessonsCommand(DayOfWeek.WEDNESDAY));
        assertParseSuccess(parser, " d/  sun  ", new ListLessonsCommand(DayOfWeek.SUNDAY));
        assertParseSuccess(parser, " d/Monday", new ListLessonsCommand(DayOfWeek.MONDAY));
        assertParseSuccess(parser, " d/tues", new ListLessonsCommand(DayOfWeek.TUESDAY));
    }

    @Test
    public void parse_missingDay_throwsParseException() {
        assertParseFailure(parser, " d/", MESSAGE_INVALID_DAY);
    }

    @Test
    public void parse_invalidDay_throwsParseException() {
        for (String args : new String[] {" d/weds", " d/mo", " d/x", " d/1", " d/mon extra"}) {
            assertParseFailure(parser, args, MESSAGE_INVALID_DAY);
        }
    }

    @Test
    public void parse_textWithoutPrefix_throwsParseException() {
        assertParseFailure(parser, "mon", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " all", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        assertParseFailure(parser, " n/Math", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_duplicateDayPrefix_throwsParseException() {
        assertParseFailure(parser, " d/mon d/tue",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DAY));
    }

    @Test
    public void equals_commandsFromEquivalentDayInputs_areEqual() throws Exception {
        assertEquals(parser.parse(" d/mon"), parser.parse(" d/MON"));
    }
}
