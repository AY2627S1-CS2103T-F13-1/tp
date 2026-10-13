package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;

import seedu.address.logic.commands.ListStudentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses an optional lesson filter. */
public class ListStudentsCommandParser implements Parser<ListStudentsCommand> {
    @Override
    public ListStudentsCommand parse(String args) throws ParseException {
        if (args.isBlank()) {
            return new ListStudentsCommand();
        }
        ArgumentMultimap values = ArgumentTokenizer.tokenize(args, PREFIX_LESSON);
        values.verifyNoDuplicatePrefixesFor(PREFIX_LESSON);
        try {
            if (!values.getPreamble().isEmpty()) {
                throw new ParseException("Missing lesson index");
            }
            return new ListStudentsCommand(ParserUtil.parseIndex(values.getValue(PREFIX_LESSON).get()));
        } catch (ParseException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListStudentsCommand.MESSAGE_USAGE), e);
        }
    }
}
