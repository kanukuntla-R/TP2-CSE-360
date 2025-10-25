package guiPasswordReset;

import database.Database;
import entityClasses.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ViewPasswordReset {

	// Window size from app constants
    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // UI labels
    private static Label label_Title = new Label("Set a New Password");
    private static Label label_Line = new Label("Enter a new password and confirm. It must meet all password rules.");

    // Password inputs
    protected static PasswordField text_NewPassword1 = new PasswordField();
    protected static PasswordField text_NewPassword2 = new PasswordField();

    // Action buttons
    private static Button button_Submit = new Button("Save New Password");
    private static Button button_Cancel = new Button("Cancel");

    // Alerts
    protected static Alert alert = new Alert(Alert.AlertType.INFORMATION);
    protected static Alert success = new Alert(Alert.AlertType.INFORMATION);

    private static ViewPasswordReset theView;
    @SuppressWarnings("unused")
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    private static Scene thePasswordResetScene;

    public static void displayPasswordReset(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewPasswordReset();

        // Clear previous input
        text_NewPassword1.setText("");
        text_NewPassword2.setText("");

        theStage.setTitle("CSE 360 Foundations: Password Reset");
        theStage.setScene(thePasswordResetScene);
        theStage.show();
    }

    // Build the UI once 
    private ViewPasswordReset() {
        theRootPane = new Pane();
        thePasswordResetScene = new Scene(theRootPane, width, height);

        setupLabelUI(label_Title, "Arial", 28, width, Pos.CENTER, 0, 20);
        setupLabelUI(label_Line, "Arial", 18, width, Pos.CENTER, 0, 70);

        setupTextUI(text_NewPassword1, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 150, true);
        text_NewPassword1.setPromptText("New Password");

        setupTextUI(text_NewPassword2, "Arial", 18, 350, Pos.BASELINE_LEFT, 50, 200, true);
        text_NewPassword2.setPromptText("Confirm New Password");

        setupButtonUI(button_Submit, "Dialog", 18, 240, Pos.CENTER, 50, 260);
        button_Submit.setOnAction(e -> ControllerPasswordReset.doSubmitNewPassword(theStage, theUser));

        setupButtonUI(button_Cancel, "Dialog", 18, 240, Pos.CENTER, 320, 260);
        button_Cancel.setOnAction(e -> ControllerPasswordReset.performCancel(theStage));

        theRootPane.getChildren().addAll(
            label_Title, label_Line, text_NewPassword1, text_NewPassword2,
            button_Submit, button_Cancel
        );
    }
    

    // Helpers to standardize control setup

    private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
        t.setFont(Font.font(ff, f));
        t.setMinWidth(w);
        t.setMaxWidth(w);
        t.setAlignment(p);
        t.setLayoutX(x);
        t.setLayoutY(y);
        t.setEditable(e);
    }
}
