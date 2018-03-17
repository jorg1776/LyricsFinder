package src;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.SwingConstants;
import java.awt.Color;

public class SongSearchWindow extends JFrame
{
	private JPanel contentPane;
	private JTextField songTitle_TextField;
	private JTextField artist_TextField;
	private JLabel errorLabel;

	public SongSearchWindow() 
	{
		LyricsFinder lyricsFinder = new LyricsFinder(this);
		
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
					String lyrics = lyricsFinder.getLyrics(song, artist);
					
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
	
	public void throwError(String errorMessage)
	{
		errorLabel.setText(errorMessage);
	}
	
	private void clearErrorLabel()
	{
		errorLabel.setText("");
	}
}
