package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LESSON;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT;

import seedu.address.logic.commands.EnrolCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the two required, single-valued displayed indices. */
public class EnrolCommandParser implements Parser<EnrolCommand> {
    @Override
    public EnrolCommand parse(String args) throws ParseException {
        ArgumentMultimap values = ArgumentTokenizer.tokenize(args, PREFIX_STUDENT, PREFIX_LESSON);
        values.verifyNoDuplicatePrefixesFor(PREFIX_STUDENT, PREFIX_LESSON);
        if (!values.getPreamble().isEmpty() || values.getValue(PREFIX_STUDENT).isEmpty()
                || values.getValue(PREFIX_LESSON).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EnrolCommand.MESSAGE_USAGE));
        }
        try {
            return new EnrolCommand(ParserUtil.parseIndex(values.getValue(PREFIX_STUDENT).get()),
                    ParserUtil.parseIndex(values.getValue(PREFIX_LESSON).get()));
        } catch (ParseException e) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EnrolCommand.MESSAGE_USAGE), e);
        }
    }
}
