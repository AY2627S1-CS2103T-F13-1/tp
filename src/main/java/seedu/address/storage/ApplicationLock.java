package seedu.address.storage;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Holds an exclusive application lock until closed. The lock file is deliberately kept on disk. */
public final class ApplicationLock implements AutoCloseable {
    private final FileChannel channel;

    private ApplicationLock(FileChannel channel) {
        this.channel = channel;
    }

    /** Returns null if another instance holds the lock; throws if the lock file cannot be accessed. */
    public static ApplicationLock tryAcquire(Path path) throws IOException {
        Path absolutePath = path.toAbsolutePath();
        Files.createDirectories(absolutePath.getParent());
        FileChannel channel = FileChannel.open(absolutePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            if (channel.tryLock() != null) {
                return new ApplicationLock(channel);
            }
        } catch (OverlappingFileLockException e) {
            // Another instance in this JVM already holds the lock.
        } catch (IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
        channel.close();
        return null;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
