package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ApplicationLockTest {
    @TempDir
    public Path folder;

    @Test
    public void tryAcquire_heldLock_rejectsSecondInstanceAndAllowsReopening() throws Exception {
        Path file = folder.resolve("data/application.lock");
        try (ApplicationLock first = ApplicationLock.tryAcquire(file)) {
            assertNotNull(first);
            assertNull(ApplicationLock.tryAcquire(file));
        }
        assertTrue(Files.exists(file));
        try (ApplicationLock reopened = ApplicationLock.tryAcquire(file)) {
            assertNotNull(reopened);
        }
    }

    @Test
    public void tryAcquire_anotherProcess_rejectedUntilLockReleased() throws Exception {
        Path file = folder.resolve("application.lock");
        try (ApplicationLock first = ApplicationLock.tryAcquire(file)) {
            assertNotNull(first);
            assertEquals(1, runProbe(file));
        }
        assertEquals(0, runProbe(file));
    }

    private int runProbe(Path file) throws Exception {
        String classPath = Path.of(ApplicationLockTest.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI())
                + File.pathSeparator
                + Path.of(ApplicationLock.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", classPath, LockProbe.class.getName(), file.toString()).inheritIO().start();
        try {
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Lock attempt should not block");
            return process.exitValue();
        } finally {
            process.destroyForcibly();
        }
    }

    /** A separate JVM checks that the lock works across application processes. */
    public static class LockProbe {
        public static void main(String[] args) throws Exception {
            boolean acquired;
            try (ApplicationLock lock = ApplicationLock.tryAcquire(Path.of(args[0]))) {
                acquired = lock != null;
            }
            System.exit(acquired ? 0 : 1);
        }
    }
}
