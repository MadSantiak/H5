package dk.tec.maso41.servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dk.tec.maso41.Toilet;

public class DBTools 
{
    private String conStr = "jdbc:sqlserver://localhost:1433;databaseName=Spoilet;encrypt=true;trustServerCertificate=true";
	Connection con;
	Statement stmt;
	
	public DBTools()
	{
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void connect() 
	{
		try {
			con = DriverManager.getConnection(conStr, "sa", "test");
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Gets a specific recordset from the DB, based on the ID 
	 * sent along in the request (path)
	 * @param id
	 * @return
	 */
	public Toilet getToiletById(int id) {
		connect();
		String selectStr = "SELECT p.* " +
				"FROM Toilet p " +
			
				"WHERE p.id = " + id;
		Toilet toilet = new Toilet();
		
		try {
			ResultSet result = stmt.executeQuery(selectStr);
			if(result.next())
			{
				toilet.setId(result.getInt("Id"));
				toilet.setLongitude(result.getDouble("Longitude"));
				toilet.setLatitude(result.getDouble("Latitude"));
				toilet.setAltitude(result.getDouble("Altitude"));
				toilet.setBearing(result.getFloat("Bearing"));
				toilet.setFavorite(result.getBoolean("Favorite"));
				
				con.close();
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		System.out.print(toilet.toString());
		return toilet;
	}	
	
	/**
	 * Gets all Toilet objects, joined with Haircolor and ProgrammingLanguage tables,
	 * in order to avoid unnecessary performance hits when generating a list view of Toilets, including their haircolor and programming language,
	 * which would otherwise necessitate a corresponding call to the database to fetch each individual color/language, per toilet in the list,
	 * increasing the load linearly (noticeably so).
	 * @return
	 */
	public List<Toilet> getAllToilet() {
		connect();
		String selectStr = 
				"SELECT p.*, " +
				"FROM Toilet p ";
		List<Toilet> toilets = new ArrayList<>();
		try {
	        ResultSet result = stmt.executeQuery(selectStr);
	        while (result.next()) {
	            Toilet toilet = new Toilet();
	            toilet.setId(result.getInt("Id"));
	            toilet.setLongitude(result.getDouble("Longitude"));
	            toilet.setLatitude(result.getDouble("Latitude"));
	            toilet.setAltitude(result.getDouble("Altitude"));
				toilet.setBearing(result.getFloat("Bearing"));
				toilet.setFavorite(result.getBoolean("Favorite"));
				
				
				toilets.add(toilet);
	        	}
	
	        con.close();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	
		    return toilets;
		
	}
	
	/**
	 * Adds a toilet object to the database, getting the toilets fields to populate the query creating it in the database.
	 * Note the check on the toilets Haircolor and Prg. Language, as these are nullable, and thus might not except setInt(), as there is no record to getId() from.
	 * Hence the addition of "setNull", using Types.NULL (0) for clarity.
	 * Returns the ID of the created record for display in the App.
	 * @param toilet
	 * @return
	 */
	public Integer addToilet(Toilet toilet) {
		Integer pId = null;
		
		connect();
		String insertStr = "INSERT INTO Toilet (longitude, latitude, altitude, bearing, favorite) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement preparedStatement = con.prepareStatement(insertStr, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setDouble(1, toilet.getLongitude());
            preparedStatement.setDouble(2, toilet.getLatitude());
            preparedStatement.setDouble(3, toilet.getAltitude());
            preparedStatement.setFloat(4, toilet.getBearing());
            preparedStatement.setBoolean(5, toilet.getFavorite());
            
            
            
            preparedStatement.executeUpdate();
            ResultSet genKey = preparedStatement.getGeneratedKeys();
            if (genKey.next()) {
            	pId = genKey.getInt(1);
            }
            con.commit();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return pId;
	}

	/**
	 * Deletes a toilet from the database. Not much else to say.
	 * @param id
	 */
	public void delToilet(int id) {
		connect();
		String delStr = "DELETE FROM Toilet WHERE id = " + id;

		try {
			stmt.executeQuery(delStr);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates pereson record based on the changed object contained in the request.
	 * @param toilet
	 */
	public void updateToilet(Toilet toilet) {
		connect();
		int id = toilet.getId();
		String updateStr = "UPDATE Toilet SET longitude = ?, latitude = ?, altitude = ?, bearing = ?, favorite = ? where ID = " + id;
		try (PreparedStatement statement = con.prepareStatement(updateStr)) {
			statement.setDouble(1, toilet.getLongitude());
			statement.setDouble(2, toilet.getLatitude());
			statement.setDouble(3, toilet.getAltitude());
			statement.setFloat(4, toilet.getBearing());
			statement.setBoolean(5, toilet.getFavorite());
			
			
			
			statement.executeUpdate();
			con.commit();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
