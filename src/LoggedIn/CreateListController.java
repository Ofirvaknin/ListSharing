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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CreateListController {

	@FXML
	private Label lblHello;

	@FXML
	private Label lblListName;

	@FXML
	private TextField txtfldListName;

	@FXML
	private Button btnAddList;

	@FXML
	private Button btnClearForm;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private Button btnMylists;

	String userName;
	String firstName;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public CreateListController() {
		con = ConnectionUtil.conDB();
	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {

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
	public void handleButtonbtnAddList(MouseEvent event) {

		if (event.getSource() == btnAddList) {
			try {
				
				String listName = txtfldListName.getText();
				int status,listID = Find.listID(listName, userName);
				if (listName.equals("")) { 
					setLblError(Color.RED, "Please enter list name");
					return;
				}
				if (listID != -1) {
					setLblError(Color.RED, "List name already exist");
					return;
				}

				// First Query - insert into tbllistidtoname table
				String sql = "insert into tbllistidtoname " + " (listName,userName)" + " values (?,?)";

				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, listName);
				preparedStatement.setString(2, userName);
				preparedStatement.executeUpdate();
				listID = Find.listID(listName, userName);

				// Second Query - add user to userinlist table
				sql = "insert into tbluserinlist " + " (listID,userName)" + " values (?,?)";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, listID);
				preparedStatement.setString(2, userName);

				status = preparedStatement.executeUpdate();
				if (status > 0) {
					 setLblError(Color.GREEN, "List added..");
				} else {
					 setLblError(Color.RED, "List add failed");
					status = 0;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	@FXML
	public void handleButtonMylist(MouseEvent event) {

		if (event.getSource() == btnMylists) {
			// My lists redirect here

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
	public void handleButtonClearForm(MouseEvent event) {
		if (event.getSource() == btnClearForm) {
			txtfldListName.clear();
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
