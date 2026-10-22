package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class SafetyRegressionTest {
    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private final Student alice = new Student(new Name("Alice"), new Phone("91234567"), null);
    private final Student bob = new Student(new Name("Bob"), new Phone("92345678"), null);
    private final Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));

    private Logic failingLogic() {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("data.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                throw new IOException("Simulated full disk");
            }
        };
        return new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
    }

    private void seed() {
        model.addStudent(alice);
        model.addStudent(bob);
        model.addLesson(lesson);
        model.enrolStudent(alice, lesson);
    }

    @Test
    public void execute_failedDeleteOrClear_restoresIdsLinksAndLessonFilter() {
        seed();
        model.filterStudentsByLesson(lesson);
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String command : List.of("deletestudent 1", "clear")) {
            assertThrows(CommandException.class, () -> failingLogic().execute(command));
            assertEquals(before, model.getAddressBook());
            assertEquals(alice.getId(), model.getFilteredStudentList().getFirst().getId());
            assertEquals(List.of(alice), model.getFilteredStudentList());
            assertEquals(List.of(lesson), model.getFilteredLessonList());
        }
        // The restored lesson filter must remain live, not just restore a snapshot of rows.
        model.enrolStudent(bob, lesson);
        assertEquals(List.of(alice, bob), model.getFilteredStudentList());
    }

    @Test
    public void execute_failedAdd_restoresSearchFilter() {
        seed();
        model.updateFilteredStudentList(student -> student.equals(bob));
        assertThrows(CommandException.class, () ->
                failingLogic().execute("addstudent n/Charlie p/93456789"));
        assertEquals(List.of(alice, bob), model.getAddressBook().getStudentList());
        assertEquals(List.of(bob), model.getFilteredStudentList());
    }

    @Test
    public void execute_failedEnrol_restoresLinksAndAllowsRetry() {
        seed();
        Logic logic = failingLogic();
        for (int attempt = 0; attempt < 2; attempt++) {
            CommandException error = assertThrows(CommandException.class, () ->
                    logic.execute("enrol s/2 l/1"));
            assertEquals(String.format(LogicManager.FILE_OPS_ERROR_FORMAT, "Simulated full disk"), error.getMessage());
            assertEquals(List.of(alice), model.getAddressBook().getStudentsInLesson(lesson));
        }
    }

    @Test
    public void execute_queries_doNotAttemptSave() throws Exception {
        seed();
        Logic logic = failingLogic();
        logic.execute("liststudents");
        logic.execute("liststudents l/1");
        logic.execute("findstudent n/Alice");
        logic.execute("help");
        logic.execute("exit");
    }

    @Test
    public void execute_extremeIndices_rejectsWithoutMutatingData() {
        seed();
        AddressBook before = new AddressBook(model.getAddressBook());
        Logic logic = failingLogic();
        for (String index : List.of("2147483648", "-2147483648", "-2147483649", "0", "+1",
                "1.0", "1e9", "9".repeat(100000))) {
            for (String command : List.of("deletestudent " + index, "liststudents l/" + index,
                    "enrol s/" + index + " l/1", "enrol s/1 l/" + index)) {
                assertThrows(ParseException.class, () -> logic.execute(command));
                assertEquals(before, model.getAddressBook());
            }
        }
        for (String command : List.of("deletestudent 2147483647", "liststudents l/2147483647",
                "enrol s/2147483647 l/1", "enrol s/1 l/2147483647")) {
            assertThrows(CommandException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
        }
    }
}
