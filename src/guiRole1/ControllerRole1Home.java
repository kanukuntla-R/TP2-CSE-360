package guiRole1;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.Map;
import entityClasses.Post;
import javafx.scene.control.ComboBox;
import guiSearchPosts.ViewSearchPosts;

public class ControllerRole1Home {

	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	 */

	
 	/**********
	 * <p> Method: performLogout() </p>
	 * 
	 * <p> Description: This method logs out the current user and proceeds to the normal login
	 * page where existing users can log in or potential new users with a invitation code can
	 * start the process of setting up an account. </p>
	 * 
	 */
	
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewRole1Home.theStage);
	}
	
	
	/**********
	 * <p> Method: performQuit() </p>
	 * 
	 * <p> Description: This method terminates the execution of the program.  It leaves the
	 * database in a state where the normal login page will be displayed when the application is
	 * restarted.</p>
	 * 
	 */
	
	protected static void performQuit() {
		System.exit(0);
	}

	/*-*******************************************************************************************

	Data Helper Methods
	
	 */

	/*****
	 * Convenience accessor for the username of the currently logged-in user.
	 *
	 * @return The current user's username, or the empty string if no user is set.
	 */
	
	protected static String currentUser(){ 
		return ViewRole1Home.theUser != null ? ViewRole1Home.theUser.getUserName() : ""; 
	}
	
	/**********
	 * <p> Method: owns(Map<String,Object> row) </p>
	 * 
	 * <p> Description: Determines if the current user owns the specified post row.
	 * Checks if the authorUsername in the row matches the current logged-in user. </p>
	 * 
	 * @param row the database row containing post information
	 * @return true if the current user owns the post, false otherwise
	 */
	
	protected static boolean owns(Map<String,Object> row){
	    if (row == null) return false;
	    String author = getString(row, "authorUsername");
	    return currentUser().equals(author);
	}
	
	/*****
	 * Show a simple informational dialog with an OK button.
	 * <p>
	 * This helper is used for lightweight, blocking notifications (e.g., permission
	 * warnings like "You can only update your own posts.") without changing the page
	 * layout or status area.
	 * </p>
	 *
	 * @param msg The message text to display in the dialog.
	 */
	
	protected static void info(String msg){ 
		new Alert(AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); 
	}

	/**********
	 * <p> Method: reloadRepliesForPost(int postId, ObservableList<Map<String,Object>> repliesUI) </p>
	 * 
	 * <p> Description: Refreshes the replies list for a given post by re-querying the database
	 * and updating the UI list with the latest replies. </p>
	 * 
	 * @param postId the ID of the post whose replies to reload
	 * @param repliesUI the ObservableList to update with fresh reply data
	 */
	
	protected static void reloadRepliesForPost(int postId, ObservableList<Map<String,Object>> repliesUI) {
	    List<Map<String,Object>> repl = Post.fetchRepliesForPost(postId);
	    repliesUI.setAll(repl);
	}

	/**********
	 * <p> Method: loadPosts(boolean mineOnly, String threadFilter) </p>
	 * 
	 * <p> Description: Loads posts from the database based on the specified filters.
	 * Updates the UI list with the retrieved posts. </p>
	 * 
	 * @param mineOnly true to show only current user's posts, false to show all posts
	 * @param threadFilter the thread type to filter by ("All Threads" shows all threads)
	 */
	
	protected static void loadPosts(boolean mineOnly, String threadFilter){
		 boolean includeDeleted = true;
		 List<Map<String,Object>> rows = Post.fetchPosts(
	            mineOnly,
	            currentUser(),
	            includeDeleted,
	            threadFilter
	    );
	    ViewRole1Home.postsUI.setAll(rows);
	}

	/**********
	 * <p> Method: getString(Map<String,Object> row, String key) </p>
	 * 
	 * <p> Description: Retrieves a String value from a database row using case-insensitive
	 * key matching. Returns empty string if key not found or value is null. </p>
	 * 
	 * @param row the database row map
	 * @param key the column name to retrieve (case-insensitive)
	 * @return the String value or empty string if not found
	 */
	
	protected static String getString(Map<String,Object> row, String key) {
	    for (String k : row.keySet()) {
	        if (k.equalsIgnoreCase(key)) {
	            Object v = row.get(k);
	            return v == null ? "" : v.toString();
	        }
	    }
	    return "";
	}

	/**********
	 * <p> Method: getInt(Map<String,Object> row, String key) </p>
	 * 
	 * <p> Description: Retrieves an integer value from a database row using case-insensitive
	 * key matching. Returns -1 if key not found or value cannot be converted to int. </p>
	 * 
	 * @param row the database row map
	 * @param key the column name to retrieve (case-insensitive)
	 * @return the integer value or -1 if not found
	 */
	
	protected static int getInt(Map<String,Object> row, String key) {
	    for (String k : row.keySet()) {
	        if (k.equalsIgnoreCase(key)) {
	            Object v = row.get(k);
	            if (v instanceof Number) return ((Number)v).intValue();
	            try { return Integer.parseInt(String.valueOf(v)); } catch (Exception ignored) {}
	        }
	    }
	    return -1;
	}

	/*-*******************************************************************************************

	Post CRUD Operations
	
	 */

	/**********
	 * <p> Method: createPost() </p>
	 * 
	 * <p> Description: Opens the post creation dialog for the current user to create a new post.
	 * Calls the openEditor method with null to indicate creation mode. </p>
	 * 
	 */
	
	protected static void createPost() {
	    openEditor(null);
	}

	/**********
	 * <p> Method: readPost() </p>
	 * 
	 * <p> Description: Opens the post reader dialog for the currently selected post.
	 * Retrieves the selected post from the list view and opens it in read-only mode. </p>
	 * 
	 */
	
	protected static void readPost() {
	    Map<String,Object> row = ViewRole1Home.lvPosts.getSelectionModel().getSelectedItem();
	    if (row != null) openReader(row);
	}

	/**********
	 * <p> Method: updatePost() </p>
	 * 
	 * <p> Description: Opens the post editor dialog for updating the currently selected post.
	 * Validates that the user owns the post and that it is not deleted before allowing editing. </p>
	 * 
	 */
	
	protected static void updatePost() {
	    Map<String,Object> row = ViewRole1Home.lvPosts.getSelectionModel().getSelectedItem();
	    if (row == null) return;
	    if (!owns(row)) { 
	        info("You can only update your own posts."); 
	        return; 
	    }
	    if (Post.getBoolCI(row, "isDeleted")) { 
	        info("This post is deleted and cannot be edited."); 
	        return; 
	    }
	    openEditor(row);
	}

	/**********
	 * <p> Method: deletePost() </p>
	 * 
	 * <p> Description: Soft deletes the currently selected post after confirming with the user.
	 * Validates ownership and shows confirmation dialog before proceeding with deletion. </p>
	 * 
	 */
	
	protected static void deletePost() {
	    Map<String,Object> row = ViewRole1Home.lvPosts.getSelectionModel().getSelectedItem();
	    if (row == null) return;
	    if (!owns(row)) { 
	        info("You can only delete your own posts."); 
	        return; 
	    }

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
	            boolean mine = "My posts".equals(ViewRole1Home.cbFilter.getValue());
	            loadPosts(mine, ViewRole1Home.cbThread.getValue());
	        }
	    });
	}

	/*-*******************************************************************************************

	Event Handling Setup Methods
	
	 */

	/**********
	 * <p> Method: setupPostSelectionListener() </p>
	 * 
	 * <p> Description: Sets up the event listener for post list selection changes.
	 * Enables/disables buttons based on selection state and post ownership. </p>
	 * 
	 */
	
	protected static void setupPostSelectionListener() {
	    ViewRole1Home.lvPosts.getSelectionModel().selectedItemProperty().addListener((o,a,b)->{
	        boolean has = (b != null);
	        ViewRole1Home.btnRead.setDisable(!has);

	        boolean mineActive = false;
	        if (has) {
	            boolean iOwn = owns(b);
	            boolean notDeleted = !Post.getBoolCI(b, "isDeleted");
	            mineActive = iOwn && notDeleted;
	        }

	        ViewRole1Home.btnUpdate.setDisable(!mineActive);
	        ViewRole1Home.btnDelete.setDisable(!mineActive);
	    });
	}

	/**********
	 * <p> Method: setupFilterListener() </p>
	 * 
	 * <p> Description: Sets up the event listener for the filter dropdown selection.
	 * Refreshes the posts list when switching between "All posts" and "My posts". </p>
	 * 
	 */
	
	protected static void setupFilterListener() {
	    ViewRole1Home.cbFilter.valueProperty().addListener((obs, ov, nv) -> 
	        loadPosts("My posts".equals(nv), ViewRole1Home.cbThread.getValue()));
	}

	/**********
	 * <p> Method: setupThreadListener() </p>
	 * 
	 * <p> Description: Sets up the event listener for the thread dropdown selection.
	 * When a thread is selected, the posts list is refreshed to show only posts
	 * from that thread type. </p>
	 * 
	 */
	
	protected static void setupThreadListener() {
	    ViewRole1Home.cbThread.valueProperty().addListener((obs, ov, nv) -> {
	        boolean mine = "My posts".equals(ViewRole1Home.cbFilter.getValue());
	        loadPosts(mine, nv);
	    });
	}

	/**********
	 * <p> Method: setupDoubleClickHandler() </p>
	 * 
	 * <p> Description: Sets up the double-click handler for the posts list.
	 * Allows users to quickly open a post by double-clicking on it. </p>
	 * 
	 */
	
	protected static void setupDoubleClickHandler() {
	    ViewRole1Home.lvPosts.setOnMouseClicked(ev -> {
	        if (ev.getClickCount() == 2) {
	            Map<String,Object> row = ViewRole1Home.lvPosts.getSelectionModel().getSelectedItem();
	            if (row != null) openReader(row);
	        }
	    });
	}

	// This opens the Search Posts page, pre-filling the keyword from the quick search box
	protected static void openSearch() {
		// This sends the prefilled search keyword from the quick search box on the home page
		String quick = ViewRole1Home.tfSearch.getText();
		ViewSearchPosts.displaySearch(ViewRole1Home.theStage, ViewRole1Home.theUser, quick);
	}

	/*-*******************************************************************************************

	Modal Dialog Methods
	
	 */

	/**********
	 * <p> Method: openEditor(Map<String,Object> existingRow) </p>
	 * 
	 * <p> Description: Opens a modal dialog for creating or editing a post.
	 * If existingRow is null, creates a new post; otherwise edits the existing post. </p>
	 * 
	 * @param existingRow the post row to edit, or null for new post creation
	 */
	
	protected static void openEditor(Map<String,Object> existingRow) {
	    // modal child window bound to the main stage
	    Stage dlg = new Stage();
	    dlg.initOwner(ViewRole1Home.theStage);
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
	    Scene sc = new Scene(root, 420, 320);
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

	    // --- Thread field ---
	    Label lblThread = new Label("Thread");
	    setupLabelUI(lblThread, "Arial", 14, 380, Pos.BASELINE_LEFT, 20, 200);

	    ComboBox<String> cbThread = new ComboBox<>();
	    cbThread.getItems().setAll("General", "Lectures", "Sections", "Problem Sets", "Assignments", "Social");
	    cbThread.getSelectionModel().select(0); // Default to "General"
	    cbThread.setLayoutX(20);
	    cbThread.setLayoutY(225);
	    cbThread.setPrefWidth(380);

	    // Custom cell factory with colored circles (same as main page)
	    cbThread.setCellFactory(listView -> new ListCell<String>() {
	        @Override
	        protected void updateItem(String item, boolean empty) {
	            super.updateItem(item, empty);
	            if (empty || item == null) {
	                setText(null);
	                setGraphic(null);
	            } else {
	                Circle circle = new Circle(6);
	                circle.setFill(getThreadColor(item));
	                
	                Label label = new Label(item);
	                HBox hbox = new HBox(8, circle, label);
	                hbox.setAlignment(Pos.CENTER_LEFT);
	                
	                setGraphic(hbox);
	                setText(null);
	            }
	        }
	    });

	    // --- Dialog buttons ---
	    Button btnOk = new Button(editing ? "Save" : "Create");
	    setupButtonUI(btnOk, "Dialog", 14, 100, Pos.CENTER, 300, 270);

	    Button btnCancel = new Button("Cancel");
	    setupButtonUI(btnCancel, "Dialog", 14, 100, Pos.CENTER, 190, 270);

	    root.getChildren().addAll(lblT, tfTitle, lblB, taBody, lblThread, cbThread, btnOk, btnCancel);

	    // close without changes
	    btnCancel.setOnAction(e -> dlg.close());

	    // validate + save
	    btnOk.setOnAction(e -> {
	        String t = tfTitle.getText().trim();
	        String b = taBody.getText().trim();
	        String thread = cbThread.getValue();

	        // remember the current filter so we reload the same view after save
	        boolean mine = "My posts".equals(ViewRole1Home.cbFilter.getValue());

	        String err;
	        if (!editing) {
	            // create new post
	            err = Post.createPost(currentUser(), t, b, thread);
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
	        loadPosts(mine, ViewRole1Home.cbThread.getValue());

	        // done
	        dlg.close();
	    });

	    dlg.showAndWait();
	}
	
	/**********
	 * <p> Method: openReader(Map<String,Object> postRow) </p>
	 * 
	 * <p> Description: Opens a modal dialog for reading a post and its replies.
	 * Displays the post content and provides CRUD operations for replies. </p>
	 * 
	 * @param postRow the post row to display and read
	 */
	
	public static void openReader(Map<String,Object> postRow) {
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
	    dlg.initOwner(ViewRole1Home.theStage);
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

	/*-*******************************************************************************************

	UI Helper Methods
	
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
	 * Used for consistent color coding across the UI components. </p>
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
