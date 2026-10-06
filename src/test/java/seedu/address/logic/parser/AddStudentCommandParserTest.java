package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

public class AddStudentCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentCommand.MESSAGE_USAGE);

    private final AddStudentCommandParser parser = new AddStudentCommandParser();

    @Test
    public void parse_missingName_throwsParseException() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " p/93210283", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " Aiden n/Tan p/93210283", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " N/Aiden p/93210283", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingContact_throwsParseException() {
        assertParseFailure(parser, " n/Ryan Lim", Student.MESSAGE_CONTACT_REQUIRED);
    }

    @Test
    public void parse_incompleteGuardian_throwsParseException() {
        assertParseFailure(parser, " n/Aiden Tan gn/Tan Mei Ling",
                AddStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
        assertParseFailure(parser, " n/Aiden Tan gp/91234567",
                AddStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        assertParseFailure(parser, " n/Aiden n/Ryan p/93210283",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, " n/Aiden p/93210283 p/92345678",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
        assertParseFailure(parser, " n/Aiden gn/Tan gn/Lim gp/91234567",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_NAME));
        assertParseFailure(parser, " n/Aiden gn/Tan gp/91234567 gp/92345678",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_GUARDIAN_PHONE));
    }

    @Test
    public void parse_invalidValues_throwsParseException() {
        assertParseFailure(parser, " n/Aiden! p/93210283", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden p/phone", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden gn/Tan! gp/91234567", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden gn/Tan gp/phone", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_studentPhoneOnly_returnsAddStudentCommand() {
        Student student = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);
        assertParseSuccess(parser, " n/Ryan Lim p/93210283", new AddStudentCommand(student));
        assertParseSuccess(parser, "  p/ 93210283  n/ Ryan Lim  ", new AddStudentCommand(student));
    }

    @Test
    public void parse_guardianOnly_returnsAddStudentCommand() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), null, guardian);
        assertParseSuccess(parser, " n/Aiden Tan gn/Tan Mei Ling gp/91234567",
                new AddStudentCommand(student));
    }

    @Test
    public void parse_studentAndGuardianPhones_returnsAddStudentCommand() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"), guardian);
        assertParseSuccess(parser, " gp/91234567 p/92345678 n/Aiden Tan gn/Tan Mei Ling",
                new AddStudentCommand(student));
    }
}
