package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListStudentsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_addStudent() throws Exception {
        Student student = new Student(new Name("Aiden Tan"), null,
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
        AddStudentCommand command = (AddStudentCommand) parser.parseCommand(
                AddStudentCommand.COMMAND_WORD + " n/Aiden Tan gn/Tan Mei Ling gp/91234567");
        assertEquals(new AddStudentCommand(student), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_deleteStudent() throws Exception {
        DeleteStudentCommand command = (DeleteStudentCommand) parser.parseCommand(
                DeleteStudentCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteStudentCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_deleteStudentWithoutIndex_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudentCommand.MESSAGE_USAGE), () ->
                        parser.parseCommand(DeleteStudentCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_findStudent() throws Exception {
        FindStudentCommand command = (FindStudentCommand) parser.parseCommand(
                FindStudentCommand.COMMAND_WORD + " n/Aiden Chloe gn/Tan");
        assertEquals(new FindStudentCommand(
                new StudentNameContainsKeywordsPredicate(List.of("Aiden", "Chloe"), List.of("Tan"))), command);

        command = (FindStudentCommand) parser.parseCommand(FindStudentCommand.COMMAND_WORD + " gn/Tan");
        assertEquals(new FindStudentCommand(new StudentNameContainsKeywordsPredicate(List.of(), List.of("Tan"))),
                command);
    }

    @Test
    public void parseCommand_findStudentWithoutKeywords_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE), () ->
                        parser.parseCommand(FindStudentCommand.COMMAND_WORD));
    }

    @Test
    public void parseCommand_findStudentWrongCase_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("FINDSTUDENT n/Aiden"));
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_listStudents() throws Exception {
        assertEquals(new ListStudentsCommand(), parser.parseCommand(ListStudentsCommand.COMMAND_WORD));
        assertEquals(new ListStudentsCommand(), parser.parseCommand(ListStudentsCommand.COMMAND_WORD + "   "));
    }

    @Test
    public void parseCommand_listStudentsWithExtraText_throwsParseException() {
        assertThrows(ParseException.class,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListStudentsCommand.MESSAGE_USAGE), () ->
                        parser.parseCommand(ListStudentsCommand.COMMAND_WORD + " 2"));
    }

    @Test
    public void parseCommand_legacyPersonCommands_throwsParseException() {
        List<String> legacyCommands = List.of(
                "add n/Alex p/91234567 e/alex@example.com a/Example Street",
                "delete 1",
                "edit 1 n/Alex",
                "find Alex",
                "list");

        for (String command : legacyCommands) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(command));
        }
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
