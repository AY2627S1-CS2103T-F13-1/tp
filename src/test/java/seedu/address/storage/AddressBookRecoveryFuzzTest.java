package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

/**
 * Deterministic corpus checks for partial JSON recovery. Malformed overall structure may throw
 * {@link IOException}; it must never escape as an unexpected runtime exception.
 */
public class AddressBookRecoveryFuzzTest {
    private static final String[] KEYS = {
        "persons", "students", "lessons", "enrolments", "unknown", "id", "name", "phone",
    };
    private static final String[] VALUES = {
        "{}", "[]", "null", "true", "false", "\"\"", "\"Aiden\"", "\"91234567\"", "\"MONDAY\"",
        "\"00000000-0000-0000-0000-000000000001\"", "0", "-1", "2147483648", "{}", "[]",
        "{\"id\":\"00000000-0000-0000-0000-000000000001\"}",
        "{\"name\":\"Aiden\",\"phone\":\"91234567\"}",
        "{\"id\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"Math\","
            + "\"day\":\"MONDAY\",\"startTime\":\"1600\",\"endTime\":\"1800\"}",
        "{\"studentId\":\"00000000-0000-0000-0000-000000000001\","
            + "\"lessonId\":\"00000000-0000-0000-0000-000000000002\"}",
    };

    private final Random random = new Random(20261025L);

    @Test
    public void randomJson_onlyThrowsIoExceptionForUnreadableStructure() {
        for (int i = 0; i < 5000; i++) {
            String json = randomJson();
            assertDoesNotThrow(() -> readOnlyExpectedFailures(json));
        }
    }

    private void readOnlyExpectedFailures(String json) throws IOException {
        try {
            AddressBookRecovery.read(json);
        } catch (IOException exception) {
            return;
        }
    }

    private String randomJson() {
        StringBuilder json = new StringBuilder("{");
        int fieldCount = random.nextInt(7);
        List<String> fields = new ArrayList<>();
        for (int i = 0; i < fieldCount; i++) {
            String key = KEYS[random.nextInt(KEYS.length)];
            int recordCount = random.nextInt(5);
            StringBuilder records = new StringBuilder("[");
            for (int j = 0; j < recordCount; j++) {
                if (j > 0) {
                    records.append(',');
                }
                records.append(VALUES[random.nextInt(VALUES.length)]);
            }
            records.append(']');
            fields.add("\"" + key + "\":" + records);
        }
        json.append(String.join(",", fields));
        json.append('}');

        if (random.nextInt(10) == 0) {
            int cut = random.nextInt(json.length());
            return json.substring(0, cut);
        }
        return json.toString();
    }
}
