package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;

import seedu.address.logic.commands.ListLessonsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional day filter for {@code ListLessonsCommand}.
 */
public class ListLessonsCommandParser implements Parser<ListLessonsCommand> {

    @Override
    public ListLessonsCommand parse(String args) throws ParseException {
        if (args.isBlank()) {
            return new ListLessonsCommand();
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DAY);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_DAY);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListLessonsCommand.MESSAGE_USAGE));
        }

        return new ListLessonsCommand(ParserUtil.parseDay(argMultimap.getValue(PREFIX_DAY).orElseThrow()));
    }
}
