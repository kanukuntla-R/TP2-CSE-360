package guiUserUpdate;

import java.util.Optional;

import database.Database;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import entityClasses.User;
import inputValidation.inputValidator;
import javafx.scene.control.Alert;

/*******
 * <p> Title: ViewUserUpdate Class. </p>
 * 
 * <p> Description: The Java/FX-based User Update Page.  This page enables the user to update the
 * attributes about the user held by the system.  Currently, this page does not provide a mechanism
 * to change the Username and not all of the functions on this page are implemented.
 * 
 * Currently the following attributes can be updated:
 * 		- First Name
 * 		- Middle Name
 * 		- Last Name
 * 		- Preferred First Name
 * 		- Email Address
 * The page uses dialog boxes for updating these items.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.01		2025-08-19 Initial version plus new internal documentation
 *  
 */

public class ViewUserUpdate {

	/*-********************************************************************************************

	Attributes

	 */

	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

	
	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// Unlike may of the other pages, the GUI on this page is not organized into areas and the user
	// is not able to logout, return, or quit from this page
	
	// These widgets display the purpose of the page and guide the user.
	private static Label label_ApplicationTitle = new Label("Update a User's Account Details");
    private static Label label_Purpose = 
    		new Label(" Use this page to define or update your account information."); 
    
    // These are static output labels and do not change during execution
	private static Label label_Username = new Label("Username:");
	private static Label label_Password = new Label("Password:");
	private static Label label_FirstName = new Label("First Name:");
	private static Label label_MiddleName = new Label("Middle Name:");
	private static Label label_LastName = new Label("Last Name:");
	private static Label label_PreferredFirstName = new Label("Preferred First Name:");
	private static Label label_EmailAddress = new Label("Email Address:");
	
	// These are dynamic labels and they change based on the user and user interactions.
	private static Label label_CurrentUsername = new Label();
	private static Label label_CurrentPassword = new Label();
	private static Label label_CurrentFirstName = new Label();
	private static Label label_CurrentMiddleName = new Label();
	private static Label label_CurrentLastName = new Label();
	private static Label label_CurrentPreferredFirstName = new Label();
	private static Label label_CurrentEmailAddress = new Label();
	
	// These buttons enable the user to edit the various dynamic fields.  The username and the
	// passwords for a user are currently not editable.
	private static Button button_UpdateUsername = new Button("Update Username");
	private static Button button_UpdatePassword = new Button("Update Password");
	private static Button button_UpdateFirstName = new Button("Update First Name");
	private static Button button_UpdateMiddleName = new Button("Update Middle Name");
	private static Button button_UpdateLastName = new Button("Update Last Name");
	private static Button button_UpdatePreferredFirstName = new Button("Update Preferred First Name");
	private static Button button_UpdateEmailAddress = new Button("Update Email Address");

	// This button enables the user to finish working on this page and proceed to the user's home
	// page determined by the user's role at the time of log in.
	private static Button button_ProceedToUserHomePage = new Button("Save and Exit");
	// Error message label for validation
		private static Label label_ValidationError = new Label();
	
	// This is the end of the GUI widgets for this page.
	
	// These are the set of pop-up dialog boxes that are used to enable the user to change the
	// the values of the various account detail items.
	private static TextInputDialog dialogUpdatePassword;
	private static TextInputDialog dialogUpdateFirstName;
	private static TextInputDialog dialogUpdateMiddleName;
	private static TextInputDialog dialogUpdateLastName;
	private static TextInputDialog dialogUpdatePreferredFirstName;
	private static TextInputDialog dialogUpdateEmailAddresss;
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewUserUpdate theView;	// Used to determine if instantiation of the class
											// is needed

	// This enables access to the application's database
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;				// The Stage that JavaFX has established for us	
	private static Pane theRootPane;			// The Pane that holds all the GUI widgets
	private static User theUser;				// The current user of the application

	public static Scene theUserUpdateScene = null;	// The Scene each invocation populates

