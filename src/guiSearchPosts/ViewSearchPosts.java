package guiSearchPosts;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import guiRole1.ViewRole1Home;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import guiRole1.ControllerRole1Home;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.Map;
import entityClasses.Post;
import entityClasses.User;

/**
 * View class for the search posts functionality in the MVC architecture.
 * Manages the user interface and display of the search window.
 * 
 * <p>This view provides:
 * <ul>
 *   <li>A keyword text field for entering search terms</li>
 *   <li>A thread dropdown for filtering by thread category</li>
 *   <li>A list view displaying search results with post details</li>
 *   <li>Double-click functionality to open posts in the reader</li>
 *   <li>Navigation buttons (Search and Back)</li>
 * </ul>
 * 
 * <p>The view uses a thread combo box with color-coded categories to match
 * the visual style of the Student Home Page. Search results display post ID,
 * title, author, thread, match type, and a contextual snippet.
 */
public class ViewSearchPosts {

    private static double width = applicationMain.FoundationsMain.WINDOW_WIDTH;
    private static double height = applicationMain.FoundationsMain.WINDOW_HEIGHT;

    protected static Stage theStage;
    protected static Pane theRootPane;
    private static Scene theScene;
    protected static User theUser;

    /**
     * Text field for entering the search keyword.
     * Used by the Controller to retrieve the search term.
     */
    protected static TextField tfKeyword = new TextField();
    
    /**
     * Combo box for selecting the thread category to filter searches.
     * Options include: All Threads, General, Lectures, Sections, Problem Sets, Assignments, Social.
     * Used by the Controller to retrieve the selected thread filter.
     */
    protected static ComboBox<String> cbThread = new ComboBox<>();
    
    /**
     * Search button that triggers the search operation.
     */
    protected static Button btnSearch = new Button("Search");
    
    /**
     * Back button that returns to the Role1 Home page.
     */
    protected static Button btnBack = new Button("Back");

    /**
     * Observable list containing the search results.
     * Each result is a Map with post information including matchType and matchSnippet.
     * Used by the ListView to display search results.
     */
    protected static final ObservableList<Map<String,Object>> resultsUI = FXCollections.observableArrayList();
    
    /**
     * ListView displaying the search results.
     * Configured with a custom cell factory to show formatted post information.
     */
    protected static ListView<Map<String,Object>> lvResults = new ListView<>();

    private static ViewSearchPosts theView;

    /**
     * Displays the search window with the specified stage and user.
     * 
     * @param ps The primary stage to display the window on
     * @param user The current user performing the search
     */
    public static void displaySearch(Stage ps, User user) {
        displaySearch(ps, user, "");
    }

    /**
     * Displays the search window with an optional initial keyword pre-filled in the search field.
     * 
     * <p>Opens the search window for the user, resetting the thread selection
     * to "All Threads" and optionally pre-filling the keyword field with the provided value.
     * If the keyword is null, an empty string is used instead.
     * 
     * @param ps The primary stage to display the window on
     * @param user The current user performing the search
     * @param initialKeyword The keyword to pre-fill in the search field (null or empty for blank)
     */
    public static void displaySearch(Stage ps, User user, String initialKeyword) {
        theStage = ps;
        theUser = user;

        if (theView == null) theView = new ViewSearchPosts();

        // This allows the reset thread selection each time the dialog is opened
        cbThread.getSelectionModel().select("All Threads");
        // This prefills the keyword on open
        tfKeyword.setText(initialKeyword == null ? "" : initialKeyword);

        theStage.setTitle("Search Posts");
        theStage.setScene(theScene);
        theStage.show();
    }

