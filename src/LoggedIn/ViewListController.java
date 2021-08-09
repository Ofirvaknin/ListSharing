package LoggedIn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Util.ConnectionUtil;
import Util.Find;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ViewListController {

	@FXML
	private Label lblHello;

	@FXML
	private Label lblList;

	@FXML
	private Button btnContinue;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private ComboBox<String> comboBoxLists;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;
	int listID;

	public ViewListController() {
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
				UserController userControl = loader.getController();
				userControl.setUserName(userName, firstName);
				Scene scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonContinue(MouseEvent event) {

		if (event.getSource() == btnContinue) {
			// Continue here

			try {
				listName = comboBoxLists.getValue();
				if (comboBoxLists.getValue() == null) {
					setLblError(Color.RED, "You did not choose any list");
					return;
				}
				listID = Find.listID(listName, userName);
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();
				stage.close();
				FXMLLoader loader = new FXMLLoader(getClass().getResource("EditList.fxml"));
				
				Parent root = loader.load();
				EditListController listEditor = loader.getController();
				listEditor.setUserName(userName, firstName, listName, listID);
				listEditor.tableUpdater();

				Scene scene = new Scene(root);

				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	// 1 successed, 0 failed.
	@FXML
	private int setComboBox() {
		try {
			comboBoxLists.getItems().clear();
			String sql = "SELECT listID FROM tbluserinlist WHERE userName = ? "; // return all of the lists ID that
																					// belong to users
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, userName);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt(1); // list id
				sql = "SELECT listName FROM tbllistidtoname WHERE listID = ?";
				PreparedStatement nestedPS = con.prepareStatement(sql);
				nestedPS.setInt(1, id);
				ResultSet rs = nestedPS.executeQuery();
				if (rs.next())
					comboBoxLists.getItems().add(rs.getString("listName"));
			}
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
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
