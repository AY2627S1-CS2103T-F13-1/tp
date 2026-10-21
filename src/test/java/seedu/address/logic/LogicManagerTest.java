package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.commands.AddLessonCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteLessonCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.EnrolCommand;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.commands.ListLessonsCommand;
import seedu.address.logic.commands.ListStudentsCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.FindStudentCommandParser;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentContainsKeywordsPredicate;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = DeleteStudentCommand.COMMAND_WORD + " 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListStudentsCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListStudentsCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_addStudentThenFindStudent_success() throws Exception {
        Student aiden = new Student(new Name("Aiden Tan"), null,
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));

        CommandResult addResult = logic.execute(
                AddStudentCommand.COMMAND_WORD + " n/Aiden Tan gn/Tan Mei Ling gp/91234567");
        assertEquals(String.format(AddStudentCommand.MESSAGE_SUCCESS, aiden), addResult.getFeedbackToUser());
        assertEquals(List.of(aiden), logic.getFilteredStudentList());

        CommandResult findResult = logic.execute(FindStudentCommand.COMMAND_WORD + " gn/ling");
        assertEquals(String.format(MESSAGE_STUDENTS_LISTED_OVERVIEW, 1), findResult.getFeedbackToUser());
        assertEquals(List.of(aiden), logic.getFilteredStudentList());
    }

    @Test
    public void execute_addDuplicateStudent_throwsCommandException() throws Exception {
        logic.execute(AddStudentCommand.COMMAND_WORD + " n/Aiden Tan gn/Tan Mei Ling gp/91234567");
        assertCommandException(AddStudentCommand.COMMAND_WORD + " n/aiden  tan p/91234567",
                AddStudentCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_findStudent_filtersStudentList() throws Exception {
        Student aiden = new Student(new Name("Aiden Tan"), null,
                new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
        Student ryan = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);
        model.addStudent(aiden);
        model.addStudent(ryan);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(
                new StudentContainsKeywordsPredicate(List.of("rya"), List.of()));
        assertCommandSuccess(FindStudentCommand.COMMAND_WORD + " n/rya",
                String.format(MESSAGE_STUDENTS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(ryan), logic.getFilteredStudentList());

        expectedModel.updateFilteredStudentList(
                new StudentContainsKeywordsPredicate(List.of(), List.of("lin")));
        assertCommandSuccess(FindStudentCommand.COMMAND_WORD + " gn/lin",
                String.format(MESSAGE_STUDENTS_LISTED_OVERVIEW, 1), expectedModel);
        assertEquals(List.of(aiden), logic.getFilteredStudentList());

        // aiden is found through the guardian's phone, ryan through their own
        expectedModel.updateFilteredStudentList(
                new StudentContainsKeywordsPredicate(List.of(), List.of(), List.of("4567", "0283")));
        assertCommandSuccess(FindStudentCommand.COMMAND_WORD + " p/4567 0283",
                String.format(MESSAGE_STUDENTS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(aiden, ryan), logic.getFilteredStudentList());
    }

    @Test
    public void execute_findStudentInvalidInput_throwsParseException() {
        assertParseException(FindStudentCommand.COMMAND_WORD,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE));
        assertParseException(FindStudentCommand.COMMAND_WORD + " n/",
                FindStudentCommandParser.MESSAGE_EMPTY_KEYWORDS);
        assertParseException(FindStudentCommand.COMMAND_WORD + " n/Aiden gp/91234567",
                String.format(FindStudentCommandParser.MESSAGE_INVALID_KEYWORD, "gp/91234567"));
        assertParseException(FindStudentCommand.COMMAND_WORD + " p/91a",
                String.format(FindStudentCommandParser.MESSAGE_INVALID_PHONE_KEYWORD, "91a"));
    }

    @Test
    public void execute_addLesson_addsAndSavesLesson() throws Exception {
        CommandResult result = logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/mon st/1600 et/1800");

        assertEquals("New lesson added: Sec 3 A-Math (Mon 1600-1800)", result.getFeedbackToUser());
        assertEquals(1, logic.getFilteredLessonList().size());
        Lesson added = logic.getFilteredLessonList().getFirst();
        assertEquals("Sec 3 A-Math", added.getName());
        assertEquals(DayOfWeek.MONDAY, added.getDay());
        assertEquals(LocalTime.of(16, 0), added.getStartTime());
        assertEquals(LocalTime.of(18, 0), added.getEndTime());

        // the saved file holds the same lesson, ID included
        assertEquals(List.of(added), readSavedAddressBook().getLessonList());
    }

    @Test
    public void execute_addLessonPastMidnight_addsAndSavesLesson() throws Exception {
        CommandResult result = logic.execute(AddLessonCommand.COMMAND_WORD + " n/Night Class d/fri st/2200 et/0100");

        assertEquals("New lesson added: Night Class (Fri 2200-0100)", result.getFeedbackToUser());
        Lesson added = logic.getFilteredLessonList().getFirst();
        assertEquals(DayOfWeek.FRIDAY, added.getDay());
        assertEquals(LocalTime.of(22, 0), added.getStartTime());
        assertEquals(LocalTime.of(1, 0), added.getEndTime());

        // the saved file must load again, or the lesson would be lost at the next launch
        assertEquals(List.of(added), readSavedAddressBook().getLessonList());
    }

    @Test
    public void execute_addDuplicateLesson_throwsCommandException() throws Exception {
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/mon st/1600 et/1800");

        assertCommandException(AddLessonCommand.COMMAND_WORD + " n/sec 3  a-math d/MON st/1600 et/1800",
                AddLessonCommand.MESSAGE_DUPLICATE_LESSON);
        assertEquals(1, logic.getFilteredLessonList().size());
        assertEquals(1, readSavedAddressBook().getLessonList().size());
    }

    @Test
    public void execute_addClashingLesson_warnsAndSavesLesson() throws Exception {
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/mon st/1600 et/1800");

        CommandResult result = logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 3 E-Math d/mon st/1700 et/1900");

        assertEquals("New lesson added: Sec 3 E-Math (Mon 1700-1900)\n"
                + "Warning: clashes with Sec 3 A-Math (Mon 1600-1800)", result.getFeedbackToUser());
        assertEquals(2, logic.getFilteredLessonList().size());
        assertEquals(logic.getFilteredLessonList(), readSavedAddressBook().getLessonList());
    }

    @Test
    public void execute_addLessonInvalidInput_throwsParseException() {
        assertParseException(AddLessonCommand.COMMAND_WORD,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddLessonCommand.MESSAGE_USAGE));
        assertParseException(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/mon st/1600 et/1600",
                Lesson.MESSAGE_TIME_CONSTRAINTS);
        assertParseException(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/monday st/1600 et/1800",
                ParserUtil.MESSAGE_INVALID_DAY);
        assertTrue(logic.getFilteredLessonList().isEmpty());
    }

    @Test
    public void execute_addLessonsThenDeleteLesson_remainingLessonSaved() throws Exception {
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 3 A-Math d/mon st/1600 et/1800");
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Sec 4 Physics d/wed st/1900 et/2030");

        CommandResult result = logic.execute(DeleteLessonCommand.COMMAND_WORD + " 1");

        assertEquals("Deleted Lesson: Sec 3 A-Math (Mon 1600-1800)", result.getFeedbackToUser());
        assertEquals(1, logic.getFilteredLessonList().size());
        assertEquals("Sec 4 Physics (Wed 1900-2030)", logic.getFilteredLessonList().getFirst().toString());
        assertEquals(logic.getFilteredLessonList(), readSavedAddressBook().getLessonList());
    }

    @Test
    public void execute_deleteLessonWithStudent_removesEnrolmentAndSaves() throws Exception {
        Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
        Lesson math = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
        model.addStudent(aiden);
        model.addLesson(math);
        model.enrolStudent(aiden, math);

        logic.execute(DeleteLessonCommand.COMMAND_WORD + " 1");

        assertTrue(logic.getFilteredLessonList().isEmpty());
        assertTrue(logic.getEnrolmentList().isEmpty());
        assertEquals(List.of(aiden), logic.getFilteredStudentList());

        // a saved enrolment without its lesson would make the file fail to load at the next launch
        ReadOnlyAddressBook saved = readSavedAddressBook();
        assertTrue(saved.getLessonList().isEmpty());
        assertTrue(saved.getEnrolmentList().isEmpty());
        assertEquals(List.of(aiden), saved.getStudentList());
    }

    @Test
    public void execute_deleteLessonInvalidIndex_throwsException() {
        assertCommandException(DeleteLessonCommand.COMMAND_WORD + " 1", MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        assertParseException(DeleteLessonCommand.COMMAND_WORD + " 0",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteLessonCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_filterAndDelete_resetsStudentFilter() throws Exception {
        logic.execute(AddStudentCommand.COMMAND_WORD + " n/Aiden Tan p/91234567");
        logic.execute(AddStudentCommand.COMMAND_WORD + " n/Ryan Lim p/92345678");
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Math d/mon st/1600 et/1800");
        logic.execute(AddLessonCommand.COMMAND_WORD + " n/Physics d/wed st/1900 et/2030");
        logic.execute(EnrolCommand.COMMAND_WORD + " s/1 l/1");

        CommandResult lessonResult = logic.execute(ListLessonsCommand.COMMAND_WORD + " d/mon");
        assertEquals(String.format(ListLessonsCommand.MESSAGE_SUCCESS_BY_DAY, "Monday"),
                lessonResult.getFeedbackToUser());
        assertEquals(List.of("Math"), logic.getFilteredLessonList().stream().map(Lesson::getName).toList());

        Lesson displayedLesson = logic.getFilteredLessonList().getFirst();
        CommandResult studentResult = logic.execute(ListStudentsCommand.COMMAND_WORD + " l/1");
        assertEquals("Listed students enrolled in " + displayedLesson, studentResult.getFeedbackToUser());
        assertEquals(List.of("Aiden Tan"), logic.getFilteredStudentList().stream()
                .map(student -> student.getName().fullName).toList());

        logic.execute(DeleteLessonCommand.COMMAND_WORD + " 1");
        assertEquals(List.of("Aiden Tan", "Ryan Lim"), logic.getFilteredStudentList().stream()
                .map(student -> student.getName().fullName).toList());
        assertTrue(logic.getFilteredLessonList().isEmpty());
        assertEquals(List.of("Physics"), readSavedAddressBook().getLessonList().stream()
                .map(Lesson::getName).toList());
    }

    @Test
    public void execute_deleteStudentInLesson_removesEnrolmentAndSaves() throws Exception {
        Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
        Lesson math = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
        model.addStudent(aiden);
        model.addLesson(math);
        model.enrolStudent(aiden, math);

        logic.execute(DeleteStudentCommand.COMMAND_WORD + " 1");

        assertTrue(logic.getFilteredStudentList().isEmpty());
        assertTrue(logic.getEnrolmentList().isEmpty());
        assertEquals(List.of(math), logic.getFilteredLessonList());

        // a saved enrolment without its student would make the file fail to load at the next launch
        ReadOnlyAddressBook saved = readSavedAddressBook();
        assertTrue(saved.getStudentList().isEmpty());
        assertTrue(saved.getEnrolmentList().isEmpty());
        assertEquals(List.of(math), saved.getLessonList());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Reads back the address book file written by {@code logic} after the last successful command.
     */
    private ReadOnlyAddressBook readSavedAddressBook() throws DataLoadingException {
        return new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"))
                .readAddressBook().orElseThrow();
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add student command
        String addCommand = AddStudentCommand.COMMAND_WORD + " n/Aiden Tan p/91234567";
        Student expectedStudent = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedStudent);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
