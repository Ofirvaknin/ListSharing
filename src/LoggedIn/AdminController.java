package LoggedIn;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Util.ConnectionUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdminController {

	@FXML
	private Button btnLogout;

	@FXML
	private Label lblHello;

	@FXML
	private Button btnAddProduct;

	@FXML
	private Button btnAddCategory;

	@FXML
	private Button btnViewUsers;

	@FXML
	private Button btnRemoveProduct;

	@FXML
	private Button btnRemoveCategory;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	@FXML
	public void handleButtonLogout(MouseEvent event) {

		if (event.getSource() == btnLogout) {
			// logout here

			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

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
	public void handleButtonAddProduct(MouseEvent event) {

		if (event.getSource() == btnAddProduct) {
			// add product
			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("AddProduct.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonAddCategory(MouseEvent event) {

		if (event.getSource() == btnAddCategory) {
			// add category

			try {
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("AddCategory.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonRemoveCategory(MouseEvent event) {

		if (event.getSource() == btnRemoveCategory) {
			// remove category

			try {
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("RemoveCategory.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	public void handleButtonRemoveProduct(MouseEvent event) {

		if (event.getSource() == btnRemoveProduct) {
			// remove product
			try {
				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("RemoveProduct.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	public AdminController() {
		con = ConnectionUtil.conDB();
	}

}
