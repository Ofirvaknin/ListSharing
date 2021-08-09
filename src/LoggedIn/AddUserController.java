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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddUserController {

	@FXML
	private Label lblHello;

	@FXML
	private Label lblList;

	@FXML
	private Button btnAdd;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private ComboBox<String> comboBoxLists;

	@FXML
	private Label lblUserName;

	@FXML
	private TextField txtfldUserName;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;

	// Add users to lists
	public AddUserController() {
		con = ConnectionUtil.conDB();
	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {
			// logout here

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
	public void handleButtonAdd(MouseEvent event) {

		if (event.getSource() == btnAdd) {
			// continue here
			int status;
			try {
				listName = comboBoxLists.getValue();
				String name = txtfldUserName.getText(), sql;
				int listID = Find.listID(listName, userName);
				if (comboBoxLists.getValue() == null) {
					setLblError(Color.RED, "You did not choose any list");
					return;
				}

				if (name.equals("")) {
					setLblError(Color.RED, "Username empty");
					txtfldUserName.clear();
					return;
				}

				if (name.equals(userName)) {
					setLblError(Color.RED, "You can not add yourself");
					txtfldUserName.clear();
					return;
				}

				if (Find.userNameToFullName(name).equals("") || name.equals("admin")) {
					setLblError(Color.RED, "Username is not valid, please check again");
					txtfldUserName.clear();
					return;
				}

				//Check if the user already belong to the same list Setting query

				sql = "SELECT listID FROM tbluserinlist WHERE userName = ? AND listID =?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, name);
				preparedStatement.setInt(2, listID);
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {		
						setLblError(Color.RED, "User already belong to the choosen list");
						return;
				}
				
				// check if the user already participate in list with the same name
				sql = "SELECT listName FROM tbllistidtoname WHERE userName = ? AND listName = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, name);
				preparedStatement.setString(2, listName);
				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					setLblError(Color.RED, "User already belong to list with the same name");
					return;
				}

			

				sql = "insert into tbluserinlist " + " (listID,userName)" + " values (?,?)";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, listID);
				preparedStatement.setString(2, name);
				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "User added to the list");
					return;
				} else {
					setLblError(Color.RED, "User was not added");
					return;
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

		}
	}

	@FXML
	private int setComboBox() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = exception
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
			status = 1;
		} catch (SQLException e) {
			e.printStackTrace();
			status = -1;
		}
		return status;
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
