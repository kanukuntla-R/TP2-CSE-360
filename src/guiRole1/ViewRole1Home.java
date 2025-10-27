package guiRole1;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import database.Database;
import entityClasses.User;
import guiUserUpdate.ViewUserUpdate;

// ====== added imports for GUI Area 2 ======
import entityClasses.Post;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
// ==========================================

/*******
 * <p> Title: GUIReviewerHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Student Home Page.  The page is a stub for some role needed for
 * the application.  The widgets on this page are likely the minimum number and kind for other role
 * pages that may be needed.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-20 Initial version
 *  
 */

public class ViewRole1Home {
	
	/*-*******************************************************************************************

	Attributes
	
	 */
	
	// These are the application values required by the user interface
	
	private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
	private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;


	// These are the widget attributes for the GUI. There are 3 areas for this GUI.
	
	// GUI Area 1: It informs the user about the purpose of this page, whose account is being used,
	// and a button to allow this user to update the account settings
	protected static Label label_PageTitle = new Label();
	protected static Label label_UserDetails = new Label();
	protected static Button button_UpdateThisUser = new Button("Account Update");
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator1 = new Line(20, 95, width-20, 95);

	// GUI ARea 2: This is a stub, so there are no widgets here.  For an actual role page, this are
	// would contain the widgets needed for the user to play the assigned role.

	// ====== BEGIN: GUI Area 2 wiring (updated) ======
	protected static final ObservableList<Map<String,Object>> postsUI =
	        FXCollections.observableArrayList();

	protected static ComboBox<String> cbThread = new ComboBox<>();
	protected static ComboBox<String> cbFilter = new ComboBox<>();
	protected static Button btnCreate = new Button("Create");
	protected static Button btnRead   = new Button("Read");
	protected static Button btnUpdate = new Button("Update");
	protected static Button btnDelete = new Button("Delete");

	protected static ListView<Map<String,Object>> lvPosts = new ListView<>();
	// This is the Search bar above posts
	protected static TextField tfSearch = new TextField();
	protected static Button btnOpenSearch = new Button("Search Posts");
	// ====== END: GUI Area 2 wiring (updated) ======
	
	// This is a separator and it is used to partition the GUI for various tasks
	protected static Line line_Separator4 = new Line(20, 525, width-20,525);
	
	// GUI Area 3: This is last of the GUI areas.  It is used for quitting the application and for
	// logging out.
	protected static Button button_Logout = new Button("Logout");
	protected static Button button_Quit = new Button("Quit");

	// This is the end of the GUI objects for the page.
	
	// These attributes are used to configure the page and populate it with this user's information
	private static ViewRole1Home theView;		// Used to determine if instantiation of the class
												// is needed

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	protected static Stage theStage;			// The Stage that JavaFX has established for us	
	protected static Pane theRootPane;			// The Pane that holds all the GUI widgets
	protected static User theUser;				// The current logged in User
	

	private static Scene theViewRole1HomeScene;	// The shared Scene each invocation populates
	protected static final int theRole = 2;		// Admin: 1; Role1: 2; Role2: 3

	/*-*******************************************************************************************

	Constructors
	
	 */


	/**********
	 * <p> Method: displayRole1Home(Stage ps, User user) </p>
	 * 
	 * <p> Description: This method is the single entry point from outside this package to cause
	 * the Student Home page to be displayed.
	 * 
	 * It first sets up every shared attributes so we don't have to pass parameters.
	 * 
	 * It then checks to see if the page has been setup.  If not, it instantiates the class, 
	 * initializes all the static aspects of the GIUI widgets (e.g., location on the page, font,
	 * size, and any methods to be performed).
	 * 
	 * After the instantiation, the code then populates the elements that change based on the user
	 * and the system's current state.  It then sets the Scene onto the stage, and makes it visible
	 * to the user.
	 * 
	 * @param ps specifies the JavaFX Stage to be used for this GUI and it's methods
	 * 
	 * @param user specifies the User for this GUI and it's methods
	 * 
	 */
	
