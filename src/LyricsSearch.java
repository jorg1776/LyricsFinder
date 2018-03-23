import java.io.IOException;
import java.sql.SQLException;

public class LyricsSearch
{	
	public static String getLyrics(String song, String artist) throws LyricsNotFoundException
	{
		String lyrics = null;
		
		try
		{
			lyrics = LyricsDatabaseManager.getLyricsFromDB(song, artist);
			return censorLyrics(lyrics);
		}
		catch (Exception e) 
		{
			try
			{
				lyrics = WebLyricsScraper.getLyricsFromWeb(song, artist);
				LyricsDatabaseManager.addSongToDB(song, artist, lyrics);
				return censorLyrics(lyrics);
			} 
			catch (IOException ioe)
			{
				throw new LyricsNotFoundException("Lyrics not found");
			}
			catch (SQLException sqle) 
			{
				System.err.println("Lyrics not updated into database");
				sqle.printStackTrace();
				return lyrics;
			}
			catch(ClassNotFoundException cnfe)
			{
				System.err.println("Lyrics not updated into database");
				cnfe.printStackTrace();
				return lyrics;
			}
		}
	}
	
	private static String censorLyrics(String lyrics)
	{
		String[] lyricsArray = lyrics.split(" ");
		for(int i = 0; i < lyricsArray.length; i++)
		{
			if(lyricsArray[i].length() > 2) //only compares longer words
			{
				switch(lyricsArray[i].toLowerCase())
				{
					case "fuck":
					case "fucking":
					case "fuckin'":
					case "damn":
					case "ass":
					case "shit":
					case "shitting":
					case "sex":
					case "bitch":
					case "bitching":
					case "cock":
					case "nigger":
					case "pussy":
						lyricsArray[i] = censorWord(lyricsArray[i]);
						break;
				}
			}
		}
		return String.join(" ", lyricsArray);
	}
	
	private static String censorWord(String word)
	{
		return word.replaceAll(".", "*");
	}

	public static class LyricsNotFoundException extends Exception
	{
		private String _errorMessage;
		public String getErrorMessage() { return _errorMessage; }
		public LyricsNotFoundException(String message)
		{
			super(message);
			_errorMessage = message;
		}
	}
}
