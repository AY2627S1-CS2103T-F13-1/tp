package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditStudentCommand.EditStudentDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentContainsKeywordsPredicate;

/**
 * Contains integration tests and unit tests for {@code EditStudentCommand}.
 */
public class EditStudentCommandTest {
    private static final Student AIDEN = new Student(new Name("Aiden Tan"), null,
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student CHLOE = new Student(new Name("Chloe Tan"), new Phone("88888888"),
            new Guardian(new Name("Tan Mei Ling"), new Phone("91234567")));
    private static final Student RYAN = new Student(new Name("Ryan Lim"), new Phone("93210283"), null);

    private static final Lesson A_MATH = new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
            LocalTime.of(16, 0), LocalTime.of(18, 0));

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getStudentAddressBook(), new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditStudentCommand(null, new EditStudentDescriptor()));
    }

    @Test
    public void constructor_nullDescriptor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditStudentCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditStudentDescriptor descriptor = createDescriptor(new Name("Aiden Lee"), null, null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_modelDoesNotApplyEdit_throwsAssertionError() {
        Model modelThatDoesNotApplyEdits = new ModelManager(getStudentAddressBook(), new UserPrefs()) {
            @Override
            public void setStudent(Student target, Student editedStudent) {
                // Simulates a broken Model implementation so the command's postcondition is exercised.
            }
        };
        EditStudentDescriptor descriptor = createDescriptor(new Name("Aiden Lee"), null, null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);

        assertThrows(AssertionError.class, () -> command.execute(modelThatDoesNotApplyEdits));
    }

    @Test
    public void execute_allFieldsSpecified_success() {
        Name name = new Name("Aiden Lee");
        Phone phone = new Phone("92345678");
        Guardian guardian = new Guardian(new Name("Lee Mei Ling"), new Phone("93456789"));
        EditStudentDescriptor descriptor = createDescriptor(name, phone, guardian);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);
        Student editedStudent = new Student(AIDEN.getId(), name, phone, guardian);
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel.setStudent(AIDEN, editedStudent);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(AIDEN.getId(), model.getAddressBook().getStudentList().get(0).getId());
    }

    @Test
    public void execute_someFieldsSpecified_unspecifiedFieldsRetained() {
        Name updatedName = new Name("Chloe Lee");
        EditStudentDescriptor descriptor = createDescriptor(updatedName, null, null);
        EditStudentCommand command = new EditStudentCommand(INDEX_SECOND_PERSON, descriptor);
        Student editedStudent = new Student(CHLOE.getId(), updatedName,
                CHLOE.getPhone().orElseThrow(), CHLOE.getGuardian().orElseThrow());
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel.setStudent(CHLOE, editedStudent);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearPhone_guardianRetained() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setPhone(null);
        EditStudentCommand command = new EditStudentCommand(INDEX_SECOND_PERSON, descriptor);
        Student editedStudent = new Student(CHLOE.getId(), CHLOE.getName(), null,
                CHLOE.getGuardian().orElseThrow());
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel.setStudent(CHLOE, editedStudent);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearGuardian_phoneRetained() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setGuardian(null);
        EditStudentCommand command = new EditStudentCommand(INDEX_SECOND_PERSON, descriptor);
        Student editedStudent = new Student(CHLOE.getId(), CHLOE.getName(), CHLOE.getPhone().orElseThrow(), null);
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel.setStudent(CHLOE, editedStudent);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearOnlyPhone_throwsCommandException() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setPhone(null);

        assertEditFailure(new EditStudentCommand(Index.fromOneBased(3), descriptor),
                Student.MESSAGE_CONTACT_REQUIRED);
    }

    @Test
    public void execute_clearOnlyGuardian_throwsCommandException() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setGuardian(null);

        assertEditFailure(new EditStudentCommand(INDEX_FIRST_PERSON, descriptor),
                Student.MESSAGE_CONTACT_REQUIRED);
    }

    @Test
    public void execute_clearAllContacts_throwsCommandException() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setPhone(null);
        descriptor.setGuardian(null);

        assertEditFailure(new EditStudentCommand(INDEX_SECOND_PERSON, descriptor),
                Student.MESSAGE_CONTACT_REQUIRED);
    }

    @Test
    public void execute_clearAlreadyAbsentPhone_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setPhone(null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(AIDEN));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_realNameCharacters_success() {
        Name updatedName = new Name("José Anne-Marie");
        Guardian updatedGuardian = new Guardian(new Name("Kumar s/o Ravi"), new Phone("93456789"));
        EditStudentDescriptor descriptor = createDescriptor(updatedName, null, updatedGuardian);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);
        Student editedStudent = new Student(AIDEN.getId(), updatedName, null, updatedGuardian);
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        expectedModel.setStudent(AIDEN, editedStudent);
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(editedStudent));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sameDetails_success() {
        EditStudentDescriptor descriptor = createDescriptor(AIDEN.getName(), null, null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);
        Model expectedModel = new ModelManager(getStudentAddressBook(), new UserPrefs());
        String expectedMessage = String.format(EditStudentCommand.MESSAGE_EDIT_STUDENT_SUCCESS,
                Messages.format(AIDEN));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        EditStudentDescriptor descriptor = createDescriptor(RYAN.getName(), RYAN.getPhone().orElseThrow(), null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);

        assertEditFailure(command, EditStudentCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_duplicateStudentHiddenByFilter_throwsCommandException() {
        model.updateFilteredStudentList(student -> student.getId().equals(AIDEN.getId()));
        EditStudentDescriptor descriptor = createDescriptor(RYAN.getName(), RYAN.getPhone().orElseThrow(), null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);

        assertEditFailure(command, EditStudentCommand.MESSAGE_DUPLICATE_STUDENT);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditStudentDescriptor descriptor = createDescriptor(new Name("Aiden Lee"), null, null);

        assertEditFailure(new EditStudentCommand(outOfBoundsIndex, descriptor),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_editsDisplayedStudentAndReevaluatesFilter() throws Exception {
        model.updateFilteredStudentList(
                new StudentContainsKeywordsPredicate(List.of("Ryan"), List.of()));
        EditStudentDescriptor descriptor = createDescriptor(new Name("Mason Lim"), null, null);

        new EditStudentCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        Student editedStudent = model.getAddressBook().getStudentList().get(2);
        assertEquals(new Name("Mason Lim"), editedStudent.getName());
        assertEquals(RYAN.getId(), editedStudent.getId());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        model.updateFilteredStudentList(student -> student.getId().equals(RYAN.getId()));
        EditStudentDescriptor descriptor = createDescriptor(new Name("Mason Lim"), null, null);

        assertEditFailure(new EditStudentCommand(INDEX_SECOND_PERSON, descriptor),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_enrolledStudent_preservesEnrolmentsAndLessonFilter() throws Exception {
        model.addLesson(A_MATH);
        model.enrolStudent(AIDEN, A_MATH);
        model.filterStudentsByLesson(A_MATH);
        List<Enrolment> enrolmentsBefore = new ArrayList<>(model.getEnrolmentList());
        EditStudentDescriptor descriptor = createDescriptor(new Name("Aiden Lee"), null, null);

        new EditStudentCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        Student editedStudent = model.getFilteredStudentList().get(0);
        assertEquals(AIDEN.getId(), editedStudent.getId());
        assertEquals(enrolmentsBefore, model.getEnrolmentList());
        assertEquals(List.of(A_MATH), model.getAddressBook().getLessonsForStudent(editedStudent));
    }

    @Test
    public void equals() {
        EditStudentDescriptor nameDescriptor = createDescriptor(new Name("Aiden Lee"), null, null);
        EditStudentDescriptor phoneDescriptor = createDescriptor(null, new Phone("92345678"), null);
        EditStudentCommand standardCommand = new EditStudentCommand(INDEX_FIRST_PERSON, nameDescriptor);

        assertTrue(standardCommand.equals(standardCommand));
        assertTrue(standardCommand.equals(new EditStudentCommand(
                INDEX_FIRST_PERSON, new EditStudentDescriptor(nameDescriptor))));
        assertFalse(standardCommand.equals(new EditStudentCommand(INDEX_SECOND_PERSON, nameDescriptor)));
        assertFalse(standardCommand.equals(new EditStudentCommand(INDEX_FIRST_PERSON, phoneDescriptor)));
        assertFalse(standardCommand.equals(new ClearCommand()));
        assertFalse(standardCommand.equals(null));
    }

    @Test
    public void descriptorEquals() {
        Name name = new Name("Aiden Lee");
        Phone phone = new Phone("92345678");
        Guardian guardian = new Guardian(new Name("Lee Mei Ling"), new Phone("93456789"));
        EditStudentDescriptor descriptor = createDescriptor(name, phone, guardian);

        assertTrue(descriptor.equals(descriptor));
        assertTrue(descriptor.equals(new EditStudentDescriptor(descriptor)));
        assertFalse(descriptor.equals(createDescriptor(name, new Phone("87654321"), guardian)));
        assertFalse(descriptor.equals(createDescriptor(name, phone,
                new Guardian(new Name("Lee Mei Ling"), new Phone("87654321")))));

        EditStudentDescriptor phoneRemovalDescriptor = new EditStudentDescriptor();
        phoneRemovalDescriptor.setPhone(null);
        assertFalse(new EditStudentDescriptor().equals(phoneRemovalDescriptor));

        EditStudentDescriptor guardianRemovalDescriptor = new EditStudentDescriptor();
        guardianRemovalDescriptor.setGuardian(null);
        assertFalse(new EditStudentDescriptor().equals(guardianRemovalDescriptor));

        assertFalse(descriptor.equals(1));
        assertFalse(descriptor.equals(null));
    }

    @Test
    public void editFlags() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        assertFalse(descriptor.isAnyFieldEdited());
        assertFalse(descriptor.isPhoneEdited());
        assertFalse(descriptor.isGuardianEdited());

        descriptor.setPhone(null);
        assertTrue(descriptor.isAnyFieldEdited());
        assertTrue(descriptor.isPhoneEdited());

        descriptor.setGuardian(null);
        assertTrue(descriptor.isGuardianEdited());
    }

    @Test
    public void toStringMethod() {
        EditStudentDescriptor descriptor = createDescriptor(new Name("Aiden Lee"), null, null);
        EditStudentCommand command = new EditStudentCommand(INDEX_FIRST_PERSON, descriptor);
        String expected = EditStudentCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", editStudentDescriptor=" + descriptor + "}";

        assertEquals(expected, command.toString());
    }

    private static EditStudentDescriptor createDescriptor(Name name, Phone phone, Guardian guardian) {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        if (name != null) {
            descriptor.setName(name);
        }
        if (phone != null) {
            descriptor.setPhone(phone);
        }
        if (guardian != null) {
            descriptor.setGuardian(guardian);
        }
        return descriptor;
    }

    /**
     * Verifies that an edit fails without changing the source or filtered student lists.
     */
    private void assertEditFailure(EditStudentCommand command, String expectedMessage) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        List<Student> expectedFilteredList = new ArrayList<>(model.getFilteredStudentList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(expectedAddressBook, model.getAddressBook());
        assertEquals(expectedFilteredList, model.getFilteredStudentList());
    }

    /** Returns an address book containing the students used by these tests. */
    private static AddressBook getStudentAddressBook() {
        AddressBook addressBook = new AddressBook();
        for (Student student : List.of(AIDEN, CHLOE, RYAN)) {
            addressBook.addStudent(student);
        }
        return addressBook;
    }
}
