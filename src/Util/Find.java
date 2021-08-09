package Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DataStructures.Product;

public class Find {

	private static Connection con = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;

	public static int catID(String str) {
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT categoryID FROM tblcategory WHERE catName = ?");
			preparedStatement.setString(1, str);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // in case there are no results
	}

	public static int productID(String str) {
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT prodID FROM tblproduct WHERE prodName = ?");
			preparedStatement.setString(1, str);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // in case there are no results
	}

	public static int listID(String str, String userName) {
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT listID FROM tbllistidtoname WHERE listName = ?");
			preparedStatement.setString(1, str);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				return resultSet.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1; // in case there are no results
	}

	public static String userNameToFullName(String str) {
		String res = "";
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT * FROM tbluser WHERE userName = ?");
			preparedStatement.setString(1, str);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = resultSet.getString("firstName") + " " + resultSet.getString("lastName");
				return res;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res; // in case there are no results
	}

	// Product Id To Product
	public static Product prodIDtoName(int id) {
		Product prod;
		String res = "";
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT * FROM tblproduct WHERE prodID = ?");
			preparedStatement.setInt(1, id);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = resultSet.getString("prodName");
				prod = new Product(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3),
						resultSet.getInt(4), resultSet.getString(5));
				return prod;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // in case there are no results
	}

	public static String findCategoryForProd(String prodName) {
		String res = "";
		int catID = 0;
		try {
			con = ConnectionUtil.conDB();
			preparedStatement = con.prepareStatement("SELECT categoryID FROM tblproduct WHERE prodName = ?");
			preparedStatement.setString(1, prodName);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				catID = resultSet.getInt("categoryID");
			preparedStatement = con.prepareStatement("SELECT catName FROM tblcategory WHERE categoryID = ?");
			preparedStatement.setInt(1, catID);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				res = resultSet.getString("catName");
				return res;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res; // in case there are no results
	}

	// Return the user name of the list owner
	public static String listOwner(int listID) {
		try {
			con = ConnectionUtil.conDB();
			String sql = "SELECT userName FROM tbllistidtoname WHERE listID = ?";
			preparedStatement = con.prepareStatement(sql);
			preparedStatement.setInt(1, listID);
			ResultSet rs = preparedStatement.executeQuery();
			rs.next();
			return rs.getString(1).toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}
}
