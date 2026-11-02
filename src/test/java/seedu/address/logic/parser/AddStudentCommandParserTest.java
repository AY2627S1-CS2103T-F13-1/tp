package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.StudentTestUtil.assertStudentDetailsEqual;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.model.ModelManager;
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
    }

    @Test
    public void parse_unknownPrefix_throwsParseException() {
        // prefixes are case-sensitive, so the error names the prefix that was not recognised
        assertParseFailure(parser, " N/Aiden p/93210283", unknownPrefix("N/"));
        assertParseFailure(parser, " n/Aiden p/93210283 x/abc", unknownPrefix("x/"));
        // l/ and s/ belong to other commands, so a tutor may type them here by mistake
        assertParseFailure(parser, " n/Aiden p/93210283 l/1", unknownPrefix("l/"));
        assertParseFailure(parser, " n/Aiden s/1 p/93210283", unknownPrefix("s/"));
    }

    @Test
    public void parse_nameConnectors_returnsAddStudentCommand() {
        Student student = new Student(new Name("Kumar s/o Ravi"), new Phone("93210283"),
                new Guardian(new Name("Siti d/o Ahmad"), new Phone("91234567")));

        assertParsedStudent(" n/Kumar s/o Ravi p/93210283 gn/Siti d/o Ahmad gp/91234567", student);
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
        assertParseFailure(parser, " n/Aiden Tan p/93210283 gn/Tan Mei Ling",
                AddStudentCommandParser.MESSAGE_INCOMPLETE_GUARDIAN);
        assertParseFailure(parser, " n/Aiden Tan p/93210283 gp/91234567",
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
        assertParseFailure(parser, " n/Aiden! p/93210283", ParserUtil.MESSAGE_STUDENT_NAME_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden2 p/93210283", ParserUtil.MESSAGE_STUDENT_NAME_CONSTRAINTS);
        assertParseFailure(parser, " n/123 p/93210283", ParserUtil.MESSAGE_STUDENT_NAME_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden p/phone", Phone.MESSAGE_STUDENT_PHONE_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden gn/Tan! gp/91234567", ParserUtil.MESSAGE_STUDENT_NAME_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden gn/Parent2 gp/91234567",
                ParserUtil.MESSAGE_STUDENT_NAME_CONSTRAINTS);
        assertParseFailure(parser, " n/Aiden gn/Tan gp/phone", Phone.MESSAGE_STUDENT_PHONE_CONSTRAINTS);
    }

    @Test
    public void parse_studentPhoneOnly_returnsAddStudentCommand() {
        Student student = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);
        assertParsedStudent(" n/Ryan Lim p/93210283", student);
        assertParsedStudent("  p/ 93210283  n/ Ryan Lim  ", student);
    }

    @Test
    public void parse_guardianOnly_returnsAddStudentCommand() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), null, guardian);
        assertParsedStudent(" n/Aiden Tan gn/Tan Mei Ling gp/91234567",
                student);
    }

    @Test
    public void parse_studentAndGuardianPhones_returnsAddStudentCommand() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"), guardian);
        assertParsedStudent(" gp/91234567 p/92345678 n/Aiden Tan gn/Tan Mei Ling",
                student);
    }

    @Test
    public void parse_realNameCharacters_returnsAddStudentCommand() {
        Guardian guardian = new Guardian(new Name("O’Connor"), new Phone("91234567"));
        Student student = new Student(new Name("Jose Anne-Marie"), new Phone("92345678"), guardian);

        assertParsedStudent(" n/Jose Anne-Marie p/92345678 gn/O’Connor gp/91234567", student);
    }

    @Test
    public void parse_commonNamePunctuation_returnsAddStudentCommand() {
        assertParsedStudent(" n/Ng Ah Kow @ Ng Kok Wee p/81000003",
                new Student(new Name("Ng Ah Kow @ Ng Kok Wee"), new Phone("81000003"), null));
        assertParsedStudent(" n/S. Ramasamy p/81000004",
                new Student(new Name("S. Ramasamy"), new Phone("81000004"), null));
        assertParsedStudent(" n/Tan Wei Ming (David) p/81000005",
                new Student(new Name("Tan Wei Ming (David)"), new Phone("81000005"), null));
        assertParsedStudent(" n/Tan Ah Kow Jr. p/81000006",
                new Student(new Name("Tan Ah Kow Jr."), new Phone("81000006"), null));
    }

    @Test
    public void parse_formattedPhones_returnsAddStudentCommandWithNormalizedPhones() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"), guardian);

        assertParsedStudent(" n/Aiden Tan p/9234 5678 gn/Tan Mei Ling gp/+65 9123-4567", student);
    }

    @Test
    public void parse_phonesWithRepeatedSeparators_returnsAddStudentCommandWithNormalizedPhones() {
        Guardian guardian = new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"));
        Student student = new Student(new Name("Aiden Tan"), new Phone("92345678"), guardian);

        assertParsedStudent(" n/Aiden Tan p/9234  5678 gn/Tan Mei Ling gp/9123 - 4567", student);
    }

    private void assertParsedStudent(String input, Student expected) {
        try {
            ModelManager model = new ModelManager();
            parser.parse(input).execute(model);
            assertEquals(1, model.getFilteredStudentList().size());
            assertStudentDetailsEqual(expected, model.getFilteredStudentList().getFirst());
        } catch (Exception e) {
            throw new AssertionError("Expected a valid add student command", e);
        }
    }

    private static String unknownPrefix(String prefix) {
        return Messages.getErrorMessageForUnknownPrefix(AddStudentCommand.COMMAND_WORD, prefix,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_GUARDIAN_NAME, PREFIX_GUARDIAN_PHONE);
    }
}
