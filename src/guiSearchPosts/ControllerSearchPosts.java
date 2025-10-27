package guiSearchPosts;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import entityClasses.Post;
import entityClasses.User;

// This is the Controller class for searching posts
public class ControllerSearchPosts {

    // This is the method that performs the search based on the keyword and thread selected in the View
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