	public static void displayRole1Home(Stage ps, User user) {
		
		// Establish the references to the GUI and the current user
		theStage = ps;
		theUser = user;
		
		// If not yet established, populate the static aspects of the GUI
		if (theView == null) theView = new ViewRole1Home();		// Instantiate singleton if needed
		
		// Populate the dynamic aspects of the GUI with the data from the user and the current
		// state of the system.
		theDatabase.getUserAccountDetails(user.getUserName());
		applicationMain.FoundationsMain.activeHomePage = theRole;
		
		label_UserDetails.setText("User: " + theUser.getUserName());
				
		// Set the title for the window, display the page, and wait for the Admin to do something
		theStage.setTitle("CSE 360 Foundations: Student Home Page");
		theStage.setScene(theViewRole1HomeScene);
		theStage.show();
	}
	
	/**********
	 * <p> Method: ViewRole1Home() </p>
	 * 
	 * <p> Description: This method initializes all the elements of the graphical user interface.
	 * This method determines the location, size, font, color, and change and event handlers for
	 * each GUI object.</p>
	 * 
	 * This is a singleton and is only performed once.  Subsequent uses fill in the changeable
	 * fields using the displayRole2Home method.</p>
	 * 
	 */
	
	private ViewRole1Home() {

		// Create the Pane for the list of widgets and the Scene for the window
		theRootPane = new Pane();
		theViewRole1HomeScene = new Scene(theRootPane, width, height);	// Create the scene
		
		// Set the title for the window
		
		// Populate the window with the title and other common widgets and set their static state
		
		// GUI Area 1
		label_PageTitle.setText("Student Home Page");
		setupLabelUI(label_PageTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

		label_UserDetails.setText("User: " + theUser.getUserName());
		setupLabelUI(label_UserDetails, "Arial", 20, width, Pos.BASELINE_LEFT, 20, 55);
		
		setupButtonUI(button_UpdateThisUser, "Dialog", 18, 170, Pos.CENTER, 610, 45);
		button_UpdateThisUser.setOnAction((event) ->
			{ViewUserUpdate.displayUserUpdate(theStage, theUser); });
		
		// GUI Area 2
		// ====== added posts/replies UI ======
		
		// Thread dropdown setup
		cbThread.getItems().setAll("All Threads", "General", "Lectures", "Sections", "Problem Sets", "Assignments", "Social");
		cbThread.getSelectionModel().select(0); // Default to "All Threads"
		cbThread.relocate(20, 110); // Position above Create button

		// This is the Search bar (above posts list)
		tfSearch.setPromptText("Enter keyword (press Search Posts to open full search)");
		tfSearch.setLayoutX(180);
		tfSearch.setLayoutY(120);
		tfSearch.setPrefWidth(420);

		btnOpenSearch.setLayoutX(610);
		btnOpenSearch.setLayoutY(120);
		setupButtonUI(btnOpenSearch, "Dialog", 14, 140, Pos.CENTER, 610, 120);
		btnOpenSearch.setOnAction(e -> ControllerRole1Home.openSearch());

		// Custom cell factory with colored circles
		cbThread.setCellFactory(listView -> new ListCell<String>() {
		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) {
		            setText(null);
		            setGraphic(null);
		        } else {
		            // Create colored circle
		            Circle circle = new Circle(6);
		            circle.setFill(getThreadColor(item));
		            
		            // Create label with text
		            Label label = new Label(item);
		            
		            // Create HBox to hold circle + text
		            HBox hbox = new HBox(8, circle, label);
		            hbox.setAlignment(Pos.CENTER_LEFT);
		            
		            setGraphic(hbox);
		            setText(null);
		        }
		    }
		});

		cbFilter.getItems().setAll("All posts", "My posts");
		cbFilter.getSelectionModel().select(0);
		cbFilter.relocate(20, 340);
		ControllerRole1Home.setupFilterListener();

