package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    public static final String MESSAGE_INVALID_PHONE_KEYWORD = "Phone search keywords should only contain digits: %1$s";

    private static final String PHONE_KEYWORD_VALIDATION_REGEX = "\\d+";

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
     * when it is a valid name.
     * @throws ParseException if the prefix is present with no keywords, or a keyword is not a valid name
     */
    private static List<String> parseNameKeywords(Optional<String> value) throws ParseException {
        return parseKeywords(value, Name::isValidName, MESSAGE_INVALID_KEYWORD);
    }

    /**
     * Splits the value of the phone prefix into keywords.
     * @throws ParseException if the prefix is present with no keywords, or a keyword is not all digits
     */
    private static List<String> parsePhoneKeywords(Optional<String> value) throws ParseException {
        return parseKeywords(value, keyword -> keyword.matches(PHONE_KEYWORD_VALIDATION_REGEX),
                MESSAGE_INVALID_PHONE_KEYWORD);
    }

    /**
     * Splits the value of a prefix into keywords. Returns an empty list if the prefix is absent.
     * @throws ParseException if the prefix is present with no keywords, or a keyword fails {@code isValidKeyword},
     *     in which case the error is {@code invalidKeywordMessage} formatted with that keyword
     */
    private static List<String> parseKeywords(Optional<String> value, Predicate<String> isValidKeyword,
            String invalidKeywordMessage) throws ParseException {
        if (value.isEmpty()) {
            return List.of();
        }

        String trimmedValue = value.get().trim();
        if (trimmedValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_KEYWORDS);
        }

        List<String> keywords = List.of(trimmedValue.split("\\s+"));
        for (String keyword : keywords) {
            if (!isValidKeyword.test(keyword)) {
                throw new ParseException(String.format(invalidKeywordMessage, keyword));
            }
        }
        return keywords;
    }

}
