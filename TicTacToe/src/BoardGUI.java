import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.ImageIcon;

/**
 * Basic Tic-Tac-Toe game
 * @author Cuong Le, Garnet Yeates
 * 
 */
public class BoardGUI extends JPanel implements MouseListener
{	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} 
				catch (Exception e)
				{ 
					e.printStackTrace();
				}
				
				startGame();				
			}
		});
	}
	
	// Game Related Methods
	
	/** A 3x3 (y,x) array of Strings representing the board. "X" means X, "O" means O, null means none */
	private String[][] board;
	
	/** Character to represent the player who draws X's */
	public final Symbol X = new X_Symbol();
	
	/** Character to represent the player who draws O's */
	public final Symbol O = new O_Symbol();
	
	/** Pointer to either {@link #X} or {@link #O} depending on whose turn it is */
	public Symbol currentPlayer = X; 
	
	/**
	 * Constructs a new Board object which starts a new Tic-Tac-Toe game
	 * this constructor initializes the board array into a 3x3 String array.
	 * Any "X" in the array represents X and "O" represents O. Blank spaces are
	 * represented by null in the array
	 * @param container The JFrame that this board exists in
	 */
	public BoardGUI(JFrame container)
	{
		this.container = container;
		board = new String[3][3];
		container.setPreferredSize(new Dimension(PREFERRED_SIZE + 7, PREFERRED_SIZE + 30));
		container.setVisible(true);
		container.getContentPane().add(this);
		container.setBackground(Color.WHITE);
		container.pack();
		container.setResizable(false);
		container.setVisible(true);
		container.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.add(this);	
	}
	
	public static void startGame()
	{
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				JFrame gameFrame = new JFrame();
				BoardGUI board = new BoardGUI(gameFrame);
				gameFrame.addMouseListener(board);
			}
		});
	}
	
	/** If this is set to true, the state of the game will be frozen, stopping painting and click events*/
	boolean frozen = false;
	
	/**
	 * Set to true to freeze the state of the game and stop mouse input from 
	 * being registered. Also, if set to true the {@link #paint(Graphics)} 
	 * method is disabled
	 * @param frozen if true, the game will be frozen
	 */
	public void setFrozen(boolean frozen)
	{
		this.frozen = frozen;
	}
	
	/**
	 * Called whenever the program detects that the player clicked on one of the 9 tic tac
	 * toe spaces. This method determines if the player clicked on an empty space, and if so,
	 * it will fill that space with the player's symbol
	 * @param y the y coordinate clicked in the {@link #board} array
	 * @param x the x coordinate clicked in the {@link #board} array
	 * @param clicker the symbol (x or o) of the player who clicked this spot
	 */
	public void onBoardClick(int y, int x)
	{
		String clicked = board[y][x];
		if (clicked == null)
		{
			board[y][x] = currentPlayer.getChar() + "";
			repaint();
			postTurn();
		}
	}
	
	/**
	 * This method is called whenever a player finishes their current turn. A turn is
	 * considered finished when a player places a symbol on an empty space in the board.
	 * This method will switch the turn over to the next player and will also check if
	 * the game has resulted in a winner or a draw
	 */
	public void postTurn()
	{
		if (!frozen)
		{
			if (currentPlayer instanceof X_Symbol)
			{
				currentPlayer = O;
			}
			else
			{
				currentPlayer = X;
			}
			
			repaint();
			
			String winner = checkThreeInARow();
			if (getNumSpacesFilled() == 9 && winner == null)
			{
				openEndGameWindow("Draw");
			}
			else if (winner != null)
			{
				openEndGameWindow(winner + " Wins");
			}	
		}
	}
	
	/**
	 * This method finds out how many of the 9 spaces on the board are currently filled
	 * @return an integer representation of how many spaces are filled
	 */
	private int getNumSpacesFilled()
	{
		int counter = 0;
		for (int y = 0; y < board.length; y++)
		{
			for (int x = 0; x < board.length; x++)
			{
				counter += board[y][x] == null ? 0 : 1;
			}
		}
		return counter;
	}
	
	/**
	 * This method checks all 8 spots on the board where there can be 3 symbols in a row
	 * If ANY of these spots has 3 of the same symbols in a row, the string representation
	 * of that symbol will be returned.
	 * @return The symbol that has connected 3 in a row, or null if there aren't any
	 */
	private String checkThreeInARow()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(checkThreeInARow(0, 0, 0, 1));
		list.add(checkThreeInARow(1, 0, 0, 1));
		list.add(checkThreeInARow(2, 0, 0, 1));
		list.add(checkThreeInARow(0, 0, 1, 0));
		list.add(checkThreeInARow(0, 1, 1, 0));
		list.add(checkThreeInARow(0, 2, 1, 0));
		list.add(checkThreeInARow(0, 0, 1, 1));
		list.add(checkThreeInARow(2, 0, -1, 1));
		for (String s : list)
		{
			if (s != null)
			{
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Checks any 3 segment line in the array for 3 symbols in a row
	 * @param y the y coordinate for the start point of the line
	 * @param x the x coordinate for the start point of the line
	 * @param yDir the y direction that it searches 3 in a row for
	 * @param xDir the x direction that it searches
	 * @return the string representation of the symbol that was in a
	 * row of 3, or null if there isn't 3 in a row here
	 */
	private String checkThreeInARow(int y, int x, int yDir, int xDir)
	{
		String sign = board[y][x];
		if (sign == null)
		{
			return null;
		}
		else
		{
			y += yDir;
			x += xDir;
			for (int i = 0; i < 2; i++, y += yDir, x += xDir)
			{
				if (board[y][x] == null)
				{
					return null;
				}
				else if (!board[y][x].equals(sign))
				{
					return null;
				}
			}
			return sign;
		}
	}
	
	/**
	 * This method is called once the game ends, regardless of if it is a draw or
	 * a win. The text parameter will change depending on the type of ending it is
	 * @param text this will be "draw" if the game is a draw, "O Wins" if O wins
	 * and "X Wins" if x wins
	 */
	public void openEndGameWindow(String text)
	{
		EventQueue.invokeLater(new Runnable()
		{	
			@Override
			public void run()
			{
				int x = (int) container.getBounds().getX();
				int y = (int) container.getBounds().getY();
				System.out.println(x);

				JFrame frame = new EndScreenGUI(BoardGUI.this, text);
				frame.setBounds(new Rectangle((x + x + PREFERRED_SIZE) / 2, (y + y + PREFERRED_SIZE) / 2, frame.getWidth(), frame.getHeight()));
				frame.setVisible(true);
				frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				setFrozen(true);		
			}
		});
	}

	
	
	// Painting Related Methods

	
	
	/** Represents the side length of the square in the GUI that the user can click inside of */
	public static final int CLICKABLE_LENGTH = 600;

	/** The remaining length on each side aside from the lines and the click-able area. */
	public static final int REM_LENGTH = 30;
	
	/** The thickness of each line drawn on the tic tac toe b */
	public static final int LINE_SIZE = 3;

	/** Constant to represent the number of lines on each side of the tic tac toe board. */
	public static final int NUM_LINES = 2;	

	/** The total length of each side of this board GUI */
	public static final int PREFERRED_SIZE = REM_LENGTH + CLICKABLE_LENGTH + NUM_LINES*LINE_SIZE;

	/** The maximum X value inside of the click-able area */
	public static final int X_MAX = PREFERRED_SIZE - REM_LENGTH/2;

	/** The maximum Y value inside of the click-able area */
	public static final int Y_MAX = X_MAX;

	/** The minimum X value inside of the click-able area */
	public static final int X_MIN = REM_LENGTH / 2;

	/** The minimum Y value inside of the click-able area */
	public static final int Y_MIN = REM_LENGTH / 2;
	
	/** The ImageIcon to paint an X on the board */
	ImageIcon xIcon = new ImageIcon("assets/TicTacToeX.png");
	
	/** The ImageIcon to paint an o on the board */
	ImageIcon oIcon = new ImageIcon("assets/TicTacToeO.png");
	
	/**
	 * This method is called every time the board is updated (i.e whenever
	 * someone clicks on one of the spaces). It is used to pass a graphics
	 * instance to paint everything on the board, such as the lines and the
	 * images. see {@link #paintLines(Graphics)} and {@link #paintImages(Graphics)}
	 */
	@Override
	public void paint(Graphics g)
	{
		if (!frozen)
		{
			paintLines(g);
			paintImages(g);
		}
//		paintBoundaryLines(g);
	}
	
	/**
	 * This method is used to paint the four lines on the tic-tac-toe board
	 * @param g the graphics instance that will be used for drawing
	 */
	private void paintLines(Graphics g)
	{
		int xPos = X_MIN;
		int yPos = Y_MIN;
		g.setColor(Color.BLACK);
		int increment = CLICKABLE_LENGTH / 3;
		xPos += increment;
		paintVerticalLine(xPos, yPos, Y_MAX, g);
		xPos += increment + LINE_SIZE;
		paintVerticalLine(xPos, yPos, Y_MAX, g);
		xPos = X_MIN;
		yPos += increment;
		paintHorizontalLine(yPos, xPos, X_MAX, g);
		yPos += increment + LINE_SIZE;
		paintHorizontalLine(yPos, xPos, X_MAX, g);
	}
	
	/**
	 * This method paints the X and O images on the board. They are painted
	 * using the two ImageIcons {@link #xIcon} and {@link #oIcon}
	 * @param g the graphics instance that will be used for drawing
	 */
	private void paintImages(Graphics g)
	{
		int xPos = X_MIN;
		int yPos = Y_MIN;
		g.setColor(Color.ORANGE);
		int increment = CLICKABLE_LENGTH / 3 + LINE_SIZE;
		for (int y = 0; y < board.length; y++)
		{
			xPos = X_MIN;
			for (int x = 0; x < board.length; x++)
			{
				String s = board[y][x];
				if (s != null)
				{
					if (s.equals("X"))
					{
						X.getImageIcon().paintIcon(this, g, xPos, yPos);
					}
					else
					{
						O.getImageIcon().paintIcon(this, g, xPos, yPos);
					}
				}
				xPos += increment;
			}
			yPos += increment;
		}
	}

	/**
	 * This method is only used for debugging. It paints the boundary lines
	 * of the tic tac toe board and paints a diagonal line to represent
	 * the click-able area
	 * @param g the graphics instance that will be used for drawing
	 */
	public void paintBoundaryLines(Graphics g)
	{
		g.setColor(Color.GREEN);
		g.drawRect(0, 0, PREFERRED_SIZE, PREFERRED_SIZE);
		paintVerticalLine(X_MIN, Y_MIN, Y_MAX, g);
	}

	/**
	 * This method is used to paint a vertical line. This method will automatically
	 * make the thickness of the line {@link #LINE_SIZE} pixels thick.
	 * @param x the x coordinate of this line
	 * @param y1 the y coordinate for one end point of the line
	 * @param y2 the y coordinate for the other end point of the line
	 * @param g the graphics instance that will be used for drawing
	 */
	private void paintVerticalLine(int x, int y1, int y2, Graphics g)
	{
		for (int i = 0; i < LINE_SIZE; i++)
		{
			g.drawLine(x, y1, x, y2);
			x++;
		}
	}
	
	/**
	 * This method is used to paint a horizontal line. This method will automatically
	 * make the thickness of the line {@link #LINE_SIZE} pixels thick.
	 * @param y the y coordinate of this line
	 * @param x2 the x coordinate for one end point of the line
	 * @param x2 the x coordinate for the other end point of the line
	 * @param g the graphics instance that will be used for drawing
	 */
	private void paintHorizontalLine(int y, int x1, int x2, Graphics g)
	{
		for (int i = 0; i < LINE_SIZE; i++)
		{
			g.drawLine(x1, y, x2, y);
			y++;
		}
	}
	
	private static final long serialVersionUID = -6832568551475015791L;
	
	/** The JFrame that this JPanel is contained in */
	private JFrame container;
	
	/**
	 * Obtains the JFrame that this Board is contained in
	 * @return a reference to the JFrame that owns this panel
	 */
	public JFrame getFrame()
	{
		return container;
	}
	

	
	// Listener Methods
	
	
	
	/**
	 * This listener method detects when someone has clicked the mouse and checks to see
	 * if it is somewhere on the tic tac toe grid. If it is somewhere on the tic tac toe
	 * grid, {@link #onBoardClick(int, int, char)} will be called and the paramters
	 * for it will be the array y coordinate that was clicked, the array x coordinate that 
	 * was clicked and the player who clicked it (either 'X' or 'O')
	 */
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (!frozen)
		{
			int x = e.getX();
			int y = e.getY();
			if (x > X_MIN && x < X_MAX && y > Y_MIN && y < Y_MAX)
			{
				int squareLength = CLICKABLE_LENGTH / 3;
				int arrX;
				if (x < X_MIN + squareLength + LINE_SIZE*NUM_LINES)
				{
					arrX = 0;
				}
				else if (x < X_MIN + 2*squareLength + 2*LINE_SIZE*NUM_LINES - 4)
				{
					arrX = 1;
				}
				else
				{
					arrX = 2;
				}
				
				int arrY;
				if (y < Y_MIN + squareLength + LINE_SIZE*NUM_LINES)
				{
					arrY = 0;
				}
				else if (y < Y_MIN + 2*squareLength + 2*LINE_SIZE*NUM_LINES - 4)
				{
					arrY = 1;
				}
				else
				{
					arrY = 2;
				}
				
				onBoardClick(arrY, arrX);
			}			
		}
	}
	
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
}