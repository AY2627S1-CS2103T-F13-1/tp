package seedu.address.pe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.storage.JsonAddressBookStorage;

/**
 * Near-duplicates that PE testers add or create by editing: the same name in another letter case or with extra
 * spaces, sharing a phone number. They must be rejected, since a duplicate saved to the data file is skipped on the
 * next launch. The sample data has Aiden Tan (guardian's phone 91234567), Chloe Lim (92345678) and Ryan Ong
 * (93456789), and the lessons Maths (Mon 1000-1200) and Science (Wed 1000-1200).
 */
public class DuplicateEntryTest {
    private static final List<String> NEAR_DUPLICATES = List.of(
            "addstudent n/chloe lim p/92345678",
            "addstudent n/Chloe  Lim p/92345678",
            "addstudent n/ Chloe Lim  p/92345678",
            "addstudent n/CHLOE LIM gn/Lim Mei gp/92345678",
            "addstudent n/Aiden Tan p/91234567",
            "editstudent 2 n/aiden tan p/91234567",
            "editstudent 3 n/Chloe Lim p/92345678",
            "addlesson n/maths d/MON st/1000 et/1200",
            "addlesson n/Maths  d/mon st/1000 et/1200",
            "editlesson 2 n/MATHS d/mon st/1000 et/1200");

    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> nearDuplicate_rejectedWithoutChange() {
        return NEAR_DUPLICATES.stream().map(command -> DynamicTest.dynamicTest("'" + command + "'", () ->
                PeTestHarness.withSampleDataAndLessons(Files.createTempDirectory(temporaryFolder, "duplicate"))
                        .assertRejectedWithoutChange(command)));
    }

    @Test
    public void execute_distinctEntriesWithSharedDetails_addedAndReloadedWithoutSkipping() throws Exception {
        PeTestHarness harness = PeTestHarness.withSampleDataAndLessons(temporaryFolder);

        // the same name with no shared phone, and a sibling sharing a guardian's phone, are different students
        harness.execute("addstudent n/Chloe Lim p/99999999");
        harness.execute("addstudent n/Ben Tan gn/Tan Mei Ling gp/91234567");
        // the same name on another day is a different lesson
        harness.execute("addlesson n/Maths d/tue st/1000 et/1200");

        JsonAddressBookStorage reloaded = new JsonAddressBookStorage(harness.getDataFile());
        assertEquals(harness.getModel().getAddressBook(), reloaded.readAddressBook().orElseThrow());
        assertEquals("", reloaded.getLoadWarning());
    }
}
