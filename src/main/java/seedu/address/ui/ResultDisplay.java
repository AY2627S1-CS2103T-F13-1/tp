package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";
    private static final int MIN_VISIBLE_ROWS = 3;
    private static final int MAX_VISIBLE_ROWS = 9;
    private static final double HORIZONTAL_CONTENT_INSETS = 24;

    @FXML
    private TextArea resultDisplay;

    /** Creates a result display that grows with its feedback up to a fixed number of rows. */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.textProperty().addListener((observable, oldValue, newValue) -> updatePreferredRowCount());
        resultDisplay.widthProperty().addListener((observable, oldValue, newValue) -> updatePreferredRowCount());
        resultDisplay.fontProperty().addListener((observable, oldValue, newValue) -> updatePreferredRowCount());
        updatePreferredRowCount();
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
    }

    /**
     * Adjusts the preferred height to reveal short feedback while keeping long feedback scrollable.
     */
    private void updatePreferredRowCount() {
        String feedback = resultDisplay.getText();
        int requiredRows = countExplicitLines(feedback);
        double availableTextWidth = resultDisplay.getWidth() - HORIZONTAL_CONTENT_INSETS;

        if (!feedback.isEmpty() && availableTextWidth > 0) {
            Text renderedFeedback = new Text(feedback);
            renderedFeedback.setFont(resultDisplay.getFont());
            renderedFeedback.setWrappingWidth(availableTextWidth);

            Text singleLine = new Text("Ag");
            singleLine.setFont(resultDisplay.getFont());
            double lineHeight = singleLine.getLayoutBounds().getHeight();
            int wrappedRows = (int) Math.ceil(renderedFeedback.getLayoutBounds().getHeight() / lineHeight);
            requiredRows = Math.max(requiredRows, wrappedRows);
        }

        resultDisplay.setPrefRowCount(Math.max(MIN_VISIBLE_ROWS, Math.min(requiredRows, MAX_VISIBLE_ROWS)));
    }

    private static int countExplicitLines(String feedback) {
        int lineCount = 1;
        for (int i = 0; i < feedback.length(); i++) {
            if (feedback.charAt(i) == '\n') {
                lineCount++;
            }
        }
        return lineCount;
    }

}
