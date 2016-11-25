import java.sql.*;
//import javax.swing.*;		//use if implementing jframe

public class sqliteConnection {

//	Connection conn = null;
	
	public static Connection dbConnector(){
		try{
//			Class.forName("org.sqlite.JDBC");
//			Connection conn = DriverManager.getConnection("jdbc:sqlite:/Users/robert/Documents/workspace/PhoneBookTest/phonebook.sqlite");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:phonebook.sqlite");
			return conn;
		} catch (Exception e){
			return null;
		}
	}
}

