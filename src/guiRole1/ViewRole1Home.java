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
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ListCell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
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
	private static final ObservableList<Map<String,Object>> postsUI =
	        FXCollections.observableArrayList();

	private static ComboBox<String> cbFilter = new ComboBox<>();
	private static Button btnCreate = new Button("Create");
	private static Button btnRead   = new Button("Read");
	private static Button btnUpdate = new Button("Update");
	private static Button btnDelete = new Button("Delete");

	private static ListView<Map<String,Object>> lvPosts = new ListView<>();
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
		cbFilter.getItems().setAll("All posts", "My posts");
		cbFilter.getSelectionModel().select(0);
		cbFilter.relocate(20, 110);
		cbFilter.valueProperty().addListener((obs, ov, nv) -> loadPosts("My posts".equals(nv)));

		// initial load: All posts
		loadPosts(false);

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
		            return;
		        }

		        String idStr    = Integer.toString(getInt(row, "id"));
		        String title    = getString(row, "title");
		        String author   = getString(row, "authorUsername");
		        boolean deleted = Post.getBoolCI(row, "isDeleted");
		        String txt = deleted
		            ? "[Deleted] " + title + " — " + author
		            : "#" + idStr + "  " + title + " — " + author;

		        setText(txt);
		    }
		});

		btnCreate.setOnAction(e -> openEditor(null));

		btnRead.setOnAction(e -> {
		    Map<String,Object> row = lvPosts.getSelectionModel().getSelectedItem();
		    if (row != null) openReader(row);
		});

		lvPosts.setOnMouseClicked(ev -> {
		    if (ev.getClickCount() == 2) {
		        Map<String,Object> row = lvPosts.getSelectionModel().getSelectedItem();
		        if (row != null) openReader(row);
		    }
		});

		btnUpdate.setOnAction(e -> {
		    Map<String,Object> row = lvPosts.getSelectionModel().getSelectedItem();
		    if (row == null) return;
		    if (!owns(row)) { info("You can only update your own posts."); return; }
		    if (Post.getBoolCI(row, "isDeleted")) { info("This post is deleted and cannot be edited."); return; }
		    openEditor(row);
		});

		btnDelete.setOnAction(e -> {
		    Map<String,Object> row = lvPosts.getSelectionModel().getSelectedItem();
		    if (row == null) return;
		    if (!owns(row)) { info("You can only delete your own posts."); return; }

		    Alert confirm = new Alert(AlertType.CONFIRMATION,
		            "Are you sure you want to delete this post?", ButtonType.YES, ButtonType.NO);

		    confirm.showAndWait().ifPresent(bt -> {
		        if (bt == ButtonType.YES) {
		            int postId = getInt(row, "id");
		            String err = Post.deletePost(postId); 
		            if (!err.isEmpty()) {
		                info(err);
		                return;
		            }
		            boolean mine = "My posts".equals(cbFilter.getValue());
		            loadPosts(mine);
		        }
		    });
		});

		lvPosts.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
		    boolean has = (b != null);
		    btnRead.setDisable(!has);

		    boolean mineActive = false;
		    if (has) {
		        boolean iOwn = owns(b);
		        boolean notDeleted = !Post.getBoolCI(b, "isDeleted");
		        mineActive = iOwn && notDeleted;
		    }

		    btnUpdate.setDisable(!mineActive);
		    btnDelete.setDisable(!mineActive);
		});

		btnRead.setDisable(true);
		btnUpdate.setDisable(true);
		btnDelete.setDisable(true);
		// ====== END: added posts/replies UI ======
		
		
		// GUI Area 3
        setupButtonUI(button_Logout, "Dialog", 18, 250, Pos.CENTER, 20, 540);
        button_Logout.setOnAction((event) -> {ControllerRole1Home.performLogout(); });
        
        setupButtonUI(button_Quit, "Dialog", 18, 250, Pos.CENTER, 300, 540);
        button_Quit.setOnAction((event) -> {ControllerRole1Home.performQuit(); });

		// This is the end of the GUI initialization code
		
		// Place all of the widget items into the Root Pane's list of children
        theRootPane.getChildren().addAll(
			label_PageTitle, label_UserDetails, button_UpdateThisUser, line_Separator1,
			// Area 2 widgets (added)
			cbFilter, btnCreate, btnRead, btnUpdate, btnDelete, lvPosts,
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

	// ====== Added helpers / dialogs ======
	
	/*****
	 * Convenience accessor for the username of the currently logged-in user.
	 *
	 * @return The current user's username, or the empty string if no user is set.
	 */
	private static String currentUser(){ return theUser != null ? theUser.getUserName() : ""; }
	
	
	private static boolean owns(Map<String,Object> row){
	    if (row == null) return false;
	    String author = getString(row, "authorUsername");
	    return currentUser().equals(author);
	}
	
	/*****
	 * Show a simple informational dialog with an OK button.
	 * <p>
	 * This helper is used for lightweight, blocking notifications (e.g., permission
	 * warnings like “You can only update your own posts.”) without changing the page
	 * layout or status area.
	 * </p>
	 *
	 * @param msg The message text to display in the dialog.
	 */
	private static void info(String msg){ new Alert(AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }

	// Refresh the repliesUI list for a given postId.
	// This re-queries the DB (via Post.fetchRepliesForPost) and replaces the ObservableList contents.
	private static void reloadRepliesForPost(int postId,
	                                         ObservableList<Map<String,Object>> repliesUI) {
	    List<Map<String,Object>> repl = Post.fetchRepliesForPost(postId);
	    repliesUI.setAll(repl);
	}

	private static void loadPosts(boolean mineOnly){
		 boolean includeDeleted = true;
		 List<Map<String,Object>> rows = Post.fetchPosts(
	            mineOnly,
	            currentUser(),
	            includeDeleted
	    );
	    postsUI.setAll(rows);
	}

	// get a String column from a DB row (case-insensitive)
	private static String getString(Map<String,Object> row, String key) {
	    for (String k : row.keySet()) {
	        if (k.equalsIgnoreCase(key)) {
	            Object v = row.get(k);
	            return v == null ? "" : v.toString();
	        }
	    }
	    return "";
	}

	// get an int column from a DB row (case-insensitive)
	private static int getInt(Map<String,Object> row, String key) {
	    for (String k : row.keySet()) {
	        if (k.equalsIgnoreCase(key)) {
	            Object v = row.get(k);
	            if (v instanceof Number) return ((Number)v).intValue();
	            try { return Integer.parseInt(String.valueOf(v)); } catch (Exception ignored) {}
	        }
	    }
	    return -1;
	}


	
	private static void openEditor(Map<String,Object> existingRow) {
	    // modal child window bound to the main stage
	    Stage dlg = new Stage();
	    dlg.initOwner(theStage);
	    dlg.initModality(Modality.APPLICATION_MODAL);

	    boolean editing = (existingRow != null);
	    dlg.setTitle(editing ? "Update Post" : "Create Post");

	    // If we're editing, block editing deleted posts
	    if (editing && Post.getBoolCI(existingRow, "isDeleted")) {
	        info("This post was deleted and cannot be edited.");
	        return;
	    }

	    // lightweight layout for a small form
	    Pane root = new Pane();
	    Scene sc = new Scene(root, 420, 260);
	    dlg.setScene(sc);

	    // --- Title field ---
	    Label lblT = new Label("Post title");
	    setupLabelUI(lblT, "Arial", 14, 380, Pos.BASELINE_LEFT, 20, 20);

	    TextField tfTitle = new TextField(
	        editing ? getString(existingRow, "title") : ""
	    );
	    tfTitle.setLayoutX(20);
	    tfTitle.setLayoutY(45);
	    tfTitle.setPrefWidth(380);

	    // --- Body field ---
	    Label lblB = new Label("Post message");
	    setupLabelUI(lblB, "Arial", 14, 380, Pos.BASELINE_LEFT, 20, 80);

	    TextArea taBody = new TextArea(
	        editing ? getString(existingRow, "body") : ""
	    );
	    taBody.setLayoutX(20);
	    taBody.setLayoutY(105);
	    taBody.setPrefWidth(380);
	    taBody.setPrefHeight(90);

	    // --- Dialog buttons ---
	    Button btnOk = new Button(editing ? "Save" : "Create");
	    setupButtonUI(btnOk, "Dialog", 14, 100, Pos.CENTER, 300, 210);

	    Button btnCancel = new Button("Cancel");
	    setupButtonUI(btnCancel, "Dialog", 14, 100, Pos.CENTER, 190, 210);

	    root.getChildren().addAll(lblT, tfTitle, lblB, taBody, btnOk, btnCancel);

	    // close without changes
	    btnCancel.setOnAction(e -> dlg.close());

	    // validate + save
	    btnOk.setOnAction(e -> {
	        String t = tfTitle.getText().trim();
	        String b = taBody.getText().trim();

	        // remember the current filter so we reload the same view after save
	        boolean mine = "My posts".equals(cbFilter.getValue());

	        String err;
	        if (!editing) {
	            // create new post
	            err = Post.createPost(currentUser(), t, b);
	        } else {
	            // update existing post
	            int postId = getInt(existingRow, "id");
	            err = Post.updatePost(postId, t, b);
	        }

	        if (!err.isEmpty()) {
	            new Alert(AlertType.ERROR, err).showAndWait();
	            return;
	        }

	        // reload list honoring the active filter ("All posts" vs "My posts")
	        loadPosts(mine);

	        // done
	        dlg.close();
	    });

	    dlg.showAndWait();
	}
	
	
	private static void openReader(Map<String,Object> postRow) {
	    if (postRow == null) return;

	    // pull fields from the selected post row
	    int    postId       = getInt(postRow, "id");
	    String postTitle    = getString(postRow, "title");
	    String postBody     = getString(postRow, "body");
	    String postAuthor   = getString(postRow, "authorUsername");
	    boolean postDeleted = Post.getBoolCI(postRow, "isDeleted");

	    String displayTitle = postDeleted ? "[Deleted Post]" : postTitle;
	    String displayBody  = postDeleted ? "[This post has been deleted]" : postBody;

	    // modal child window for reading a single post + replies
	    Stage dlg = new Stage();
	    dlg.initOwner(theStage);
	    dlg.initModality(Modality.APPLICATION_MODAL);
	    dlg.setTitle("Post #" + postId + " — " + displayTitle);

	    Pane root = new Pane();
	    Scene sc = new Scene(root, 600, 420);
	    dlg.setScene(sc);

	    // --- Post header + body (read-only) ---
	    Label lblTitle = new Label(displayTitle + "  (by " + postAuthor + ")");
	    setupLabelUI(lblTitle, "Arial", 18, 560, Pos.BASELINE_LEFT, 20, 15);

	    TextArea taBody = new TextArea(displayBody);
	    taBody.setEditable(false);
	    taBody.setWrapText(true);
	    taBody.setLayoutX(20);
	    taBody.setLayoutY(45);
	    taBody.setPrefWidth(560);
	    taBody.setPrefHeight(160);

	    // --- Replies list ---
	    Label lblReplies = new Label("Replies");
	    setupLabelUI(lblReplies, "Arial", 14, 560, Pos.BASELINE_LEFT, 20, 215);

	    // replies for THIS post
	    ObservableList<Map<String,Object>> repliesUI = FXCollections.observableArrayList();

	    ListView<Map<String,Object>> lv = new ListView<>();
	    lv.setLayoutX(20);
	    lv.setLayoutY(235);
	    lv.setPrefWidth(560);
	    lv.setPrefHeight(120);
	    lv.setItems(repliesUI);

	    // cell text for each reply: "authorUsername: body"
	    lv.setCellFactory(v -> new ListCell<>() {
	        @Override protected void updateItem(Map<String,Object> rr, boolean empty) {
	            super.updateItem(rr, empty);
	            if (empty || rr == null) {
	                setText(null);
	                return;
	            }
	            String a = getString(rr, "authorUsername");
	            String b = getString(rr, "body");
	            setText(a + ": " + b);
	        }
	    });

	    // helper to refresh replies list
	 // initial load of replies for this post
	    reloadRepliesForPost(postId, repliesUI);
	    
	    // --- Reply CRUD controls + Back ---
	    Button btnReply   = new Button("Reply");   // create
	    Button btnReadR   = new Button("Read");    // read
	    Button btnUpdateR = new Button("Update");  // update (author only)
	    Button btnDeleteR = new Button("Delete");  // delete (author only)
	    Button btnBack    = new Button("Back");    // close this dialog

	    double y = 365;
	    setupButtonUI(btnBack,    "Dialog", 14, 100, Pos.CENTER, 460, y);
	    setupButtonUI(btnReply,   "Dialog", 14, 100, Pos.CENTER, 20,  y);
	    setupButtonUI(btnReadR,   "Dialog", 14, 100, Pos.CENTER, 130, y);
	    setupButtonUI(btnUpdateR, "Dialog", 14, 100, Pos.CENTER, 240, y);
	    setupButtonUI(btnDeleteR, "Dialog", 14, 100, Pos.CENTER, 350, y);

	    // initial enablement rules (no selection yet)
	    btnReply.setDisable(postDeleted); // cannot reply to deleted post
	    btnReadR.setDisable(true);
	    btnUpdateR.setDisable(true);
	    btnDeleteR.setDisable(true);

	    // toggle read/update/delete based on list selection and ownership
	    lv.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
	        boolean has = (b != null);
	        btnReadR.setDisable(!has);

	        boolean mine = false;
	        if (has) {
	            String replyAuthor = getString(b, "authorUsername");
	            mine = currentUser().equals(replyAuthor);
	        }

	        btnUpdateR.setDisable(!mine);
	        btnDeleteR.setDisable(!mine);
	    });

	    // close the read view
	    btnBack.setOnAction(ev -> dlg.close());

	    root.getChildren().addAll(
	        lblTitle, taBody,
	        lblReplies, lv,
	        btnReply, btnReadR, btnUpdateR, btnDeleteR, btnBack
	    );

	    // UX shortcut: double-click a reply to read it
	    lv.setOnMouseClicked(ev -> {
	        if (ev.getClickCount() == 2) {
	            btnReadR.fire();
	        }
	    });

	    // ---- Create Reply flow ----
	    btnReply.setOnAction(e -> {
	        Stage ed = new Stage();
	        ed.initOwner(dlg);
	        ed.initModality(Modality.APPLICATION_MODAL);
	        ed.setTitle("Write a reply");

	        Pane rp = new Pane();
	        Scene rsc = new Scene(rp, 560, 300);
	        ed.setScene(rsc);

	        Label who = new Label(currentUser());
	        setupLabelUI(who, "Arial", 16, 520, Pos.BASELINE_LEFT, 20, 15);

	        TextArea ta = new TextArea();
	        ta.setWrapText(true);
	        ta.setLayoutX(20);
	        ta.setLayoutY(45);
	        ta.setPrefWidth(520);
	        ta.setPrefHeight(200);
	        ta.setPromptText("1–3000 characters");

	        Button ok = new Button("OK");
	        setupButtonUI(ok, "Dialog", 14, 100, Pos.CENTER, 440, 255);
	        Button cancel = new Button("Cancel");
	        setupButtonUI(cancel, "Dialog", 14, 100, Pos.CENTER, 330, 255);

	        rp.getChildren().addAll(who, ta, ok, cancel);

	        cancel.setOnAction(ev -> ed.close());

	        ok.setOnAction(ev -> {
	            String body = ta.getText().trim();

	            // validation + insert handled by Post.createReply(...)
	            String err = Post.createReply(postId, currentUser(), body);
	            if (!err.isEmpty()) {
	                new Alert(AlertType.ERROR, err).showAndWait();
	                return;
	            }

	            reloadRepliesForPost(postId, repliesUI);
	            ed.close();
	        });

	        ed.showAndWait();
	    });

	    // ---- Read Reply flow (read-only) ----
	    btnReadR.setOnAction(e -> {
	        Map<String,Object> rr = lv.getSelectionModel().getSelectedItem();
	        if (rr == null) return;

	        TextArea a = new TextArea(getString(rr, "body"));
	        a.setWrapText(true);
	        a.setEditable(false);

	        Alert box = new Alert(AlertType.INFORMATION);
	        box.setTitle("Reply");
	        box.setHeaderText(getString(rr, "authorUsername"));
	        box.getDialogPane().setContent(a);
	        box.showAndWait();
	    });

	    // ---- Update Reply flow ----
	    btnUpdateR.setOnAction(e -> {
	        Map<String,Object> rr = lv.getSelectionModel().getSelectedItem();
	        if (rr == null) return;

	        Stage ed = new Stage();
	        ed.initOwner(dlg);
	        ed.initModality(Modality.APPLICATION_MODAL);
	        ed.setTitle("Update reply");

	        Pane rp = new Pane();
	        Scene rsc = new Scene(rp, 560, 300);
	        ed.setScene(rsc);

	        Label who = new Label(getString(rr, "authorUsername"));
	        setupLabelUI(who, "Arial", 16, 520, Pos.BASELINE_LEFT, 20, 15);

	        TextArea ta = new TextArea(getString(rr, "body"));
	        ta.setWrapText(true);
	        ta.setLayoutX(20);
	        ta.setLayoutY(45);
	        ta.setPrefWidth(520);
	        ta.setPrefHeight(200);

	        Button ok = new Button("OK");
	        setupButtonUI(ok, "Dialog", 14, 100, Pos.CENTER, 440, 255);
	        Button cancel = new Button("Cancel");
	        setupButtonUI(cancel, "Dialog", 14, 100, Pos.CENTER, 330, 255);

	        rp.getChildren().addAll(who, ta, ok, cancel);

	        cancel.setOnAction(ev -> ed.close());
	        ok.setOnAction(ev -> {
	            String newBody = ta.getText().trim();
	            int replyId = getInt(rr, "id");

	            String err = Post.updateReply(replyId, newBody);
	            if (!err.isEmpty()) {
	                new Alert(AlertType.ERROR, err).showAndWait();
	                return;
	            }

	            reloadRepliesForPost(postId, repliesUI);
	            ed.close();
	        });

	        ed.showAndWait();
	    });

	    // ---- Delete Reply flow ----
	    btnDeleteR.setOnAction(e -> {
	        Map<String,Object> rr = lv.getSelectionModel().getSelectedItem();
	        if (rr == null) return;

	        Alert confirm = new Alert(AlertType.CONFIRMATION,
	            "Are you sure you want to delete this reply?", ButtonType.YES, ButtonType.NO);

	        confirm.showAndWait().ifPresent(bt -> {
	            if (bt == ButtonType.YES) {
	                int replyId = getInt(rr, "id");
	                String err = Post.deleteReply(replyId);
	                if (!err.isEmpty()) {
	                    info(err);
	                    return;
	                }
	                reloadRepliesForPost(postId, repliesUI);
	            }
	        });
	    });

	    dlg.showAndWait();
	}
}