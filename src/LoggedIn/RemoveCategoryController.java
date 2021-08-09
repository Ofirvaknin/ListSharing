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

public class RemoveCategoryController implements Initializable {

	@FXML
	private Pane paneMain;

	@FXML
	private Label lblAddProd;

	@FXML
	private Button btnRemoveCategory;

	@FXML
	private Label lblCat;

	@FXML
	private ComboBox<String> comboBoxCat;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtfldNote.setEditable(false);	
	}

	
	
	public RemoveCategoryController() {
		con = ConnectionUtil.conDB();

	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {
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
		if (comboBoxCat.getItems().isEmpty())
			try {

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
	public void handleButtonRemoveCategory(MouseEvent event) {

		if (event.getSource() == btnRemoveCategory) {
			try {
				String cat = comboBoxCat.getValue();
				if (cat == null) {
					setLblError(Color.RED, "Please fill the form properly");
					return;
				}

				int status, alertRes = 1;

				int catID = Find.catID(cat);
				String sql = "SELECT prodID FROM tblproduct where categoryID = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, catID);
				ResultSet rs = preparedStatement.executeQuery();

				if (rs.next()) // // product is connected with that category
				{
					alertRes = setAlertWindowDelete();
					if (alertRes != 1) {
						setLblError(Color.RED, "Delete canceled");
						return;
					}
					status = deleteAllProductsFromCategory(rs);
					if (status < 0)
						return;
				}

				// delete category query
				sql = "DELETE FROM tblcategory WHERE categoryID = ?";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, catID);
				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "Category deleted");
					comboBoxCat.getItems().clear();
				} else {
					setLblError(Color.RED, "Category deleting failed");
					status = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	private int deleteAllProductsFromCategory(ResultSet rs) {
		try {
			int statusFirst, statusSecond, productID;
			do {
				productID = rs.getInt("prodID");
				// first query
				String deleteProductFromListSQL = "DELETE from tbllist WHERE prodID = ?"; // delete product from all of
																							// the lists
				preparedStatement = con.prepareStatement(deleteProductFromListSQL);
				preparedStatement.setInt(1, productID);
				statusFirst = preparedStatement.executeUpdate(); // product deleted from all connected lists.

				// second query
				String deleteProdSQL = "DELETE from tblproduct where prodID = ?";
				preparedStatement = con.prepareStatement(deleteProdSQL);
				preparedStatement.setInt(1, productID);
				statusSecond = preparedStatement.executeUpdate();

				if (statusFirst <= 0 && statusSecond <= 0) {
					setLblError(Color.RED, "Error has occurred, aborting");
					return -1;// error has occurred , one of the queries has failed.
				}
			} while (rs.next());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	private int setAlertWindowDelete() {
		Stage stage = (Stage) paneMain.getScene().getWindow();
		Alert.AlertType type = Alert.AlertType.CONFIRMATION;
		Alert alert = new Alert(type, "");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(stage);
		alert.getDialogPane().setContentText("Category belong to certion product, are you sure you want to procceed?");
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
