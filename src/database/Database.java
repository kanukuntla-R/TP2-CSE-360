package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import entityClasses.User;

/*******
 /*******
 * <p> Title: Database Class. </p>
 * 
 * <p> Description: This is an in-memory database built on H2.  Detailed documentation of H2 can
 * be found at https://www.h2database.com/html/main.html (Click on "PDF (2MP) for a PDF of 438 pages
 * on the H2 main page.)  This class leverages H2 and provides numerous special supporting methods.
 * </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 2.00		2025-04-29 Updated and expanded from the version produce by on a previous
 * 							version by Pravalika Mukkiri and Ishwarya Hidkimath Basavaraj
 */

/*
 * The Database class is responsible for establishing and managing the connection to the database,
 * and performing operations such as user registration, login validation, handling invitation 
 * codes, and numerous other database related functions.
 */
public class Database {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	
	// Invitation code expiry in minutes - change this one number later if needed
	public static final int INVITATION_TTL_MINUTES = 15;

	//  Shared variables used within this class
	private Connection connection = null;		// Singleton to access the database 
	private Statement statement = null;			// The H2 Statement is used to construct queries
	
	// These are the easily accessible attributes of the currently logged-in user
	// This is only useful for single user applications
	private String currentUsername;
	private String currentPassword;
	private String currentFirstName;
	private String currentMiddleName;
	private String currentLastName;
	private String currentPreferredFirstName;
	private String currentEmailAddress;
	private boolean currentAdminRole;
	private boolean currentNewRole1;
	private boolean currentNewRole2;
	
	/** Minimal DB rows used by Role1 UI */
	public static class PostRow {
	    public int id;
	    public String title;
	    public String body;
	    public String author;
	    public boolean deleted;
	}

	public static class ReplyRow {
	    public int id;
	    public int postId;
	    public String author;
	    public String body;
	    public boolean deleted;
	}
	
	/*******
	 * <p> Method: Database </p>
	 * 
	 * <p> Description: The default constructor used to establish this singleton object.</p>
	 * 
	 */
	
	public Database () {
		
	}
	
	
	/*******
	 * <p> Attribute: activeOtps </p>
	 *
	 * <p> Description: An in-memory map from <code>username</code> to the currently active
	 * one-time password (OTP) for that user.  This is intentionally not persisted in SQL
	 * to keep OTP handling simple and ephemeral. </p>
	 */
	private java.util.Map<String, String> activeOtps = new java.util.HashMap<>();

	/*******
	 * <p> Method: generateOneTimePasswordFor </p>
	 * <p> Create a 6-digit OTP for an existing user; store and return it. Null if user unknown. </p>
	 *
	 * @param username the target account
	 * @return 6-digit OTP or null
	 */
	public String generateOneTimePasswordFor(String username) {
	    // Ensure user exists first
	    if (!getUserAccountDetails(username)) return null;

	    // 6-digit numeric OTP (you can change the format if you want)
	    String otp = String.format("%06d", new java.util.Random().nextInt(1_000_000));
	    activeOtps.put(username, otp);
	    return otp;
	}

	/*******
	 * <p> Method: hasActiveOtp </p>
	 * <p> True if an unconsumed OTP exists for the user. </p>
	 */
	public boolean hasActiveOtp(String username) {
	    return activeOtps.containsKey(username);
	}

	/*******
	 * <p> Method: checkAndConsumeOtp </p>
	 * <p> Validate OTP; on match, remove it (single use) and return true. </p>
	 */
	public boolean checkAndConsumeOtp(String username, String otp) {
	    String stored = activeOtps.get(username);
	    if (stored != null && stored.equals(otp)) {
	        activeOtps.remove(username);  // single-use
	        return true;
	    }
	    return false;
	}

	/*******
	 * <p> Method: clearOtp </p>
	 * <p> Revoke any active OTP for the user (admin action). </p>
	 */
	public void clearOtp(String username) { activeOtps.remove(username); }

	/*******
	 * <p> Method: updatePassword </p>
	 * <p> Persist the new password to userDB and refresh the cached currentPassword. </p>
	 *
	 * @param username the account to update
	 * @param newPassword the new password
	 */
	public void updatePassword(String username, String newPassword) {
		String sql = "UPDATE userDB SET password = ? WHERE userName = ?"; 

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, username);
	        int rows = pstmt.executeUpdate();

	        if (rows == 0) {
	            System.err.println("*** WARNING *** updatePassword: no rows updated for user '" + username + "'");
	            return;
	        }

	        // keep cache in sync for this run (used by login flow)
	        if (getUserAccountDetails(username)) {
	            this.currentPassword = newPassword; // keep cache in sync for this run
	        }

