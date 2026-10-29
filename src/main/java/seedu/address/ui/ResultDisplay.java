package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
    }

    /**
     * Shows the start of the feedback, as setting the text leaves it scrolled to the end.
     * Runs after the pending layout pass, which would otherwise scroll the wrapped text back down.
     */
    public void scrollToTop() {
        Platform.runLater(() -> {
            resultDisplay.applyCss();
            resultDisplay.layout();
            resultDisplay.positionCaret(0);
            resultDisplay.setScrollTop(0);
        });
    }

}
