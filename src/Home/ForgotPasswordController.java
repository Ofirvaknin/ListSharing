package Home;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Util.ConnectionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ForgotPasswordController {

	@FXML
	private Label lblHello;

	@FXML
	private GridPane gpWrapCredentials;

	@FXML
	private Label lblUserName;

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
	private TextField txtfldFirstname;

	@FXML
	private TextField txtfldLastname;

	@FXML
	private TextField txtfldPhone;

	@FXML
	private TextField txtfldEmail;

	@FXML
	private Label lblError;

	@FXML
	private Button btnVerify;

	@FXML
	private Label lblAlreadyRegistered;

	@FXML
	private Button btnSignIn;

	String userName;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	@FXML
	public void handleButtonVerify(ActionEvent event) {
		if (event.getSource() == btnVerify) {

			if (Verify() > 0) {
				try {

					Node node = (Node) event.getSource();
					Stage stage = (Stage) node.getScene().getWindow();

					stage.close();

					FXMLLoader loader = new FXMLLoader(getClass().getResource("PasswordReset.fxml"));
					Parent root = loader.load();
					PasswordResetController pwReset = loader.getController();
					pwReset.setUserName(userName);
					Scene scene = new Scene(root);

					stage.setScene(scene);
					stage.show();

				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}

			}
		}
	}

	@FXML
	public void handleButtonSignin(ActionEvent event) {
		if (event.getSource() == btnSignIn) {

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

	private int Verify() {
		int status = 0;
		String userName = txtfldUsername.getText().toString();
		String firstname = txtfldFirstname.getText().toString();
		String lastname = txtfldLastname.getText().toString();
		String phone = txtfldPhone.getText().toString();
		String email = txtfldEmail.getText().toString();

		if (userName.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			setLblError(Color.RED, "Fill the form properly");

		} else {
			// query
			String sql = "SELECT * FROM tbluser WHERE username = ? AND firstname = ? "
					+ "AND lastname = ? AND phonenumber = ? AND emailAdd = ? AND Permission = ?";

			try {
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, firstname);
				preparedStatement.setString(3, lastname);
				preparedStatement.setString(4, phone);
				preparedStatement.setString(5, email);
				preparedStatement.setInt(6, 0);

				ResultSet rs = preparedStatement.executeQuery();
				if (rs.next()) {
					setLblError(Color.GREEN, "Redirecting..");
					this.userName = userName;
					status = 1;
				} else {
					setLblError(Color.RED, "Details do not match to proper registry");
					status = 0;
				}

			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
				status = -1; // -1 = Exception
			}
		}
		return status;
	}

	public ForgotPasswordController() {
		con = ConnectionUtil.conDB();
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
