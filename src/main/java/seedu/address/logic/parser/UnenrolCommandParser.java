package seedu.address.logic.parser;

import seedu.address.logic.commands.UnenrolCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the two required, single-valued displayed indices. */
public class UnenrolCommandParser implements Parser<UnenrolCommand> {
    @Override
    public UnenrolCommand parse(String args) throws ParseException {
        return ParserUtil.parseStudentAndLessonIndices(args, UnenrolCommand.MESSAGE_USAGE, UnenrolCommand::new);
    }
}
