package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

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

    @Test
    public void formatDuplicateLesson_namesLessonAndWhetherItIsShown() {
        Lesson math = lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00");

        assertEquals("This lesson already exists: Sec 3 A-Math (Mon 1600-1800)",
                Messages.formatDuplicateLesson(math, true));
        assertEquals("This lesson already exists: Sec 3 A-Math (Mon 1600-1800)\n"
                + "It is hidden by the lesson filter; run listlessons to show every lesson.",
                Messages.formatDuplicateLesson(math, false));
    }

    @Test
    public void formatStudentClashWarning_listsStudentsClashingLessons() {
        Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);

        assertEquals("", Messages.formatStudentClashWarning(aiden, List.of()));
        assertEquals("\nWarning: Aiden Tan already attends clashing lesson(s): Sec 3 A-Math (Mon 1600-1800); "
                + "Night Class (Fri 2200-0100) (next day)", Messages.formatStudentClashWarning(aiden,
                List.of(lesson("Sec 3 A-Math", DayOfWeek.MONDAY, "16:00", "18:00"),
                        lesson("Night Class", DayOfWeek.FRIDAY, "22:00", "01:00"))));
    }

    @Test
    public void formatLongLessonWarning_overnightOverTwelveHours_warns() {
        assertEquals("\nWarning: this lesson lasts 23 h 59 min and ends the next day. Check that the start and"
                + " end times are not swapped.",
                Messages.formatLongLessonWarning(lesson("Math", DayOfWeek.MONDAY, "16:01", "16:00")));
        assertEquals("\nWarning: this lesson lasts 12 h 1 min and ends the next day. Check that the start and"
                + " end times are not swapped.",
                Messages.formatLongLessonWarning(lesson("Math", DayOfWeek.MONDAY, "20:00", "08:01")));
    }

    @Test
    public void formatLongLessonWarning_shortOrSameDayLesson_noWarning() {
        // twelve hours exactly, a usual late lesson, and a long lesson within one day
        assertEquals("", Messages.formatLongLessonWarning(lesson("Math", DayOfWeek.MONDAY, "20:00", "08:00")));
        assertEquals("", Messages.formatLongLessonWarning(lesson("Math", DayOfWeek.FRIDAY, "22:00", "01:00")));
        assertEquals("", Messages.formatLongLessonWarning(lesson("Camp", DayOfWeek.SATURDAY, "00:00", "23:59")));
    }

    /** Creates a lesson with a newly generated ID; times are given as {@code HH:mm}. */
    private static Lesson lesson(String name, DayOfWeek day, String startTime, String endTime) {
        return new Lesson(name, day, LocalTime.parse(startTime), LocalTime.parse(endTime));
    }
}
