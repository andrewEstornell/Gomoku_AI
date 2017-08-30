package tree;

import java.util.ArrayList;


import board.BoardInterface;

public class Tree 
{	
	private BoardInterface rootBoardInterface;
	
	
	private ArrayList<int[]> rootBoardInterfaceMoves; // Allows us to pull the best move from the list of next possible moves
	private int maxDepth; // Prevents the tree from growing too big
	private int optimalSymCheck; // The depth at which checking for symmetry is no longer more efficient
	private int maxOrderCheck;
	private ArrayList<Integer> childValues;
	private int optimalValue;
	private int optimalMoveLocation;
	private ArrayList<int[]> initalMoves;
	
	private int numberOfTrivialBoards;
	private long totalNumberOfBoards;
	private long numberOfPrunes;
	private ArrayList<ArrayList<BoardInterface>> firstRows; // Stores previously investigated boards, used to check for triviality
	
	
	
	public Tree(BoardInterface rootBoardInterface)
	{	
		
		// These are temperary values, they work for now, but more calculation is needed to find the exact values
		this.maxDepth = (2 * rootBoardInterface.getInARowToWin()) - 2;
		this.optimalSymCheck = this.maxDepth - 3;
		
		this.maxOrderCheck = this.maxDepth - 1;
		
		this.numberOfTrivialBoards = 0;
		this.totalNumberOfBoards = 0;
		this.numberOfPrunes = 0;

		this.rootBoardInterface = rootBoardInterface;
		this.rootBoardInterface.generatePossibleMoves(); // This function also generates the ArrayList of possibleMoves for the board
		//this.rootBoardInterface.setChildren(new ArrayList<BoardInterface>()); // Reinitializes the child ArrayList so that there are not leftover children from previous iterations
		this.rootBoardInterfaceMoves = this.rootBoardInterface.getPossibleMoves(); 
		this.rootBoardInterface.setValue(-2); // Used in print out later to test if the rootBoardInterface value was actually changed
		this.firstRows = new ArrayList<ArrayList<BoardInterface>>(); // Stores the first few rows up to this.optimalSymCheck, so that we may check them for symmetry
		this.childValues = new ArrayList<Integer>();
		
		
		
		// Adds rows 0 to optimalSymCheck -1  of the tree so we can check these rows for symmetry
		for(int i = 0; i < this.optimalSymCheck; i++)
		{
			this.firstRows.add(new ArrayList<BoardInterface>());
		}

		
		// Creates the tree and evaluates it
		this.optimalValue = this.alphaBeta(this.rootBoardInterface, 0, -10000, 10000, 1);
		System.out.println("Number of Trivial boards: " + this.numberOfTrivialBoards);
		System.out.println("Total number of boards:   " + this.totalNumberOfBoards);
		System.out.println("Number of prunes: " + this.numberOfPrunes);
		System.out.println("% pruned: " + (double)this.numberOfPrunes/(double)this.totalNumberOfBoards);
	}
	
	
	/**
	 * 	Recursive function generates the tree of possibilities
	 * 	Once a board has generated all of its children, minmax with alpha-beta pruning is run on that board
	 * 	The last iteration of this function will store the optimal value in the rootBoardInterface
	 * @param boardInterface Current board interface, children of this will be generated and evaluated
	 * @param currentDepth how far into the tree we are3
	 * @param alpha 
	 * @param beta 
	 * @scalar -1 if human turn, 1 if AI turn
	 * @return best value for a given position
	 */
	public int alphaBeta(BoardInterface boardInterface, int currentDepth, int alpha, int beta, int scalar)
	{
		// Once a leaf is reached, return the value of the leaf
		if(currentDepth == this.maxDepth || boardInterface.hasWon() || boardInterface.getWinner() != 0)
		{
			boardInterface.evaluateBoard(currentDepth, this.maxDepth);
			boardInterface.setEvaluationType(0);
		    return  (scalar * boardInterface.getValue());
		}
		
		// Check for sym, if value is exact, return value, otherwise adjust upper and lower bound accordingly
		if(currentDepth < this.optimalSymCheck)
		{
			if(this.isTrivialBoard(boardInterface, currentDepth))
			{
				
				
				this.numberOfTrivialBoards++;
				if(boardInterface.getEvaluationType() == 0)
				{
					return boardInterface.getValue();
				}
				// Adjusting upper and lower bounds if needed
				else if(boardInterface.getEvaluationType() == -1)
				{
					beta =  Math.min(beta, boardInterface.getValue());
				}
				else if(boardInterface.getEvaluationType() == 1)
				{
					alpha =  Math.max(alpha, boardInterface.getValue());
				}
				// Fast prune
				if(alpha >= beta)
				{
					this.numberOfPrunes++;
					return boardInterface.getValue();
				}
			}
		}
		int bestValue = -10000; // Worst possible value
		
		// Gets all possible moves, orders them if doing so is still efficient
		if(currentDepth < this.maxOrderCheck)//|| currentDepth == 1)
		{
			boardInterface.generateOrderedMoves();
			if(currentDepth == 0)
			{
				if(boardInterface.getTurn() == 0)
				{
					boardInterface.generateTrunZeroMove();
				}
				this.initalMoves = boardInterface.getPossibleMoves();
			}
		}
		else
		{
			boardInterface.generatePossibleMoves();
		}
		ArrayList<int[]> possibleMoves = boardInterface.getPossibleMoves();
		/*
		 * Iterates through each board position one move in the future
		 * Recursively calls alpha beta on this new position until one of two cases is met
		 * 1.) the board is no loner in play
		 * 		- in this case the baord is given an exact evaulation
		 * 2.) the board is discovered to be trivial
		 * 		- the board will be given a derived value
		 * 		- if the derived value is discored to be exact, that exact value is  the value of alpha beta
		 * 		- if the derived value is a bound, we update its corrisponding bound as needed
		 */
		for(int[] move: possibleMoves)
		{
			this.totalNumberOfBoards ++;
			BoardInterface childBoardInterface = new BoardInterface(boardInterface);
			childBoardInterface.makeMove(move[0], move[1]);
			if(currentDepth <= this.optimalSymCheck + 1)
			{
				childBoardInterface.updateBounds(move[0], move[1]);
			}
			int childValue =  -this.alphaBeta(childBoardInterface, (currentDepth + 1), -beta, -alpha, -scalar);
			//childBoardInterface.displayBoard();
			//System.out.println("Child value^^: " + childValue);
			if(currentDepth == 0)
			{
				this.childValues.add(childValue);
			}
			
			bestValue =  Math.max(bestValue, childValue); // updating best value
			alpha =  Math.max(alpha, childValue); // updating upper bound
			// Slow prune
			if(alpha >= beta)
			{
				this.numberOfPrunes++;
				break;
			}
		}
		// Seting value and bound type
		boardInterface.setValue(bestValue);
		if(scalar == -1)
		{
			boardInterface.setEvaluationType(-1);
		}
		else if(scalar == 1)
		{
			boardInterface.setEvaluationType(1);
		}
		return bestValue;
	}
	
	
	
	

	
	
