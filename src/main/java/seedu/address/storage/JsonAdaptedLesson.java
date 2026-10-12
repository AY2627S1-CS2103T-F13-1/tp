package seedu.address.storage;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.lesson.Lesson;

/** JSON lesson fields, with times saved in four-digit HHmm format. */
class JsonAdaptedLesson {
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
            return new Lesson(UUID.fromString(id), name, DayOfWeek.valueOf(day),
                    parseTime(startTime), parseTime(endTime));
        } catch (IllegalArgumentException | NullPointerException | java.time.DateTimeException e) {
            throw new IllegalValueException("Invalid lesson: " + e.getMessage());
        }
    }

    private static LocalTime parseTime(String value) {
        if (value == null || !value.matches("[0-9]{4}")) {
            throw new IllegalArgumentException("Time must use HHmm format.");
        }
        return LocalTime.of(Integer.parseInt(value.substring(0, 2)), Integer.parseInt(value.substring(2)));
    }
}
