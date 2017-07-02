package main;

import java.util.Scanner;

import board.BoardInterface;
import tree.Tree;

public class Main 
{
	
	
	
	
	public static void main(String[] args)
	{
		// Set up and user input
		Scanner scan = new Scanner(System.in);
		System.out.print("Size of board1: ");
		byte boardSize1 = scan.nextByte();
		System.out.print("Size of board2: ");
		byte boardSize2 = scan.nextByte();
		System.out.println("Number in a row to win: ");
		byte inARowToWin = scan.nextByte();
		BoardInterface boardInterface = new BoardInterface(boardSize1, boardSize2, inARowToWin);
		
		
		
		
		// Plays the actual game
		while(!boardInterface.hasWon())
		{
			boardInterface.displayBoard();
			
			if(boardInterface.getTurn() % 2 == 0)
			{
				// Get user move via console and make move
				consoleMove(boardInterface);
				System.out.println("userMove made"); // Debug
			}
			else
			{
				Tree tree = new Tree(boardInterface);
				System.out.println("Tree made"); // Debug
				byte[] AImove = tree.getBestMove();
				System.out.println("AI move generated"); // Debug
				boardInterface.makeMove(AImove[0], AImove[1]);
				System.out.println("AI move made"); // Debug
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
		byte x = scan.nextByte();
		System.out.print("y: ");
		byte y = scan.nextByte();
		
		while(!boardInterface.makeMove(x, y))
		{
			System.out.println("retry");
			System.out.print("x: ");
			x = scan.nextByte();
			System.out.print("y: ");
			y = scan.nextByte();
		}
	}
	
}
