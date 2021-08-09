package LoggedIn;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Util.ConnectionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class UserController {

	@FXML
	private Button btnMylist;

	@FXML
	private Button btnCreateList;

	@FXML
	private Button btnDeleteList;

	@FXML
	private Button btnLogout;

	@FXML
	private Label lblHello;

	@FXML
	private Button btnAddUserToList;

	@FXML
	private Button btnDeleteUserFromMyList;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName, firstName;

	@FXML
	public void handleButtonLogout(MouseEvent event) {

		if (event.getSource() == btnLogout) {
			// logout here

			try {

				// add you loading or delays - ;-)
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				// stage.setMaximized(true);
				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/Home/Login.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonMylist(MouseEvent event) {

		if (event.getSource() == btnMylist) {
			// MyLists here

			try {
				Scene scene;

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("MyLists.fxml"));
				Parent root = loader.load();
				ViewListController listController = loader.getController();
				listController.setUserName(userName, firstName);
				scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonCreateList(MouseEvent event) {

		if (event.getSource() == btnCreateList) {
			// Create List here

			try {
				Scene scene;
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateList.fxml"));
				Parent root = loader.load();
				CreateListController createListController = loader.getController();
				createListController.setUserName(userName, firstName);
				scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonDeleteList(MouseEvent event) {

		if (event.getSource() == btnDeleteList) {
			// Delete list here

			try {
				Scene scene;
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveList.fxml"));
				Parent root = loader.load();
				RemoveListController removeListController = loader.getController();
				removeListController.setUserName(userName, firstName);
				scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonAddUsers(MouseEvent event) {

		if (event.getSource() == btnAddUserToList) {
			// Add user to list here

			try {
				Scene scene;
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("AddUsers.fxml"));
				Parent root = loader.load();
				AddUserController addUserController = loader.getController();
				addUserController.setUserName(userName, firstName);
				scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonRemoveUsers(MouseEvent event) {

		if (event.getSource() == btnDeleteUserFromMyList) {
			// Remove user from list here

			try {
				Scene scene;	
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveUsersFromMyLists.fxml"));
				Parent root = loader.load();
				RemoveUserFromMyListController removeUserFromListController = loader.getController();
				removeUserFromListController.setUserName(userName, firstName);
				scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	// user
	public UserController() {
		con = ConnectionUtil.conDB();
	}

	public void setUserName(String userName, String firstName) {
		this.userName = userName;
		this.firstName = firstName;
		lblHello.setText("Hello " + firstName);
	}

}
