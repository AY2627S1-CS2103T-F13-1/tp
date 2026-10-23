package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_END_TIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_TIME;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.lesson.Lesson;

/**
 * Edits the details of an existing lesson in the address book.
 */
public class EditLessonCommand extends Command {

    public static final String COMMAND_WORD = "editlesson";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the lesson identified "
            + "by the index number used in the displayed lesson list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_DAY + "DAY] "
            + "[" + PREFIX_START_TIME + "START_TIME] "
            + "[" + PREFIX_END_TIME + "END_TIME]\n"
            + "DAY is a day's name or at least its first 3 letters, e.g. mon or monday.\n"
            + "Times are in 24-hour format, e.g. 1600 or 16:00.\n"
            + "An end time earlier than the start time means the lesson ends on the next day.\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_DAY + "tue "
            + PREFIX_START_TIME + "1700 "
            + PREFIX_END_TIME + "1900";

    public static final String MESSAGE_EDIT_LESSON_SUCCESS = "Edited lesson: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_LESSON = "This lesson already exists";

    private static final Logger logger = LogsCenter.getLogger(EditLessonCommand.class);

    private final Index index;
    private final EditLessonDescriptor editLessonDescriptor;

    /**
     * Creates an {@code EditLessonCommand} to edit the lesson at {@code index} using the supplied details.
     */
    public EditLessonCommand(Index index, EditLessonDescriptor editLessonDescriptor) {
        this.index = requireNonNull(index);
        this.editLessonDescriptor = new EditLessonDescriptor(requireNonNull(editLessonDescriptor));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Lesson> lastShownList = model.getFilteredLessonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LESSON_DISPLAYED_INDEX);
        }

        Lesson lessonToEdit = lastShownList.get(index.getZeroBased());
        Lesson editedLesson;
        try {
            editedLesson = createEditedLesson(lessonToEdit, editLessonDescriptor);
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage());
        }

        boolean hasDuplicate = model.getAddressBook().getLessonList().stream()
                .filter(lesson -> !lesson.getId().equals(lessonToEdit.getId()))
                .anyMatch(lesson -> lesson.isSameLesson(editedLesson));
        if (hasDuplicate) {
            logger.fine("Rejected duplicate lesson edit for ID: " + lessonToEdit.getId());
            throw new CommandException(MESSAGE_DUPLICATE_LESSON);
        }

        // the full list is checked, so lessons hidden by a day filter count; the lesson's own old times do not
        List<Lesson> clashingLessons = model.getAddressBook().getClashingLessons(editedLesson);
        model.setLesson(lessonToEdit, editedLesson);
        Lesson storedLesson = model.getAddressBook().getLessonList().stream()
                .filter(lesson -> lesson.getId().equals(editedLesson.getId()))
                .findFirst()
                .orElse(null);
        assert editedLesson.equals(storedLesson) : "Editing a lesson should update the address book";
        logger.info("Edited lesson with ID: " + editedLesson.getId() + ", clashing with "
                + clashingLessons.size() + " lesson(s)");
        return new CommandResult(String.format(MESSAGE_EDIT_LESSON_SUCCESS, Messages.format(editedLesson))
                + Messages.formatClashWarning(clashingLessons));
    }

    /**
     * Creates a lesson with the edited details while retaining the original lesson ID.
     */
    private static Lesson createEditedLesson(Lesson lessonToEdit, EditLessonDescriptor editLessonDescriptor) {
        requireNonNull(lessonToEdit);

        String updatedName = editLessonDescriptor.getName().orElse(lessonToEdit.getName());
        DayOfWeek updatedDay = editLessonDescriptor.getDay().orElse(lessonToEdit.getDay());
        LocalTime updatedStartTime = editLessonDescriptor.getStartTime().orElse(lessonToEdit.getStartTime());
        LocalTime updatedEndTime = editLessonDescriptor.getEndTime().orElse(lessonToEdit.getEndTime());

        return new Lesson(lessonToEdit.getId(), updatedName, updatedDay, updatedStartTime, updatedEndTime);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditLessonCommand otherEditLessonCommand)) {
            return false;
        }

        return index.equals(otherEditLessonCommand.index)
                && editLessonDescriptor.equals(otherEditLessonCommand.editLessonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editLessonDescriptor", editLessonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit a lesson with. Each non-empty field replaces the corresponding lesson value.
     */
    public static class EditLessonDescriptor {
        private String name;
        private DayOfWeek day;
        private LocalTime startTime;
        private LocalTime endTime;

        public EditLessonDescriptor() {}

        /**
         * Creates a copy of {@code toCopy}.
         */
        public EditLessonDescriptor(EditLessonDescriptor toCopy) {
            requireNonNull(toCopy);
            name = toCopy.name;
            day = toCopy.day;
            startTime = toCopy.startTime;
            endTime = toCopy.endTime;
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, day, startTime, endTime);
        }

        public void setName(String name) {
            this.name = name;
        }

        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        public void setDay(DayOfWeek day) {
            this.day = day;
        }

        public Optional<DayOfWeek> getDay() {
            return Optional.ofNullable(day);
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public Optional<LocalTime> getStartTime() {
            return Optional.ofNullable(startTime);
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public Optional<LocalTime> getEndTime() {
            return Optional.ofNullable(endTime);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditLessonDescriptor otherEditLessonDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditLessonDescriptor.name)
                    && Objects.equals(day, otherEditLessonDescriptor.day)
                    && Objects.equals(startTime, otherEditLessonDescriptor.startTime)
                    && Objects.equals(endTime, otherEditLessonDescriptor.endTime);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("day", day)
                    .add("startTime", startTime)
                    .add("endTime", endTime)
                    .toString();
        }
    }
}
