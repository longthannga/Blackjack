import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
public class Runner
{
	public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
		Table game = new Table();
		JFrame frame = new JFrame("BlackJack");
		
		frame.add(game);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}