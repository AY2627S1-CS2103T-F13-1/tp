package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.student.StudentContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindStudentCommand object
 */
public class FindStudentCommandParser implements Parser<FindStudentCommand> {

    public static final String MESSAGE_EMPTY_KEYWORDS = "The search text cannot be empty.";
    public static final String MESSAGE_INVALID_KEYWORD =
            "Search keywords should only contain alphanumeric characters, as names do: %1$s";
    public static final String MESSAGE_INVALID_PHONE_KEYWORD =
            "Phone search should contain only digits, spaces, hyphens and a leading +: %1$s";

    // one number written as it often is, such as +65 9123-4567
    private static final String PHONE_SEARCH_VALIDATION_REGEX = "\\+?[0-9][0-9\\s-]*";
    private static final String NON_DIGIT_REGEX = "[^0-9]";

    /**
     * Parses the given {@code String} of arguments in the context of the FindStudentCommand
     * and returns a FindStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_PHONE);
        Optional<String> nameKeywords = argMultimap.getValue(PREFIX_NAME);
        Optional<String> guardianNameKeywords = argMultimap.getValue(PREFIX_GUARDIAN_NAME);
        Optional<String> phoneKeywords = argMultimap.getValue(PREFIX_PHONE);

        if (!argMultimap.getPreamble().isEmpty()
                || (nameKeywords.isEmpty() && guardianNameKeywords.isEmpty() && phoneKeywords.isEmpty())) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_GUARDIAN_NAME, PREFIX_PHONE);

        return new FindStudentCommand(new StudentContainsKeywordsPredicate(parseNameKeywords(nameKeywords),
                parseNameKeywords(guardianNameKeywords), parsePhoneKeywords(phoneKeywords)));
    }

    /**
     * Splits the value of a name prefix into keywords. A keyword is a single word, so it is valid exactly
     * when it is a valid name. Returns an empty list if the prefix is absent.
     * @throws ParseException if the prefix is present with no keywords, or a keyword is not a valid name
     */
    private static List<String> parseNameKeywords(Optional<String> value) throws ParseException {
        if (value.isEmpty()) {
            return List.of();
        }

        List<String> keywords = List.of(requireSearchText(value.get()).split("\\s+"));
        for (String keyword : keywords) {
            if (!Name.isValidName(keyword)) {
                throw new ParseException(String.format(MESSAGE_INVALID_KEYWORD, keyword));
            }
        }
        return keywords;
    }

    /**
     * Reads the value of the phone prefix as one phone number, or part of one, which may contain spaces, hyphens
     * and a leading {@code +}. Returns its digits as the only keyword, or an empty list if the prefix is absent.
     * @throws ParseException if the prefix is present with no text, or the text is not such a phone number
     */
    private static List<String> parsePhoneKeywords(Optional<String> value) throws ParseException {
        if (value.isEmpty()) {
            return List.of();
        }

        String phoneNumber = requireSearchText(value.get());
        if (!phoneNumber.matches(PHONE_SEARCH_VALIDATION_REGEX)) {
            throw new ParseException(String.format(MESSAGE_INVALID_PHONE_KEYWORD, phoneNumber));
        }
        return List.of(phoneNumber.replaceAll(NON_DIGIT_REGEX, ""));
    }

    /**
     * Returns {@code value} without leading and trailing whitespace.
     * @throws ParseException if {@code value} is blank
     */
    private static String requireSearchText(String value) throws ParseException {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_KEYWORDS);
        }
        return trimmedValue;
    }

}
