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

public class RemoveListController { // implements Initializable {
	@FXML
	private Pane paneMain;

	@FXML
	private Label lblAddProd;

	@FXML
	private Button btnRemoveList;

	@FXML
	private Label lblList;

	@FXML
	private ComboBox<String> comboBoxList;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;
	int listID;

	public RemoveListController() {
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
		if (comboBoxList.getItems().isEmpty())
			try {
				comboBoxList.getItems().clear();
				String sql = "SELECT * FROM tbllistidtoname WHERE userName = ? "; // return all of the lists ID
																					// that user has created
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, userName);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next())
					comboBoxList.getItems().add(resultSet.getString("listName"));
				status = 1;
			} catch (SQLException e) {
				e.printStackTrace();
				status = -1;
			}
		return status;
	}

	@FXML
	public void handleButtonRemoveList(MouseEvent event) {

		if (event.getSource() == btnRemoveList) {
			try {
				int listID;
				String listName = comboBoxList.getValue();
				if (listName == null) {
					setLblError(Color.RED, "Please fill the form properly");
					return;
				}

				int status, alertRes = 1;

				listID = Find.listID(listName, userName);

				String sql = "SELECT * FROM tbluserinlist where listID = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, listID);
				ResultSet rs = preparedStatement.executeQuery();

				if (otherUserInList(rs))
					alertRes = setAlertWindowDelete();
				if (alertRes != 1) {
					setLblError(Color.RED, "Delete cancled");
					return;
				}

				status = deleteFromLists(listID);
				if (status > 0) {
					setLblError(Color.GREEN, "List deleted");
					comboBoxList.getItems().clear();
				}

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	private int deleteFromLists(int listID) {

		try {
			int status;
			String deleteFromtblListSQL = "DELETE FROM tbllist where listID = ? ";
			preparedStatement = con.prepareStatement(deleteFromtblListSQL);
			preparedStatement.setInt(1, listID);
			preparedStatement.executeUpdate();

			String deleteFromtbluserinlist = "DELETE FROM tbluserinlist where listID = ? ";
			preparedStatement = con.prepareStatement(deleteFromtbluserinlist);
			preparedStatement.setInt(1, listID);
			preparedStatement.executeUpdate();

			String deleteFromtbllistidtoname = "DELETE FROM tbllistidtoname where listID = ? ";
			preparedStatement = con.prepareStatement(deleteFromtbllistidtoname);
			preparedStatement.setInt(1, listID);
			status = preparedStatement.executeUpdate();

			if (status <= 0) { // if 0 - the list is not valid
				setLblError(Color.RED, "Error has occurred");
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
				"There are more users that are connected to that certion list, are you sure you want to procceed?");
		alert.getDialogPane().setHeaderText("Confirm delete");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return 1;
		if (result.get() == ButtonType.CANCEL)
			return 0;
		return -1; // error has occurred
	}

	private boolean otherUserInList(ResultSet rs) {
		try {
			while (rs.next())
				if (!rs.getString("userName").equals(userName))
					return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setUserName(String userName, String firstName) {
		this.userName = userName;
		this.firstName = firstName;
		this.listID = Find.listID(listName, userName);
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
