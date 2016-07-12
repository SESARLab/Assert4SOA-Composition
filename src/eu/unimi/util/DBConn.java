package eu.unimi.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;

import java.sql.PreparedStatement;

public class DBConn {
	private String url;
	private String dbName;
	private String username;
	private String driver;
	private String password;
	private Connection conn;
	private PreparedStatement queryCheck = null;
	private String query = "SELECT Result from  compositionDB.Rules WHERE " +
			"(Operator1 = ? AND Operation = ? AND Operator2 = ?) OR " +
			"(Operator1 = ? AND Operation = ? AND Operator2 = ?)" ;
	
	public Connection getConnection(){
		return this.conn;
	}
	
	
	public DBConn(String url, String dbName, String username, String password ){
		if(url == null || url.equals("")){
			this.url = "jdbc:mysql://localhost:3306/";
		} else {
			this.url = url;
		}
		
		this.dbName = dbName;
		this.username = username;
		this.password = password;
		this.driver = "com.mysql.jdbc.Driver";
		
		try{
			Class.forName(driver);
			this.conn = DriverManager.getConnection(this.url+this.dbName, this.username, this.password);
			this.queryCheck = conn.prepareStatement(this.query); 
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Check for the rule of the property
	 * @param operator1
	 * @param operation
	 * @param operator2
	 * @return
	 */
	public String checkRule(String operator1, RuleOperation operation, String operator2){
		String result = null;
		try {
			//TODO: Ricordarsi di cambiare il modo in cui si fa il check delle rules, altrimenti è troppo limitato
			queryCheck.setString(1, operator1);

			queryCheck.setString(2, operation.toString());
			queryCheck.setString(3, operator2);
			queryCheck.setString(4, operator2);
			queryCheck.setString(5, operation.toString());
			queryCheck.setString(6, operator1);
			
			ResultSet rs = this.queryCheck.executeQuery();
			while(rs.next()){
				result = rs.getString(1);
				
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Close the connection
	 */
	public void closeConn(){
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
