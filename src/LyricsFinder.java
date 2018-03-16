

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;

public class LyricsFinder
{
	private SongSearchWindow searchWindow;
	
	public LyricsFinder(SongSearchWindow searchWindow)
	{
		this.searchWindow = searchWindow;
	}
	
	public String getLyrics(String songName, String artist) 
	{
		songName = formatInput(songName);
		artist = formatInput(artist);
		String url = "https://www.azlyrics.com/lyrics/" + artist + "/" + songName + ".html";
		
		Document songPage = loadWebsite(url);
		
		String lyrics;
		if(songPage != null)
		{
			lyrics = extractLyrics(songPage);
			lyrics = censorLyrics(lyrics);
		}
		else
		{
			lyrics = null;
		}
		
		return lyrics;
	}

	private String formatInput(String input)
	{
		input = input.toLowerCase();
		input = input.replaceAll("\\s+","");
		return input;
	}
	
	private Document loadWebsite(String url)
	{
		try
		{
			Document songPage =Jsoup.connect(url).get();
			return songPage;
		}
		catch(IOException e)
		{
			searchWindow.throwError("Page not found");
			return null;
		}
	}
	
	private String extractLyrics(Document songPage)
	{	
		Element referenceDiv = songPage.getElementById("cf_text_top");
		Element lyricsDiv = selectLyricsDiv(referenceDiv, "div", 1);
		
		String lyrics = extractLyricsFromDiv(lyricsDiv.toString());
		
		return lyrics;
	}

	//it works somehow. got it from StackOverflow
	private static Element selectLyricsDiv(Element origin, String query, int count) {
	    Element currentElement = origin;
	    Evaluator evaluator = QueryParser.parse(query);
	    while ((currentElement = currentElement.nextElementSibling()) != null) 
	    {
	        int val = 0;
	        if (currentElement.is(evaluator)) 
	        {
	            if (--count == 0)
	                return currentElement;
	            val++;
	        }
	        Elements elems = currentElement.select(query);
	        if (elems.size() > val) 
	        {
	            int childCount = elems.size() - val;
	            int diff = count - childCount;

	            if (diff == 0) {
	                return elems.last();
	            }
	            if (diff > 0) {
	                count -= childCount;
	            }
	            if (diff < 0) {
	                return elems.get(childCount + diff);
	            }
	        }
	    }
	    if (origin.parent() != null && currentElement == null) 
	    {
	        return selectLyricsDiv(origin.parent(), query, count);
	    }
	    return currentElement;
	}

	private String extractLyricsFromDiv(String lyricsDiv)
	{
		String lyrics = lyricsDiv.replaceAll("<.*?>", "");
		lyrics.replaceAll("\t", "");
		
		return lyrics;
	}
	
	private String censorLyrics(String lyrics)
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
	
	private String censorWord(String word)
	{
		return word.replaceAll(".", "*");
	}
}
