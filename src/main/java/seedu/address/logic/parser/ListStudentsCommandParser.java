package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListStudentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListStudentsCommand object
 */
public class ListStudentsCommandParser implements Parser<ListStudentsCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListStudentsCommand
     * and returns a ListStudentsCommand object for execution.
     * Unlike {@code list}, extra text is rejected rather than ignored, so that a mistyped filter
     * such as {@code liststudents 2} does not silently show every student.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ListStudentsCommand parse(String args) throws ParseException {
        if (!args.isBlank()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListStudentsCommand.MESSAGE_USAGE));
        }
        return new ListStudentsCommand();
    }

}
