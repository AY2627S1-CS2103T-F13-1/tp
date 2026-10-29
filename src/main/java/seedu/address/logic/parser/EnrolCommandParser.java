package seedu.address.logic.parser;

import seedu.address.logic.commands.EnrolCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the two required, single-valued displayed indices. */
public class EnrolCommandParser implements Parser<EnrolCommand> {
    @Override
    public EnrolCommand parse(String args) throws ParseException {
        return ParserUtil.parseStudentAndLessonIndices(args, EnrolCommand.MESSAGE_USAGE, EnrolCommand::new);
    }
}
