package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

public class StudentModelTest {
    private final Student aiden = new Student(new Name("Aiden Tan"), new Phone("91234567"), null);
    private final Student chloe = new Student(new Name("Chloe Tan"), new Phone("91234567"), null);

    @Test
    public void studentList_observableAndUnmodifiable() {
        Model model = new ModelManager();
        ObservableList<Student> displayed = model.getFilteredStudentList();
        AtomicInteger changes = new AtomicInteger();
        displayed.addListener((ListChangeListener<Student>) change -> changes.incrementAndGet());
        model.addStudent(aiden);
        assertEquals(List.of(aiden), displayed);
        assertTrue(changes.get() > 0);
        assertThrows(UnsupportedOperationException.class, () -> displayed.add(chloe));
        assertThrows(UnsupportedOperationException.class, () -> model.getAddressBook().getStudentList().add(chloe));
        assertThrows(DuplicateStudentException.class, () -> model.addStudent(aiden));
    }

    @Test
    public void filtering_deletePreservesFilter_addClearsFilter() {
        Model model = new ModelManager();
        model.addStudent(aiden);
        model.addStudent(chloe);
        model.updateFilteredStudentList(student -> student.equals(aiden));
        assertEquals(List.of(aiden), model.getFilteredStudentList());
        model.deleteStudent(aiden);
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(List.of(chloe), model.getAddressBook().getStudentList());
        assertThrows(StudentNotFoundException.class, () -> model.deleteStudent(aiden));
        model.addStudent(aiden);
        assertEquals(List.of(chloe, aiden), model.getFilteredStudentList());
    }

    @Test
    public void copyAndReset_preserveStudentsAndNotifyExistingView() {
        AddressBook original = new AddressBook();
        original.addStudent(aiden);
        Model model = new ModelManager(original, new UserPrefs());
        ObservableList<Student> displayed = model.getFilteredStudentList();
        original.addStudent(chloe);
        assertEquals(List.of(aiden), displayed);
        model.setAddressBook(original);
        assertEquals(List.of(aiden, chloe), displayed);
        assertEquals(original, model.getAddressBook());
        model.setAddressBook(new AddressBook());
        assertTrue(displayed.isEmpty());
    }
}
