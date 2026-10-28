package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;

/**
 * Edits the details of an existing student in the address book.
 */
public class EditStudentCommand extends Command {

    public static final String COMMAND_WORD = "editstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the student identified by the index number "
            + "used in the displayed student list. Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_GUARDIAN_NAME + "GUARDIAN_NAME "
            + PREFIX_GUARDIAN_PHONE + "GUARDIAN_PHONE]\n"
            + "Leave " + PREFIX_PHONE + " blank to remove the student's phone, or leave both "
            + PREFIX_GUARDIAN_NAME + " and " + PREFIX_GUARDIAN_PHONE + " blank to remove the guardian.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "92345678";

    public static final String MESSAGE_EDIT_STUDENT_SUCCESS = "Edited student: %1$s";
    public static final String MESSAGE_IDENTICAL_CHANGES = "The incoming changes are identical to the current student "
            + "details. Change at least one field and try again.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "This student already exists";

    private static final Logger logger = LogsCenter.getLogger(EditStudentCommand.class);

    private final Index index;
    private final EditStudentDescriptor editStudentDescriptor;

    /**
     * Creates an {@code EditStudentCommand} to edit the student at the specified {@code index}.
     */
    public EditStudentCommand(Index index, EditStudentDescriptor editStudentDescriptor) {
        requireNonNull(index);
        requireNonNull(editStudentDescriptor);

        this.index = index;
        this.editStudentDescriptor = new EditStudentDescriptor(editStudentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Student> lastShownList = model.getFilteredStudentList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToEdit = lastShownList.get(index.getZeroBased());
        Student editedStudent;
        try {
            editedStudent = createEditedStudent(studentToEdit, editStudentDescriptor);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

        if (studentToEdit.equals(editedStudent)) {
            logger.fine("Rejected unchanged student edit for ID: " + studentToEdit.getId());
            throw new CommandException(MESSAGE_IDENTICAL_CHANGES);
        }

        boolean hasDuplicate = model.getAddressBook().getStudentList().stream()
                .filter(student -> !student.getId().equals(studentToEdit.getId()))
                .anyMatch(editedStudent::isSameStudent);
        if (hasDuplicate) {
            logger.fine("Rejected duplicate student edit for ID: " + studentToEdit.getId());
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        StudentPhoneConflictValidator.validate(editedStudent, model.getAddressBook());

        model.setStudent(studentToEdit, editedStudent);
        Student storedStudent = model.getAddressBook().getStudentList().stream()
                .filter(student -> student.getId().equals(editedStudent.getId()))
                .findFirst()
                .orElse(null);
        assert editedStudent.equals(storedStudent) : "Editing a student should update the address book";
        logger.info("Edited student with ID: " + editedStudent.getId());
        return new CommandResult(String.format(MESSAGE_EDIT_STUDENT_SUCCESS, Messages.format(editedStudent)));
    }

    /**
     * Creates a student with the original ID and the requested replacement details.
     */
    private static Student createEditedStudent(Student studentToEdit,
            EditStudentDescriptor editStudentDescriptor) {
        requireNonNull(studentToEdit);

        Name updatedName = editStudentDescriptor.getName().orElse(studentToEdit.getName());
        Phone updatedPhone = editStudentDescriptor.isPhoneEdited()
                ? editStudentDescriptor.getPhone().orElse(null)
                : studentToEdit.getPhone().orElse(null);
        Guardian updatedGuardian = editStudentDescriptor.isGuardianEdited()
                ? editStudentDescriptor.getGuardian().orElse(null)
                : studentToEdit.getGuardian().orElse(null);

        return new Student(studentToEdit.getId(), updatedName, updatedPhone, updatedGuardian);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditStudentCommand otherEditStudentCommand)) {
            return false;
        }

        return index.equals(otherEditStudentCommand.index)
                && editStudentDescriptor.equals(otherEditStudentCommand.editStudentDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editStudentDescriptor", editStudentDescriptor)
                .toString();
    }

    /**
     * Stores the replacement details for a student. Optional contact fields can be set to {@code null} to remove them.
     */
    public static class EditStudentDescriptor {
        private Name name;
        private Phone phone;
        private Guardian guardian;
        private boolean isPhoneEdited;
        private boolean isGuardianEdited;

        public EditStudentDescriptor() {}

        /**
         * Creates a defensive copy of the specified descriptor.
         */
        public EditStudentDescriptor(EditStudentDescriptor toCopy) {
            name = toCopy.name;
            phone = toCopy.phone;
            guardian = toCopy.guardian;
            isPhoneEdited = toCopy.isPhoneEdited;
            isGuardianEdited = toCopy.isGuardianEdited;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return name != null || isPhoneEdited || isGuardianEdited;
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
            isPhoneEdited = true;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        /** Returns true if the student's phone should be replaced or removed. */
        public boolean isPhoneEdited() {
            return isPhoneEdited;
        }

        public void setGuardian(Guardian guardian) {
            this.guardian = guardian;
            isGuardianEdited = true;
        }

        public Optional<Guardian> getGuardian() {
            return Optional.ofNullable(guardian);
        }

        /** Returns true if the student's guardian should be replaced or removed. */
        public boolean isGuardianEdited() {
            return isGuardianEdited;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStudentDescriptor otherEditStudentDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditStudentDescriptor.name)
                    && Objects.equals(phone, otherEditStudentDescriptor.phone)
                    && Objects.equals(guardian, otherEditStudentDescriptor.guardian)
                    && isPhoneEdited == otherEditStudentDescriptor.isPhoneEdited
                    && isGuardianEdited == otherEditStudentDescriptor.isGuardianEdited;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("guardian", guardian)
                    .add("isPhoneEdited", isPhoneEdited)
                    .add("isGuardianEdited", isGuardianEdited)
                    .toString();
        }
    }
}
