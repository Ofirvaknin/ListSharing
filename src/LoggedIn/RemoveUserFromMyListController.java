package LoggedIn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import Util.ConnectionUtil;
import Util.Find;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RemoveUserFromMyListController { // implements Initializable {

	@FXML
	private Pane paneMain;

	@FXML
	private Label lblHello;

	@FXML
	private Label lblList;

	@FXML
	private Button btnRemove;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private ComboBox<String> comboBoxLists;

	@FXML
	private Label lblUserName;

	@FXML
	private ComboBox<String> comboBoxUser;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;

	public RemoveUserFromMyListController() {
		con = ConnectionUtil.conDB();

	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {
			// Return here
			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();

				FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSection.fxml"));
				Parent root = loader.load();
				UserController userController = loader.getController();
				userController.setUserName(userName, firstName);
				Scene scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	private int setComboBoxList() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		if (comboBoxLists.getItems().isEmpty())
			try {
				comboBoxLists.getItems().clear();
				String sql = "SELECT * FROM tbllistidtoname WHERE userName = ? "; // return all of the lists ID
																					// that user has created
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, userName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next())
					comboBoxLists.getItems().add(resultSet.getString("listName"));
				status = 1;
			} catch (SQLException e) {
				e.printStackTrace();
				status = -1;
			}
		return status;
	}

	@FXML
	private int setComboBoxUser() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption

		if (comboBoxLists.getItems().isEmpty()) {
			setLblError(Color.RED, "Please choose list first");
			return 0;
		}

		try {
			comboBoxUser.getItems().clear();
			int listID = Find.listID(comboBoxLists.getValue(), userName);
			String sql = "SELECT * FROM tbluserinlist WHERE listID = ? "; // return all of the lists ID
																			// that user has created
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				if (!resultSet.getString("userName").equals(userName))
					comboBoxUser.getItems().add(resultSet.getString("userName"));
			}
			status = 1;
		} catch (SQLException e) {
			e.printStackTrace();
			status = -1;
		}
		return status;
	}

	@FXML
	public void handleButtonRemoveUserFromList(MouseEvent event) {

		if (event.getSource() == btnRemove) {
			if (comboBoxLists.getItems().isEmpty() || comboBoxUser.getItems().isEmpty()) {
				setLblError(Color.RED, "Please fill the form properly");
				return;
			}
			try {
				int listID;
				String listName = comboBoxLists.getValue();
				String userToRemove = comboBoxUser.getValue();

				int status, alertRes = 1;

				listID = Find.listID(listName, userName);

				String sql = "SELECT * FROM tbllist where listID = ? AND responsibleUser = ?"; // make sure that the
																								// asked user is in the
																								// list

				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, listID);
				preparedStatement.setString(2, userToRemove);
				ResultSet rs = preparedStatement.executeQuery();

				System.out.println("Test2");
				if (userResponsibleForSomthing(rs, userToRemove))
					alertRes = setAlertWindowDelete();
				if (alertRes != 1) {
					setLblError(Color.RED, "Delete canceled");
					return;
				}

				status = deleteUserAndProductFromList(listID, userToRemove);

				if (status > 0) {
					setLblError(Color.GREEN, "User deleted from list");
					comboBoxLists.getItems().clear();
					comboBoxUser.getItems().clear();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	private int deleteUserAndProductFromList(int listID, String user) { // delete all the products that the user is
																		// responsible for
																		// as well, delete the user from the list
		try {
			System.out.println("Test3");
			int status;
			String deleteFromtblListSQL = "DELETE FROM tbllist where listID = ? AND responsibleUser = ?";
			preparedStatement = con.prepareStatement(deleteFromtblListSQL);
			preparedStatement.setInt(1, listID);
			preparedStatement.setString(2, user);
			preparedStatement.executeUpdate(); // status = 0 IF User is not responsible for any products in
												// that list

			String deleteFromtbluserinlistSQL = "DELETE FROM tbluserinlist where listID = ? AND userName = ?";
			preparedStatement = con.prepareStatement(deleteFromtbluserinlistSQL);
			preparedStatement.setInt(1, listID);
			preparedStatement.setString(2, user);
			status = preparedStatement.executeUpdate(); // status = 0 IF error has occurred

			if (status <= 0) {
				setLblError(Color.RED, "Error has occurred while trying to delete the user");
				return 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1; // failed
		}
		return 1;
	}

	private int setAlertWindowDelete() {
		Stage stage = (Stage) paneMain.getScene().getWindow();
		Alert.AlertType type = Alert.AlertType.CONFIRMATION;
		Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stage);
		alert.getDialogPane().setContentText(
				"The user is responsible for products in that certion list, are you sure you want to procceed?");
		alert.getDialogPane().setHeaderText("Confirm delete");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return 1;
		if (result.get() == ButtonType.CANCEL)
			return 0;
		return -1; // error has occurred
	}

	private boolean userResponsibleForSomthing(ResultSet rs, String user) {
		try {
			while (rs.next())
				if (rs.getString("responsibleUser").equals(user)) {
					System.out.println("RETURN true");
					return true;
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public void setUserName(String userName, String firstName) {
		this.userName = userName;
		this.firstName = firstName;
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
