package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import seedu.address.model.Model;

/**
 * Lists all students in the address book to the user, clearing any filter on the students panel.
 */
public class ListStudentsCommand extends Command {

    public static final String COMMAND_WORD = "liststudents";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Lists all students.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all students.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListStudentsCommand;
    }

    @Override
    public int hashCode() {
        return ListStudentsCommand.class.hashCode();
    }
}
