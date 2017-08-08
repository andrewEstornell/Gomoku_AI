package gui;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame
{
	private JPanel panel = new JPanel();
	private boolean wasClicked;
	private Button[][] buttons;
	private int boardSize1;
	private int boardSize2;
	
	
	public GUI(int boardSize1, int boardSize2)
	{
		this.boardSize1 = boardSize1;
		this.boardSize2 = boardSize2;
		this.buttons = new Button[this.boardSize1][this.boardSize2];
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//dimensions of the board
		setSize(700,700);
		//does not allow the user to resize the board, may change later if we get better with scaling
		setResizable(true);
		this.setWasClicked(false);
		//sets up an array that we will add buttons to\
		this.panel.setLayout(new GridLayout(boardSize1, boardSize2));
			
		//creates the actual buttons
		for(int i = 0; i < boardSize1; i++)
		{
			for(int j = 0; j < boardSize2; j++)
			{
				//Initializes the button
				this.buttons[i][j] = new Button();
				//colors every offset buttons black creating the checker board effect
				this.panel.add(buttons[i][j]);
				this.buttons[i][j].setActionCommand(i + " " + j);
			}
		}
		//adds the grid of buttons to the window
		add(this.panel);
		//displays the window to the user
		setVisible(true);
		setFocusable(true);
	}
	
	/**
	 * Waits for the action listener of a click to store the coordinates of a potential GUI move
	 * @return thee x, y coordinate of the GUI move
	 */
	public int[] getMoveGUI()
	{
		int[] guiMove = new int[2];
		while(!this.wasClicked())
		{
			boolean breakOut = false;
			int i = 0;
			while(i < this.boardSize1)
			{
				int j = 0;
				while(j < this.boardSize2)
				{
					if(this.buttons[i][j].getGUIClick()[0] >= 0 && this.buttons[i][j].getGUIClick()[1] >= 0)
					{
						guiMove[0] = (int) this.buttons[i][j].getGUIClick()[0];
						guiMove[1] = (int) this.buttons[i][j].getGUIClick()[1];
						
						buttons[i][j].resetUserClick();
						breakOut = true;
						break;
					}
					j++;
				}
				if(breakOut)
				{
					break;
				}
				i++;
			}
		}
		this.resetClick();
		return guiMove;
	}
	
	public void resetClick()
	{
		this.wasClicked = false;
	}
	
	public void click()
	{
		this.wasClicked = true;
	}


	public boolean wasClicked(){return this.wasClicked;}
	public void setWasClicked(boolean wasClicked){this.wasClicked = wasClicked;}
	
	public Button[][] getButtons(){return this.buttons;}
	
}
