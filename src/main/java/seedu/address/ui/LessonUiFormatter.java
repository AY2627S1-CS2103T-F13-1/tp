package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import seedu.address.model.lesson.Lesson;

/**
 * Formats lesson details consistently across lesson and student cards.
 */
final class LessonUiFormatter {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private LessonUiFormatter() {
    }

    static String formatDay(Lesson lesson) {
        return lesson.getDay().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    static String formatTimeRange(Lesson lesson) {
        String nextDaySuffix = lesson.getEndTime().isBefore(lesson.getStartTime()) ? " (next day)" : "";
        return lesson.getStartTime().format(TIME_FORMAT) + " – " + lesson.getEndTime().format(TIME_FORMAT)
                + nextDaySuffix;
    }

    static String formatMembership(Lesson lesson) {
        return lesson.getName() + " — " + formatDay(lesson) + ", " + formatTimeRange(lesson);
    }
}
