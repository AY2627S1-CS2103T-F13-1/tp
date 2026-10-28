package seedu.address.storage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Lesson;

/** JSON lesson fields, with times saved in four-digit HHmm format. */
class JsonAdaptedLesson {
    private static final int MIN_DAY_PREFIX_LENGTH = 3;
    // a common abbreviation that is not the start of the day's name
    private static final String WEDNESDAY_ABBREVIATION = "weds";
    private static final Pattern TIME_WITHOUT_COLON =
            Pattern.compile("(?<hour>[01][0-9]|2[0-3])(?<minute>[0-5][0-9])");
    // with a colon, the hour may drop its leading zero, as in 9:30
    private static final Pattern TIME_WITH_COLON =
            Pattern.compile("(?<hour>[01]?[0-9]|2[0-3]):(?<minute>[0-5][0-9])");

    private final String id;
    private final String name;
    private final String day;
    private final String startTime;
    private final String endTime;

    @JsonCreator
    public JsonAdaptedLesson(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("day") String day, @JsonProperty("startTime") String startTime,
            @JsonProperty("endTime") String endTime) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public JsonAdaptedLesson(Lesson lesson) {
        id = lesson.getId().toString();
        name = lesson.getName();
        day = lesson.getDay().name();
        startTime = lesson.getStartTime().format(DateTimeFormatter.ofPattern("HHmm"));
        endTime = lesson.getEndTime().format(DateTimeFormatter.ofPattern("HHmm"));
    }

    /** Validates stored fields before constructing the lesson. */
    public Lesson toModelType() throws IllegalValueException {
        try {
            return new Lesson(UUID.fromString(id), name, parseDay(day),
                    parseTime(startTime), parseTime(endTime));
        } catch (IllegalArgumentException | NullPointerException | java.time.DateTimeException e) {
            throw new IllegalValueException("Invalid lesson: " + e.getMessage());
        }
    }

    /**
     * Parses a stored day in the same way that commands parse one, so that a day hand-written as
     * {@code mon} or {@code Monday} loads just as {@code addlesson d/mon} does.
     */
    private static DayOfWeek parseDay(String value) {
        if (value == null) {
            throw new NullPointerException("Day is missing.");
        }
        String normalizedDay = value.trim().toLowerCase(Locale.ROOT);
        if (normalizedDay.equals(WEDNESDAY_ABBREVIATION)) {
            return DayOfWeek.WEDNESDAY;
        }
        if (normalizedDay.length() >= MIN_DAY_PREFIX_LENGTH) {
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                // the first three letters of each day differ, so at most one day can match
                if (dayOfWeek.name().toLowerCase(Locale.ROOT).startsWith(normalizedDay)) {
                    return dayOfWeek;
                }
            }
        }
        throw new IllegalArgumentException("Day must be a day's name or at least its first 3 letters.");
    }

    private static LocalTime parseTime(String value) {
        if (value == null) {
            throw new NullPointerException("Time is missing.");
        }
        String trimmedTime = value.trim();
        for (Pattern format : List.of(TIME_WITHOUT_COLON, TIME_WITH_COLON)) {
            Matcher matcher = format.matcher(trimmedTime);
            if (matcher.matches()) {
                return LocalTime.of(Integer.parseInt(matcher.group("hour")),
                        Integer.parseInt(matcher.group("minute")));
            }
        }
        throw new IllegalArgumentException("Times must use 24-hour HHmm or HH:mm, from 0000 to 2359.");
    }
}
