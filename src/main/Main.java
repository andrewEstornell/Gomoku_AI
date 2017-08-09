package main;

import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import board.BoardInterface;
import tree.MoveWrapper;
import gui.Button;
import gui.GUI;
import tree.Tree;


public class Main 
{
	
	public static GUI gui;
	public static Button[][] buttons;
	public static BoardInterface boardInterface;
	public static AtomicLong numberOfGeneratedBoards = new AtomicLong(0);
	public static AtomicLong timesPruned = new AtomicLong(0);
	public static AtomicLong numberOfTrivialBoards = new AtomicLong(0);
	public static void main(String[] args)
	{
		// Set up and user input
		Scanner scan = new Scanner(System.in);
		System.out.print("Parallelism level: ");
		int parallelismLevel = scan.nextInt();
		System.out.print("Size of board1: ");
		int boardSize1 = scan.nextInt();
		System.out.print("Size of board2: ");
		int boardSize2 = scan.nextInt();
		System.out.print("Number in a row to win: ");
		int inARowToWin = scan.nextInt();
		System.out.print("1 to go first, 2 to go second: ");
		int whoGoesFirst = scan.nextInt();
		boardInterface = new BoardInterface(boardSize1, boardSize2, inARowToWin, whoGoesFirst);
		
		scan.close();
		
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
				boardInterface.updateBounds(x, y);
				buttons[x][y].iconSetting(x, y);
				System.out.println("userMove made"); // Debug
			}
			else
			{
				int[] AImove = getAIMove(new BoardInterface(boardInterface), parallelismLevel);
				boardInterface.makeMove(AImove[0], AImove[1]);
				boardInterface.updateBounds(AImove[0], AImove[1]);
				System.out.println("Times Pruned: " + timesPruned.get());
				System.out.println("Number of trivial boards: " + numberOfTrivialBoards.get());
				System.out.println("Number of boards: " + numberOfGeneratedBoards.get());
				System.out.println("Percent pruned: " + (timesPruned.doubleValue()/numberOfGeneratedBoards.doubleValue()) * 100);
				System.out.println("AI move made"); // Debug
				Main.buttons[AImove[0]][AImove[1]].iconSetting(AImove[0], AImove[1]);
			}
			
			
		}
		boardInterface.displayBoard();
		System.out.println();
		System.out.println(boardInterface.getWinner() + " has won!");

	}
	private static int[] getAIMove(BoardInterface possibleBoardInterface, int parallelismLevel)
	{
		ExecutorService executor = Executors.newFixedThreadPool(parallelismLevel);
		ArrayList<Future<MoveWrapper>> results = new ArrayList<Future<MoveWrapper>>();
		// Maybe use generatePossibleMoves() here instead?
		possibleBoardInterface.generateOrderedMoves();
		System.out.println(possibleBoardInterface.getPossibleMoves().size());
		// Create a Callable for each possible moves
		for(int[] work: possibleBoardInterface.getPossibleMoves())
		{
			Callable<MoveWrapper> task = () -> 
			{
					Tree tree = new Tree(new BoardInterface(possibleBoardInterface), work);
					while(!numberOfGeneratedBoards.compareAndSet(numberOfGeneratedBoards.get(), numberOfGeneratedBoards.get() + tree.getNumberOfGeneratedBoards()));
					while(!timesPruned.compareAndSet(timesPruned.get(), timesPruned.get() + tree.getTimesPruned()));
					while(!numberOfTrivialBoards.compareAndSet(numberOfTrivialBoards.get(), numberOfTrivialBoards.get() + tree.getNumberOfTrivialBoards()));
					//System.out.println(tree.getBestMoveWrapper().toString());
					return tree.getBestMoveWrapper();
				
			};
			results.add(executor.submit(task));
		}
		
		ArrayList<MoveWrapper> bestMoves = new ArrayList<>();
		for(Future<MoveWrapper> result: results)
		{
			try {
				bestMoves.add(result.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		executor.shutdown();
		MoveWrapper bestMove = new MoveWrapper(bestMoves.get(0).getMove(), bestMoves.get(0).getValue());
		for(MoveWrapper move: bestMoves)
		{
			if (move.getValue() > bestMove.getValue())
			{
				//System.out.println(move.getValue());
				bestMove = move;
			}
		}
		return bestMove.getMove();
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
			
			scan.close();
		}
	}
	
}
