package guiSearchPosts;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import entityClasses.Post;

/**
 * Controller class for the search posts functionality in the MVC architecture.
 * Handles the business logic for searching posts and replies based on keywords.
 * Performs search across post titles and bodies, as well as reply content.
 * 
 * <p>This controller coordinates between the View and Model components to:
 * <ul>
 *   <li>Validate search input (keyword required)</li>
 *   <li>Filter posts by thread selection (or search all threads)</li>
 *   <li>Match keywords against post titles, bodies, and reply content</li>
 *   <li>Generate text snippets highlighting matches</li>
 *   <li>Display results or appropriate error messages</li>
 * </ul>
 */
public class ControllerSearchPosts {

    /**
     * Private constructor to prevent instantiation.
     * This class contains only static utility methods.
     */
    private ControllerSearchPosts() {}

    /**
     * Performs a search operation based on the keyword and thread selected in the View.
     * 
     * <p>Searches for the keyword in:
     * <ul>
     *   <li>Post titles</li>
     *   <li>Post bodies</li>
     *   <li>Reply content within posts</li>
     * </ul>
     * 
     * <p>Matches are collected in a list and displayed in the View's results ListView.
     * Each match includes:
     * <ul>
     *   <li>Original post information</li>
     *   <li>Match type ("post" or "reply")</li>
     *   <li>A text snippet highlighting the matched keyword</li>
     * </ul>
     * 
     * <p>Shows an error alert if keyword is empty, or an information alert if no matches found.
     */
    public static void performSearch() {
        String keyword = ViewSearchPosts.tfKeyword.getText();
    String thread = ViewSearchPosts.cbThread.getValue();

        if (keyword == null || keyword.trim().isEmpty()) {
            new Alert(AlertType.ERROR, "Please enter a keyword to search for.").showAndWait();
            return;
        }

        keyword = keyword.trim();
        String kl = keyword.toLowerCase();

    if (thread == null || thread.trim().isEmpty()) thread = "All Threads";

        // This fetches posts across the chosen thread 
        List<Map<String,Object>> rows = Post.fetchPosts(false, "", true, thread);

        List<Map<String,Object>> matches = new ArrayList<>();

        for (Map<String,Object> row : rows) {
            boolean matched = false;
            String title = Post.getStringCI(row, "title");
            String body = Post.getStringCI(row, "body");
            if ((title != null && title.toLowerCase().contains(kl)) || (body != null && body.toLowerCase().contains(kl))) {
                Map<String,Object> copy = new HashMap<>(row);
                copy.put("matchType", "post");
                copy.put("matchSnippet", makeSnippet(title + "\n" + body, kl));
                matches.add(copy);
                matched = true;
            }

            if (!matched) {
                int postId = Post.getIntCI(row, "id");
                List<Map<String,Object>> replies = Post.fetchRepliesForPost(postId);
                for (Map<String,Object> rr : replies) {
                    String rbody = Post.getStringCI(rr, "body");
                    if (rbody != null && rbody.toLowerCase().contains(kl)) {
                        Map<String,Object> copy = new HashMap<>(row);
                        copy.put("matchType", "reply");
                        copy.put("matchSnippet", makeSnippet(rbody, kl));
                        matches.add(copy);
                        matched = true;
                        break;
                    }
                }
            }
        }

        ViewSearchPosts.resultsUI.setAll(matches);
        if (matches.isEmpty()) {
            new Alert(AlertType.INFORMATION, "No posts matched the search.").showAndWait();
        }
    }

    /**
     * Creates a text snippet containing the matched keyword with surrounding context.
     * 
     * <p>Extracts approximately 20 characters before and after the keyword match
     * to provide context for the search result. Removes newlines for cleaner display
     * and adds ellipsis (...) if the snippet is truncated.
     * 
     * @param text The text to create a snippet from
     * @param keywordLower The keyword to search for (in lowercase)
     * @return A snippet string with the keyword and surrounding context, or empty string if keyword not found
     */
    private static String makeSnippet(String text, String keywordLower) {
        if (text == null) return "";
        String tl = text.toLowerCase();
        int idx = tl.indexOf(keywordLower);
        if (idx < 0) return "";
        int start = Math.max(0, idx - 20);
        int end = Math.min(text.length(), idx + keywordLower.length() + 20);
        String snip = text.substring(start, end).replaceAll("\n", " ");
        if (start > 0) snip = "..." + snip;
        if (end < text.length()) snip = snip + "...";
        return snip;
    }

}
