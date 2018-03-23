import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;

public class WebLyricsScraper
{
	public static String getLyricsFromWeb(String song, String artist) throws IOException
	{
		song= formatInput(song);
		artist = formatInput(artist);
		
		String url = "https://www.azlyrics.com/lyrics/" + artist + "/" + song + ".html";
		Document songPage = loadWebsite(url);
		
		return extractLyrics(songPage);
	}
	
	private static String formatInput(String input)
	{
		input = input.toLowerCase();
		input = input.replaceAll("\\s+","").replaceAll("'", ""); //removes spaces and apostrophes from string
		return input;
	}
	
	private static Document loadWebsite(String url) throws IOException
	{
		return Jsoup.connect(url).get();
	}
	
	private static String extractLyrics(Document songPage)
	{	
		Element referenceDiv = songPage.getElementById("cf_text_top");
		Element lyricsDiv = selectLyricsDiv(referenceDiv, "div", 1);
				
		return extractLyricsFromDiv(lyricsDiv.toString());
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

	private static String extractLyricsFromDiv(String lyricsDiv)
	{
		String lyrics = lyricsDiv.replaceAll("<.*?>", "");
		lyrics.replaceAll("\t", "");
		
		return lyrics;
	}
}
