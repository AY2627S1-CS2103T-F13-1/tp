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
        assertParseFailure(parser, "\u0000", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unsupportedPrefix_throwsParseException() {
        assertParseSuccess(parser, " l/2",
                new ListStudentsCommand(seedu.address.commons.core.index.Index.fromOneBased(2)));
        assertParseFailure(parser, " n/Aiden", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidLessonFilter_reportsUsage() {
        for (String args : new String[] {" l/", " l/0", " l/-1", " l/+1", " l/1.5",
            " l/2147483648", " l/one", " l/1 extra", " text l/1"}) {
            assertParseFailure(parser, args, MESSAGE_INVALID_FORMAT);
        }
        assertParseFailure(parser, " l/1 l/2",
                seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_LESSON));
    }

}
