package com.example.android.shuttershock;

public class Contact {

	// private variables
	int _id;
//	String _name;
	String imagePath;
	String date;
	// Empty constructor
	public Contact() {

	}
	//comment test

	// constructor
	/*public Contact(int keyId, String name, byte[] image) {
		this._id = keyId;
		this._name = name;
		this._image = image;

	}*/

	public Contact(int keyId, String imagePath, String date) {
		this._id = keyId;
		this.imagePath = imagePath;
		this.date = date;

	}
/*
	// constructor
	public Contact(String contactID, String imagePath) {
		this.imagePath = imagePath;

	}

*/	// constructor
	public Contact(String imagePath, String date) {
		this.imagePath = imagePath;
		this.date = date;
	}


	//getting date

	public String getDate(){
		return date;
	}

	public void setDate(String date){
		this.date = date;
	}
	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int keyId) {
		this._id = keyId;
	}
/*
	// getting name
	public String getName() {
		return this._name;
	}

	// setting name
	public void setName(String name) {
		this._name = name;
	}
*/
	// getting phone number
	public String getImage() {
		return this.imagePath;
	}

	// setting phone number
	public void setImage(String imagePath) {
		this.imagePath = imagePath;
	}
}
