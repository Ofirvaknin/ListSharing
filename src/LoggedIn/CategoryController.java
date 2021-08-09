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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CategoryController {

	@FXML
	private Label lblHello;

	@FXML
	private Label lblProdName;

	@FXML
	private Label lblCategory;

	@FXML
	private TextField txtfldCatName;

	@FXML
	private Button btnAddCategory;

	@FXML
	private Button btnClearForm;

	@FXML
	private Button btnReturn;

	@FXML
	private Label lblError;

	@FXML
	private ComboBox<String> comboBoxCat;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	public CategoryController() {
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
	public void handleButtonAddCategory(MouseEvent event) {

		if (event.getSource() == btnAddCategory) {
			try {
				int status;
				String catName = txtfldCatName.getText();

				if (catName.equals("")) {
					setLblError(Color.RED, "Please enter category");
					return;
				}
				if (Find.catID(catName) != -1) {
					setLblError(Color.RED, "Category already exist");
					return;
				}

				String sql = "insert into tblcategory " + " (catName)" + " values (?)";

				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setString(1, catName);

				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "Category added..");
				} else {
					setLblError(Color.RED, "Category failed");
					status = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}

		}
	}

	@FXML
	public void handleButtonClearForm(MouseEvent event) {
		if (event.getSource() == btnClearForm) {
			txtfldCatName.clear();
			comboBoxCat.setPromptText("Category");
		}
	}

	@FXML
	private int setComboBox() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = exception
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