	private static Optional<String> result;		// The result from a pop-up dialog
	
	protected static Alert alertEmailError = new Alert(AlertType.INFORMATION);

	/*-********************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayUserUpdate(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the UserUpdate page to be displayed.
	 * 
	 * It first sets up very shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User whose roles will be updated
	 *
	 */
	public static void displayUserUpdate(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theUser = user;
		theStage = ps;
		
		// If not yet established, populate the static aspects of the GUI by creating the 
		// singleton instance of this class
		if (theView == null) theView = new ViewUserUpdate();
		
		// Set the widget values that change from use of page to another use of the page.
		String s = "";
		
		// Set the dynamic aspects of the window based on the user logged in and the current state
		// of the various account elements.
		s = theUser.getUserName();
		System.out.println("*** Fetching account data for user: " + s);
    	if (s == null || s.length() < 1)label_CurrentUsername.setText("<none>");
    	else label_CurrentUsername.setText(s);
		
		s = theUser.getPassword();
    	if (s == null || s.length() < 1)label_CurrentPassword.setText("<none>");
    	else label_CurrentPassword.setText(s);
    	
		s = theUser.getFirstName();
    	if (s == null || s.length() < 1)label_CurrentFirstName.setText("<none>");
    	else label_CurrentFirstName.setText(s);
       
        s = theUser.getMiddleName();
    	if (s == null || s.length() < 1)label_CurrentMiddleName.setText("<none>");
    	else label_CurrentMiddleName.setText(s);
        
        s = theUser.getLastName();
    	if (s == null || s.length() < 1)label_CurrentLastName.setText("<none>");
    	else label_CurrentLastName.setText(s);
        
		s = theUser.getPreferredFirstName();
    	if (s == null || s.length() < 1)label_CurrentPreferredFirstName.setText("<none>");
    	else label_CurrentPreferredFirstName.setText(s);
        
		s = theUser.getEmailAddress();
    	if (s == null || s.length() < 1)label_CurrentEmailAddress.setText("<none>");
    	else label_CurrentEmailAddress.setText(s);
    	
    	// Validate fields and update button state
    	validateAndUpdateButtonState();

		// Set the title for the window, display the page, and wait for the Admin to do something
    	theStage.setTitle("CSE 360 Foundation Code: Update User Account Details");
        theStage.setScene(theUserUpdateScene);
		theStage.show();
	}

	/**********
	 * Check if all fields are valid and enable/disable Save and Exit button
	 */
	private static void validateAndUpdateButtonState() {
	    // Get current values from the UI labels, not the user object
		String firstName = label_CurrentFirstName.getText();
		String middleName = label_CurrentMiddleName.getText();
		String lastName = label_CurrentLastName.getText();
		String preferredFirstName = label_CurrentPreferredFirstName.getText();
		String email = label_CurrentEmailAddress.getText();
	    
	    // Convert "<none>" to empty string for validation
		if ("<none>".equals(firstName)) firstName = "";
		if ("<none>".equals(middleName)) middleName = "";
		if ("<none>".equals(lastName)) lastName = "";
		if ("<none>".equals(preferredFirstName)) preferredFirstName = "";
		if ("<none>".equals(email)) email = "";
	    
	    // Check if any field is empty or invalid
	    String validationError = inputValidator.validateAllFields(
	        firstName != null ? firstName : "",
	        middleName != null ? middleName : "",
	        lastName != null ? lastName : "",
	        preferredFirstName != null ? preferredFirstName : "",
	        email != null ? email : ""
	    );
	    
	    if (validationError.isEmpty()) {
	        // All fields are valid, enable Save and Exit button
	        button_ProceedToUserHomePage.setDisable(false);
	        label_ValidationError.setText("");
	    } else {
	        // Some fields are invalid, disable Save and Exit button
	        button_ProceedToUserHomePage.setDisable(true);
	        label_ValidationError.setText(validationError);
	    }
	}
	
