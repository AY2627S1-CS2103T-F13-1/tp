package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Lesson;

public class MessagesTest {

    @Test
    public void formatLesson_sameDay_showsNameDayAndTimes() {
        // the full format is spelled out once to pin its exact wording
        assertEquals("Sec 3 A-Math (Mon 1600-1800)", Messages.format(lesson("Sec 3 A-Math", DayOfWeek.MONDAY,
                "16:00", "18:00")));
    }

    @Test
    public void formatLesson_pastMidnight_marksNextDay() {
        assertEquals("Night Class (Fri 2200-0100) (next day)", Messages.format(lesson("Night Class", DayOfWeek.FRIDAY,
                "22:00", "01:00")));

        // ending at midnight still ends on the next day
        assertEquals("Late Revision (Sun 2359-0000) (next day)", Messages.format(lesson("Late Revision",
                DayOfWeek.SUNDAY, "23:59", "00:00")));
    }

    @Test
    public void formatLesson_wholeDay_notMarkedNextDay() {
        assertEquals("Holiday Camp (Sat 0000-2359)", Messages.format(lesson("Holiday Camp", DayOfWeek.SATURDAY,
                "00:00", "23:59")));
    }

    @Test
    public void formatClashWarning_noClashes_returnsEmptyString() {
        assertEquals("", Messages.formatClashWarning(List.of()));
    }

    @Test
    public void formatClashWarning_clashes_listsThemOnNewLine() {
        assertEquals("\nWarning: clashes with Sec 3 A-Math (Mon 1600-1800); Night Class (Fri 2200-0100) (next day)",
                Messages.formatClashWarning(List.of(lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00"),
                        lesson("Night Class", DayOfWeek.FRIDAY, "22:00", "01:00"))));
    }

    /** Creates a lesson with a newly generated ID; times are given as {@code HH:mm}. */
    private static Lesson lesson(String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }
}
