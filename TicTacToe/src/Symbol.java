import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public abstract class Symbol
{
	public ImageIcon getImageIcon()
	{
		BufferedImage buff = null;
		try
		{
			buff = ImageIO.read(getClass().getResourceAsStream(getClass().getSimpleName() + ".png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new ImageIcon(buff);
	}
	
	public abstract char getChar();
}
