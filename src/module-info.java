module FoundationsF25 {
	requires javafx.controls;
	requires java.sql;
	
	opens applicationMain to javafx.graphics, javafx.fxml;
	//Extra module for reading from the database and storing user information
    opens entityClasses to javafx.base;
}
