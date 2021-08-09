package LoggedIn;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import DataStructures.Product;
import Util.ConnectionUtil;
import Util.Find;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EditListController implements Initializable {

	@FXML
	private Label lblAddProd;

	@FXML
	private Label lblCat;

	@FXML
	private ComboBox<String> comboBoxCat;

	@FXML
	private Label lblProd;

	@FXML
	private ComboBox<String> comboBoxProd;

	@FXML
	private Label lblQuantity;

	@FXML
	private TextField txtfldQuantity;

	@FXML
	private Button btnAddProduct;

	@FXML
	private Button btnReturn;

	@FXML
	private TableView<ModelTable> tableViewList;

	@FXML
	private TableColumn<ModelTable, String> colProdName;

	@FXML
	private TableColumn<ModelTable, Integer> colReqQuantity;

	@FXML
	private TableColumn<ModelTable, String> colResponsibleUser;

	@FXML
	private TableColumn<ModelTable, String> colLink;

	@FXML
	private Label lblSearch;

	@FXML
	private TextField filterField;

	@FXML
	private Label lblError;

	@FXML
	private Label lblCatEdit;

	@FXML
	private Label lblProductEdit;

	@FXML
	private TextField txtfldQuantityEdit;

	@FXML
	private ComboBox<String> comboBoxUsers;

	@FXML
	private Button btnSelect;

	@FXML
	private Button btnDelete;

	@FXML
	private Button btnSave;

	@FXML
	private TextField txtFldLink;

	Connection con = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;

	String userName;
	String firstName;
	String listName;
	int listID, rowIndex = -1;

	ObservableList<ModelTable> oblist = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		txtFldLink.setEditable(false);
	}

	public EditListController() {
		con = ConnectionUtil.conDB();

	}

	public class ModelTable {

		String prodName, responsible, link;
		int reqQuantity;

		public ModelTable(String prodName, String responsible, String reqQuantity, String link) {
			super();
			this.prodName = prodName;
			this.responsible = responsible;
			this.reqQuantity = Integer.parseInt(reqQuantity);
			this.link = link;

		}

		public String getProdName() {
			return prodName;
		}

		public void setProdName(String prodName) {
			this.prodName = prodName;
		}

		public String getResponsible() {
			return responsible;
		}

		public void setResponsible(String responsible) {
			this.responsible = responsible;
		}

		public int getReqQuantity() {
			return reqQuantity;
		}

		public void setReqQuantity(int reqQuantity) {
			this.reqQuantity = reqQuantity;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

	}

	public void tableUpdater() {

		try {

			oblist.clear();
			tableViewList.getItems().clear();
			// Get list details
			String sql = "SELECT * FROM tbllist WHERE listID = ?";
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				Product prod = Find.prodIDtoName(Integer.parseInt(rs.getString("prodID")));
				String name = prod.getProdName();
				String responsible = rs.getString("responsibleUser");
				String quantity = rs.getString("reqQuantity");
				String link = prod.getLink();
				ModelTable tb = new ModelTable(name, responsible, quantity, link);
				oblist.add(tb);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		colProdName.setCellValueFactory(new PropertyValueFactory<>("prodName"));
		colReqQuantity.setCellValueFactory(new PropertyValueFactory<>("reqQuantity"));
		colResponsibleUser.setCellValueFactory(new PropertyValueFactory<>("Responsible"));

		// New Addition
		colLink.setCellValueFactory(new PropertyValueFactory<>("Link"));

		tableViewList.setItems(oblist);

		FilteredList<ModelTable> filteredData = new FilteredList<>(oblist, b -> true);

		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(modeltbl -> {
				// If filter text is empty, display all persons.

				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (modeltbl.getProdName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
					return true; // Filter matches first name.
				} else if (Integer.toString(modeltbl.getReqQuantity()).indexOf(lowerCaseFilter) != -1) {
					return true;
				} else if (String.valueOf(modeltbl.getResponsible()).indexOf(lowerCaseFilter) != -1)
					return true;
				else
					return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<ModelTable> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		// Otherwise, sorting the TableView would have no effect.
		sortedData.comparatorProperty().bind(tableViewList.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableViewList.setItems(sortedData);

	}

	@FXML
	public void handleButtonReturn(MouseEvent event) {

		if (event.getSource() == btnReturn) {
			// Return here

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
	private int setComboBoxCategory() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		comboBoxProd.getItems().clear();
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
	private int setComboBoxProduct() {
		int status = 0;// 1 = succeed ,0 = failed,-1 = execption
		if (comboBoxProd.getItems().isEmpty())
			try {
				String catName = comboBoxCat.getValue();
				if (catName == null) {
					setLblError(Color.RED, "You did not choose any catgory");
					return 0;
				}

				comboBoxProd.getItems().clear();
				String sql = "SELECT prodName From tblProduct WHERE categoryID = ?";

				preparedStatement = con.prepareStatement(sql);
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
	public void getSelected(MouseEvent event) {
		rowIndex = tableViewList.getSelectionModel().getSelectedIndex();
		if (rowIndex <= -1)
			return;
		String catName, prodName, rUser;
		prodName = colProdName.getCellData(rowIndex).toString();
		catName = Find.findCategoryForProd(prodName);
		rUser = colResponsibleUser.getCellData(rowIndex).toString();

		lblCatEdit.setText(catName);
		lblProductEdit.setText(prodName);
		txtfldQuantityEdit.setText(colReqQuantity.getCellData(rowIndex).toString());
		txtFldLink.setText(colLink.getCellData(rowIndex).toString());
		setComboBoxUsersInList();
		comboBoxUsers.setValue(rUser);

	}

	private void setComboBoxUsersInList() {
		try {
			comboBoxUsers.getItems().clear();
			String sql = "SELECT * FROM tbluserinlist WHERE listID = ? "; // return all of the lists ID
																			// that user has created
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				comboBoxUsers.getItems().add(resultSet.getString("userName"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void clearEditForm() {
		lblCatEdit.setText("Select");
		lblProductEdit.setText("Select");
		txtFldLink.setText("");
		txtfldQuantityEdit.setText("");
		comboBoxUsers.getItems().clear();
	}

	@FXML
	public void Edit() {
		if (lblCatEdit.getText().equals("Category")) {
			setLblError(Color.RED, "Please choose line in order to edit");
			return;
		}
		try {
			String rUser = comboBoxUsers.getValue();
			int quantity = Integer.parseInt(txtfldQuantityEdit.getText().toString()), status = 0;

			if (rUser.equals("") || quantity <= 0) {
				setLblError(Color.RED, "Please fill the form properly");
				return;
			}

			String sql = "UPDATE tbllist set reqQuantity= '" + quantity + "',responsibleUser= '" + rUser
					+ "' where listID = ? AND prodID = ?";
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			preparedStatement.setInt(2, Find.productID(lblProductEdit.getText()));
			status = preparedStatement.executeUpdate();
			if (status > 0) {
				setLblError(Color.GREEN, "Updated");
				clearEditForm();
			} else {
				setLblError(Color.RED, "Error has occurred");
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		tableUpdater();
	}

	@FXML
	public void handleButtonAddProduct(MouseEvent event) {

		if (event.getSource() == btnAddProduct) {
			try {

				String prodName = comboBoxProd.getValue();
				String cat = comboBoxCat.getValue();
				String quantityStr = txtfldQuantity.getText();
				if (prodName == null || cat == null || quantityStr.isEmpty()) {
					setLblError(Color.RED, "Please fill the form properly");
					return;
				}

				int status;

				listID = Find.listID(listName, userName);
				int quantity = Integer.parseInt(txtfldQuantity.getText());

				if (quantity <= 0) {
					setLblError(Color.RED, "Please set valid product quantity");
					return;
				}

				if (prodExistInList(prodName, listID)) {
					setLblError(Color.RED, "The product is already belong to the list");
					return;
				}

				String sql = "insert into tbllist " + " (listID,prodID,reqQuantity,responsibleUser)"
						+ " values (?,?,?,?)";

				preparedStatement = con.prepareStatement(sql);
				preparedStatement.setInt(1, listID);
				preparedStatement.setInt(2, Find.productID(prodName));
				preparedStatement.setInt(3, quantity);
				preparedStatement.setString(4, userName);

				status = preparedStatement.executeUpdate();
				if (status > 0) {
					setLblError(Color.GREEN, "Product added");
				} else {
					setLblError(Color.RED, "Product adding failed");
					status = 0;
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("Somthing went wrong");
			}
			// need to clean table before
			clearEditForm();
			tableUpdater();
		}
	}

	// This function check if the product is already belong to the list
	private boolean prodExistInList(String prodName, int listID) {
		try {
			int prodID = Find.productID(prodName);
			String sql = "Select * FROM tbllist WHERE listID = ? AND prodID = ?";
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			preparedStatement.setInt(2, prodID);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next())
				return true;
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Somthing went wrong");
		}
		return false;
	}

	public void delTupleInList() {
		if (lblCatEdit.getText().equals("Category")) {
			setLblError(Color.RED, "Please choose line in order to delete it");
			return;
		}

		if (!colResponsibleUser.getCellData(rowIndex).toString().equals(userName)) {
			String owner = Find.listOwner(listID);
			if (!userName.equals(owner)) {
				setLblError(Color.RED, "You cannot delete other participants \n products.");
				return;
			}
		}

		int prodID = Find.productID(lblProductEdit.getText().toString());
		try {
			String sql = "DELETE FROM tbllist WHERE listID = ? AND prodID = ?";
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			preparedStatement.setInt(2, prodID);
			int status = preparedStatement.executeUpdate();
			if (status > 0) {
				setLblError(Color.GREEN, "Item deleted");
				clearEditForm();
			} else {
				setLblError(Color.RED, "There was a problem deleting the item");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tableUpdater();
	}

	public void setUserName(String userName, String firstName, String listName, int listID) {
		this.userName = userName;
		this.firstName = firstName;
		this.listName = listName;
		this.listID = listID;
	}

	private void setLblError(Color color, String txt) {
		lblError.setTextFill(color);
		lblError.setText(txt);
	}

}
