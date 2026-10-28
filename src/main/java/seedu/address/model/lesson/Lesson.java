package seedu.address.model.lesson;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable weekly lesson. Overlapping lessons are allowed; {@link #overlaps(Lesson)} finds them. An end time
 * earlier than the start time means the lesson runs past midnight and ends on the day after its day.
 */
public final class Lesson {
    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Lesson names must be 1 to 50 characters, start with a letter or digit, "
            + "and contain only English letters, digits, spaces and - : # @ ' . , & / ( ) +.";
    public static final String MESSAGE_TIME_CONSTRAINTS = "End time must not be the same as start time.";
    public static final String MESSAGE_TIME_PRECISION = "Times must be whole minutes, with no seconds.";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmm");
    private static final int MINUTES_PER_DAY = 24 * 60;
    private static final int MINUTES_PER_WEEK = 7 * MINUTES_PER_DAY;

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
        checkArgument(normalizedName.matches("[A-Za-z0-9][A-Za-z0-9 :#@'.\\-.,&/()+]{0,49}"),
                MESSAGE_NAME_CONSTRAINTS);
        checkArgument(isWholeMinute(startTime) && isWholeMinute(endTime), MESSAGE_TIME_PRECISION);
        checkArgument(!endTime.equals(startTime), MESSAGE_TIME_CONSTRAINTS);
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

    /** Returns true if this lesson runs past midnight and ends on the day after its day. */
    public boolean endsNextDay() {
        return endTime.isBefore(startTime);
    }

    /** Duplicate check: same name ignoring case, day and times, regardless of ID. */
    public boolean isSameLesson(Lesson other) {
        return other != null && name.equalsIgnoreCase(other.name) && day == other.day
                && startTime.equals(other.startTime) && endTime.equals(other.endTime);
    }

    /**
     * Returns true if this lesson's weekly time range overlaps {@code other}'s. A lesson that starts exactly
     * when the other ends does not overlap it. A lesson past midnight also covers the start of the next day,
     * so a Sunday lesson past midnight can overlap a Monday lesson.
     */
    public boolean overlaps(Lesson other) {
        requireNonNull(other);
        int start = getStartMinuteOfWeek();
        int otherStart = other.getStartMinuteOfWeek();
        // on a weekly cycle, two lessons overlap if either starts before the other has ended
        return Math.floorMod(otherStart - start, MINUTES_PER_WEEK) < getDurationInMinutes()
                || Math.floorMod(start - otherStart, MINUTES_PER_WEEK) < other.getDurationInMinutes();
    }

    private int getStartMinuteOfWeek() {
        return (day.getValue() - 1) * MINUTES_PER_DAY + toMinuteOfDay(startTime);
    }

    /** Returns the length of the lesson, including the hours after midnight for a lesson that ends the next day. */
    public int getDurationInMinutes() {
        int duration = Math.floorMod(toMinuteOfDay(endTime) - toMinuteOfDay(startTime), MINUTES_PER_DAY);
        assert duration > 0 : "The constructor rejects an end time equal to the start time";
        return duration;
    }

    private static boolean isWholeMinute(LocalTime time) {
        return time.getSecond() == 0 && time.getNano() == 0;
    }

    private static int toMinuteOfDay(LocalTime time) {
        return time.get(ChronoField.MINUTE_OF_DAY);
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
