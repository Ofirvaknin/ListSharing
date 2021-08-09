package Home;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import Util.ConnectionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SignupController {

	@FXML
	private Label lblHello;

	@FXML
	private GridPane gpWrapCredentials;

	@FXML
	private Label lblUserName;

	@FXML
	private Label lblPassword;

	@FXML
	private Label lblFirstname;

	@FXML
	private Label lblLastname;

	@FXML
	private Label lblPhone;

	@FXML
	private Label lblEmail;

	@FXML
	private TextField txtfldUsername;

	@FXML
	private TextField txtfldPassword;

	@FXML
	private TextField txtfldFirstname;

	@FXML
	private TextField txtfldEmail;

	@FXML
	private TextField txtfldPhone;

	@FXML
	private TextField txtfldLastname;

	@FXML
	private Label lblError;

	@FXML
	private Button btnSignup;

	@FXML
	private Label lblAlreadyRegistered;

	@FXML
	private Button btnSignIn;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	@FXML
	public void handleButtonSignUP(MouseEvent event) {
		if (event.getSource() == btnSignup) {
			// sign up here
			if (SignUp() > 0) {
				try {

					Node node = (Node) event.getSource();
					Stage stage = (Stage) node.getScene().getWindow();

					stage.close();
					Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Login.fxml")));
					stage.setScene(scene);
					stage.show();

				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}

			}
		}
	}

	@FXML
	public void handleButtonSignin(MouseEvent event) {

		if (event.getSource() == btnSignIn) {
			// login here

			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("Login.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	private int SignUp() {
		int status = 0;
		String userName = txtfldUsername.getText().toString();
		String password = txtfldPassword.getText().toString();
		String firstname = txtfldFirstname.getText().toString();
		String lastname = txtfldLastname.getText().toString();
		String phone = txtfldPhone.getText().toString();
		String email = txtfldEmail.getText().toString();

		if (userName.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty()
				|| email.isEmpty()) {
			setLblError(Color.RED, "Fill the form properly");
			// status = "Error";
		} else {
			// query
			String sql = "insert into tbluser "
					+ " (username, password, firstname, lastname, phonenumber, emailAdd, permission)"
					+ " values (?, ?, ?, ?, ?, ?,?)";

			try {
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, password);
				preparedStatement.setString(3, firstname);
				preparedStatement.setString(4, lastname);
				preparedStatement.setString(5, phone);
				preparedStatement.setString(6, email);
				preparedStatement.setInt(7, 0);

				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "User registered successfully.. Redirecting..");
				} else {
					setLblError(Color.RED, "Sign up failed");
					status = 0;
				}

			} catch (SQLException ex) {
				if (ex instanceof java.sql.SQLIntegrityConstraintViolationException) {
					setLblError(Color.RED, "Username already taken");
					status = 0;
				} else {
					System.err.println(ex.getMessage());
					status = -1; // -1 = Exception
				}
			}
		}
		return status;
	}

	public SignupController() {
		con = ConnectionUtil.conDB();
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
