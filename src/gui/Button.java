package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import main.Main;



public class Button extends JButton implements ActionListener
{
	
	private ImageIcon blackKingIcon = new ImageIcon(this.getClass().getResource("Chess_kdt60.png"));
	private ImageIcon whiteKingIcon = new ImageIcon(this.getClass().getResource("Chess_klt60.png"));

	
	
	private int[] userClick;
	private Button[][] currentBoard;
	
	
	public Button()
	{
		addActionListener(this);//gives each ChessButton the ability to read clicks are user input
		this.userClick = new int[2];
		this.userClick[0] = -1;
		this.userClick[1] = -1;
	}

	
	public void actionPerformed(ActionEvent e)
	{
		this.userClick[0] = Integer.parseInt(e.getActionCommand().split(" ")[0]);
		this.userClick[1] = Integer.parseInt(e.getActionCommand().split(" ")[1]);
		
		
		//this.userClick[0] = (int)e.getActionCommand().charAt(0) - 48;//stores x coordinate of user click
		//this.userClick[1] = (int)e.getActionCommand().charAt(1) - 48;//stores y coordinate of user click
		Main.gui.click();
		System.out.println(this.userClick[0] + " " + userClick[1]);
	}
	
	public void iconSetting(int x, int y)
	{
		this.currentBoard = Main.buttons;
		if(Main.boardInterface.getTurn() % 2 == 0)
		{
			setIcon(this.whiteKingIcon);
		}
		else if(Main.boardInterface.getTurn() % 2 == 1)
		{
			setIcon(this.blackKingIcon);
		}
	}
	
	
	public void resetUserClick()
	{
		this.userClick[0] = -1;
		this.userClick[1] = -1;
	}
	
	public int[] getGUIClick()
	{
		return this.userClick;
	}
}