	        System.out.println("** Password updated for user: " + username);
	    } catch (SQLException e) {
	        System.err.println("*** ERROR *** updatePassword failed for " + username + ": " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	
	
/*******
 * <p> Method: connectToDatabase </p>
 * 
 * <p> Description: Used to establish the in-memory instance of the H2 database from secondary
 *		storage.</p>
 *
 * @throws SQLException when the DriverManager is unable to establish a connection
 * 
 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	
/*******
 * <p> Method: createTables </p>
 * 
 * <p> Description: Used to create new instances of the two database tables used by this class.</p>
 * 
 */
	private void createTables() throws SQLException {
		// Create the user database
		String userTable = "CREATE TABLE IF NOT EXISTS userDB ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "middleName VARCHAR(255), "
				+ "lastName VARCHAR (255), "
				+ "preferredFirstName VARCHAR(255), "
				+ "emailAddress VARCHAR(255), "
				+ "adminRole BOOL DEFAULT FALSE, "
				+ "newRole1 BOOL DEFAULT FALSE, "
				+ "newRole2 BOOL DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
		        + "code VARCHAR(10) PRIMARY KEY, "
		        + "emailAddress VARCHAR(255), "
		        + "role VARCHAR(10), "
		        + "expiresAt TIMESTAMP)";
		statement.execute(invitationCodesTable);

		// If the table already existed without the column, add it quietly
		try {
		    statement.execute("ALTER TABLE InvitationCodes ADD COLUMN expiresAt TIMESTAMP");
		} catch (SQLException ignore) { /* column already exists - ok */ }
		
		// Posts table (ensure these columns exist)
		String postsTable = "CREATE TABLE IF NOT EXISTS Posts ("
		    + "id INT AUTO_INCREMENT PRIMARY KEY, "
		    + "authorUsername VARCHAR(255) NOT NULL, "
		    + "title VARCHAR(120) NOT NULL, "
		    + "body VARCHAR(5000) NOT NULL, "
		    + "createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
		    + "updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
		    + "isDeleted BOOL DEFAULT FALSE"
		    + ")";
		statement.execute(postsTable);

		// Replies table (ensure FK and timestamps)
		String repliesTable = "CREATE TABLE IF NOT EXISTS Replies ("
		    + "id INT AUTO_INCREMENT PRIMARY KEY, "
		    + "postId INT NOT NULL, "
		    + "authorUsername VARCHAR(255) NOT NULL, "
		    + "body VARCHAR(3000) NOT NULL, "
		    + "createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
		    + "updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
		    + "CONSTRAINT fk_reply_post FOREIGN KEY (postId) REFERENCES Posts(id)"
		    + ")";
		statement.execute(repliesTable);
		
		try {
		    statement.execute(
		        "ALTER TABLE Posts ADD COLUMN IF NOT EXISTS isDeleted BOOL DEFAULT FALSE");
		} catch (SQLException ignore) { /* column already present – that's fine */ }
	}

	
/*******
 * <p> Method: isDatabaseEmpty </p>
 * 
 * <p> Description: If the user database has no rows, true is returned, else false.</p>
 * 
 * @return true if the database is empty, else it returns false
 * 
 */
	public boolean isDatabaseEmpty() {
	    String query = "SELECT COUNT(*) AS count FROM userDB";
	    try {
	        ResultSet resultSet = statement.executeQuery(query);
	        if (resultSet.next()) {
	            return resultSet.getInt("count") == 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Treat as empty on error so first-admin setup can proceed.
	        return true;
	    }
	    return true;
	}
	
	
/*******
 * <p> Method: getNumberOfUsers </p>
 * 
 * <p> Description: Returns an integer .of the number of users currently in the user database. </p>
 * 
 * @return the number of user records in the database.
 * 
 */
	public int getNumberOfUsers() {
		String query = "SELECT COUNT(*) AS count FROM userDB";
		try {
			ResultSet resultSet = statement.executeQuery(query);
			if (resultSet.next()) {
				return resultSet.getInt("count");
			}
		} catch (SQLException e) {
	        return 0;
	    }
		return 0;
	}

/*******
 * <p> Method: register(User user) </p>
 * 
 * <p> Description: Creates a new row in the database using the user parameter. </p>
 * 
 * @throws SQLException when there is an issue creating the SQL command or executing it.
 * 
 * @param user specifies a user object to be added to the database.
 * 
 */
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO userDB (userName, password, firstName, middleName, "
				+ "lastName, preferredFirstName, emailAddress, adminRole, newRole1, newRole2) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			currentUsername = user.getUserName();
			pstmt.setString(1, currentUsername);
			
			currentPassword = user.getPassword();
			pstmt.setString(2, currentPassword);
			
			currentFirstName = user.getFirstName();
			pstmt.setString(3, currentFirstName);
			
			currentMiddleName = user.getMiddleName();			
			pstmt.setString(4, currentMiddleName);
			
			currentLastName = user.getLastName();
			pstmt.setString(5, currentLastName);
			
			currentPreferredFirstName = user.getPreferredFirstName();
			pstmt.setString(6, currentPreferredFirstName);
			
			currentEmailAddress = user.getEmailAddress();
			pstmt.setString(7, currentEmailAddress);
			
			currentAdminRole = user.getAdminRole();
			pstmt.setBoolean(8, currentAdminRole);
			
			currentNewRole1 = user.getNewRole1();
			pstmt.setBoolean(9, currentNewRole1);
			
			currentNewRole2 = user.getNewRole2();
			pstmt.setBoolean(10, currentNewRole2);
			
			pstmt.executeUpdate();
		}
		
	}
	
/*******
 *  <p> Method: List getUserList() </p>
 *  
 *  <P> Description: Generate an List of Strings, one for each user in the database,
 *  starting with "<Select User>" at the start of the list. </p>
 *  
 *  @return a list of userNames found in the database.
 */
	public List<String> getUserList () {
		List<String> userList = new ArrayList<String>();
		userList.add("<Select a User>");
		String query = "SELECT userName FROM userDB";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				userList.add(rs.getString("userName"));
			}
		} catch (SQLException e) {
	        return null;
	    }
//		System.out.println(userList);
		return userList;
	}

