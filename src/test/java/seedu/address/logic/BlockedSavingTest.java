package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.exceptions.CommandException;
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

public class BlockedSavingTest {
    @TempDir
    public Path folder;

    @Test
    public void blockedStorage_readOnlyCommandsWork_mutationsLeaveModelAndFileUnchanged() throws Exception {
        Path file = folder.resolve("invalid.json");
        Files.writeString(file, "{");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        ModelManager model = new ModelManager();
        model.addStudent(new Student(new Name("Aiden"), new Phone("91234567"), null));
        model.addLesson(new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0)));
        LogicManager logic = logic(model, storage);
        for (String command : List.of("liststudents", "listlessons", "findstudent n/Aiden",
                "listlessons d/mon", "liststudents l/1")) {
            logic.execute(command);
        }
        assertTrue(logic.execute("help").isShowHelp());
        assertTrue(logic.execute("exit").isExit());
        logic.execute("liststudents");
        AddressBook before = new AddressBook(model.getAddressBook());
        var displayedStudents = List.copyOf(model.getFilteredStudentList());
        var displayedLessons = List.copyOf(model.getFilteredLessonList());
        for (String command : List.of("addstudent n/Ben p/92345678", "deletestudent 1",
                "addlesson n/Science d/tue st/1600 et/1800", "deletelesson 1", "enrol s/1 l/1",
                "clear confirm")) {
            CommandException error = assertThrows(CommandException.class, () -> logic.execute(command));
            assertTrue(error.getMessage().contains("Saving is disabled"));
            assertEquals(before, model.getAddressBook());
            assertEquals(displayedStudents, model.getFilteredStudentList());
            assertEquals(displayedLessons, model.getFilteredLessonList());
        }
        assertEquals("{", Files.readString(file));
    }

    @Test
    public void readOnlyCommands_neverAttemptSave_evenWhenWritesWouldFail() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("data.json")) {
            @Override
            public void checkSaveAllowed() throws IOException {
                throw new AssertionError("Read-only commands must not check saving");
            }

            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                throw new AssertionError("Read-only commands must not save");
            }
        };
        LogicManager logic = logic(new ModelManager(), storage);
        for (String command : List.of("liststudents", "listlessons", "findstudent n/Aiden", "help", "exit")) {
            logic.execute(command);
        }
    }

    @Test
    public void allowedMutation_stillSaves() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("data.json"));
        ModelManager model = new ModelManager();
        LogicManager logic = logic(model, storage);
        logic.execute("addstudent n/Aiden p/91234567");
        assertEquals(model.getAddressBook(), new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void clearUnconfirmed_doesNotClearOrAttemptWrite() throws Exception {
        JsonAddressBookStorage storage = new JsonAddressBookStorage(folder.resolve("data.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook data) throws IOException {
                throw new AssertionError("Unchanged data must not be saved");
            }
        };
        ModelManager model = new ModelManager();
        model.addStudent(new Student(new Name("Aiden"), new Phone("91234567"), null));
        logic(model, storage).execute("clear");
        assertEquals(1, model.getFilteredStudentList().size());
    }

    private LogicManager logic(ModelManager model, JsonAddressBookStorage storage) {
        return new LogicManager(model,
                new StorageManager(storage, new JsonUserPrefsStorage(folder.resolve("prefs.json"))));
    }
}
