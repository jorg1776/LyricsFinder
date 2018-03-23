import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.text.WordUtils;

import com.sun.glass.ui.TouchInputSupport;

public class LyricsDatabaseManager
{
    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    private static String connectionUrl = "jdbc:mysql://localhost:3306/lyricsdb?autoReconnect=true&useSSL=false";
    private static String userName;
    private static String password;
    
    public static String getLyricsFromDB(String song, String artist) throws Exception
    {		
    	try
    	{
	    	connectToDB();
	    	
	    	String lyrics = "";
	    	song = WordUtils.capitalize(formatApostrophes(song));
	    	artist = WordUtils.capitalize(formatApostrophes(artist));
	    	
	    	String getLyricsQuery = String.format("SELECT lyrics FROM songs WHERE songName = '%s'" 
        			+ " AND artistID = (SELECT artistID FROM artists WHERE artistName = '%s')", song, artist);
	        resultSet = statement.executeQuery(getLyricsQuery);
	        
	        if(resultSet.next())
	    	{
	        	lyrics = resultSet.getString(1);
	    	}
	        else
	        {
	        	throw new Exception("Lyrics not in database");
	        }
	        return lyrics;
    	}
        finally 
        {
			closeConnection();
		}
    }
    
    public static void addSongToDB(String song, String artist, String lyrics) throws SQLException, ClassNotFoundException
    {
    	connectToDB();
    	
    	song = WordUtils.capitalize(formatApostrophes(song));
    	artist = WordUtils.capitalize(formatApostrophes(artist));
    	lyrics = formatApostrophes(lyrics);
    	
    	int artistID = getArtistId(artist);    		
    	
    	String songUpdateQuery = String.format("INSERT INTO songs VALUES (DEFAULT, '%s', '%d', '%s')", song, artistID, lyrics);
    	statement.executeUpdate(songUpdateQuery);
    	
    	closeConnection();
    }
    
    //formats apostrophes to avoid SQL query problems
    private static String formatApostrophes(String s)
    {
    	return s.replaceAll("'", "''");
    }
    
    private static int getArtistId(String artist) throws SQLException
	{
    	String artistIDQuery = String.format("SELECT artistID FROM artists WHERE artistName = '%s'", artist);
    	resultSet = statement.executeQuery(artistIDQuery);
    	
    	int artistID;
    	if(resultSet.next())
    	{
    		artistID = resultSet.getInt(1);
    	}
    	else
    	{
    		addArtistToDB(artist);
    		artistID = getArtistId(artist);
    	}
		return artistID;
	}

	private static void addArtistToDB(String artist) throws SQLException
	{
		String artistUpdateQuery = String.format("INSERT INTO artists VALUES (DEFAULT, '%s')", artist);
		statement.executeUpdate(artistUpdateQuery);
	}

	private static void connectToDB() throws SQLException, ClassNotFoundException
    {
	    Class.forName("com.mysql.jdbc.Driver");
	    userName = getDatabaseProperty("dbusername");
	    password = getDatabaseProperty("dbpassword");
	    connection = DriverManager.getConnection(connectionUrl, userName, password);
	
	    statement = connection.createStatement();
    }
	
	private static String getDatabaseProperty(String property)
	{
		Properties configFile = new Properties();
		InputStream inputStream;
		String fileName = "config/config.properties";
		String value = "";
		
		try
		{
			inputStream = LyricsDatabaseManager.class.getClassLoader().getSystemResourceAsStream(fileName);
			
			configFile.load(inputStream);
			value = configFile.getProperty(property);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return value;
	}
    
    private static void closeConnection() 
    {
        try
        {
            if (resultSet != null) { resultSet.close(); }

            if (statement != null) { statement.close(); }

            if (connection != null) { connection.close(); }
        } 
        catch (SQLException e) { }
    }
}