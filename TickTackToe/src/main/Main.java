package main;

import java.util.Scanner;

import board.BoardInterface;
import gui.Button;
import gui.GUI;
import tree.Tree;

public class Main 
{
	
	public static GUI gui;
	public static Button[][] buttons;
	public static BoardInterface boardInterface;
	
	public static void main(String[] args)
	{
		// Set up and user input
		Scanner scan = new Scanner(System.in);
		System.out.print("Size of board1: ");
		int boardSize1 = scan.nextInt();
		System.out.print("Size of board2: ");
		int boardSize2 = scan.nextInt();
		System.out.println("Number in a row to win: ");
		int inARowToWin = scan.nextInt();
		System.out.println("1 to go first, 2 to go second: ");
		int whoGoesFirst = scan.nextInt();
		boardInterface = new BoardInterface(boardSize1, boardSize2, inARowToWin, whoGoesFirst);
		
		gui = new GUI(boardSize1, boardSize2);
		buttons = gui.getButtons();
		
		
		
		// Plays the actual game
		while(!boardInterface.hasWon())
		{
			boardInterface.displayBoard();
			
			if((boardInterface.getTurn() + whoGoesFirst - 1)  % 2 == 0)
			{
				// Get user move via console and make move
				//consoleMove(boardInterface);
				int [] move = gui.getMoveGUI();
				int x = move[0];
				int y = move[1];
				while(!boardInterface.makeMove(x, y))
				{
					System.out.print("Invalid move, retry\nMove x: ");
					
					move = gui.getMoveGUI();
					x = move[0];
					y = move[1];
					/*x = scan.nextInt();
					System.out.print("Move y: ");
					y = scan.nextInt();*/
				}
				buttons[x][y].iconSetting(x, y);
				System.out.println("userMove made"); // Debug
			}
			else
			{
				Tree tree = new Tree(boardInterface);
				System.out.println("Tree made"); // Debug
				int[] AImove = tree.getBestMove2();
				System.out.println("AI move generated"); // Debug
				boardInterface.makeMove(AImove[0], AImove[1]);
				System.out.println("AI move made"); // Debug
				Main.buttons[AImove[0]][AImove[1]].iconSetting(AImove[0], AImove[1]);
			}
			
			
		}
		boardInterface.displayBoard();
		System.out.println(boardInterface.getWinner() + " has won!");

	}
	
	
	/**
	 * 	gets a move from the console, tests that move, if it is valid, makes the move. If not, reprompts the user for another move
	 */
	private static void consoleMove(BoardInterface boardInterface)
	{
		Scanner scan = new Scanner(System.in);
		System.out.print("x: ");
		int x = scan.nextInt();
		System.out.print("y: ");
		int y = scan.nextInt();
		
		while(!boardInterface.makeMove(x, y))
		{
			System.out.println("retry");
			System.out.print("x: ");
			x = scan.nextInt();
			System.out.print("y: ");
			y = scan.nextInt();
		}
	}
	
}
