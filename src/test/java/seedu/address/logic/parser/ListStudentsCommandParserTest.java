package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListStudentsCommand;

public class ListStudentsCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListStudentsCommand.MESSAGE_USAGE);

    private final ListStudentsCommandParser parser = new ListStudentsCommandParser();

    @Test
    public void parse_noArgs_returnsListStudentsCommand() {
        assertParseSuccess(parser, "", new ListStudentsCommand());
        assertParseSuccess(parser, " \t ", new ListStudentsCommand());
    }

    @Test
    public void parse_textWithoutPrefix_throwsParseException() {
        // the tutor probably meant a lesson filter, so showing everyone would mislead
        assertParseFailure(parser, "2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " all", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        // the l/ lesson filter is not supported until lessons exist
        assertParseFailure(parser, " l/2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " n/Aiden", MESSAGE_INVALID_FORMAT);
    }
}
