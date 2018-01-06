import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.select.QueryParser;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;

public class SongSearchWindow extends JFrame {

	private JPanel contentPane;
	private JTextField songTitle_TextField;
	private JTextField artist_TextField;
	private JLabel errorLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SongSearchWindow frame = new SongSearchWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SongSearchWindow() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 200, 329, 206);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		songTitle_TextField = new JTextField();
		songTitle_TextField.setBounds(117, 35, 112, 20);
		contentPane.add(songTitle_TextField);
		songTitle_TextField.setColumns(10);
		
		JLabel songTitle_Label = new JLabel("Song Title:");
		songTitle_Label.setBounds(53, 38, 64, 14);
		contentPane.add(songTitle_Label);
		
		artist_TextField = new JTextField();
		artist_TextField.setColumns(10);
		artist_TextField.setBounds(117, 66, 112, 20);
		contentPane.add(artist_TextField);
		
		JLabel artist_Label = new JLabel("Artist:");
		artist_Label.setBounds(78, 69, 39, 14);
		contentPane.add(artist_Label);
		
		JButton findLyrics_Button = new JButton("Find Lyrics");
		findLyrics_Button.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				String song = songTitle_TextField.getText();
				String artist = artist_TextField.getText();
				
				if(isInputValid(song, artist))
				{
					String lyrics = getLyrics(song, artist);
					
					if(lyrics != null)
					{
						LyricsDisplay.displayLyrics(song, artist, lyrics);
						clearErrorLabel();
					} 
				}
				else
				{
					throwError("Please enter song/artist");
				}
			}
		});
		
		findLyrics_Button.setBounds(112, 97, 97, 23);
		contentPane.add(findLyrics_Button);
		
		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.RED);
		errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
		errorLabel.setBounds(89, 152, 151, 14);
		contentPane.add(errorLabel);
	}

	private boolean isInputValid(String song, String artist)
	{
		if(song.equals("") || artist_TextField.getText().equals(""))
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
	
	private String getLyrics(String songName, String artist) 
	{
		songName = formatInput(songName);
		artist = formatInput(artist);
		String url = "https://www.azlyrics.com/lyrics/" + artist + "/" + songName + ".html";
		
		Document songPage = loadWebsite(url);
		
		String lyrics;
		if(songPage != null)
		{
			lyrics = extractLyrics(songPage);
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
			throwError("Page not found");
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
	
	private void throwError(String errorMessage)
	{
		errorLabel.setText(errorMessage);
	}
	
	private void clearErrorLabel()
	{
		errorLabel.setText("");
	}
}
