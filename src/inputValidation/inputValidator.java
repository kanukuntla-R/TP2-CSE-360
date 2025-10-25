package inputValidation;

public class inputValidator {
	// Regex for email
	private static final java.util.regex.Pattern EMAIL_PATTERN = java.util.regex.Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
	
	// Regex for names: only letters, optional single spaces between, no leading/trailing spaces
    private static final java.util.regex.Pattern NAME_PATTERN = java.util.regex.Pattern.compile("^[A-Za-z]+(?: [A-Za-z]+)*$");
  
	
	/**********
	 * Validate an email address. 
	 * 
	 * @param email		The input string for the email
	 * @return			An output string that is empty if email is okay or it is a String
	 * 						with a helpful description of the error
	 */
	
	public static String checkForValidEmail(String email) {
	    if (email == null || email.isEmpty()) {
	        return "Email cannot be empty.";
	    }
	    if (!EMAIL_PATTERN.matcher(email).matches()) {
	        return "Invalid email format. Expected something like user@example.com";
	    }		
		return "";
	}
	
	/**********
     * Validate a name field (First, Middle, Last, Preferred First).
     *
     * @param name       The input string for the name
     * @param fieldName  The label for the field (e.g., "First Name")
     * @return           Empty string if valid, otherwise an error message
     */
	
    public static String checkForValidName(String name, String fieldName) {
        if (name == null || name.isEmpty()) {
            return fieldName + " cannot be empty.";
        }
        if (name.length() > 24) {
            return fieldName + " cannot exceed 24 characters.";
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            return fieldName + " must contain only alphabetic characters and a single space between letters.";
        }
        return "";
    }
	
    
    /**********
     * Check if all required fields are filled and valid
     * 
     * @param firstName         First name
     * @param middleName        Middle name  
     * @param lastName          Last name
     * @param preferredFirstName Preferred first name
     * @param email             Email address
     * @return                  Empty string if all valid, otherwise error message
     */
    public static String validateAllFields(String firstName, String middleName, String lastName, 
                                         String preferredFirstName, String email) {
        String error = "";
        
        // Check each field
        String firstNameError = checkForValidName(firstName, "First Name");
        if (!firstNameError.isEmpty()) error += firstNameError + "\n";
        
        String middleNameError = checkForValidName(middleName, "Middle Name");
        if (!middleNameError.isEmpty()) error += middleNameError + "\n";
        
        String lastNameError = checkForValidName(lastName, "Last Name");
        if (!lastNameError.isEmpty()) error += lastNameError + "\n";
        
        String preferredFirstNameError = checkForValidName(preferredFirstName, "Preferred First Name");
        if (!preferredFirstNameError.isEmpty()) error += preferredFirstNameError + "\n";
        
        String emailError = checkForValidEmail(email);
        if (!emailError.isEmpty()) error += emailError + "\n";
        
        return error.trim();
    }
    
	
}
