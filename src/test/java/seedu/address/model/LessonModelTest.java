package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;

public class LessonModelTest {
    private final Student student = new Student(new Name("Aiden"), new Phone("91234567"), null);
    private final Lesson lesson = new Lesson("Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));

    @Test
    public void enrolment_validatesReferencesAndDuplicates_notifiesObservers() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        AtomicInteger changes = new AtomicInteger();
        model.getEnrolmentList().addListener((ListChangeListener<Enrolment>) change -> changes.incrementAndGet());
        model.enrolStudent(student, lesson);
        assertEquals(List.of(student), model.getAddressBook().getStudentsInLesson(lesson));
        assertEquals(List.of(lesson), model.getAddressBook().getLessonsForStudent(student));
        assertEquals(1, changes.get());
        assertThrows(IllegalArgumentException.class, () -> model.enrolStudent(student, lesson));
        assertThrows(IllegalArgumentException.class, () -> model.addLesson(new Lesson("math", DayOfWeek.MONDAY,
                LocalTime.of(16, 0), LocalTime.of(18, 0))));
        assertThrows(IllegalArgumentException.class, () -> new AddressBook()
                .addEnrolment(new Enrolment(UUID.randomUUID(), lesson.getId())));
        assertThrows(UnsupportedOperationException.class, () -> model.getEnrolmentList().clear());
        assertThrows(UnsupportedOperationException.class, () -> model.getFilteredLessonList().clear());
    }

    @Test
    public void filteringAndDeletion_keepReferencesConsistent() {
        Model model = new ModelManager();
        model.addStudent(student);
        model.addLesson(lesson);
        model.filterStudentsByLesson(lesson);
        assertTrue(model.getFilteredStudentList().isEmpty());
        model.enrolStudent(student, lesson);
        assertEquals(List.of(student), model.getFilteredStudentList());
        model.updateFilteredLessonList(l -> false);
        assertEquals(List.of(lesson), model.getAddressBook().getLessonsForStudent(student));
        model.deleteLesson(lesson);
        assertTrue(model.getEnrolmentList().isEmpty());
        assertEquals(List.of(student), model.getFilteredStudentList());
        model.addLesson(lesson);
        assertEquals(List.of(lesson), model.getFilteredLessonList());
        model.enrolStudent(student, lesson);
        model.deleteStudent(student);
        assertTrue(model.getEnrolmentList().isEmpty());
        assertEquals(List.of(lesson), model.getFilteredLessonList());
    }

    @Test
    public void filterStudentsByLesson_lessonAbsent_throwsIllegalArgumentException() {
        Model model = new ModelManager();

        assertThrows(IllegalArgumentException.class, () -> model.filterStudentsByLesson(lesson));
    }

    @Test
    public void setAddressBook_activeLessonFilter_refreshesEnrolledStudents() {
        Student ryan = new Student(new Name("Ryan"), new Phone("92345678"), null);
        AddressBook initial = new AddressBook();
        initial.addStudent(student);
        initial.addStudent(ryan);
        initial.addLesson(lesson);
        initial.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        Model model = new ModelManager(initial, new UserPrefs());
        model.filterStudentsByLesson(lesson);
        var filteredStudents = model.getFilteredStudentList();
        assertEquals(List.of(student), filteredStudents);

        AddressBook replacement = new AddressBook();
        replacement.addStudent(student);
        replacement.addStudent(ryan);
        replacement.addLesson(lesson);
        replacement.addEnrolment(new Enrolment(ryan.getId(), lesson.getId()));
        model.setAddressBook(replacement);

        assertEquals(List.of(ryan), filteredStudents);
        assertEquals(filteredStudents, model.getFilteredStudentList());
    }

    @Test
    public void getClashingLessons_listsOverlappingLessonsInOrder() {
        Lesson physics = new Lesson("Physics", DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(19, 0));
        Lesson english = new Lesson("English", DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(19, 0));
        Lesson wednesday = new Lesson("Math", DayOfWeek.WEDNESDAY, LocalTime.of(16, 0), LocalTime.of(18, 0));
        Lesson chemistry = new Lesson("Chemistry", DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30));
        Model model = new ModelManager();
        for (Lesson each : List.of(physics, english, wednesday, chemistry)) {
            model.addLesson(each);
        }
        model.updateFilteredLessonList(l -> false);

        // hidden lessons still clash; English starts exactly when Math ends
        assertEquals(List.of(physics, chemistry), model.getAddressBook().getClashingLessons(lesson));
    }

    @Test
    public void getClashingLessons_lessonWithSameId_excluded() {
        Lesson physics = new Lesson("Physics", DayOfWeek.MONDAY, LocalTime.of(17, 0), LocalTime.of(19, 0));
        Model model = new ModelManager();
        model.addLesson(lesson);
        model.addLesson(physics);

        // a lesson does not clash with itself, nor with its own record when its times change
        assertEquals(List.of(physics), model.getAddressBook().getClashingLessons(lesson));
        Lesson moved = new Lesson(lesson.getId(), "Math", DayOfWeek.MONDAY, LocalTime.of(17, 30), LocalTime.of(18, 30));
        assertEquals(List.of(physics), model.getAddressBook().getClashingLessons(moved));
        assertTrue(new AddressBook().getClashingLessons(lesson).isEmpty());
    }

    @Test
    public void getClashingLessons_nullLesson_throwsNullPointerException() {
        // checked even when there are no lessons to compare against
        assertThrows(NullPointerException.class, () -> new AddressBook().getClashingLessons(null));
    }

    @Test
    public void reset_preservesLinksAndExistingObservableViews() {
        Model model = new ModelManager();
        var view = model.getFilteredLessonList();
        AddressBook replacement = new AddressBook();
        replacement.addStudent(student);
        replacement.addLesson(lesson);
        replacement.addEnrolment(new Enrolment(student.getId(), lesson.getId()));
        model.setAddressBook(replacement);
        assertEquals(List.of(lesson), view);
        assertTrue(model.getAddressBook().isEnrolled(student, lesson));
        model.filterStudentsByLesson(lesson);
        model.setAddressBook(new AddressBook());
        assertTrue(view.isEmpty());
        assertTrue(model.getEnrolmentList().isEmpty());
        model.addStudent(student);
        assertEquals(List.of(student), model.getFilteredStudentList());
    }
}
