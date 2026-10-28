package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;

/**
 * Parses input arguments and creates a new {@code EditStudentCommand} object.
 */
public class EditStudentCommandParser implements Parser<EditStudentCommand> {

    public static final String MESSAGE_INCOMPLETE_GUARDIAN =
            "Guardian name and guardian phone must both have values, or both be blank to remove the guardian.";

    /**
     * Parses the given arguments in the context of {@code EditStudentCommand}.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public EditStudentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentTokenizer.verifyNoUnknownPrefixes(args, EditStudentCommand.COMMAND_WORD,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditStudentCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(
                PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);

        Optional<String> phoneValue = argMultimap.getValue(PREFIX_PHONE);
        Optional<String> guardianNameValue = argMultimap.getValue(PREFIX_GUARDIAN_NAME);
        Optional<String> guardianPhoneValue = argMultimap.getValue(PREFIX_GUARDIAN_PHONE);
        if (guardianNameValue.isPresent() != guardianPhoneValue.isPresent()) {
            throw new ParseException(MESSAGE_INCOMPLETE_GUARDIAN);
        }

        EditStudentDescriptor editStudentDescriptor = new EditStudentDescriptor();
        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editStudentDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).orElseThrow()));
        }
        parsePhone(phoneValue, editStudentDescriptor);
        parseGuardian(guardianNameValue, guardianPhoneValue, editStudentDescriptor);

        if (!editStudentDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditStudentCommand.MESSAGE_NOT_EDITED);
        }

        return new EditStudentCommand(index, editStudentDescriptor);
    }

    /**
     * Parses a phone edit, including an empty value that removes the student's phone.
     */
    private static void parsePhone(Optional<String> phoneValue, EditStudentDescriptor editStudentDescriptor)
            throws ParseException {
        if (phoneValue.isPresent()) {
            String value = phoneValue.orElseThrow();
            editStudentDescriptor.setPhone(
                    value.isEmpty() ? null : ParserUtil.parseStudentPhone(value));
        }
    }

    /**
     * Parses a complete guardian edit, including empty values that remove the guardian.
     */
    private static void parseGuardian(Optional<String> nameValue, Optional<String> phoneValue,
            EditStudentDescriptor editStudentDescriptor) throws ParseException {
        if (nameValue.isEmpty()) {
            return;
        }
        String name = nameValue.orElseThrow();
        String phone = phoneValue.orElseThrow();
        if (name.isEmpty() != phone.isEmpty()) {
            throw new ParseException(MESSAGE_INCOMPLETE_GUARDIAN);
        }
        if (name.isEmpty()) {
            editStudentDescriptor.setGuardian(null);
            return;
        }
        Name guardianName = ParserUtil.parseName(name);
        Phone guardianPhone = ParserUtil.parseStudentPhone(phone);
        editStudentDescriptor.setGuardian(new Guardian(guardianName, guardianPhone));
    }
}
