package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.model.student.StudentContainsKeywordsPredicate;

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
        assertParseFailure(parser, " 1 gn/Tan", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_upperCasePrefix_throwsParseException() {
        // prefixes are case-sensitive, so the error names the prefix that was not recognised
        assertParseFailure(parser, " N/Aiden", unknownPrefix("N/"));
        assertParseFailure(parser, " GN/Tan", unknownPrefix("GN/"));
        assertParseFailure(parser, " P/9123", unknownPrefix("P/"));
    }

    @Test
    public void parse_unrelatedPrefixOnly_throwsParseException() {
        // gp/ is how addstudent takes a guardian's phone, so a tutor may expect it here too
        assertParseFailure(parser, " gp/91234567", unknownPrefix("gp/"));
        assertParseFailure(parser, " l/1", unknownPrefix("l/"));
    }

    @Test
    public void parse_unrelatedPrefixAfterSearchText_throwsParseException() {
        assertParseFailure(parser, " n/Aiden gp/91234567", unknownPrefix("gp/"));
        assertParseFailure(parser, " gn/Tan t/friend", unknownPrefix("t/"));
        assertParseFailure(parser, " p/9123 l/1", unknownPrefix("l/"));
    }

    @Test
    public void parse_emptyKeywords_throwsParseException() {
        assertParseFailure(parser, " n/", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " gn/  ", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/Aiden gn/", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/ gn/Tan", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/ \t\n gn/ ", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " p/", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseFailure(parser, " n/Aiden p/  ", FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
    }

    @Test
    public void parse_invalidNameKeyword_throwsParseException() {
        assertParseFailure(parser, " n/Tan!", invalidKeyword("Tan!"));
        assertParseFailure(parser, " gn/Anne--Marie", invalidKeyword("Anne--Marie"));
        assertParseFailure(parser, " n/Aiden gn/O''Neil", invalidKeyword("O''Neil"));
        assertParseFailure(parser, " n/Aiden Tan*", invalidKeyword("Tan*"));
        assertParseFailure(parser, " n/Kumar and/or Ravi", invalidKeyword("and/or"));
    }

    @Test
    public void parse_invalidPhoneSearch_throwsParseException() {
        assertParseFailure(parser, " p/91a", invalidPhoneKeyword("91a"));
        assertParseFailure(parser, " p/Tan", invalidPhoneKeyword("Tan"));
        assertParseFailure(parser, " n/Aiden p/9123 x", invalidPhoneKeyword("9123 x"));

        // a + only at the start, and at least one digit
        assertParseFailure(parser, " p/91+23", invalidPhoneKeyword("91+23"));
        assertParseFailure(parser, " p/+-", invalidPhoneKeyword("+-"));
        assertParseFailure(parser, " p/+", invalidPhoneKeyword("+"));
        assertParseFailure(parser, " p/- -", invalidPhoneKeyword("- -"));

        // other separators and brackets are not ignored
        assertParseFailure(parser, " p/9123.4567", invalidPhoneKeyword("9123.4567"));
        assertParseFailure(parser, " p/(65) 91234567", invalidPhoneKeyword("(65) 91234567"));

    }

    @Test
    public void parse_digitsAsNameKeyword_searchesNamesOnly() {
        // digits after n/ are a name keyword, not a phone search
        assertParseSuccess(parser, " n/9123",
                new FindStudentCommand(new StudentContainsKeywordsPredicate(List.of("9123"), List.of())));
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        assertParseFailure(parser, " n/Aiden n/Chloe", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " gn/Tan gn/Lim",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_NAME));
        assertParseFailure(parser, " p/9123 p/4567", Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_studentNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("Aiden", "Chloe"), List.of()));
        assertParseSuccess(parser, " n/Aiden Chloe", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n n/ Aiden \n \t Chloe  \t", expectedCommand);
    }

    @Test
    public void parse_singleKeyword_returnsFindStudentCommand() {
        assertParseSuccess(parser, " n/Aiden",
                new FindStudentCommand(new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of())));
        assertParseSuccess(parser, " gn/Tan",
                new FindStudentCommand(new StudentContainsKeywordsPredicate(List.of(), List.of("Tan"))));
    }

    @Test
    public void parse_mixedCaseKeywords_keywordsKeptAsTyped() {
        assertParseSuccess(parser, " n/aIDEN gn/tAN", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("aIDEN"), List.of("tAN"))));
    }

    @Test
    public void parse_numericKeyword_returnsFindStudentCommand() {
        assertParseSuccess(parser, " n/Aiden 2",
                new FindStudentCommand(new StudentContainsKeywordsPredicate(List.of("Aiden", "2"), List.of())));
    }

    @Test
    public void parse_guardianNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of("Tan", "Lim")));
        assertParseSuccess(parser, " gn/Tan Lim", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \t gn/  Tan \n Lim ", expectedCommand);
    }

    @Test
    public void parse_realNameCharacters_returnsFindStudentCommand() {
        assertParseSuccess(parser, " n/José Anne-Marie s/o", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("José", "Anne-Marie", "s/o"), List.of())));
        assertParseSuccess(parser, " gn/O'Neil", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of("O'Neil"))));
    }

    @Test
    public void parse_studentAndGuardianNameKeywords_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Mei", "Ling")));
        assertParseSuccess(parser, " n/Aiden gn/Mei Ling", expectedCommand);

        // prefixes in either order
        assertParseSuccess(parser, " gn/Mei Ling n/Aiden", expectedCommand);
    }

    @Test
    public void parse_phoneNumberAsTyped_returnsFindStudentCommand() {
        // p/ takes one number, so spaces and hyphens inside it are dropped instead of splitting it
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("91234567")));
        assertParseSuccess(parser, " p/91234567", expectedCommand);
        assertParseSuccess(parser, " p/9123 4567", expectedCommand);
        assertParseSuccess(parser, " p/9123-4567", expectedCommand);
        assertParseSuccess(parser, " \t p/ 9123 \n 45-67 ", expectedCommand);

        // a country code is kept, so that the full number can be matched against stored numbers
        assertParseSuccess(parser, " p/+65 9123 4567", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("6591234567"))));
        assertParseSuccess(parser, " p/+ 65 9123 4567", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("6591234567"))));

        // copying the end of 9123-4567 can bring its hyphen along
        assertParseSuccess(parser, " p/-4567", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("4567"))));

        // single digit and leading zeros are kept as typed
        assertParseSuccess(parser, " p/0", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("0"))));
        assertParseSuccess(parser, " p/0283", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("0283"))));
    }

    @Test
    public void parse_allPrefixes_returnsFindStudentCommand() {
        FindStudentCommand expectedCommand = new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("Aiden"), List.of("Tan"), List.of("9123")));
        assertParseSuccess(parser, " n/Aiden gn/Tan p/9123", expectedCommand);

        // prefixes in any order
        assertParseSuccess(parser, " p/9123 n/Aiden gn/Tan", expectedCommand);
        assertParseSuccess(parser, " gn/Tan p/9123 n/Aiden", expectedCommand);
    }

    @Test
    public void parse_nameAndPhoneKeywords_returnsFindStudentCommand() {
        assertParseSuccess(parser, " n/Ryan p/0283", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of("Ryan"), List.of(), List.of("0283"))));
        assertParseSuccess(parser, " gn/Tan p/9123", new FindStudentCommand(
                new StudentContainsKeywordsPredicate(List.of(), List.of("Tan"), List.of("9123"))));
    }

    private static String invalidPhoneKeyword(String keyword) {
        return String.format(FindStudentCommandParser.MESSAGE_INVALID_PHONE_KEYWORD, keyword);
    }

    private static String unknownPrefix(String prefix) {
        return Messages.getErrorMessageForUnknownPrefix(FindStudentCommand.COMMAND_WORD, prefix,
                PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_PHONE);
    }

    private static String invalidKeyword(String keyword) {
        return String.format(FindStudentCommandParser.MESSAGE_INVALID_KEYWORD, keyword);
    }
}