	/**********
	 * <p> Method: ViewUserUpdate() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayUserUpdate method.</p>
	 * 
	 */
	
	private ViewUserUpdate() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theUserUpdateScene = new Scene(theRootPane, width, height);

		// Initialize the pop-up dialogs to an empty text filed.
		dialogUpdatePassword = new TextInputDialog("");
		dialogUpdateFirstName = new TextInputDialog("");
		dialogUpdateMiddleName = new TextInputDialog("");
		dialogUpdateLastName = new TextInputDialog("");
		dialogUpdatePreferredFirstName = new TextInputDialog("");
		dialogUpdateEmailAddresss = new TextInputDialog("");

		// Establish the label for each of the dialogs.
		dialogUpdatePassword.setTitle("Update Password");
		dialogUpdatePassword.setHeaderText("Update your Password");
		
		dialogUpdateFirstName.setTitle("Update First Name");
		dialogUpdateFirstName.setHeaderText("Update your First Name");
		
		dialogUpdateMiddleName.setTitle("Update Middle Name");
		dialogUpdateMiddleName.setHeaderText("Update your Middle Name");
		
		dialogUpdateLastName.setTitle("Update Last Name");
		dialogUpdateLastName.setHeaderText("Update your Last Name");
		
		dialogUpdatePreferredFirstName.setTitle("Update Preferred First Name");
		dialogUpdatePreferredFirstName.setHeaderText("Update your Preferred First Name");
		
		dialogUpdateEmailAddresss.setTitle("Update Email Address");
		dialogUpdateEmailAddresss.setHeaderText("Update your Email Address");

		// Add validation error label
		setupLabelUI(label_ValidationError, "Arial", 14, width, Pos.CENTER, 0, 500);
		label_ValidationError.setStyle("-fx-text-fill: red;");
		
		// Label theScene with the name of the startup screen, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        // Label to display the welcome message for the first theUser
        setupLabelUI(label_Purpose, "Arial", 20, width, Pos.CENTER, 0, 50);
        
        // Display the titles, values, and update buttons for the various admin account attributes.
        // If the attributes is null or empty, display "<none>".
        
        // USername
        setupLabelUI(label_Username, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 100);
        setupLabelUI(label_CurrentUsername, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 100);
        setupButtonUI(button_UpdateUsername, "Dialog", 18, 275, Pos.CENTER, 500, 93);
       
        // password
        setupLabelUI(label_Password, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 150);
        setupLabelUI(label_CurrentPassword, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 150);
        setupButtonUI(button_UpdatePassword, "Dialog", 18, 275, Pos.CENTER, 500, 143);
        
     // password
        setupLabelUI(label_Password, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 150);
        setupLabelUI(label_CurrentPassword, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 150);
        setupButtonUI(button_UpdatePassword, "Dialog", 18, 275, Pos.CENTER, 500, 143);
        button_UpdatePassword.setOnAction((event) -> {
            result = dialogUpdatePassword.showAndWait();
            result.ifPresent(password -> {
                // Use the existing password validation - much cleaner!
                String validationError = passwordPopUpWindow.Model.evaluatePassword(password);
                if (!validationError.isEmpty()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invalid Password");
                    alert.setHeaderText("Password does not meet requirements");
                    alert.setContentText(validationError);
                    alert.showAndWait();
                    return;
                }
                
                // Update the database
                theDatabase.updatePassword(theUser.getUserName(), password);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newPassword = theDatabase.getCurrentPassword();
                theUser.setPassword(newPassword);
                if (newPassword == null || newPassword.length() < 1) {
                    label_CurrentPassword.setText("<none>");
                } else {
                	// Show actual password
                    label_CurrentPassword.setText(newPassword);
                }
                
                validateAndUpdateButtonState();
            });
        });
                
        
        
