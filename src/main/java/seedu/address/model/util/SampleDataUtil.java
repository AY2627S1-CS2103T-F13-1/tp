package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Guardian;
import seedu.address.model.student.Student;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
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

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Student sampleStudent : getSampleStudents()) {
            sampleAb.addStudent(sampleStudent);
        }
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
