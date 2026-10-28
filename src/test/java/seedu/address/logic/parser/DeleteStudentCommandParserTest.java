package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteStudentCommand;

public class DeleteStudentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudentCommand.MESSAGE_USAGE);

    private final DeleteStudentCommandParser parser = new DeleteStudentCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteStudentCommand() {
        assertParseSuccess(parser, "1", new DeleteStudentCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  3  ", new DeleteStudentCommand(Index.fromOneBased(3)));
    }

    @Test
    public void parse_leadingZero_readAsSameIndex() {
        assertParseSuccess(parser, "01", new DeleteStudentCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_largestInteger_returnsDeleteStudentCommand() {
        assertParseSuccess(parser, "2147483647", new DeleteStudentCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nonPositiveInteger_throwsInvalidIndex() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "000", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-0", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, "-99999999999999999999", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_malformedIndex_throwsInvalidFormat() {
        assertParseFailure(parser, "+1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1.5", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "abc", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_indexTooLargeForAnyList_throwsInvalidIndex() {
        assertParseFailure(parser, "2147483648", MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        assertParseFailure(parser, " 99999999999999999999 ", MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        assertParseFailure(parser, "0099999999999", MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 abc", MESSAGE_INVALID_FORMAT);
    }
}
