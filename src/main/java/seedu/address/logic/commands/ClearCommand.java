package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;

/**
 * Clears the address book after explicit confirmation.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String CONFIRMATION_WORD = "confirm";

    public static final String MESSAGE_SUCCESS = "Cleared all students and lessons.";
    public static final String MESSAGE_CONFIRMATION_REQUIRED =
            "Clearing will delete all students, lessons and enrolments, and cannot be undone. "
            + "Type 'clear confirm' to continue.";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Deletes all students and lessons.\n"
            + "Format: " + COMMAND_WORD + " [" + CONFIRMATION_WORD + "]\n"
            + "Run " + COMMAND_WORD + " first to see the warning, then " + COMMAND_WORD + " " + CONFIRMATION_WORD
            + " to delete everything.";

    private final boolean isConfirmed;

    public ClearCommand() {
        this(false);
    }

    private ClearCommand(boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    /** Creates the command that has passed the explicit confirmation step. */
    public static ClearCommand confirmed() {
        return new ClearCommand(true);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (!isConfirmed) {
            return new CommandResult(MESSAGE_CONFIRMATION_REQUIRED);
        }
        model.setAddressBook(new AddressBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof ClearCommand otherClearCommand
                && isConfirmed == otherClearCommand.isConfirmed);
    }
}