        // First Name
        setupLabelUI(label_FirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 200);
        setupLabelUI(label_CurrentFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 200);
        setupButtonUI(button_UpdateFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 193);
        button_UpdateFirstName.setOnAction((event) -> {
        	dialogUpdateFirstName.getEditor().setText(""); // reset before showing
            result = dialogUpdateFirstName.showAndWait();
            result.ifPresent(name -> {
                String validationError = inputValidator.checkForValidName(name, "First Name");
                if (!validationError.isEmpty()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("First Name Error");
                    alert.setContentText(validationError);
                    alert.showAndWait();
                    return;
                }
                
                theDatabase.updateFirstName(theUser.getUserName(), name);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newName = theDatabase.getCurrentFirstName();
                theUser.setFirstName(newName);
                if (newName == null || newName.length() < 1) {
                    label_CurrentFirstName.setText("<none>");
                } else {
                    label_CurrentFirstName.setText(newName);
                }
                
                validateAndUpdateButtonState();
            });
        });
               
        // Middle Name
        setupLabelUI(label_MiddleName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 250);
        setupLabelUI(label_CurrentMiddleName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 250);
        setupButtonUI(button_UpdateMiddleName, "Dialog", 18, 275, Pos.CENTER, 500, 243);
        button_UpdateMiddleName.setOnAction((event) -> {
        	dialogUpdateMiddleName.getEditor().setText(""); // reset before showing
            result = dialogUpdateMiddleName.showAndWait();
            result.ifPresent(name -> {
                String validationError = inputValidator.checkForValidName(name, "Middle Name");
                if (!validationError.isEmpty()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Middle Name Error");
                    alert.setContentText(validationError);
                    alert.showAndWait();
                    return;
                }
                
                theDatabase.updateMiddleName(theUser.getUserName(), name);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newName = theDatabase.getCurrentMiddleName();
                theUser.setMiddleName(newName);
                if (newName == null || newName.length() < 1) {
                    label_CurrentMiddleName.setText("<none>");
                } else {
                    label_CurrentMiddleName.setText(newName);
                }
                
                validateAndUpdateButtonState();
            });
        });
        
        // Last Name
        setupLabelUI(label_LastName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 300);
        setupLabelUI(label_CurrentLastName, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 300);
        setupButtonUI(button_UpdateLastName, "Dialog", 18, 275, Pos.CENTER, 500, 293);
        button_UpdateLastName.setOnAction((event) -> {
        	dialogUpdateLastName.getEditor().setText(""); // reset before showing
            result = dialogUpdateLastName.showAndWait();
            result.ifPresent(name -> {
                String validationError = inputValidator.checkForValidName(name, "Last Name");
                if (!validationError.isEmpty()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Last Name Error");
                    alert.setContentText(validationError);
                    alert.showAndWait();
                    return;
                }
                
                theDatabase.updateLastName(theUser.getUserName(), name);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newName = theDatabase.getCurrentLastName();
                theUser.setLastName(newName);
                if (newName == null || newName.length() < 1) {
                    label_CurrentLastName.setText("<none>");
                } else {
                    label_CurrentLastName.setText(newName);
                }
                
                validateAndUpdateButtonState();
            });
        });
        
