package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GUARDIAN_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

/**
 * Finds and lists all students whose name, or whose guardian's name, contains any of the given keywords.
 * Keyword matching is case insensitive.
 */
public class FindStudentCommand extends Command {

    public static final String COMMAND_WORD = "findstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all students whose names, or whose "
            + "guardians' names, contain any of the specified keywords (case-insensitive) and displays them "
            + "as a list with index numbers. If both are given, a student must match both.\n"
            + "Parameters: [" + PREFIX_NAME + "NAME_KEYWORD [MORE_NAME_KEYWORDS]...] "
            + "[" + PREFIX_GUARDIAN_NAME + "GUARDIAN_NAME_KEYWORD [MORE_GUARDIAN_NAME_KEYWORDS]...] "
            + "(at least one)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "aiden chloe " + PREFIX_GUARDIAN_NAME + "tan";

    private final StudentNameContainsKeywordsPredicate predicate;

    /**
     * Creates a command that filters the student list with {@code predicate}.
     */
    public FindStudentCommand(StudentNameContainsKeywordsPredicate predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW, model.getFilteredStudentList().size()));
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
