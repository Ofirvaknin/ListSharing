package LoggedIn;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import Util.ConnectionUtil;
import Util.Find;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RemoveProductController implements Initializable {

	@FXML
	private Pane paneMain;

	@FXML
	private Label lblAddProd;

	@FXML
	private Button btnRemoveProduct;

	@FXML
	private Label lblCat;

	@FXML
	private ComboBox<String> comboBoxCat;

	@FXML
	private Label lblProd;

	@FXML
	private ComboBox<String> comboBoxProd;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private TextArea txtfldNote;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;
	int listID;

	public RemoveProductController() {
		con = ConnectionUtil.conDB();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtfldNote.setEditable(false);
	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {
			// Return here

			try {

				Node node = (Node) event.getSource();
				Stage stage = (Stage) node.getScene().getWindow();

				stage.close();
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("AdminSection.fxml")));
				stage.setScene(scene);
				stage.show();

			} catch (IOException ex) {
				System.err.println(ex.getMessage());
			}

		}
	}

	@FXML
	private int setComboBoxCategory() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		comboBoxProd.getItems().clear();
		if (comboBoxCat.getItems().isEmpty())
			try {
				comboBoxCat.getItems().clear();
				String sql = "SELECT catName From tblCategory";
				preparedStatement = con.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();

				while (resultSet.next()) {
					comboBoxCat.getItems().add(resultSet.getString("catName"));
				}
				status = 1;
			} catch (SQLException e) {
				e.printStackTrace();
				status = -1;
			}
		return status;
	}

	@FXML
	private int setComboBoxProduct() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		try {
			comboBoxProd.getItems().clear();
			String catName = comboBoxCat.getValue();
			if (catName == null) {
				setLblError(Color.RED, "You did not choose any catgory");
				return 0;
			}

			comboBoxProd.getItems().clear();
			String sql = "SELECT prodName From tblProduct WHERE categoryID = ?";
			preparedStatement = con.prepareStatement(sql);
			Find.catID(catName);
			preparedStatement.setInt(1, Find.catID(catName));
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				comboBoxProd.getItems().add(resultSet.getString("prodName"));
			}

			status = 1;
		} catch (SQLException e) {
			e.printStackTrace();
			status = -1;
		}
		return status;
	}

	@FXML
	public void handleButtonRemoveProduct(MouseEvent event) {

		if (event.getSource() == btnRemoveProduct) {
			try {
				int prodID;
				String prodName = comboBoxProd.getValue();
				String cat = comboBoxCat.getValue();
				if (prodName == null || cat == null) {
					setLblError(Color.RED, "Please fill the form properly");
					return;
				}

				int status, alertRes = 1;

				prodID = Find.productID(prodName);

				String sql = "SELECT listID FROM tbllist WHERE prodID = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, prodID);
				ResultSet rs = preparedStatement.executeQuery();

				if (rs.next())
					alertRes = setAlertWindowDelete();
				if (alertRes != 1) {
					setLblError(Color.RED, "Delete cancled");
					return;
				}
				// setting query
				sql = "DELETE from tbllist WHERE prodID = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, prodID);

				preparedStatement.executeUpdate();

				sql = "DELETE FROM tblproduct where prodID = ? ";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, prodID);

				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "Product deleted");
					comboBoxCat.getItems().clear();
					comboBoxProd.getItems().clear();
				} else {
					setLblError(Color.RED, "Product deleting failed");
					status = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	private int setAlertWindowDelete() {
		Stage stage = (Stage) paneMain.getScene().getWindow();
		Alert.AlertType type = Alert.AlertType.CONFIRMATION;
		Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stage);
		alert.getDialogPane().setContentText("Product belong to certion list, are you sure you want to procceed?");
		alert.getDialogPane().setHeaderText("Confirm delete");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK)
			return 1;
		if (result.get() == ButtonType.CANCEL)
			return 0;
		return -1; // error has occurred
	}

	public void setUserName(String userName, String firstName, String listName) {
		this.userName = userName;
		this.firstName = firstName;
		this.listName = listName;
		this.listID = Find.listID(listName, userName);
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
