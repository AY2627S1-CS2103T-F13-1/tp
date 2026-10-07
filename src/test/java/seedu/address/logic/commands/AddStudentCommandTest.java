package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

public class AddStudentCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    @Test
    public void constructor_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddStudentCommand(null));
    }

    @Test
    public void execute_studentAcceptedByModel_addSuccessful() throws Exception {
        Model model = new ModelManager();

        CommandResult result = new AddStudentCommand(AIDEN).execute(model);

        assertEquals(String.format(AddStudentCommand.MESSAGE_SUCCESS, AIDEN), result.getFeedbackToUser());
        assertEquals(List.of(AIDEN), model.getAddressBook().getStudentList());
    }

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        Model model = new ModelManager();
        model.addStudent(AIDEN);
        Student duplicate = new Student(new Name("aiden  tan"), new Phone("91234567"), null);

        assertThrows(CommandException.class, AddStudentCommand.MESSAGE_DUPLICATE_STUDENT, () ->
                new AddStudentCommand(duplicate).execute(model));
        assertEquals(List.of(AIDEN), model.getAddressBook().getStudentList());
    }

    @Test
    public void equals() {
        AddStudentCommand addAidenCommand = new AddStudentCommand(AIDEN);
        AddStudentCommand addRyanCommand = new AddStudentCommand(RYAN);

        assertTrue(addAidenCommand.equals(addAidenCommand));
        assertTrue(addAidenCommand.equals(new AddStudentCommand(AIDEN)));
        assertFalse(addAidenCommand.equals(addRyanCommand));
        assertFalse(addAidenCommand.equals(1));
        assertFalse(addAidenCommand.equals(null));
    }

    @Test
    public void toStringMethod() {
        AddStudentCommand command = new AddStudentCommand(AIDEN);
        String expected = AddStudentCommand.class.getCanonicalName() + "{toAdd=" + AIDEN + "}";
        assertEquals(expected, command.toString());
    }
}
