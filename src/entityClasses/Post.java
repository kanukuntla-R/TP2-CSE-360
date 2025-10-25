package entityClasses;

import database.Database;
import inputValidation.PostReplyValidator;

import java.util.List;
import java.util.Map;


public final class Post {

    // no instances
    private Post() {}

    /* ==========================================================
     * INTERNAL DB ACCESS
     * ==========================================================
     */

    /**
     * Always return the single shared Database from FoundationsMain.
     * This guarantees we never hit a null Post.db.
     */
    private static Database db() {
        return applicationMain.FoundationsMain.database;
    }

    /**
     * Create a new post.
     *
     * 1. Validate title/body text.
     * 2. Insert into DB.
     *
     * @param authorUsername logged-in username
     * @param titleInput     raw title from UI
     * @param bodyInput      raw body from UI
     * @return "" on success, else error string
     */
    public static String createPost(String authorUsername,
                                    String titleInput,
                                    String bodyInput) {

        String err = PostReplyValidator.validatePost(titleInput, bodyInput);
        if (err != null && !err.isEmpty()) {
            return err;
        }

        db().createPost(authorUsername, titleInput.trim(), bodyInput.trim());
        return "";
    }

    /**
     * Fetch posts for the list on the home page.
     *
     * @param mineOnly        true  -> only posts authored by 'username'
     *                        false -> all posts
     * @param username        current user's username (only relevant if mineOnly == true)
     * @param includeDeleted  true  -> include rows with isDeleted=TRUE
     *                        false -> hide soft-deleted posts
     *
     * The UI decides:
     *   - "All posts": mineOnly=false, includeDeleted=false
     *   - "My posts" : mineOnly=true,  includeDeleted=true
     *
     * @return List of Maps (one map per post row). Each map should contain:
     *         id, authorUsername, title, body,
     *         createdAt, updatedAt, isDeleted
     *         (case-insensitive access; use get*CI helpers)
     */
    public static List<Map<String,Object>> fetchPosts(boolean mineOnly,
                                                      String username,
                                                      boolean includeDeleted) {
        return db().fetchPosts(mineOnly, username, includeDeleted);
    }

    /**
     * Fetch a single post row by id (even if it's soft-deleted).
     *
     * @param postId id of the post
     * @return row map or null if not found
     */
    public static Map<String,Object> getPost(int postId) {
        return db().getPost(postId);
    }

    /**
     * Update title/body of an existing post.
     * Caller must confirm the user is allowed to edit (owner check).
     *
     * 1. Validate.
     * 2. Update DB.
     *
     * @param postId        which post
     * @param newTitleInput new title from UI
     * @param newBodyInput  new body from UI
     * @return "" on success, else validation error
     */
    public static String updatePost(int postId,
                                    String newTitleInput,
                                    String newBodyInput) {

        String err = PostReplyValidator.validatePost(newTitleInput, newBodyInput);
        if (err != null && !err.isEmpty()) {
            return err;
        }

        db().updatePost(postId, newTitleInput.trim(), newBodyInput.trim());
        return "";
    }

    /**
     * Soft delete a post.
     *
     * This DOES NOT remove the post row.
     * It sets isDeleted = TRUE in the DB (via softDeletePost).
     *
     * After this:
     *   - fetchPosts(..., includeDeleted=false) will hide it from "All posts".
     *   - fetchPosts(..., includeDeleted=true) will still return it for "My posts",
     *     and the UI can render it as:
     *        [Deleted] <title> — <author>
     *
     *   - buildThreadView(postId) WILL STILL RETURN THIS POST ROW.
     *     The UI can then show:
     *        displayTitle = "[Deleted Post]"
     *        displayBody  = "[This post has been deleted]"
     *     and still render all replies.
     *
     *   - createReply(...) will reject adding new replies to a deleted post.
     *
     * @param postId id of the post to soft-delete
     * @return "" (no validation errors here)
     */
    public static String deletePost(int postId) {
        db().softDeletePost(postId);
        return "";
    }

