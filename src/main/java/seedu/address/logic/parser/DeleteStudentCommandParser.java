package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteStudentCommand object
 */
public class DeleteStudentCommandParser implements Parser<DeleteStudentCommand> {

    private static final String POSITIVE_INTEGER_REGEX = "0*[1-9][0-9]*";
    private static final String NON_POSITIVE_INTEGER_REGEX = "-[0-9]+|0+";

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteStudentCommand
     * and returns a DeleteStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteStudentCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseIndex(args);
            return new DeleteStudentCommand(index);
        } catch (ParseException pe) {
            String trimmedArgs = args.trim();
            if (trimmedArgs.matches(POSITIVE_INTEGER_REGEX)) {
                // a well-formed index too large to parse is beyond any displayed list
                throw new ParseException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, pe);
            }
            if (trimmedArgs.matches(NON_POSITIVE_INTEGER_REGEX)) {
                throw new ParseException(ParserUtil.MESSAGE_INVALID_INDEX, pe);
            }
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudentCommand.MESSAGE_USAGE), pe);
        }
    }

}
