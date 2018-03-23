import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;

import org.apache.commons.text.WordUtils;

public class LyricsDisplay extends JFrame
{
	private JPanel contentPane;

	public static void displayLyrics(String song, String artist, String lyrics)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					LyricsDisplay frame = new LyricsDisplay(WordUtils.capitalize(song), WordUtils.capitalize(artist), lyrics);
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public LyricsDisplay(String song, String artist, String lyrics)
	{
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(850, 100, 500, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea lyricsTextArea = new JTextArea();
		lyricsTextArea.setEditable(false);
		lyricsTextArea.setBounds(51, 41, 361, 469);
		lyricsTextArea.setText(lyrics);
		lyricsTextArea.setCaretPosition(0);
		contentPane.add(lyricsTextArea);
		
		JScrollPane scrollPane = new JScrollPane(lyricsTextArea);
		scrollPane.setBounds(50, 41, 362, 469);
		contentPane.add(scrollPane);
		
		JLabel songLabel = new JLabel("");
		songLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		songLabel.setBounds(50, 11, 310, 19);
		songLabel.setText(song + " by: " + artist);
		contentPane.add(songLabel);
	}

}
