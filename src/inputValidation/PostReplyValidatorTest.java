package inputValidation;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test suite for {@link PostReplyValidator}.
 * 
 * Tests validation logic for posts and replies, including edge cases for null values,
 * empty strings, minimum/maximum length boundaries, and trimming behavior.
 */
class PostReplyValidatorTest {

    /**
     * Helper method to create a string by repeating a character a specified number of times.
     * 
     * @param ch The character to repeat
     * @param n The number of times to repeat the character
     * @return A string containing the character repeated n times
     */
    private static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

    // =================================
    // validatePost(String title, String body)
    // =================================

    /**
     * Tests that a post with minimum valid title and body lengths passes validation.
     */
    @Test
    void testValidatePost_valid_minLengths() {
        String result = PostReplyValidator.validatePost("T", "B");
        assertEquals("", result);
    }

    /**
     * Tests that a post with maximum valid title (120 chars) and body (5000 chars) lengths passes validation.
     */
    @Test
    void testValidatePost_valid_maxBoundaries() {
        String title120 = repeat('x', 120);
        String body5000 = repeat('y', 5000);
        String result = PostReplyValidator.validatePost(title120, body5000);
        assertEquals("", result);
    }

    /**
     * Tests that whitespace trimming works correctly for valid inputs.
     */
    @Test
    void testValidatePost_valid_trimming() {
        String result = PostReplyValidator.validatePost("   Title   ", "   Body   ");
        assertEquals("", result);
    }

    /**
     * Tests that a null title returns the appropriate error message.
     */
    @Test
    void testValidatePost_titleNull() {
        String result = PostReplyValidator.validatePost(null, "body");
        assertEquals("Title must not be null.", result);
    }

    /**
     * Tests that a title containing only whitespace returns the appropriate error message.
     */
    @Test
    void testValidatePost_titleEmptyAfterTrim() {
        String result = PostReplyValidator.validatePost("   ", "body");
        assertEquals("Title must not be empty.", result);
    }

    /**
     * Tests that a title exceeding 120 characters returns the appropriate error message.
     */
    @Test
    void testValidatePost_titleTooLong() {
        String result = PostReplyValidator.validatePost(repeat('x', 121), "body");
        assertEquals("Title must be 1–120 characters.", result);
    }

    /**
     * Tests that a null body returns the appropriate error message.
     */
    @Test
    void testValidatePost_bodyNull() {
        String result = PostReplyValidator.validatePost("title", null);
        assertEquals("Message must not be null.", result);
    }

    /**
     * Tests that a body containing only whitespace returns the appropriate error message.
     */
    @Test
    void testValidatePost_bodyEmptyAfterTrim() {
        String result = PostReplyValidator.validatePost("title", "   ");
        assertEquals("Message must not be empty.", result);
    }

    /**
     * Tests that a body exceeding 5000 characters returns the appropriate error message.
     */
    @Test
    void testValidatePost_bodyTooLong() {
        String result = PostReplyValidator.validatePost("title", repeat('y', 5001));
        assertEquals("Message must be 1–5000 characters.", result);
    }

    // =============================
    // validateReply(String body)
    // =============================

    /**
     * Tests that a reply with minimum valid length passes validation.
     */
    @Test
    void testValidateReply_valid_minLength() {
        String result = PostReplyValidator.validateReply("x");
        assertEquals("", result);
    }

    /**
     * Tests that a reply with maximum valid length (3000 chars) passes validation.
     */
    @Test
    void testValidateReply_valid_maxBoundary() {
        String result = PostReplyValidator.validateReply(repeat('r', 3000));
        assertEquals("", result);
    }

    /**
     * Tests that whitespace trimming works correctly for valid reply inputs.
     */
    @Test
    void testValidateReply_valid_trimming() {
        String result = PostReplyValidator.validateReply("   ok   ");
        assertEquals("", result);
    }

    /**
     * Tests that a null reply returns the appropriate error message.
     */
    @Test
    void testValidateReply_null() {
        String result = PostReplyValidator.validateReply(null);
        assertEquals("Reply must not be null.", result);
    }

    /**
     * Tests that a reply containing only whitespace returns the appropriate error message.
     */
    @Test
    void testValidateReply_emptyAfterTrim() {
        String result = PostReplyValidator.validateReply("   ");
        assertEquals("Reply must not be empty.", result);
    }

    /**
     * Tests that a reply exceeding 3000 characters returns the appropriate error message.
     */
    @Test
    void testValidateReply_tooLong() {
        String result = PostReplyValidator.validateReply(repeat('r', 3001));
        assertEquals("Reply must be 1–3000 characters.", result);
    }
}