    /**
     * Create a reply under a post.
     *
     * 1. Validate reply text.
     * 2. Confirm parent post exists.
     * 3. Confirm parent post is NOT soft-deleted.
     * 4. Insert reply in DB.
     *
     * This enforces the rule:
     *   "Deleted post still shows its old replies, but you can't add NEW replies to it."
     *
     * @param parentPostId   post being replied to
     * @param authorUsername current username
     * @param replyBodyInput raw reply text
     * @return "" on success, else error string
     */
    public static String createReply(int parentPostId,
                                     String authorUsername,
                                     String replyBodyInput) {

        String err = PostReplyValidator.validateReply(replyBodyInput);
        if (err != null && !err.isEmpty()) {
            return err;
        }

        Map<String,Object> parentRow = db().getPost(parentPostId);
        if (parentRow == null) {
            return "That post no longer exists.";
        }
        if (isPostDeleted(parentRow)) {
            // <- this is the soft-delete rule enforcement
            return "You can't reply to a deleted post.";
        }

        db().createReply(parentPostId, authorUsername, replyBodyInput.trim());
        return "";
    }

    /**
     * Fetch all replies for a given post.
     *
     * NOTE: We ALWAYS return replies, even if the post is soft-deleted.
     * The caller can decide how to display them.
     *
     * @param parentPostId which post's replies
     * @return list of reply row maps (possibly empty)
     *
     * Each reply row map is expected to include:
     *   id, postId, authorUsername, body, createdAt, updatedAt
     * (access via get*CI helpers to be case-insensitive)
     */
    public static List<Map<String,Object>> fetchRepliesForPost(int parentPostId) {
        return db().getRepliesForPost(parentPostId);
    }

    /**
     * Get one reply row by id.
     *
     * @param replyId which reply
     * @return row map or null if not found
     */
    public static Map<String,Object> getReply(int replyId) {
        return db().getReply(replyId);
    }

    /**
     * Update a reply body.
     * Caller must confirm "is this my reply?" first.
     *
     * Steps:
     * 1. Validate new reply body.
     * 2. Update DB.
     *
     * We do NOT care if the parent post is deleted. Editing *existing* text is allowed
     * IF the caller decides it's allowed by policy. If you want to forbid that,
     * do that check in the controller before calling this.
     *
     * @param replyId            which reply
     * @param newReplyBodyInput  new body text
     * @return "" on success, else validation error
     */
    public static String updateReply(int replyId,
                                     String newReplyBodyInput) {

        String err = PostReplyValidator.validateReply(newReplyBodyInput);
        if (err != null && !err.isEmpty()) {
            return err;
        }

        db().updateReply(replyId, newReplyBodyInput.trim());
        return "";
    }

    /**
     * Delete a reply permanently.
     * Caller enforces ownership/authorization.
     *
     * @param replyId which reply to delete
     * @return "" always
     */
    public static String deleteReply(int replyId) {
        db().deleteReply(replyId);
        return "";
    }

    public static String getStringCI(Map<String,Object> row, String keyWanted) {
        if (row == null) return "";
        for (String k : row.keySet()) {
            if (k.equalsIgnoreCase(keyWanted)) {
                Object v = row.get(k);
                return v == null ? "" : v.toString();
            }
        }
        return "";
    }

    
    public static int getIntCI(Map<String,Object> row, String keyWanted) {
        if (row == null) return -1;
        for (String k : row.keySet()) {
            if (k.equalsIgnoreCase(keyWanted)) {
                Object v = row.get(k);
                if (v instanceof Number) return ((Number)v).intValue();
                try {
                    return Integer.parseInt(String.valueOf(v));
                } catch (Exception ignore) {
                    return -1;
                }
            }
        }
        return -1;
    }

    
    public static boolean getBoolCI(Map<String,Object> row, String keyWanted) {
        if (row == null) return false;

        // normalise the key we want: lower-case, no underscores
        String want = keyWanted.replace("_", "").toLowerCase();

        for (String k : row.keySet()) {
            String normalised = k.replace("_", "").toLowerCase();
            if (normalised.equals(want)) {
                Object v = row.get(k);
                if (v instanceof Boolean) return (Boolean) v;
                if (v instanceof Number)  return ((Number) v).intValue() != 0;
                if (v instanceof String)  return Boolean.parseBoolean((String) v);
            }
        }
        return false;
    }

    
    private static boolean isPostDeleted(Map<String,Object> postRow) {
        return getBoolCI(postRow, "isDeleted");
    }
}