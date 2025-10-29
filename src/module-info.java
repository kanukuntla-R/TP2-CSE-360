/**
 * Module definition for the FoundationsF25 application.
 * Specifies required dependencies and module exports.
 */
module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	//requires javafx.graphics;
	//requires static junit;
	// JUnit dependency added back to support test files
	
	// Restoring the related opens statement for JUnit
	opens inputValidation;
	 
	opens applicationMain to javafx.graphics, javafx.fxml;
	//Extra module for reading from the database and storing user information
    opens entityClasses to javafx.base;
}