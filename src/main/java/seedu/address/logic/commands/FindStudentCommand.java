package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.student.StudentContainsKeywordsPredicate;

/**
 * Finds and lists all students whose name, guardian's name and phone numbers match the given keywords.
 * Keyword matching is case insensitive and supports partial names and partial phone numbers.
 */
public class FindStudentCommand extends Command {

    public static final String COMMAND_WORD = "findstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds students matching all given prefixes (case-insensitive, partial matches).\n"
            + "Parameters: [" + PREFIX_NAME + "NAME_KEYWORDS...] "
            + "[" + PREFIX_GUARDIAN_NAME + "GUARDIAN_NAME_KEYWORDS...] "
            + "[" + PREFIX_PHONE + "PHONE_DIGITS...] (at least one)\n"
            + PREFIX_PHONE + " searches the student's and the guardian's phone numbers.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "aid " + PREFIX_GUARDIAN_NAME + "tan "
            + PREFIX_PHONE + "4567";

    private static final Logger logger = LogsCenter.getLogger(FindStudentCommand.class);

    private final StudentNameContainsKeywordsPredicate predicate;

    /**
     * Creates a command that filters the student list with {@code predicate}.
     */
    public FindStudentCommand(StudentContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicate);
        int matchCount = model.getFilteredStudentList().size();
        logger.fine("Found " + matchCount + " student(s) matching " + predicate);
        return new CommandResult(String.format(Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW, matchCount));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindStudentCommand otherFindStudentCommand)) {
            return false;
        }

        return predicate.equals(otherFindStudentCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
