package seedu.address.ui;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javafx.application.Platform;

/**
 * Utilities for running UI assertions on the JavaFX application thread.
 */
final class JavaFxTestUtil {

    private static boolean isToolkitInitialized;

    private JavaFxTestUtil() {}

    /**
     * Initializes the JavaFX toolkit once for the UI test suite.
     */
    static synchronized void initializeToolkit() {
        if (!isToolkitInitialized) {
            Platform.startup(() -> Platform.setImplicitExit(false));
            isToolkitInitialized = true;
        }
    }

    /**
     * Runs the task on the JavaFX application thread and returns its result.
     */
    static <T> T runOnFxThread(Callable<T> task) throws Exception {
        if (Platform.isFxApplicationThread()) {
            return task.call();
        }

        FutureTask<T> futureTask = new FutureTask<>(task);
        Platform.runLater(futureTask);
        return futureTask.get();
    }
}
