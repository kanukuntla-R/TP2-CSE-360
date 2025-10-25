package guiAdminHome;


import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entityClasses.User;
import java.util.List;
import guiAdminHome.ViewUserList;




//This public class creates the popup window for viewing the user list, similar to that of the ViewAdminHome.java file.
public class ViewUserList {
    @SuppressWarnings("unchecked")
	public static void displayUserList() {
        Stage popupStage = new Stage();
        TableView<User> table = new TableView<>();


        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));


        TableColumn<User, String> firstNameCol = new TableColumn<>("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));


        TableColumn<User, String> middleNameCol = new TableColumn<>("Middle Name");
        middleNameCol.setCellValueFactory(new PropertyValueFactory<>("middleName"));


        TableColumn<User, String> lastNameCol = new TableColumn<>("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));


        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("emailAddress"));


        TableColumn<User, String> rolesCol = new TableColumn<>("Roles");
        rolesCol.setCellValueFactory(new PropertyValueFactory<>("rolesString"));


        table.getColumns().addAll(usernameCol, firstNameCol, middleNameCol, lastNameCol, emailCol, rolesCol);


        List<User> users = applicationMain.FoundationsMain.database.getAllUsers();
        
        // Debug code to help identify the warning issue
        System.out.println("=== TableView Debug Info ===");
        System.out.println("Number of users: " + (users != null ? users.size() : "null"));
        
        if (users != null && !users.isEmpty()) {
            System.out.println("First user details:");
            User firstUser = users.get(0);
            System.out.println("  - Username: " + firstUser.getUserName());
            System.out.println("  - First Name: " + firstUser.getFirstName());
            System.out.println("  - Middle Name: " + firstUser.getMiddleName());
            System.out.println("  - Last Name: " + firstUser.getLastName());
            System.out.println("  - Email: " + firstUser.getEmailAddress());
            System.out.println("  - Roles String: " + firstUser.getRolesString());
        } else {
            System.out.println("No users found or users list is null!");
        }
        System.out.println("=== End Debug Info ===");
        
        ObservableList<User> data = FXCollections.observableArrayList(users);
        table.setItems(data);


        Scene scene = new Scene(table, 800, 400);
        popupStage.setScene(scene);
        popupStage.setTitle("User List");
        popupStage.initOwner(ViewAdminHome.theStage);
        popupStage.show();
    }
}
