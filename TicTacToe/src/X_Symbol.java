import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class X_Symbol extends Symbol
{
	@Override
	public ImageIcon getImageIcon()
	{
		BufferedImage buff = null;
		try
		{
			buff = ImageIO.read(getClass().getResourceAsStream("TicTacToeX.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new ImageIcon(buff);
	}

	@Override
	public char getChar()
	{
		return 'X';
	}

}
