package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.UnenrolCommand;

public class UnenrolCommandParserTest {
    private final UnenrolCommandParser parser = new UnenrolCommandParser();

    @Test
    public void parse_validIndices_acceptsEitherOrderAndLeadingZeros() {
        UnenrolCommand expected = new UnenrolCommand(Index.fromOneBased(1), Index.fromOneBased(2));
        assertParseSuccess(parser, " s/1 l/2", expected);
        assertParseSuccess(parser, " l/02 s/01", expected);
    }

    @Test
    public void parse_invalidArguments_reportsUsage() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnenrolCommand.MESSAGE_USAGE);
        for (String args : new String[] {"", " 1 2", " s/1", " l/1", " text s/1 l/2",
            " s/ l/1", " s/1 l/", " s/0 l/1", " s/-1 l/1", " s/+1 l/1", " s/1.5 l/1",
            " s/1 l/0", " s/1 l/-1", " s/1 l/+1", " s/1 l/1.5", " s/2147483648 l/1",
            " s/1 l/2 extra", " s/one l/2", " s/1 l/2 n/Aiden"}) {
            assertParseFailure(parser, args, message);
        }
    }

    @Test
    public void parse_duplicatePrefixes_reportsPrefixes() {
        assertParseFailure(parser, " s/1 s/2 l/1",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_STUDENT));
        assertParseFailure(parser, " s/1 l/1 l/2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_LESSON));
        assertParseFailure(parser, " s/1 s/1 l/1 l/1",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_STUDENT, CliSyntax.PREFIX_LESSON));
    }
}