    /**
     * Private constructor that initializes the view and sets up all UI components.
     * 
     * <p>Creates and configures:
     * <ul>
     *   <li>Window title and layout</li>
     *   <li>Keyword input field with label</li>
     *   <li>Thread dropdown with color-coded categories</li>
     *   <li>Search and Back buttons with action handlers</li>
     *   <li>Results ListView with custom cell rendering</li>
     *   <li>Double-click handler for opening posts</li>
     * </ul>
     */
    private ViewSearchPosts() {
        theRootPane = new Pane();
        theScene = new Scene(theRootPane, width, height);

        Label lblTitle = new Label("Search Posts");
        setupLabelUI(lblTitle, "Arial", 28, width, Pos.CENTER, 0, 5);

        Label lblKeyword = new Label("Keyword (required)");
        setupLabelUI(lblKeyword, "Arial", 14, 300, Pos.BASELINE_LEFT, 20, 65);
        tfKeyword.setLayoutX(20);
        tfKeyword.setLayoutY(90);
        tfKeyword.setPrefWidth(360);

    Label lblThread = new Label("Thread (choose or leave as 'All Threads')");
    setupLabelUI(lblThread, "Arial", 14, 400, Pos.BASELINE_LEFT, 400, 65);
        cbThread.getItems().setAll("All Threads", "General", "Lectures", "Sections", "Problem Sets", "Assignments", "Social");
        // cell factory with colored circles like that in Student Home Page (Thread drop down)
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
                    label.setTextFill(Color.BLACK);
                    HBox hbox = new HBox(8, circle, label);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });
        // This shows the same graphic in the closed combo box
        cbThread.setButtonCell(new ListCell<String>() {
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
                    label.setTextFill(Color.BLACK);
                    HBox hbox = new HBox(8, circle, label);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        cbThread.setLayoutX(400);
        cbThread.setLayoutY(90);
        cbThread.setPrefWidth(360);

        btnSearch.setLayoutX(20);
        btnSearch.setLayoutY(130);
        setupButtonUI(btnSearch, "Dialog", 14, 100, Pos.CENTER, 20, 130);

        btnBack.setLayoutX(130);
        btnBack.setLayoutY(130);
        setupButtonUI(btnBack, "Dialog", 14, 100, Pos.CENTER, 130, 130);

        lvResults.setLayoutX(20);
        lvResults.setLayoutY(180);
        lvResults.setPrefWidth(width - 40);
        lvResults.setPrefHeight(height - 220);
        lvResults.setItems(resultsUI);

        // This is the cell factory for displaying search results, showing id, title, author, thread and match type (snippet is used to display part of the matched text)
        lvResults.setCellFactory(v -> new ListCell<>() {
            @Override protected void updateItem(Map<String,Object> row, boolean empty) {
                super.updateItem(row, empty);
                if (empty || row == null) { setText(null); setGraphic(null); return; }

                int idNum = Post.getIntCI(row, "id");
                String id = idNum >= 0 ? Integer.toString(idNum) : "?";
                String title = Post.getStringCI(row, "title");
                String author = Post.getStringCI(row, "authorUsername");
                String thread = Post.getStringCI(row, "thread");
                String matchType = (row.getOrDefault("matchType", "post")).toString();
                String snippet = (row.getOrDefault("matchSnippet", "")).toString();

                String text = "#" + id + " — " + title + " — " + author + " — " + thread + "\n" +
                              "Matched in: " + matchType + (snippet.isEmpty() ? "" : " — " + snippet);
                setText(text);
            }
        });

        // This is the feature to double click a search result to open the same post reader used on the Student Home Page
        lvResults.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 2) {
                Map<String,Object> row = lvResults.getSelectionModel().getSelectedItem();
                if (row != null) ControllerRole1Home.openReader(row);
            }
        });

        // This sets up the button actions so that Search button calls the search function and Back button returns to Role1Home Page
        btnSearch.setOnAction(e -> ControllerSearchPosts.performSearch());
        btnBack.setOnAction(e -> ViewRole1Home.displayRole1Home(theStage, theUser));

        theRootPane.getChildren().addAll(lblTitle, lblKeyword, tfKeyword, lblThread, cbThread, btnSearch, btnBack, lvResults);
    }

    /**
     * Helper method to set up label UI properties.
     * 
     * @param l The label to configure
     * @param ff The font family
     * @param f The font size
     * @param w The minimum width
     * @param p The text alignment position
     * @param x The x-coordinate layout position
     * @param y The y-coordinate layout position
     */
    private static void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
        l.setFont(Font.font(ff, f));
        l.setMinWidth(w);
        l.setAlignment(p);
        l.setLayoutX(x);
        l.setLayoutY(y);
    }

    /**
     * Helper method to set up button UI properties.
     * 
     * @param b The button to configure
     * @param ff The font family
     * @param f The font size
     * @param w The minimum width
     * @param p The text alignment position
     * @param x The x-coordinate layout position
     * @param y The y-coordinate layout position
     */
    private static void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
        b.setFont(Font.font(ff, f));
        b.setMinWidth(w);
        b.setAlignment(p);
        b.setLayoutX(x);
        b.setLayoutY(y);
    }

    /**
     * Maps thread names to their corresponding colors for visual display.
     * 
     * <p>Provides consistent color-coding across the application:
     * <ul>
     *   <li>General - Blue</li>
     *   <li>Lectures - Green</li>
     *   <li>Sections - Orange</li>
     *   <li>Problem Sets - Red</li>
     *   <li>Assignments - Purple</li>
     *   <li>Social - Cyan</li>
     *   <li>Default/Unknown - Gray</li>
     * </ul>
     * 
     * @param thread The thread name to get the color for
     * @return The Color corresponding to the thread, or Color.GRAY for unknown threads
     */
    private static Color getThreadColor(String thread) {
        switch (thread) {
            case "General": return Color.BLUE;
            case "Lectures": return Color.GREEN;
            case "Sections": return Color.ORANGE;
            case "Problem Sets": return Color.RED;
            case "Assignments": return Color.PURPLE;
            case "Social": return Color.CYAN;
            default: return Color.GRAY;
        }
    }

}
