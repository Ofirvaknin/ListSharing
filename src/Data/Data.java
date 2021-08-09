package Data;

import java.sql.*;

public class Data {

	/*
	 * try {
	 * 
	 * Class.forName("com.mysql.cj.jdbc.Driver");
	 * 
	 * 
	 * // 1. Get a connection to database Connection myConn =
	 * DriverManager.getConnection(
	 * "jdbc:mysql://localhost:3306/shoppinglistdb?serverTimezone=UTC", "root",
	 * "root");
	 * 
	 * 
	 * // 2.Create a statement Statement myStmt = myConn.createStatement();
	 * 
	 * // my own test addCategory(myStmt);
	 * 
	 * // 3. Execute SQL query ResultSet myRs =
	 * myStmt.executeQuery("select * from tblcategory");
	 * 
	 * // 4. Process the result set while (myRs.next())
	 * System.out.println(myRs.getString("categoryID") + " " +
	 * myRs.getString("catName"));
	 * 
	 * } catch (Exception e) { e.printStackTrace();
	 * //System.out.println("Connection Error"); }
	 * 
	 */

	public static void main(String[] args) {
		try {

			Class.forName("com.mysql.cj.jdbc.Driver");

			// 1. Get a connection to database
			Connection myConn = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/shoppinglistdb?serverTimezone=UTC", "root", "root");

			// 2.Create a statement
			Statement myStmt = myConn.createStatement();

			// 2.5 Add user
			addUser(myStmt);

			// 3. Execute SQL query
			ResultSet myRs = myStmt.executeQuery("SELECT catName FROM shoppinglistdb.tblcategory");

			// 4. Process the result set
			while (myRs.next())
				System.out.println(
						myRs.getString("catName"));// + " " + myRs.getString("password") +" "+ myRs.getString("firstName"));

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Connection Error");
		}

	}

	public static void addCategory(Statement myStmt) {

		// Instead of test2, need to add category name
		try {
			String sql = "insert into tblcategory " + " (catName)" + " values ('test2')";
			myStmt.executeUpdate(sql);
			System.out.println("Inserted, check DB");
		} catch (Exception e) {
			System.out.println("Somthing went wrong");
		}
	}

	public static void addUser(Statement myStmt) {

		// Instead of my own details, need to add user details.
		try {
			String sql = "insert into tbluser " + " (userName, password, firstName, lastName, phoneNumber, emailAdd)"
					+ " values ('admin', 'admin', 'Ofir Avraham', 'Vaknin', '0546590043', 'ofirvaknin55@gmail.com')";
			myStmt.executeUpdate(sql);
			System.out.println("Inserted, check DB");
		} catch (Exception e) {
			//	e.printStackTrace();
			if(e instanceof java.sql.SQLIntegrityConstraintViolationException)
				System.out.println("User name allready exist !");
			else
				System.out.println("Somthing went wrong");
		}
	}

	public static void addProduct(Statement myStmt) {

		// Instead of my own details, need to add product details.
		try {
			String sql = "insert into tblproduct " + " (prodName, description, categoryID, link)"
					+ " values ('test1', 'test2', '4', 'Not available')";
			myStmt.executeUpdate(sql);
			System.out.println("Inserted, check DB");
		} catch (Exception e) {
			System.out.println("Somthing went wrong");
		}
	}

	public static void addlist(Statement myStmt) {

		// Instead of my own details, need to add list details.
		try {
			String sql = "insert into tblproduct " + " (userName, prodID, reqQuantity, missingQuantity)"
					+ " values ('test1', 'test2', '4', 'Not available')";
			myStmt.executeUpdate(sql);
			System.out.println("Inserted, check DB");
		} catch (Exception e) {
			System.out.println("Somthing went wrong");
		}
	}

	
	
}
