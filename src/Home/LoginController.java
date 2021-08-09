package Home;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import LoggedIn.UserController;
import Util.ConnectionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginController {

	@FXML
	private TextField txtfldUserName;

	@FXML
	private PasswordField txtfldPassword;

	@FXML
	private Button btnSignin;

	@FXML
	private Button btnSignUp;

	@FXML
	private Button btnForgotPassword;

	@FXML
	private Label lblError;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	String page;
	String userName, firstName;
	

	
	@FXML // Login Function
	public void handleButtonLogin(ActionEvent event) {

		if (event.getSource() == btnSignin) {
			// login here
			if (logIn().equals("Success")) {
				try {
					Scene scene;
					Node node = (Node) event.getSource();
					Stage stage = (Stage) node.getScene().getWindow();

					stage.close();
					if (page.equals("Admin"))
						scene = new Scene(FXMLLoader.load(getClass().getResource("/LoggedIn/AdminSection.fxml")));
					else {

						FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoggedIn/UserSection.fxml"));
						Parent root = loader.load();
						UserController userControl = loader.getController();
						userControl.setUserName(userName, firstName);
						scene = new Scene(root);
					}
					stage.setScene(scene);
					stage.show();

				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}

			}
		}
	}

	@FXML
	public void handleButtonSignup(ActionEvent event) {

		if (event.getSource() == btnSignUp) {
			// redirecting to sign up here
			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("SignUp.fxml")));
				stage.setScene(scene);
				stage.show();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonForgotPassword(ActionEvent event) {

		if (event.getSource() == btnForgotPassword) {
			// redirecting to forgot password here
			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();

				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("ForgotPassword.fxml")));

				stage.setScene(scene);
				stage.show();
			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	private String logIn() {
		String status = "Success";
		String userNamefld = txtfldUserName.getText().toString(); // usernameFld = the username that was entered
		String password = txtfldPassword.getText().toString();
		int permission;

		if (userNamefld.isEmpty() || password.isEmpty()) {
			setLblError(Color.RED, "Empty credentials");
			status = "Error";
		} else {
			// query
			String sql = "SELECT * FROM tbluser Where username = ? and password = ?";

			try {
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, userNamefld);
				preparedStatement.setString(2, password);

				resultSet = preparedStatement.executeQuery();
				if (!resultSet.next()) {
					setLblError(Color.RED, "Enter Correct Username/Password");
					status = "Error";
				} else {
					permission = resultSet.getInt("permission");
					firstName = resultSet.getString("firstName");
					if (permission == 1)
						page = "Admin";
					else {
						userName = userNamefld;
						page = "User";
					}
					setLblError(Color.GREEN, "Login Successful.. Redirecting..");
				}

			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
				status = "Exception";
			}
		}
		return status;
	}

	public LoginController() {
		con = ConnectionUtil.conDB();
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
