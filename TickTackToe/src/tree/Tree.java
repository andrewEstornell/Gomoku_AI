package tree;

import java.util.ArrayList;


import board.BoardInterface;

public class Tree 
{	
	private BoardInterface rootBoardInterface;
	
	
	private ArrayList<byte[]> rootBoardInterfaceMoves; // Allows us to pull the best move from the list of next possible moves
	private int maxDepth; // Prevents the tree from growing too big
	private int optimalSymCheck; // The depth at which checking for symmetry is no longer more efficent

	
	private ArrayList<ArrayList<BoardInterface>> firstRows;
	
	public Tree(BoardInterface rootBoardInterface)
	{	
		
		// These are temperary values, they work for now, but more calculation is needed to find the exact values
		this.maxDepth  = 2 *  rootBoardInterface.getInARowToWin();
		this.optimalSymCheck = (3 * this.rootBoardInterface.getInARowToWin()) / 2;
		

		this.rootBoardInterface = rootBoardInterface;
		this.rootBoardInterface.isPlayable(); // This function also generates the ArrayList of possibleMoves for the board
		this.rootBoardInterface.setChildren(new ArrayList<BoardInterface>()); // Reinitializes the child ArrayList so that there are not leftover children from previous iterations
		this.rootBoardInterfaceMoves = this.rootBoardInterface.getPossibleMoves(); 
		this.rootBoardInterface.setValue((byte)-2); // Used in print out later to test if the rootBoardInterface value was actually changed
		this.firstRows = new ArrayList<ArrayList<BoardInterface>>(); // Stores the first few rows up to this.optimalSymCheck, so that we may check them for symmetry

		// Adds rows 0 to optimalSymCheck -1  of the tree so we can check these rows for symmetry
		for(int i = 0; i < this.optimalSymCheck; i++)
		{
			this.firstRows.add(new ArrayList<BoardInterface>());
		}

		
		// Creates the tree and evaluates it
		this.alphaBeta(this.rootBoardInterface, true, 0, (byte)-100, (byte)100);	
	}
	
	
	/**
	 * 	Recursive function generates the tree of possibilities
	 * 	Once a board has generated all of its children, minmax with alpha-beta pruning is run on that board
	 * 	The last iteration of this function will store the optimal value in the rootBoardInterface
	 * @param boardInterface Current board interface, children of this will be generated and evaluated
	 * @param maximize true if we are taking the max value, false if we are taking the min value
	 * @param currentDepth how far into the tree we are
	 * @param alpha 
	 * @param beta 
	 * @return 
	 */
	public byte alphaBeta(BoardInterface boardInterface, boolean maximize, int currentDepth, byte alpha, byte beta)
	{
		// Generate more childBoards if conditions are met
		if(boardInterface.isPlayable() && currentDepth < this.maxDepth)
		{
			/*
			 * Each row is mined or maxed, since the function is recursive we use booleans to keep track of this rather than current row number
			 * If maximizing the AI is looking for the move that gives it the best rating, i.e. the best chance of the AI winning
			 * The AI assumes a perfect opponent,
			 * hence when we are minimizing the AI looks for the opponents move that gives the opponent the best move, i.e the worst chance of the AI winning
			 * This can cause strange behavior, such as the AI making "non moves" once it sees a loss
			 * To mitigate this we can make the static evaluations a function of both the board evaluation and the current depth
			 * This way the AI tries to win as fast as possible, and lose as slowly as possible
			 * Because humans are not perfect opponents this allows the AI to better capitalize on their mistakes instead of giving up when it notices a lose 
			 */
			if(maximize)
			{
				ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();
		     
				for(byte[] move: possibleMoves)
		        {
		            BoardInterface childBoardInterface = new BoardInterface(boardInterface); // Creates a deep copy of the current board so that we make moves on it
		            childBoardInterface.makeMove(move[0], move[1]);
		            
		            // Check for symmetry. Growth rate of each row in the tree becomes almost linear with this, although the algorithm itself has roughly n!(n^2) run time
		            if(currentDepth < this.optimalSymCheck && this.firstRows.get(currentDepth).size() > 0)
		            {
		            	boolean repeatBoard = false;
		            	for(BoardInterface boardInterfaceToCheck: this.firstRows.get(currentDepth))
		            	 {
		            		// If the board is trivial, we do not store it and simply start the processes over on the next board
		            		if(boardInterfaceToCheck.isEqualUpToSymmetry(childBoardInterface))
		            		{
		            			repeatBoard = true;
		            			break;
		            		}
		            	}
		            	  if(repeatBoard)
		            	  {
		            		  continue;
		            	  }
		              }
		              
		              childBoardInterface.evaluateBoard(currentDepth);
		              boardInterface.addChild(childBoardInterface);
		              // While we are still checking for symmetry, the boards are addd to an array list so that they may be easily recalled for evlauation
		             
		              /********************************************************************************************
		               * *******************************************************************************************
		               *  ////// SHould be fixed to only store the array of bytes that the BoardInterface holds////////////
		               * ********************************************************************************************
		               * *****************************************************************************************/
		              if(currentDepth < this.optimalSymCheck)
		              {
		            	  this.firstRows.get(currentDepth).add(childBoardInterface);
		              }
		              
		              /*
		               * This is the bread and butter of the whole method
		               * Run min max on the children, but now store the max value in Alpha and the min value in Beta
		               * When checking if alpha > beta, then we can prune and not do not need continue with any evaluation of the current board or its children
		               * This is achieved by simply breaking the loop 
		               */
		              alpha = (byte) Math.max(alpha, alphaBeta(childBoardInterface, false, currentDepth + 1, alpha, beta));
		              if(alpha >= beta)
		              {
		                  break;
		              }
		          }
				  // Now that the optimal value of the children has been found, it can now be stored in the parent boardInterface
		          boardInterface.setValue(alpha);
		          /*
		           * At the end of algorithm we only care which child of the very first board interface has the optimal value, 
		           * To save memory we can toast the child of any board that is at a depth of 2 or greater
		           */
		          if(currentDepth > 1)
		          {
		        	  boardInterface.setChildren(null);
		          }
		          return alpha; // Alpha is the optimal value of the children node, it is being returned to the parent board
			  }
		      /*
		       * If we are minimizing, then we look for the lowest value, i.e. the best move that AI thinks the human player make,
		       * The lowest value is now the most optimal
		       * Beta is produced here instead of alpha
		       */
			  else if(!maximize)
			  {
		          ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();    
		  	       for(byte[] move: possibleMoves)
		  	       {
		  		      BoardInterface childBoardInterface = new BoardInterface(boardInterface);
		              childBoardInterface.makeMove(move[0], move[1]);
		              
		              if(currentDepth < this.optimalSymCheck && this.firstRows.get(currentDepth).size() > 0)
		              {
		            	  boolean repeatBoard = false;
		            	  for(BoardInterface boardInterfaceToCheck: this.firstRows.get(currentDepth))
		            	  {
		            		  if(boardInterfaceToCheck.isEqualUpToSymmetry(childBoardInterface))
		            		  {
		            			  repeatBoard = true;
		            			  break;
		            		  }
		            	  }
		            	  if(repeatBoard)
		            	  {
		            		  continue;
		            	  }
		              }
		              
		              childBoardInterface.evaluateBoard(currentDepth);
		              boardInterface.addChild(childBoardInterface);
		              
		              
		              if(currentDepth < this.optimalSymCheck)
		              {
		            	  this.firstRows.get(currentDepth).add(childBoardInterface);
		              }
		              beta = (byte) Math.min(beta, alphaBeta(childBoardInterface, true, currentDepth + 1, alpha, beta));
		              if(alpha >= beta)
		              {
		            	  
		                  break;
		              }
		  	       }
		          boardInterface.setValue(beta);
		          
		          if(currentDepth > 1)
		          {
		        	  boardInterface.setChildren(null);
		          }
		          return beta;
			  }
		  }
		  else
		  {
			  boardInterface.evaluateBoard(currentDepth);
			  boardInterface.setIsLeaf(true);
		      return boardInterface.getValue();
		  }
		  return -1; // Nothing should ever have a value of -1, can use this for debugging
	}
	
	
	
	

	
	
	/**
	 * Checks through the children of the starting BoardInterface for the child that had the optimal value
	 * @return the x, y coordinate of the move of the child board with the best value
	 */
	public byte[] getBestMove2()
	{
		ArrayList<BoardInterface> rootChildren = this.rootBoardInterface.getChildren();
		int numberOfChildren = rootChildren.size();
		
		// Debug print out loop
		for(int i = 0; i < this.optimalSymCheck; i++)
		{
			System.out.println("Current row0 size: " + this.firstRows.get(i).size()); // Using in run time calculations
		}
		System.out.println("optimal value: " + this.rootBoardInterface.getValue()); // Debug print out
		
		// Finds the child with the optimal value
		for(int i = 0; i < numberOfChildren; i++)
		{
			if(rootChildren.get(i).getValue() == this.rootBoardInterface.getValue())
			{
				System.out.println( rootBoardInterfaceMoves.get(i)[0] + ", " + rootBoardInterfaceMoves.get(i)[1]); // Debug print out
				return rootBoardInterfaceMoves.get(i);
			}
		}
		System.out.println("ERROR"); // Should never reach here
		return null;
		
		
	}
	
	
	
	
}
