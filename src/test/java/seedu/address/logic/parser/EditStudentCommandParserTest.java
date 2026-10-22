package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditStudentCommand;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;

/**
 * Contains unit tests for {@code EditStudentCommandParser}.
 */
public class EditStudentCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditStudentCommand.MESSAGE_USAGE);

    private final EditStudentCommandParser parser = new EditStudentCommandParser();

    @Test
    public void parse_missingParts_throwsParseException() {
        assertParseFailure(parser, " n/Aiden Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1", EditStudentCommand.MESSAGE_NOT_EDITED);
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_throwsParseException() {
        assertParseFailure(parser, "-1 n/Aiden Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "0 n/Aiden Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 extra n/Aiden Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "1 x/value n/Aiden Tan", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_incompleteGuardian_throwsParseException() {
        assertParseFailure(parser, "1 gn/Tan Mei Ling",
                EditStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
        assertParseFailure(parser, "1 gp/91234567",
                EditStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
        assertParseFailure(parser, "1 p/92345678 gn/Tan Mei Ling",
                EditStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        assertParseFailure(parser, "1 n/Aiden n/Ryan",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, "1 p/92345678 p/93456789",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, "1 gn/Tan gn/Lim gp/91234567",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_NAME));
        assertParseFailure(parser, "1 gn/Tan gp/91234567 gp/92345678",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_PHONE));
    }

    @Test
    public void parse_invalidValues_throwsParseException() {
        assertParseFailure(parser, "1 n/Aiden!", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 p/phone", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 gn/Tan! gp/91234567", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 gn/Tan gp/phone", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_returnsEditStudentCommand() {
        Index index = Index.fromOneBased(2);
        Name name = new Name("Ryan Lim");
        Phone phone = new Phone("92345678");
        Guardian guardian = new Guardian(new Name("Lim Mei Ling"), new Phone("91234567"));
        EditStudentDescriptor descriptor = createDescriptor(name, phone, guardian);

        assertParseSuccess(parser,
                "2 gp/91234567 p/92345678 n/Ryan Lim gn/Lim Mei Ling",
                new EditStudentCommand(index, descriptor));
    }

    @Test
    public void parse_eachFieldSpecified_returnsEditStudentCommand() {
        Index index = Index.fromOneBased(1);

        EditStudentDescriptor nameDescriptor = createDescriptor(new Name("Aiden Tan"), null, null);
        assertParseSuccess(parser, "1 n/Aiden Tan", new EditStudentCommand(index, nameDescriptor));

        EditStudentDescriptor phoneDescriptor = createDescriptor(null, new Phone("92345678"), null);
        assertParseSuccess(parser, "1 p/92345678", new EditStudentCommand(index, phoneDescriptor));

        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        EditStudentDescriptor guardianDescriptor = createDescriptor(null, null, guardian);
        assertParseSuccess(parser, "1 gn/Tan Mei Ling gp/91234567",
                new EditStudentCommand(index, guardianDescriptor));
    }

    private static EditStudentDescriptor createDescriptor(Name name, Phone phone, Guardian guardian) {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(name);
        descriptor.setPhone(phone);
        descriptor.setGuardian(guardian);
        return descriptor;
    }
}
