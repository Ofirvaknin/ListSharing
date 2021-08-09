package Home;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import Util.ConnectionUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PasswordResetController implements Initializable {

	@FXML
	private Label lblHello;

	@FXML
	private GridPane gpWrapCredentials;

	@FXML
	private TextField txtfldFirst;

	@FXML
	private PasswordField txtfldSecond;

	@FXML
	private Label lblError;

	@FXML
	private Button btnReset;

	@FXML
	private Button btnSignIn;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnSignIn.setDisable(true);
		btnSignIn.setVisible(false);

	}

	@FXML
	public void handleButtonReset(ActionEvent event) {
		if (event.getSource() == btnReset) {
			// sign up here
			if (Reset() > 0) {
				setLblError(Color.GREEN, "password reset successfully,\n you may log in");
				btnReset.setVisible(false);
				btnReset.setDisable(true);

				btnSignIn.setDisable(false);
				btnSignIn.setVisible(true);

			}
		}
	}

	@FXML
	public void handleButtonSignin(ActionEvent event) {

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

	private int Reset() {
		int status = 0;
		String first = txtfldFirst.getText().toString();
		String second = txtfldSecond.getText().toString();
		if (first.equals("") || second.equals("")) {
			setLblError(Color.RED, "Fill the form properly");
			txtfldFirst.clear();
			txtfldSecond.clear();
			return 0;
		}
		if (!(first.equals(second))) {
			setLblError(Color.RED, "password do not match, try again!");
			txtfldFirst.clear();
			txtfldSecond.clear();
			return 0;
		}

		// query
		String sql = "UPDATE tbluser SET password = ? WHERE userName = ?";

		try {
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setString(1, first);
			preparedStatement.setString(2, userName);

			status = preparedStatement.executeUpdate();

		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
			status = -1; // -1 = Exception
		}
		return status;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public PasswordResetController() {
		con = ConnectionUtil.conDB();
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
