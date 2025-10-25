package inputValidation;

/**
 * Central validator for posts and replies.
 * Returns "" (empty string) when valid, or a descriptive error message when invalid.
 */
public final class PostReplyValidator {

    private PostReplyValidator() {}
    /** Validate a post's title and body. */
    public static String validatePost(String title, String body) {
        // Title checks
        if (title == null) return "Title must not be null.";
        String t = title.trim();
        if (t.isEmpty()) return "Title must not be empty.";
        if (t.length() < 1 || t.length() > 120) return "Title must be 1–120 characters.";

        // Body checks
        if (body == null) return "Message must not be null.";
        String b = body.trim();
        if (b.isEmpty()) return "Message must not be empty.";
        if (b.length() < 1 || b.length() > 5000) return "Message must be 1–5000 characters.";

        return ""; // valid
    }

    /** Validate a reply's body. */
    public static String validateReply(String body) {
        if (body == null) return "Reply must not be null.";
        String b = body.trim();
        if (b.isEmpty()) return "Reply must not be empty.";
        if (b.length() < 1 || b.length() > 3000) return "Reply must be 1–3000 characters.";
        return ""; // valid
    }
}