package guiDeleteUser;

import java.util.List;

import database.Database;
import entityClasses.User;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/*******
 * <p> Title: ViewDeleteUser Class. </p>
 * 
 * <p> Description: The Java/FX-based Delete User Page. This page allows an admin
 * to select and delete a user account, with safeguards to prevent deleting
 * themselves. Layout is modeled after the Add/Remove Roles page for consistency. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author 
 * @version 1.02   2025-09-25 Updated to include User info and Account Update
 */

public class ViewDeleteUser {

    /*-*******************************************************************************************
     * Attributes
     ********************************************************************************************/

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    // Stage, root pane, and current user
    protected static Stage theStage;
    protected static Pane theRootPane;
    protected static User theUser;

    // Reference to database
    private static Database theDatabase = applicationMain.FoundationsMain.database;

    public static Scene theDeleteUserScene = null;

    // GUI Elements
    protected static Label label_PageTitle = new Label("Delete User Page");
    protected static Label label_UserDetails = new Label(); // shows "User: <username>"
    protected static Button button_UpdateThisUser = new Button("Account Update");

    protected static Label label_SelectUser = new Label("Select a user to delete:");
    protected static ComboBox<String> combobox_SelectUser = new ComboBox<>();
    protected static Button button_DeleteUser = new Button("Delete User");

    // Separators
    protected static Line line_Separator1 = new Line(20, 95, width - 20, 95);
    protected static Line line_Separator4 = new Line(20, 525, width - 20, 525);

    // Bottom buttons
    protected static Button button_Return = new Button("Return");
    protected static Button button_Logout = new Button("Logout");
    protected static Button button_Quit = new Button("Quit");

    // Singleton reference
    private static ViewDeleteUser theView;

    /*-*******************************************************************************************
     * Methods
     ********************************************************************************************/

    /**********
     * <p> Method: displayDeleteUser(Stage, User) </p>
     * 
     * <p> Description: Displays the Delete User page. Refreshes user list and shows UI. </p>
     */
    public static void displayDeleteUser(Stage ps, User user) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewDeleteUser();

        refreshUserList();

        // Show username in header
        label_UserDetails.setText("User: " + theUser.getUserName());

        theStage.setTitle("Admin: Delete User");
        theStage.setScene(theDeleteUserScene);
        theStage.show();
    }

    /**********
     * <p> Constructor: ViewDeleteUser() </p>
     * 
     * <p> Description: Initializes the layout and widgets for the Delete User page. </p>
     */
    public ViewDeleteUser() {
        theRootPane = new Pane();
        theDeleteUserScene = new Scene(theRootPane, width, height);

        // Page title
        setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // User details + Account update
        setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
        setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
        button_UpdateThisUser.setOnAction((event) -> {
            guiUserUpdate.ViewUserUpdate.displayUserUpdate(theStage, theUser);
        });

        // Section separator
        theRootPane.getChildren().add(line_Separator1);

        // User selection
        setupLabelUI(label_SelectUser, "Arial", 20, 300, Pos.BASELINE_LEFT, 20, 130);
        setupComboBoxUI(combobox_SelectUser, "Dialog", 16, 250, 280, 125);

        // Delete button
        setupButtonUI(button_DeleteUser, "Dialog", 18, 200, Pos.CENTER, 280, 200);
        button_DeleteUser.setOnAction((event) -> ControllerDeleteUser.performDeleteUser());

        // Bottom separator
        theRootPane.getChildren().add(line_Separator4);

        // Bottom control buttons
        setupButtonUI(button_Return, "Dialog", 18, 210, Pos.CENTER, 20, 540);
        button_Return.setOnAction((event) -> ControllerDeleteUser.performReturn());

        setupButtonUI(button_Logout, "Dialog", 18, 210, Pos.CENTER, 300, 540);
        button_Logout.setOnAction((event) -> ControllerDeleteUser.performLogout());

        setupButtonUI(button_Quit, "Dialog", 18, 210, Pos.CENTER, 570, 540);
        button_Quit.setOnAction((event) -> ControllerDeleteUser.performQuit());

        // Add all widgets to root pane
        theRootPane.getChildren().addAll(
            label_PageTitle,
            label_UserDetails,
            button_UpdateThisUser,
            label_SelectUser,
            combobox_SelectUser,
            button_DeleteUser,
            button_Return,
            button_Logout,
            button_Quit
        );
    }

    /**********
     * <p> Method: refreshUserList() </p>
     * 
     * <p> Description: Reloads the user list into the ComboBox. </p>
     */
    protected static void refreshUserList() {
        List<String> userList = theDatabase.getUserList();
        combobox_SelectUser.setItems(FXCollections.observableArrayList(userList));
        combobox_SelectUser.getSelectionModel().select(0);
    }

    /*-*******************************************************************************************
     * Helper methods
     ********************************************************************************************/

    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y) {
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    protected static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y) {
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    private static void setupComboBoxUI(ComboBox<String> c, String ff, double f, double w, double x, double y) {
        c.setStyle("-fx-font: " + f + " " + ff + ";");
        c.setMinWidth(w);
        c.setLayoutX(x);
        c.setLayoutY(y);
    }
}
