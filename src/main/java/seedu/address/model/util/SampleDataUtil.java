package seedu.address.model.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.lesson.Enrolment;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("friends")),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("colleagues", "friends")),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("neighbours")),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("family")),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("classmates")),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("colleagues"))
        };
    }

    /**
     * Returns sample students covering the supported combinations of contact details.
     */
    public static Student[] getSampleStudents() {
        return new Student[] {
            new Student(new Name("Aiden Tan"), null,
                    new Guardian(new Name("Tan Mei Ling"), new Phone("91234567"))),
            new Student(new Name("Chloe Lim"), new Phone("92345678"), null),
            new Student(new Name("Ryan Ong"), new Phone("93456789"),
                    new Guardian(new Name("Ong Wei Ling"), new Phone("94567890")))
        };
    }

    /**
     * Returns sample weekly lessons covering daytime and past-midnight schedules.
     */
    public static Lesson[] getSampleLessons() {
        return new Lesson[] {
            new Lesson("Sec 3 A-Math", DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(18, 0)),
            new Lesson("Late Physics", DayOfWeek.FRIDAY, LocalTime.of(23, 0), LocalTime.of(1, 0)),
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }
        for (Student sampleStudent : getSampleStudents()) {
            sampleAb.addStudent(sampleStudent);
        }
        for (Lesson sampleLesson : getSampleLessons()) {
            sampleAb.addLesson(sampleLesson);
        }

        Student aiden = sampleAb.getStudentList().get(0);
        Student chloe = sampleAb.getStudentList().get(1);
        Lesson mathsLesson = sampleAb.getLessonList().get(0);
        Lesson physicsLesson = sampleAb.getLessonList().get(1);
        sampleAb.addEnrolment(new Enrolment(aiden.getId(), mathsLesson.getId()));
        sampleAb.addEnrolment(new Enrolment(chloe.getId(), physicsLesson.getId()));
        return sampleAb;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
