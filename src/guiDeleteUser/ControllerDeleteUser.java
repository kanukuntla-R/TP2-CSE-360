package guiDeleteUser;

import database.Database;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class ControllerDeleteUser {

    // Reference to the in-memory database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    /**********
     * <p> Method: performDeleteUser </p>
     * 
     * <p> Description: Deletes the selected user if confirmed and ensures 
     * the logged-in admin cannot delete their own account. </p>
     */
    protected static void performDeleteUser() {
        String selectedUser = (String) ViewDeleteUser.combobox_SelectUser.getValue();

        // Ignore placeholder option
        if (selectedUser.equals("<Select a User>")) {
            return;
        }

        // Prevent admin from deleting themselves
        if (selectedUser.equals(theDatabase.getCurrentUsername())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Delete User Error");
            alert.setHeaderText("Operation Not Allowed");
            alert.setContentText("You cannot delete your own account while logged in.");
            alert.showAndWait();
            return;
        }

        // Confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Are you sure you want to delete this user?");
        confirm.setContentText(selectedUser);
        
        // Change OK button text to "Yes" while keeping original positions
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yesButton, cancelButton);

        confirm.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                if (theDatabase.removeUser(selectedUser)) {
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("User Deleted");
                    success.setHeaderText("Success");
                    success.setContentText(selectedUser + " has been deleted.");
                    success.showAndWait();

                    // Refresh user list in ComboBox
                    ViewDeleteUser.refreshUserList();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Delete Failed");
                    error.setHeaderText("Database Error");
                    error.setContentText("Unable to delete " + selectedUser + ".");
                    error.showAndWait();
                }
            }
        });
    }

    // Buttons to perform Return, Logout or Quit
    protected static void performReturn() {
        guiAdminHome.ViewAdminHome.displayAdminHome(ViewDeleteUser.theStage, ViewDeleteUser.theUser);
    }

    protected static void performLogout() {
        guiUserLogin.ViewUserLogin.displayUserLogin(ViewDeleteUser.theStage);
    }

    protected static void performQuit() {
        System.exit(0);
    }
}
