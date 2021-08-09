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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ProductController {

	@FXML
	private Label lblHello;

	@FXML
	private Label lblProdName;

	@FXML
	private Label lblDescription;

	@FXML
	private Label lblCategory;

	@FXML
	private Label lblLink;

	@FXML
	private TextField txtfldProductName;

	@FXML
	private Button btnAddProduct;

	@FXML
	private Button btnClearForm;

	@FXML
	private Button btnReturn;

	@FXML
	private ComboBox<String> comboBoxCat;

	@FXML
	private TextField txtfldLink;

	@FXML
	private TextArea txtAreaDescription;

	@FXML
	private Label lblError;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public ProductController() {
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
				Scene scene = new Scene(FXMLLoader.load(getClass().getResource("AdminSection.fxml")));
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
			try {
				int status;
				String pName = txtfldProductName.getText();
				String desc = txtAreaDescription.getText();
				String cat = comboBoxCat.getValue();
				String link = txtfldLink.getText();

				if (cat == null) {
					setLblError(Color.RED, "Please choose category");
					return;
				}
				if (pName.equals("") || desc.equals("")) {
					setLblError(Color.RED, "Please fill the form properly");
					return;
				}
				if(link.equals(""))
					link = "Missing";
				if (Find.productID(pName) != -1) {
					setLblError(Color.RED, "Product already exist");
					return;
				}
				int catID = Find.catID(cat); // in this from, the category must be listed with valid id.

				String sql = "insert into tblproduct " + " (prodName, description, categoryID, link)"
						+ " values (?, ?, ?, ?)";
				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, pName);
				preparedStatement.setString(2, desc);
				preparedStatement.setInt(3, catID);
				preparedStatement.setString(4, link);
				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "Product added..");
				} else {
					setLblError(Color.RED, "Product add failed");
					status = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	@FXML
	public void handleButtonClearForm(MouseEvent event) {
		if (event.getSource() == btnClearForm) {
			txtfldProductName.clear();
			txtAreaDescription.clear();
			txtfldLink.clear();
			comboBoxCat.setPromptText("Category");
		}
	}

	@FXML
	private int setComboBox() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		if (comboBoxCat.getItems().isEmpty())
			try {
				comboBoxCat.getItems().clear();
				String sql = "SELECT catName FROM tblcategory";
				preparedStatement = con.prepareStatement(sql);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next())
					comboBoxCat.getItems().add(resultSet.getString("catName"));
				status = 1;
			} catch (SQLException e) {
				e.printStackTrace();
				status = -1;
			}
		return status;
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