        // Preferred First Name
        setupLabelUI(label_PreferredFirstName, "Arial", 18, 190, Pos.BASELINE_RIGHT, 
        		5, 350);
        setupLabelUI(label_CurrentPreferredFirstName, "Arial", 18, 260, Pos.BASELINE_LEFT, 
        		200, 350);
        setupButtonUI(button_UpdatePreferredFirstName, "Dialog", 18, 275, Pos.CENTER, 500, 343);
        button_UpdatePreferredFirstName.setOnAction((event) -> {
        	dialogUpdatePreferredFirstName.getEditor().setText(""); // reset before showing
            result = dialogUpdatePreferredFirstName.showAndWait();
            result.ifPresent(name -> {
                String validationError = inputValidator.checkForValidName(name, "Preferred First Name");
                if (!validationError.isEmpty()) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Preferred First Name Error");
                    alert.setContentText(validationError);
                    alert.showAndWait();
                    return;
                }
                
                theDatabase.updatePreferredFirstName(theUser.getUserName(), name);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newName = theDatabase.getCurrentPreferredFirstName();
                theUser.setPreferredFirstName(newName);
                if (newName == null || newName.length() < 1) {
                    label_CurrentPreferredFirstName.setText("<none>");
                } else {
                    label_CurrentPreferredFirstName.setText(newName);
                }
                
                validateAndUpdateButtonState();
            });
        });
        
        // Email Address
        setupLabelUI(label_EmailAddress, "Arial", 18, 190, Pos.BASELINE_RIGHT, 5, 400);
        setupLabelUI(label_CurrentEmailAddress, "Arial", 18, 260, Pos.BASELINE_LEFT, 200, 400);
        setupButtonUI(button_UpdateEmailAddress, "Dialog", 18, 275, Pos.CENTER, 500, 393);
        button_UpdateEmailAddress.setOnAction((event) -> {
        dialogUpdateEmailAddresss.getEditor().setText(""); // reset before showing
            
            result = dialogUpdateEmailAddresss.showAndWait();
            result.ifPresent(inputEmail -> {
                // Validate email BEFORE updating the database
                String validationError = inputValidation.inputValidator.checkForValidEmail(inputEmail);
                
                if (!validationError.isEmpty()) {
                    // Show validation error to user
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Invalid Email");
                    errorAlert.setHeaderText("Email Validation Error");
                    errorAlert.setContentText(validationError);
                    errorAlert.showAndWait();
                    // Don't update, keep previous email
                    return;
                }
                
                // Email is valid, update it
                theDatabase.updateEmailAddress(theUser.getUserName(), inputEmail);
                theDatabase.getUserAccountDetails(theUser.getUserName());
                String newEmail = theDatabase.getCurrentEmailAddress();
                
                if (newEmail == null || newEmail.length() < 1) {
                    label_CurrentEmailAddress.setText("<none>");
                } else {
                    label_CurrentEmailAddress.setText(newEmail);
                }
            });
            
            validateAndUpdateButtonState();
        });
        
        // Set up the button to proceed to this user's home page
        setupButtonUI(button_ProceedToUserHomePage, "Dialog", 18, 300, 
        		Pos.CENTER, width/2-150, 450);
        button_ProceedToUserHomePage.setDisable(true); // Start disabled
        button_ProceedToUserHomePage.setOnAction((event) -> 
        	{ControllerUserUpdate.goToUserHomePage(theStage, theUser);});
    	
        // Populate the Pane's list of children widgets
        theRootPane.getChildren().addAll(
        		label_ApplicationTitle, label_Purpose, label_Username,
                label_CurrentUsername, 
                label_Password, label_CurrentPassword, 
                button_UpdatePassword, 
                label_FirstName, label_CurrentFirstName, button_UpdateFirstName,
                label_MiddleName, label_CurrentMiddleName, button_UpdateMiddleName,
                label_LastName, label_CurrentLastName, button_UpdateLastName,
                label_PreferredFirstName, label_CurrentPreferredFirstName,
                button_UpdatePreferredFirstName, button_UpdateEmailAddress,
                label_EmailAddress, label_CurrentEmailAddress, 
                button_ProceedToUserHomePage, label_ValidationError);
	}
	
	
	
	/*-********************************************************************************************

	Helper methods to reduce code length

	 */
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 * 
	 * @param l		The Label object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	
	/**********
	 * Private local method to initialize the standard fields for a button
	 * 
	 * @param b		The Button object to be initialized
	 * @param ff	The font to be used
	 * @param f		The size of the font to be used
	 * @param w		The width of the Button
	 * @param p		The alignment (e.g. left, centered, or right)
	 * @param x		The location from the left edge (x axis)
	 * @param y		The location from the top (y axis)
	 */
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
}
