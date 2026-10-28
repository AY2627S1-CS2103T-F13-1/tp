package seedu.address.pe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.parser.ParserUtil;
import seedu.address.storage.JsonAddressBookStorage;

/**
 * Inputs that PE testers type to break an app: indexes that are zero, negative, fractional or too large, values out
 * of range, and commands on empty lists. Each must be rejected with an error message, never a crash, and must leave
 * the data and the data file unchanged.
 */
public class InvalidInputTest {
    private static final List<String> INVALID_ON_SAMPLE_DATA = List.of(
            "deletestudent 0", "deletestudent -1", "deletestudent 1.5", "deletestudent abc", "deletestudent",
            "deletestudent 1 1", "deletestudent 99", "deletestudent 2147483648", "deletestudent 99999999999",
            "editstudent 0 n/Zoe", "editstudent 99 n/Zoe", "editstudent 2147483648 n/Zoe", "editstudent 1",
            "deletelesson 0", "deletelesson -1", "deletelesson 99", "deletelesson 99999999999",
            "editlesson 0 n/Art", "editlesson 99 n/Art", "editlesson 99999999999 n/Art",
            "enrol s/0 l/1", "enrol s/1 l/0", "enrol s/99 l/1", "enrol s/1 l/99", "enrol s/2147483648 l/1",
            "enrol s/1 l/1", "unenrol s/2 l/1", "unenrol s/-1 l/1",
            "liststudents l/0", "liststudents l/99", "liststudents l/2147483648", "liststudents l/abc",
            "addlesson n/Art d/mon st/2400 et/0100", "addlesson n/Art d/mon st/1600 et/1600",
            "addlesson n/Art d/xyz st/1600 et/1800", "addlesson n/Art d/mon st/-100 et/0100",
            "addlesson n/" + "A".repeat(51) + " d/mon st/1600 et/1800", "addlesson n/Art d/mon st/1600",
            "addstudent n/Zoe", "addstudent n/Zoe p/12", "addstudent n/Zoe gn/Mum", "addstudent n/ p/91234567",
            "findstudent n/", "listlessons d/xyz", "blah", "", "   ");
    private static final List<String> INVALID_ON_NO_DATA = List.of(
            "deletestudent 1", "editstudent 1 n/Zoe", "enrol s/1 l/1", "unenrol s/1 l/1", "liststudents l/1",
            "deletelesson 1", "editlesson 1 n/Art");

    @TempDir
    public Path temporaryFolder;

    @TestFactory
    public Stream<DynamicTest> invalidCommand_onSampleData_rejectedWithoutChange() {
        return rejectEach(INVALID_ON_SAMPLE_DATA, PeTestHarness::withSampleDataAndLessons);
    }

    @TestFactory
    public Stream<DynamicTest> indexCommand_onNoData_rejectedWithoutChange() {
        return rejectEach(INVALID_ON_NO_DATA, PeTestHarness::withNoData);
    }

    @Test
    public void execute_veryLongName_rejectedWithoutCrash() throws Exception {
        PeTestHarness harness = PeTestHarness.withSampleData(temporaryFolder);

        harness.assertRejectedWithoutChange("addstudent n/" + "A".repeat(10_000) + " p/90000000");
        harness.execute("findstudent n/" + "a".repeat(5_000));
    }

    @Test
    public void execute_longestAllowedName_addsSearchesAndSaves() throws Exception {
        PeTestHarness harness = PeTestHarness.withSampleData(temporaryFolder);
        String name = "A".repeat(ParserUtil.MAX_NAME_LENGTH);

        harness.execute("addstudent n/" + name + " p/90000000");
        harness.execute("findstudent n/" + "a".repeat(ParserUtil.MAX_NAME_LENGTH));

        assertEquals(1, harness.getModel().getFilteredStudentList().size());
        assertTrue(new JsonAddressBookStorage(harness.getDataFile()).readAddressBook().orElseThrow()
                .getStudentList().stream().anyMatch(student -> student.getName().fullName.equals(name)));
    }

    private Stream<DynamicTest> rejectEach(List<String> commands, Function<Path, PeTestHarness> setUp) {
        return commands.stream().map(command -> DynamicTest.dynamicTest("'" + command + "'", () ->
                setUp.apply(Files.createTempDirectory(temporaryFolder, "input"))
                        .assertRejectedWithoutChange(command)));
    }
}
