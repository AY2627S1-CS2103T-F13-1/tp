package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/**
 * Parses input arguments and creates a new {@code AddStudentCommand} object.
 */
public class AddStudentCommandParser implements Parser<AddStudentCommand> {

    public static final String MESSAGE_INCOMPLETE_GUARDIAN =
            "Guardian name and guardian phone must be provided together.";

    /**
     * Parses the given {@code String} of arguments in the context of the {@code AddStudentCommand}
     * and returns an {@code AddStudentCommand} object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

        if (argMultimap.getValue(PREFIX_NAME).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

        Optional<String> phoneValue = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> guardianNameValue = argMultimap.getValue(PREFIX_GUARDIAN_NAME);
        Optional<String> guardianPhoneValue = argMultimap.getValue(PREFIX_GUARDIAN_PHONE);

        if (guardianNameValue.isPresent() != guardianPhoneValue.isPresent()) {
            throw new ParseException(MESSAGE_INCOMPLETE_GUARDIAN);
        }
        if (phoneValue.isEmpty() && guardianPhoneValue.isEmpty()) {
            throw new ParseException(Student.MESSAGE_CONTACT_REQUIRED);
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).orElseThrow());
        Phone phone = phoneValue.isPresent() ? ParserUtil.parsePhone(phoneValue.get()) : null;
        Guardian guardian = parseGuardian(guardianNameValue, guardianPhoneValue);

        return new AddStudentCommand(new Student(name, phone, guardian));
    }

    private static Guardian parseGuardian(Optional<String> nameValue, Optional<String> phoneValue)
            throws ParseException {
        if (nameValue.isEmpty()) {
            return null;
        }
        Name name = ParserUtil.parseName(nameValue.get());
        Phone phone = ParserUtil.parsePhone(phoneValue.orElseThrow());
        return new Guardian(name, phone);
    }
}
