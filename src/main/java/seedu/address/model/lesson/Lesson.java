package seedu.address.model.lesson;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Immutable weekly lesson. Overlapping lessons are allowed. */
public final class Lesson {
    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Lesson names must be 1 to 50 characters, start with a letter or digit, "
            + "and contain only English letters, digits, spaces and - ' . , & ( ) +.";
    public static final String MESSAGE_TIME_CONSTRAINTS =
            "Times must have minute precision, and end time must be later than start time.";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");

    private final UUID id;
    private final String name;
    private final DayOfWeek day;
    private final LocalTime startTime;
    private final LocalTime endTime;

    /** Creates a new lesson with a stable ID. */
    public Lesson(String name, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this(UUID.randomUUID(), name, day, startTime, endTime);
    }

    /** Restores a lesson with its persisted ID. */
    public Lesson(UUID id, String name, DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        requireAllNonNull(id, name, day, startTime, endTime);
        String normalizedName = name.trim().replaceAll(" +", " ");
        checkArgument(normalizedName.matches("[A-Za-z0-9][A-Za-z0-9 '\\-.,&()+]{0,49}"), MESSAGE_NAME_CONSTRAINTS);
        checkArgument(startTime.getSecond() == 0 && startTime.getNano() == 0
                && endTime.getSecond() == 0 && endTime.getNano() == 0 && endTime.isAfter(startTime),
                MESSAGE_TIME_CONSTRAINTS);
        this.id = id;
        this.name = normalizedName;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /** Duplicate check: same name ignoring case, day and times, regardless of ID. */
    public boolean isSameLesson(Lesson other) {
        return other != null && name.equalsIgnoreCase(other.name) && day == other.day
                && startTime.equals(other.startTime) && endTime.equals(other.endTime);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Lesson lesson && id.equals(lesson.id)
                && name.equals(lesson.name) && day == lesson.day
                && startTime.equals(lesson.startTime) && endTime.equals(lesson.endTime));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, day, startTime, endTime);
    }

    @Override
    public String toString() {
        return name + " (" + day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " "
                + startTime.format(TIME_FORMAT) + "-" + endTime.format(TIME_FORMAT) + ")";
    }
}