	/**
	 * Checks through the children of the starting BoardInterface for the child that had the optimal value
	 * @return the x, y coordinate of the move of the child board with the best value
	 */
	public int[] getBestMove2()
	{
		int k = 0;
		for(int value: this.childValues)
		{
			if(value == this.optimalValue)
			{
				this.optimalMoveLocation = k;
				break;
			}
			k++;
		}
		System.out.println("Total board: " + this.totalNumberOfBoards);
		return this.initalMoves.get(this.optimalMoveLocation);
	}
	
	
	/**
	 * Checks for redundant boards, the board is determined to be new, the board is stored in first rows
	 * @param boardInterface
	 * @param currentDepth
	 * @return true if board interface is redundant, false otherwise
	 */
	private boolean isTrivialBoard(BoardInterface boardInterface, int currentDepth)
	{
		if(this.firstRows.get(currentDepth).size() == 0)
		{
			this.firstRows.get(currentDepth).add(boardInterface);
			return false;
		}
		for(BoardInterface storedBoardInterface: this.firstRows.get(currentDepth))
		{
			if(boardInterface.isEqualUpToSymmetry(storedBoardInterface))
			{
				boardInterface.setValue(storedBoardInterface.getValue());
				boardInterface.setEvaluationType(storedBoardInterface.getEvaluationType());
				//System.out.println("____________________\n StoredBoard");
				//System.out.println("Ux = " + storedBoardInterface.getUpperX() + ", Lx = " + storedBoardInterface.getLowerX()
				//				 + "|||| Uy = " + storedBoardInterface.getUpperY() + ", Ly = " + storedBoardInterface.getLowerY());
				//storedBoardInterface.displayBoard();
				//System.out.println("ChildBoard");
				//System.out.println("Ux = " + boardInterface.getUpperX() + ", Lx = " + boardInterface.getLowerX()
				//				 + "|||| Uy = " + boardInterface.getUpperY() + ", Ly = " + boardInterface.getLowerY());
				//boardInterface.displayBoard();
				//System.out.println("_____________________");
				return true;
			}
		}
		this.firstRows.get(currentDepth).add(boardInterface);
		return false;
	}
}
