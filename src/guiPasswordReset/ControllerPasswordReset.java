package guiPasswordReset;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;
import passwordPopUpWindow.Model;

public class ControllerPasswordReset {

	  // DB singleton used across the app
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    protected static void doSubmitNewPassword(Stage theStage, User theUser) {
        String p1 = ViewPasswordReset.text_NewPassword1.getText();
        String p2 = ViewPasswordReset.text_NewPassword2.getText();

        // 1) same password in both fields?
        if (p1 == null || p2 == null || !p1.equals(p2)) {
            ViewPasswordReset.alert.setTitle("Password Mismatch");
            ViewPasswordReset.alert.setHeaderText(null);
            ViewPasswordReset.alert.setContentText("The two passwords must match. Please try again.");
            ViewPasswordReset.alert.showAndWait();
            return;
        }

        // 2) password policy check (your evaluator)
        String policyMsg = Model.evaluatePassword(p1);
        if (policyMsg != null && !policyMsg.isEmpty()) {
            ViewPasswordReset.alert.setTitle("Invalid Password");
            ViewPasswordReset.alert.setHeaderText("Password does not meet requirements.");
            ViewPasswordReset.alert.setContentText(policyMsg);
            ViewPasswordReset.alert.showAndWait();
            return;
        }

        // 3) persist new password
        theDatabase.updatePassword(theUser.getUserName(), p1);

        // 4) read-back for your console to verify the DB now returns the new value
        theDatabase.getUserAccountDetails(theUser.getUserName());
        System.out.println("** Password reset read-back for " + theUser.getUserName()
                + " | currentPassword now = " + theDatabase.getCurrentPassword());

        // keep in-memory User in sync 
        theUser.setPassword(p1);

        // 5) Notify success and return to login
        ViewPasswordReset.success.setTitle("Password Reset");
        ViewPasswordReset.success.setHeaderText("Your password has been updated.");
        ViewPasswordReset.success.setContentText("Please log in with your new password.");
        ViewPasswordReset.success.showAndWait();

        guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
    }

    protected static void performCancel(Stage theStage) {
        // return to login without changing anything
        guiUserLogin.ViewUserLogin.displayUserLogin(theStage);
    }
}