		int leftX = 20, topY = 155, gap = 45, widthBtn = 140;
		setupButtonUI(btnCreate, "Dialog", 14, widthBtn, Pos.CENTER, leftX, topY);
		setupButtonUI(btnRead,   "Dialog", 14, widthBtn, Pos.CENTER, leftX, topY + gap);
		setupButtonUI(btnUpdate, "Dialog", 14, widthBtn, Pos.CENTER, leftX, topY + 2*gap);
		setupButtonUI(btnDelete, "Dialog", 14, widthBtn, Pos.CENTER, leftX, topY + 3*gap);

		lvPosts.setLayoutX(180);
		lvPosts.setLayoutY(155);
		lvPosts.setPrefWidth(width - 200);
		lvPosts.setPrefHeight(350);
		lvPosts.setItems(postsUI);

		// each item is now a Map<String,Object> row from the DB (Posts table)
		lvPosts.setCellFactory(v -> new ListCell<>() {
		    @Override protected void updateItem(Map<String,Object> row, boolean empty) {
		        super.updateItem(row, empty);
		        if (empty || row == null) {
		            setText(null);
		            setGraphic(null);
		            return;
		        }

		        String idStr    = Integer.toString(ControllerRole1Home.getInt(row, "id"));
		        String title    = ControllerRole1Home.getString(row, "title");
		        String author   = ControllerRole1Home.getString(row, "authorUsername");
		        String thread   = ControllerRole1Home.getString(row, "thread");
		        boolean deleted = Post.getBoolCI(row, "isDeleted");
		        
		        // Create styled text with colored thread
		        TextFlow textFlow = new TextFlow();
		        
		        if (deleted) {
		            Text deletedText = new Text("[Deleted] " + title + " — " + author + " — ");
		            Text threadText = new Text(thread);
		            threadText.setFill(getThreadColor(thread));
		            textFlow.getChildren().addAll(deletedText, threadText);
		        } else {
		            Text idText = new Text("#" + idStr + "  " + title + " — " + author + " — ");
		            Text threadText = new Text(thread);
		            threadText.setFill(getThreadColor(thread));
		            textFlow.getChildren().addAll(idText, threadText);
		        }
		        
		        setGraphic(textFlow);
		        setText(null);
		    }
		});

		btnCreate.setOnAction(e -> ControllerRole1Home.createPost());
		btnRead.setOnAction(e -> ControllerRole1Home.readPost());
		btnUpdate.setOnAction(e -> ControllerRole1Home.updatePost());
		btnDelete.setOnAction(e -> ControllerRole1Home.deletePost());

		ControllerRole1Home.setupDoubleClickHandler();
		ControllerRole1Home.setupPostSelectionListener();
		ControllerRole1Home.setupThreadListener();

		btnRead.setDisable(true);
		btnUpdate.setDisable(true);
		btnDelete.setDisable(true);

		// Initial load: All posts
		ControllerRole1Home.loadPosts(false, "All Threads");
		// ====== END: added posts/replies UI ======
		
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerRole1Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> {ControllerRole1Home.performQuit(); });

		// This is the end of the GUI initialization code
		// This places all of the widget items into the Root Pane's list of children
		theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			// Area 2 widgets
			cbThread, cbFilter, btnCreate, btnRead, btnUpdate, btnDelete, lvPosts,
			// This is the search bar widgets
			tfSearch, btnOpenSearch,
			// Area 3
			line_Separator4, button_Logout, button_Quit
		);
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
	
	private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, 
			double y){
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
	
	private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, 
			double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}

	/**********
	 * <p> Method: getThreadColor(String thread) </p>
	 * 
	 * <p> Description: Returns the color associated with a specific thread type.
	 * Used for consistent color coding in the UI components. </p>
	 * 
	 * @param thread the thread type name
	 * @return the Color object for the thread type, or GRAY for unknown types
	 */
	
	private static Color getThreadColor(String thread) {
	    switch (thread) {
	        case "General": return Color.BLUE;           // Blue
	        case "Lectures": return Color.GREEN;          // Green  
	        case "Sections": return Color.ORANGE;         // Orange
	        case "Problem Sets": return Color.RED;        // Red
	        case "Assignments": return Color.PURPLE;      // Purple
	        case "Social": return Color.CYAN;             // Turquoise (lighter blue)
	        default: return Color.GRAY;
	    }
	}

}