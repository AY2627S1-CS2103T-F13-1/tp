package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

public class FindStudentCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE);

    private FindStudentCommandParser parser = new FindStudentCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_noPrefix_throwsParseException() {
        assertParseFailure(parser, " Aiden Tan", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, " Aiden n/Tan", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_emptyKeywords_throwsParseException() {
        assertParseFailure(parser, " n/", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " gn/  ", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/Aiden gn/", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/ gn/Tan", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        assertParseFailure(parser, " n/Aiden n/Chloe", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " gn/Tan gn/Lim",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_NAME));
    }

    @Test
    public void parse_studentNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Aiden", "Chloe"), List.of()));
        assertParseSuccess(parser, " n/Aiden Chloe", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/ Aiden \n \t Chloe  \t", expectedCommand);
    }

    @Test
    public void parse_guardianNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of(), List.of("Tan", "Lim")));
        assertParseSuccess(parser, " gn/Tan Lim", expectedCommand);
    }

    @Test
    public void parse_studentAndGuardianNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Aiden"), List.of("Mei", "Ling")));
        assertParseSuccess(parser, " n/Aiden gn/Mei Ling", expectedCommand);

        // prefixes in either order
        assertParseSuccess(parser, " gn/Mei Ling n/Aiden", expectedCommand);
    }
}