/*******
 * <p> Method: boolean loginAdmin(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Admin role.
 * 
 * @return true if the specified user has been logged in as an Admin else false.
 * 
 */
	public boolean loginAdmin(User user){
		// Validates an admin user's login credentials so the user can login in as an Admin.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "adminRole = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();	// If a row is returned, rs.next() will return true		
		} catch  (SQLException e) {
	        e.printStackTrace();
	    }
		return false;
	}
	
	
/*******
 * <p> Method: boolean loginRole1(User user) </p>
 * 
 * <p> Description: Check to see that a user with the specified username, password, and role
 * 		is the same as a row in the table for the username, password, and role. </p>
 * 
 * @param user specifies the specific user that should be logged in playing the Student role.
 * 
 * @return true if the specified user has been logged in as an Student else false.
 * 
 */
	public boolean loginRole1(User user) {
		// Validates a student user's login credentials.
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole1 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}

	/*******
	 * <p> Method: boolean loginRole2(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username, password, and role
	 * 		is the same as a row in the table for the username, password, and role. </p>
	 * 
	 * @param user specifies the specific user that should be logged in playing the Reviewer role.
	 * 
	 * @return true if the specified user has been logged in as an Student else false.
	 * 
	 */
	// Validates a reviewer user's login credentials.
	public boolean loginRole2(User user) {
		String query = "SELECT * FROM userDB WHERE userName = ? AND password = ? AND "
				+ "newRole2 = TRUE";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			ResultSet rs = pstmt.executeQuery();
			return rs.next();
		} catch  (SQLException e) {
		       e.printStackTrace();
		}
		return false;
	}
	
	
	/*******
	 * <p> Method: boolean doesUserExist(User user) </p>
	 * 
	 * <p> Description: Check to see that a user with the specified username is  in the table. </p>
	 * 
	 * @param userName specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return true if the specified user is in the table else false.
	 * 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	/*******
	 * <p> Method: boolean removeUser(String username) </p>
	 * 
	 * <p> Description: Permanently deletes a user from the userDB table. </p>
	 * 
	 * @param username is the username of the user to be removed
	 * 
	 * @return true if the delete was successful, else false
	 *  
	 */
	public boolean removeUser(String username) {
	    String query = "DELETE FROM userDB WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        int rowsAffected = pstmt.executeUpdate();
	        return rowsAffected > 0; // true if at least one row was deleted
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	
	/*******
	 * <p> Method: int getNumberOfRoles(User user) </p>
	 * 
	 * <p> Description: Determine the number of roles a specified user plays. </p>
	 * 
	 * @param user specifies the specific user that we want to determine if it is in the table.
	 * 
	 * @return the number of roles this user plays (0 - 5).
	 * 
	 */	
	// Get the number of roles that this user plays
	public int getNumberOfRoles (User user) {
		int numberOfRoles = 0;
		if (user.getAdminRole()) numberOfRoles++;
		if (user.getNewRole1()) numberOfRoles++;
		if (user.getNewRole2()) numberOfRoles++;
		return numberOfRoles;
	}	

	
	/*******
	 * <p> Method: String generateInvitationCode(String emailAddress, String role) </p>
	 * 
	 * <p> Description: Given an email address and a roles, this method establishes and invitation
	 * code and adds a record to the InvitationCodes table.  When the invitation code is used, the
	 * stored email address is used to establish the new user and the record is removed from the
	 * table.</p>
	 * 
	 * @param emailAddress specifies the email address for this new user.
	 * 
	 * @param role specified the role that this new user will play.
	 * 
	 * @return the code of six characters so the new user can use it to securely setup an account.
	 * 
	 */
	// Generates a new invitation code and inserts it into the database.
		public String generateInvitationCode(String emailAddress, String role) {
		    String code = UUID.randomUUID().toString().substring(0, 6); // 6-char code
		    java.sql.Timestamp expiresAt =
		        new java.sql.Timestamp(System.currentTimeMillis() + INVITATION_TTL_MINUTES * 60L * 1000L);

		    String query = "INSERT INTO InvitationCodes (code, emailaddress, role, expiresAt) VALUES (?, ?, ?, ?)";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, code);
		        pstmt.setString(2, emailAddress);
		        pstmt.setString(3, role);
		        pstmt.setTimestamp(4, expiresAt);
		        pstmt.executeUpdate();
		    } catch (SQLException e) { e.printStackTrace(); }
		    return code;
		}

	
	/*******
	 * <p> Method: int getNumberOfInvitations() </p>
	 * 
	 * <p> Description: Determine the number of outstanding invitations in the table.</p>
	 *  
	 * @return the number of invitations in the table.
	 * 
	 */
		// Number of invitations in the database
		public int getNumberOfInvitations() {
			String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE expiresAt > CURRENT_TIMESTAMP";
			try {
				ResultSet resultSet = statement.executeQuery(query);
				if (resultSet.next()) {
					return resultSet.getInt("count");
				}
			} catch  (SQLException e) {
		        e.printStackTrace();
		    }
			return 0;
		}
	
	
	/*******
	 * <p> Method: boolean emailaddressHasBeenUsed(String emailAddress) </p>
	 * 
	 * <p> Description: Determine if an email address has been user to establish a user.</p>
	 * 
	 * @param emailAddress is a string that identifies a user in the table
	 *  
	 * @return true if the email address is in the table, else return false.
	 * 
	 */
		// Check to see if an email address is already in the database
		public boolean emailaddressHasBeenUsed(String emailAddress) {
		    String query = "SELECT COUNT(*) AS count FROM InvitationCodes " +
		                   "WHERE emailAddress = ? AND expiresAt > CURRENT_TIMESTAMP";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, emailAddress);
		        ResultSet rs = pstmt.executeQuery();
		        if (rs.next()) return rs.getInt("count") > 0;
		    } catch (SQLException e) { e.printStackTrace(); }
		    return false;
		}
		
	  /*******
	   * <p> Method: void purgeExpiredInvitationsForEmail(String emailAddress) </p>
	   * 
	   * <p> Description: Delete all invitation code rows for the given email whose
	   *     {@code expiresAt} is less than or equal to the current timestamp. </p>
	   * 
	   * @param emailAddress a string identifying the user whose expired invitations are purged
	   * 
	   */

		
		public void purgeExpiredInvitationsForEmail(String emailAddress) {
		    String sql = "DELETE FROM InvitationCodes " +
		                 "WHERE emailAddress = ? AND expiresAt <= CURRENT_TIMESTAMP";
		    try (PreparedStatement ps = connection.prepareStatement(sql)) {
		        ps.setString(1, emailAddress);
		        ps.executeUpdate();
		    } catch (SQLException e) { e.printStackTrace(); }
		}
	
	
	/*******
	 * <p> Method: String getRoleGivenAnInvitationCode(String code) </p>
	 * 
	 * <p> Description: Get the role associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the role for the code or an empty string.
	 * 
	 */
		// Obtain the roles associated with an invitation code.
		public String getRoleGivenAnInvitationCode(String code) {
			String query = "SELECT role, expiresAt FROM InvitationCodes WHERE code = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			    pstmt.setString(1, code);
			    ResultSet rs = pstmt.executeQuery();
			    if (rs.next()) {
			        java.sql.Timestamp exp = rs.getTimestamp("expiresAt");
			        if (exp != null && exp.getTime() <= System.currentTimeMillis()) return ""; // expired
			        return rs.getString("role");
			    }
			} catch (SQLException e) { e.printStackTrace(); }
			return "";
		}

	
	/*******
	 * <p> Method: String getEmailAddressUsingCode (String code ) </p>
	 * 
	 * <p> Description: Get the email addressed associated with an invitation code.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 * @return the email address for the code or an empty string.
	 * 
	 */
		// For a given invitation code, return the associated email address of an empty string
		public String getEmailAddressUsingCode (String code ) {
			String query = "SELECT emailAddress, expiresAt FROM InvitationCodes WHERE code = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			    pstmt.setString(1, code);
			    ResultSet rs = pstmt.executeQuery();
			    if (rs.next()) {
			        java.sql.Timestamp exp = rs.getTimestamp("expiresAt");
			        if (exp != null && exp.getTime() <= System.currentTimeMillis()) return ""; // expired
			        return rs.getString("emailAddress");
			    }
			} catch (SQLException e) { e.printStackTrace(); }
			return "";
		}
	
	
	/*******
	 * <p> Method: void removeInvitationAfterUse(String code) </p>
	 * 
	 * <p> Description: Remove an invitation record once it is used.</p>
	 * 
	 * @param code is the 6 character String invitation code
	 *  
	 */
	// Remove an invitation using an email address once the user account has been setup
	public void removeInvitationAfterUse(String code) {
	    String query = "SELECT COUNT(*) AS count FROM InvitationCodes WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	        	int counter = rs.getInt(1);
	            // Only do the remove if the code is still in the invitation table
	        	if (counter > 0) {
        			query = "DELETE FROM InvitationCodes WHERE code = ?";
	        		try (PreparedStatement pstmt2 = connection.prepareStatement(query)) {
	        			pstmt2.setString(1, code);
	        			pstmt2.executeUpdate();
	        		}catch (SQLException e) {
	        	        e.printStackTrace();
	        	    }
	        	}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return;
	}
	
	
	/*******
	 * <p> Method: String getFirstName(String username) </p>
	 * 
	 * <p> Description: Get the first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the first name of a user given that user's username 
	 *  
	 */
	// Get the First Name
	public String getFirstName(String username) {
		String query = "SELECT firstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("firstName"); // Return the first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	

	/*******
	 * <p> Method: void updateFirstName(String username, String firstName) </p>
	 * 
	 * <p> Description: Update the first name of a user given that user's username and the new
	 *		first name.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @param firstName is the new first name for the user
	 *  
	 */
	// update the first name
	public void updateFirstName(String username, String firstName) {
	    String query = "UPDATE userDB SET firstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, firstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentFirstName = firstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	/*******
	 * <p> Method: String getMiddleName(String username) </p>
	 * 
	 * <p> Description: Get the middle name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the middle name of a user given that user's username 
	 *  
	 */
	// get the middle name
	public String getMiddleName(String username) {
		String query = "SELECT MiddleName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("middleName"); // Return the middle name if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

	
	/*******
	 * <p> Method: void updateMiddleName(String username, String middleName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param middleName is the new middle name for the user
	 *  
	 */
	// update the middle name
	public void updateMiddleName(String username, String middleName) {
	    String query = "UPDATE userDB SET middleName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, middleName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentMiddleName = middleName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getLastName(String username) </p>
	 * 
	 * <p> Description: Get the last name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the last name of a user given that user's username 
	 *  
	 */
	// get he last name
	public String getLastName(String username) {
		String query = "SELECT LastName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("lastName"); // Return last name role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateLastName(String username, String lastName) </p>
	 * 
	 * <p> Description: Update the middle name of a user given that user's username and the new
	 * 		middle name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param lastName is the new last name for the user
	 *  
	 */
	// update the last name
	public void updateLastName(String username, String lastName) {
	    String query = "UPDATE userDB SET lastName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, lastName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentLastName = lastName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getPreferredFirstName(String username) </p>
	 * 
	 * <p> Description: Get the preferred first name of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the preferred first name of a user given that user's username 
	 *  
	 */
	// get the preferred first name
	public String getPreferredFirstName(String username) {
		String query = "SELECT preferredFirstName FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("preferredFirstName"); // Return the preferred first name if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updatePreferredFirstName(String username, String preferredFirstName) </p>
	 * 
	 * <p> Description: Update the preferred first name of a user given that user's username and
	 * 		the new preferred first name.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param preferredFirstName is the new preferred first name for the user
	 *  
	 */
	// update the preferred first name of the user
	public void updatePreferredFirstName(String username, String preferredFirstName) {
	    String query = "UPDATE userDB SET preferredFirstName = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, preferredFirstName);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentPreferredFirstName = preferredFirstName;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: String getEmailAddress(String username) </p>
	 * 
	 * <p> Description: Get the email address of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return the email address of a user given that user's username 
	 *  
	 */
	// get the email address
	public String getEmailAddress(String username) {
		String query = "SELECT emailAddress FROM userDB WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("emailAddress"); // Return the email address if user exists
	        }
			
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	/*******
	 * <p> Method: void updateEmailAddress(String username, String emailAddress) </p>
	 * 
	 * <p> Description: Update the email address name of a user given that user's username and
	 * 		the new email address.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param emailAddress is the new preferred first name for the user
	 *  
	 */
	// update the email address
	public void updateEmailAddress(String username, String emailAddress) {
	    String query = "UPDATE userDB SET emailAddress = ? WHERE username = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, emailAddress);
	        pstmt.setString(2, username);
	        pstmt.executeUpdate();
	        currentEmailAddress = emailAddress;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	/*******
	 * <p> Method: boolean getUserAccountDetails(String username) </p>
	 * 
	 * <p> Description: Get all the attributes of a user given that user's username.</p>
	 * 
	 * @param username is the username of the user
	 * 
	 * @return true of the get is successful, else false
	 *  
	 */
	// get the attributes for a specified user
	public boolean getUserAccountDetails(String username) {
		String query = "SELECT * FROM userDB WHERE username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();			
			rs.next();
	    	currentUsername = rs.getString(2);
	    	currentPassword = rs.getString(3);
	    	currentFirstName = rs.getString(4);
	    	currentMiddleName = rs.getString(5);
	    	currentLastName = rs.getString(6);
	    	currentPreferredFirstName = rs.getString(7);
	    	currentEmailAddress = rs.getString(8);
	    	currentAdminRole = rs.getBoolean(9);
	    	currentNewRole1 = rs.getBoolean(10);
	    	currentNewRole2 = rs.getBoolean(11);
			return true;
	    } catch (SQLException e) {
			return false;
	    }
	}
	
	
	/*******
	 * <p> Method: boolean updateUserRole(String username, String role, String value) </p>
	 * 
	 * <p> Description: Update a specified role for a specified user's and set and update all the
	 * 		current user attributes.</p>
	 * 
	 * @param username is the username of the user
	 *  
	 * @param role is string that specifies the role to update
	 * 
	 * @param value is the string that specified TRUE or FALSE for the role
	 * 
	 * @return true if the update was successful, else false
	 *  
	 */
	// Update a users role
		public boolean updateUserRole(String username, String role, String value) {
			if (role.compareTo("Admin") == 0) {
				String query = "UPDATE userDB SET adminRole = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentAdminRole = true;
					else
						currentAdminRole = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
			if (role.compareTo("Student") == 0) {
				String query = "UPDATE userDB SET newRole1 = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentNewRole1 = true;
					else
						currentNewRole1 = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
			if (role.compareTo("Staff") == 0) {
				String query = "UPDATE userDB SET newRole2 = ? WHERE username = ?";
				try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					pstmt.setString(1, value);
					pstmt.setString(2, username);
					pstmt.executeUpdate();
					if (value.compareTo("true") == 0)
						currentNewRole2 = true;
					else
						currentNewRole2 = false;
					return true;
				} catch (SQLException e) {
					return false;
				}
			}
			return false;
		}
	
	
	// Attribute getters for the current user
	/*******
	 * <p> Method: String getCurrentUsername() </p>
	 * 
	 * <p> Description: Get the current user's username.</p>
	 * 
	 * @return the username value is returned
	 *  
	 */
	public String getCurrentUsername() { return currentUsername;};

	
	/*******
	 * <p> Method: String getCurrentPassword() </p>
	 * 
	 * <p> Description: Get the current user's password.</p>
	 * 
	 * @return the password value is returned
	 *  
	 */
	public String getCurrentPassword() { return currentPassword;};

	
	/*******
	 * <p> Method: String getCurrentFirstName() </p>
	 * 
	 * <p> Description: Get the current user's first name.</p>
	 * 
	 * @return the first name value is returned
	 *  
	 */
	public String getCurrentFirstName() { return currentFirstName;};

	
	/*******
	 * <p> Method: String getCurrentMiddleName() </p>
	 * 
	 * <p> Description: Get the current user's middle name.</p>
	 * 
	 * @return the middle name value is returned
	 *  
	 */
	public String getCurrentMiddleName() { return currentMiddleName;};

	
	/*******
	 * <p> Method: String getCurrentLastName() </p>
	 * 
	 * <p> Description: Get the current user's last name.</p>
	 * 
	 * @return the last name value is returned
	 *  
	 */
	public String getCurrentLastName() { return currentLastName;};

	
	/*******
	 * <p> Method: String getCurrentPreferredFirstName( </p>
	 * 
	 * <p> Description: Get the current user's preferred first name.</p>
	 * 
	 * @return the preferred first name value is returned
	 *  
	 */
	public String getCurrentPreferredFirstName() { return currentPreferredFirstName;};

	
	/*******
	 * <p> Method: String getCurrentEmailAddress() </p>
	 * 
	 * <p> Description: Get the current user's email address name.</p>
	 * 
	 * @return the email address value is returned
	 *  
	 */
	public String getCurrentEmailAddress() { return currentEmailAddress;};

	
	/*******
	 * <p> Method: boolean getCurrentAdminRole() </p>
	 * 
	 * <p> Description: Get the current user's Admin role attribute.</p>
	 * 
	 * @return true if this user plays an Admin role, else false
	 *  
	 */
	public boolean getCurrentAdminRole() { return currentAdminRole;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole1() </p>
	 * 
	 * <p> Description: Get the current user's Student role attribute.</p>
	 * 
	 * @return true if this user plays a Student role, else false
	 *  
	 */
	public boolean getCurrentNewRole1() { return currentNewRole1;};

	
	/*******
	 * <p> Method: boolean getCurrentNewRole2() </p>
	 * 
	 * <p> Description: Get the current user's Reviewer role attribute.</p>
	 * 
	 * @return true if this user plays a Reviewer role, else false
	 *  
	 */
	public boolean getCurrentNewRole2() { return currentNewRole2;};

	
	/*******
	 * <p> Debugging method</p>
	 * 
	 * <p> Description: Debugging method that dumps the database of the console.</p>
	 * 
	 * @throws SQLException if there is an issues accessing the database.
	 * 
	 */
	// Dumps the database.
	public void dump() throws SQLException {
		String query = "SELECT * FROM userDB";
		ResultSet resultSet = statement.executeQuery(query);
		ResultSetMetaData meta = resultSet.getMetaData();
		while (resultSet.next()) {
		for (int i = 0; i < meta.getColumnCount(); i++) {
		System.out.println(
		meta.getColumnLabel(i + 1) + ": " +
				resultSet.getString(i + 1));
		}
		System.out.println();
		}
		resultSet.close();
	}


	/*******
	 * <p> Method: void closeConnection()</p>
	 * 
	 * <p> Description: Closes the database statement and connection.</p>
	 * 
	 */
	// Closes the database statement and connection.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	//This method retrieves all users from the database and their related information, then sets them as user objects in a list
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT userName, firstName, middleName, lastName, emailAddress, adminRole, newRole1, newRole2 FROM userDB";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String userName = rs.getString("userName");
                String firstName = rs.getString("firstName");
                String middleName = rs.getString("middleName");
                String lastName = rs.getString("lastName");
                String emailAddress = rs.getString("emailAddress");
                boolean adminRole = rs.getBoolean("adminRole");
                boolean newRole1 = rs.getBoolean("newRole1");
                boolean newRole2 = rs.getBoolean("newRole2");


                // This is a way to put all the roles down into a single string
                List<String> roles = new ArrayList<>();
                if (adminRole) roles.add("Admin");
                if (newRole1) roles.add("Student");
                if (newRole2) roles.add("Staff");
                String rolesString = String.join(", ", roles);


                // This will set the user attributes to the values obtained from the database
                User user = new User();
                user.setUserName(userName);
                user.setFirstName(firstName);
                user.setMiddleName(middleName);
                user.setLastName(lastName);
                user.setEmailAddress(emailAddress);
                user.setRolesString(rolesString);


                users.add(user);
            }
            //Error handling case
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    
 // ====== POSTS ======
    
    /*****
     * Creates a new post authored by the specified user.
     * <p>
     * Inserts one row into the posts table with the deleted flag set to false.
     * The caller is responsible for validating inputs and enforcing authorization.
     * </p>
     *
     * @param authorUsername The username of the author creating the post.
     * @param title          The title of the post (validated by the caller).
     * @param body           The body of the post (validated by the caller).
     * @return The number of rows inserted, or the new post identifier if implemented to return it.
     * @throws RuntimeException If a database error occurs while creating the post.
     */
    public void createPost(String authorUsername, String title, String body) {
        String sql = "INSERT INTO Posts (authorUsername, title, body, createdAt, updatedAt, isDeleted)"
                   + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, authorUsername);
            ps.setString(2, title);
            ps.setString(3, body);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*****
     * Retrieves posts for display.
     * <p>
     * When {@code mineOnly} is true, only posts authored by {@code currentUser} are returned;
     * otherwise all posts are returned. When {@code includeDeleted} is false, soft-deleted
     * posts are filtered out. Results are returned as maps of column names to values for UI binding.
     * </p>
     *
     * @param mineOnly       True to return only the current user's posts; false to return all.
     * @param currentUser    The username used when filtering by author.
     * @param includeDeleted True to include soft-deleted posts; false to exclude them.
     * @return A list of row maps representing posts.
     * @throws RuntimeException If a database error occurs while fetching posts.
     */
    public List<Map<String,Object>> fetchPosts(boolean mineOnly, String username, boolean includeDeleted) {
        String where = mineOnly ? "authorUsername = ?" : "1=1";
        String deleted = includeDeleted ? "1=1" : "isDeleted = FALSE";
        String sql = "SELECT * FROM Posts WHERE " + where + " AND " + deleted + " ORDER BY updatedAt DESC";
        List<Map<String,Object>> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (mineOnly) ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int n = md.getColumnCount();
            while (rs.next()) {
                Map<String,Object> row = new HashMap<>();
                for (int i=1;i<=n;i++) row.put(md.getColumnName(i), rs.getObject(i));
                out.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }
    
    /*****
     * Updates the title and body of an existing post.
     * <p>
     * Only mutable fields are changed. The author and deleted status remain unchanged.
     * The caller must perform ownership/authorization checks before invoking this method.
     * </p>
     *
     * @param postId   The unique identifier of the post to update.
     * @param newTitle The new title to store.
     * @param newBody  The new body to store.
     * @return The number of rows affected.
     * @throws RuntimeException If the post does not exist or a database error occurs.
     */
    public void updatePost(int id, String title, String body) {
        String sql = "UPDATE Posts SET title=?, body=?, updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, body);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    /*****
     * Soft deletes a post by marking it as deleted rather than removing it.
     * <p>
     * Soft-deleted posts should be excluded from normal listings unless explicitly requested.
     * Replies remain intact.
     * </p>
     *
     * @param postId The unique identifier of the post to mark as deleted.
     * @return The number of rows affected.
     * @throws RuntimeException If a database error occurs while updating the post.
     */
    public void softDeletePost(int id) {
        String sql = "UPDATE Posts SET isDeleted=TRUE, updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    /*****
     * Retrieves a single post by its identifier.
     * <p>
     * Returns the row for the specified post as a map of column names to values.
     * Implementations typically include fields such as ID, AUTHORUSERNAME, TITLE,
     * BODY, and ISDELETED. If no post exists with the given ID, {@code null} is returned.
     * </p>
     *
     * @param postId The unique identifier of the post to fetch.
     * @return A map of column names to values representing the post, or {@code null} if not found.
     * @throws RuntimeException If a database error occurs while fetching the post.
     */
    public Map<String,Object> getPost(int id) {
        String sql = "SELECT * FROM Posts WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String,Object> row = new HashMap<>();
                    ResultSetMetaData md = rs.getMetaData();
                    for (int i=1;i<=md.getColumnCount();i++) row.put(md.getColumnName(i), rs.getObject(i));
                    return row;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ====== REPLIES ======
    /*****
     * Creates a new reply on the specified post.
     * <p>
     * The caller must ensure that the parent post accepts replies (e.g., is not soft-deleted)
     * and that the reply content has been validated.
     * </p>
     *
     * @param postId         The identifier of the post being replied to.
     * @param authorUsername The username of the reply author.
     * @param body           The reply text (validated by the caller).
     * @return The number of rows inserted, or the new reply identifier if implemented to return it.
     * @throws RuntimeException If a database error occurs while creating the reply.
     */
    public void createReply(int postId, String authorUsername, String body) {
        String sql = "INSERT INTO Replies (postId, authorUsername, body, createdAt, updatedAt)"
                   + " VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setString(2, authorUsername);
            ps.setString(3, body);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*****
     * Retrieves all replies belonging to a specific post.
     * <p>
     * Replies are returned as maps of column names to values. The ordering should be
     * deterministic (e.g., by creation timestamp or identifier) for consistent UI rendering.
     * </p>
     *
     * @param postId The identifier of the parent post.
     * @return A list of row maps representing replies for the post.
     * @throws RuntimeException If a database error occurs while fetching replies.
     */

    public List<Map<String,Object>> getRepliesForPost(int postId) {
        String sql = "SELECT * FROM Replies WHERE postId=? ORDER BY createdAt ASC";
        List<Map<String,Object>> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int n = md.getColumnCount();
                while (rs.next()) {
                    Map<String,Object> row = new HashMap<>();
                    for (int i=1;i<=n;i++) row.put(md.getColumnName(i), rs.getObject(i));
                    out.add(row);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return out;
    }

    /*****
     * Retrieves a single reply by its identifier.
     *
     * @param replyId The unique identifier of the reply.
     * @return A map of column names to values for the reply, or {@code null} if not found.
     * @throws RuntimeException If a database error occurs while fetching the reply.
     */
    public Map<String,Object> getReply(int replyId) {
        String sql = "SELECT * FROM Replies WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, replyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String,Object> row = new HashMap<>();
                    ResultSetMetaData md = rs.getMetaData();
                    for (int i=1;i<=md.getColumnCount();i++) row.put(md.getColumnName(i), rs.getObject(i));
                    return row;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    /*****
     * Updates the text of an existing reply.
     * <p>
     * Ownership/authorization checks are expected to be performed by the caller.
     * </p>
     *
     * @param replyId The unique identifier of the reply to update.
     * @param body    The new reply text (validated by the caller).
     * @return The number of rows affected.
     * @throws RuntimeException If the reply does not exist or a database error occurs.
     */
    public void updateReply(int replyId, String body) {
        String sql = "UPDATE Replies SET body=?, updatedAt=CURRENT_TIMESTAMP WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, body);
            ps.setInt(2, replyId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /*****
     * Permanently deletes a reply.
     * <p>
     * Unlike post deletion, this is a hard delete. If soft-delete semantics for replies are
     * preferred, the implementation should be adjusted accordingly.
     * </p>
     *
     * @param replyId The unique identifier of the reply to delete.
     * @return The number of rows affected.
     * @throws RuntimeException If a database error occurs while deleting the reply.
     */
    public void deleteReply(int replyId) {
        String sql = "DELETE FROM Replies WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, replyId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
}
