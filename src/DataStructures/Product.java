package DataStructures;

public class Product {

	private String prodName, description, link;
	private int prodID, categoryID;

	public Product(int prodID, String prodName, String description, int categoryID, String link) {
		this.prodID = prodID;
		this.prodName = prodName;
		this.description = description;
		this.categoryID = categoryID;
		this.link = link;
	}

	public int getProdID() {
		return prodID;
	}

	public void setProdID(int prodID) {
		this.prodID = prodID;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

}
