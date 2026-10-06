package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.util.List;
import java.util.Optional;

import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindStudentCommand object
 */
public class FindStudentCommandParser implements Parser<FindStudentCommand> {

    public static final String MESSAGE_EMPTY_KEYWORDS = "The search text cannot be empty.";
    public static final String MESSAGE_INVALID_KEYWORD =
            "Search keywords should only contain alphanumeric characters, as names do: %1$s";

    private static final String KEYWORD_VALIDATION_REGEX = "\\p{Alnum}+";

    /**
     * Parses the given {@code String} of arguments in the context of the FindStudentCommand
     * and returns a FindStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_GUARDIAN_NAME);
        Optional<String> nameKeywords = argMultimap.getValue(PREFIX_NAME);
        Optional<String> guardianNameKeywords = argMultimap.getValue(PREFIX_GUARDIAN_NAME);

        if (!argMultimap.getPreamble().isEmpty() || (nameKeywords.isEmpty() && guardianNameKeywords.isEmpty())) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_GUARDIAN_NAME);

        return new FindStudentCommand(new StudentNameContainsKeywordsPredicate(
                parseKeywords(nameKeywords), parseKeywords(guardianNameKeywords)));
    }

    /**
     * Splits the value of a prefix into keywords. Returns an empty list if the prefix is absent.
     * @throws ParseException if the prefix is present with no keywords, or a keyword cannot match a name
     */
    private static List<String> parseKeywords(Optional<String> value) throws ParseException {
        if (value.isEmpty()) {
            return List.of();
        }

        String trimmedValue = value.get().trim();
        if (trimmedValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_KEYWORDS);
        }

        List<String> keywords = List.of(trimmedValue.split("\\s+"));
        for (String keyword : keywords) {
            if (!keyword.matches(KEYWORD_VALIDATION_REGEX)) {
                throw new ParseException(String.format(MESSAGE_INVALID_KEYWORD, keyword));
            }
        }
        return keywords;
    }

}
