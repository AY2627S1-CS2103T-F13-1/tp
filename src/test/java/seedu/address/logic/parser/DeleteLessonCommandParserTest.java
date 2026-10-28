package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_LESSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_LESSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteLessonCommand;

public class DeleteLessonCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteLessonCommand.MESSAGE_USAGE);

    private final DeleteLessonCommandParser parser = new DeleteLessonCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteLessonCommand() {
        assertParseSuccess(parser, "1", new DeleteLessonCommand(INDEX_FIRST_LESSON));
        assertParseSuccess(parser, "  3  ", new DeleteLessonCommand(INDEX_THIRD_LESSON));
    }

    @Test
    public void parse_leadingZero_readAsSameIndex() {
        assertParseSuccess(parser, "01", new DeleteLessonCommand(INDEX_FIRST_LESSON));
    }

    @Test
    public void parse_largestInteger_returnsDeleteLessonCommand() {
        // far beyond any list, but still a positive integer: the command reports it as an invalid index
        assertParseSuccess(parser, "2147483647", new DeleteLessonCommand(Index.fromOneBased(Integer.MAX_VALUE)));
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
        assertParseFailure(parser, "one", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_indexTooLargeForAnyList_throwsInvalidIndex() {
        // a well-formed index beyond what an int holds is out of range, not a format error
        assertParseFailure(parser, "2147483648", MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertParseFailure(parser, " 99999999999999999999 ", MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertParseFailure(parser, "0099999999999", MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
    }

    @Test
    public void parse_extraArgs_throwsParseException() {
        assertParseFailure(parser, "1 2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 abc", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1,2", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1-2", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_prefixOrNameInsteadOfIndex_throwsParseException() {
        // a lesson is chosen by its displayed index only
        assertParseFailure(parser, " l/1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " n/Sec 3 A-Math", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " Sec 3 A-Math", MESSAGE_INVALID_FORMAT);
    }
}
