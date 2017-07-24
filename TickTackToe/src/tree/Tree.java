package tree;

import java.util.ArrayList;


import board.BoardInterface;

public class Tree 
{	
	private BoardInterface rootBoardInterface;
	
	
	private ArrayList<byte[]> rootBoardInterfaceMoves; // Allows us to pull the best move from the list of next possible moves
	private int maxDepth; // Prevents the tree from growing too big
	private int optimalSymCheck; // The depth at which checking for symmetry is no longer more efficent
	private ArrayList<Byte> childValues;
	private byte optimalVlaue;
	private byte optimalMoveLocation;
	private ArrayList<byte[]> initalMoves;
	
	private ArrayList<ArrayList<BoardInterface>> firstRows;
	
	public Tree(BoardInterface rootBoardInterface)
	{	
		
		// These are temperary values, they work for now, but more calculation is needed to find the exact values
		this.maxDepth  = 2 *  rootBoardInterface.getInARowToWin() ;
		this.optimalSymCheck = 2;//Math.min(rootBoardInterface.getInARowToWin(), (rootBoardInterface.getBoardSize1() * rootBoardInterface.getBoardSize2()) - rootBoardInterface.getTurn() -1);
		

		this.rootBoardInterface = rootBoardInterface;
		this.rootBoardInterface.isPlayable(); // This function also generates the ArrayList of possibleMoves for the board
		this.rootBoardInterface.setChildren(new ArrayList<BoardInterface>()); // Reinitializes the child ArrayList so that there are not leftover children from previous iterations
		this.rootBoardInterfaceMoves = this.rootBoardInterface.getPossibleMoves(); 
		this.rootBoardInterface.setValue((byte)-2); // Used in print out later to test if the rootBoardInterface value was actually changed
		this.firstRows = new ArrayList<ArrayList<BoardInterface>>(); // Stores the first few rows up to this.optimalSymCheck, so that we may check them for symmetry
		this.childValues = new ArrayList<Byte>();
		
		// Adds rows 0 to optimalSymCheck -1  of the tree so we can check these rows for symmetry
		for(int i = 0; i < this.optimalSymCheck; i++)
		{
			this.firstRows.add(new ArrayList<BoardInterface>());
		}

		
		// Creates the tree and evaluates it
		this.optimalVlaue = this.alphaBeta(this.rootBoardInterface, 0, (byte)-122, (byte)122, (byte) 1);	
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
	public byte alphaBeta(BoardInterface boardInterface, int currentDepth, byte alpha, byte beta, byte scalar)
	{
		// Once a leaf is reached, return the value of the leaf
		if(boardInterface.hasWon() || !boardInterface.isPlayable() || currentDepth == this.maxDepth)
		{
			boardInterface.evaluateBoard(currentDepth, this.maxDepth);
			boardInterface.setEvaluationType("exact");
		    return (byte) (scalar * boardInterface.getValue());
		}
		if(currentDepth == 0)
		{
			this.initalMoves = boardInterface.getPossibleMoves();
		}
		// Check for sym, if value is exact, return value, otherwise adjust upper and lower bound accordingly
		if(currentDepth < this.optimalSymCheck)
		{
			if(this.isTrivialBoard(boardInterface, currentDepth))
			{
				if(boardInterface.getEvaluationType().compareTo("exact") == 0)
				{
					return boardInterface.getValue();
				}
				// Adjusting upper and lower bounds if needed
				else if(boardInterface.getEvaluationType().compareTo("lower") == 0)
				{
					beta = (byte) Math.min(beta, boardInterface.getValue());
				}
				else if(boardInterface.getEvaluationType().compareTo("upper") == 0)
				{
					alpha = (byte) Math.max(alpha, boardInterface.getValue());
				}
				// Fast prune
				if(alpha >= beta)
				{
					return boardInterface.getValue();
				}
			}
		}
		
		byte bestValue = -122; // Worst possible value
		ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();
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
		for(byte[] move: possibleMoves)
		{
			BoardInterface childBoardInterface = new BoardInterface(boardInterface);
			childBoardInterface.makeMove(move[0], move[1]);
			
			byte childValue = (byte) -this.alphaBeta(childBoardInterface, (byte)(currentDepth + 1), (byte)-beta, (byte)-alpha, (byte)-scalar);
			if(currentDepth == 0)
			{
				this.childValues.add(childValue);
			}
			
			bestValue = (byte) Math.max(bestValue, childValue); // updating best value
			alpha = (byte) Math.max(alpha, childValue); // updating upper bound
			// Slow prune
			if(alpha >= beta)
			{
				break;
			}
		}
		// Seting value and bound type
		boardInterface.setValue(bestValue);
		if(scalar == -1)
		{
			boardInterface.setEvaluationType("lower");
		}
		else if(scalar == 1)
		{
			boardInterface.setEvaluationType("upper");
		}
		return bestValue;
	}
	
	
	
	

	
	
	/**
	 * Checks through the children of the starting BoardInterface for the child that had the optimal value
	 * @return the x, y coordinate of the move of the child board with the best value
	 */
	public byte[] getBestMove2()
	{
		byte k = 0;
		for(Byte value: this.childValues)
		{
			if(value == this.optimalVlaue)
			{
				this.optimalMoveLocation = k;
				break;
			}
			k++;
		}
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
				return true;
			}
		}
		this.firstRows.get(currentDepth).add(boardInterface);
		return false;
	}
	
	
	
	
}
