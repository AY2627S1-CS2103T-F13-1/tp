package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;

/**
 * Adds a student to the address book.
 */
public class AddStudentCommand extends Command {

    public static final String COMMAND_WORD = "addstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to the address book. "
            + "A student must have either their own phone number or a guardian with a phone number.\n"
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_GUARDIAN_NAME + "GUARDIAN_NAME "
            + PREFIX_GUARDIAN_PHONE + "GUARDIAN_PHONE]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Aiden Tan "
            + PREFIX_GUARDIAN_NAME + "Tan Mei Ling "
            + PREFIX_GUARDIAN_PHONE + "91234567";

    public static final String MESSAGE_SUCCESS = "New student added: %1$s";
    public static final String MESSAGE_DUPLICATE_STUDENT = "This student already exists";

    private final Student toAdd;

    /**
     * Creates an {@code AddStudentCommand} to add the specified {@code Student}.
     */
    public AddStudentCommand(Student student) {
        toAdd = requireNonNull(student);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.hasStudent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        model.addStudent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddStudentCommand otherAddStudentCommand)) {
            return false;
        }

        return toAdd.equals(otherAddStudentCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
