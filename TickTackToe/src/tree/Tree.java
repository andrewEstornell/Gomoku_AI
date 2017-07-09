package tree;

import java.util.ArrayList;


import board.BoardInterface;

public class Tree 
{	
	private BoardInterface rootBoardInterface;
	private int depth;
	
	private ArrayList<byte[]> rootBoardInterfaceMoves;
	private int maxDepth;
	
	
	
	public Tree(BoardInterface rootBoardInterface)
	{	
		
		
		/*
		 * 	I have no idea how to make this work well, trying to create a function to stop the tree from getting too large
		 */
		/*this.maxDepth = 1;
		long optimalSize = 15*14*13*12*11*10*9*8*7*6*5*4*3*2;
		long currentSize = (rootBoardInterface.getBoardSize1() * rootBoardInterface.getBoardSize2()) - rootBoardInterface.getTurn() + 1;
		
		while(currentSize< optimalSize && currentSize > 0)
		{
			currentSize = currentSize * (currentSize - 1);
			this.maxDepth ++;
		}*/
	
		
		this.maxDepth  = rootBoardInterface.getInARowToWin() + 4;
		
		
		this.rootBoardInterface = rootBoardInterface;
		this.rootBoardInterface.isPlayable();
		this.rootBoardInterface.setChildren(new ArrayList<BoardInterface>());
		this.rootBoardInterfaceMoves = this.rootBoardInterface.getPossibleMoves();
		this.depth = (this.rootBoardInterface.getBoardSize1() * this.rootBoardInterface.getBoardSize2()) - this.rootBoardInterface.getTurn();
		this.rootBoardInterface.setValue((byte)-2);
		this.alphaBeta(this.rootBoardInterface, true, 0, (byte)-100, (byte)100);
		//this.generateChildren(this.rootBoardInterface, (byte) 0);		
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
		
		//////// NEW IDEA /////////////
		
		  
		  if(boardInterface.isPlayable() && currentDepth < this.maxDepth)
		  {
			  if(maximize)
			  {
				  ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();
		          for(byte[] move: possibleMoves)
		          {
		              BoardInterface childBoardInterface = new BoardInterface(boardInterface);
		              childBoardInterface.makeMove(move[0], move[1]);
		              childBoardInterface.evaluateBoard();
		              boardInterface.addChild(childBoardInterface);
		              alpha = (byte) Math.max(alpha, alphaBeta(childBoardInterface, false, currentDepth + 1, alpha, beta));
		              if(alpha >= beta)
		              {
		            	  System.out.println("a > b");
		                  break;
		              }
		          }
		          boardInterface.setValue(alpha);
		          System.out.println("ALPHA: " + alpha);
		          if(currentDepth > 1)
		          {
		        	  boardInterface.setChildren(null);
		          }
		          return alpha;
			  }
		              
			  else if(!maximize)
			  {
		          ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();    
		  	       for(byte[] move: possibleMoves)
		  	       {
		  		      BoardInterface childBoardInterface = new BoardInterface(boardInterface);
		              childBoardInterface.makeMove(move[0], move[1]);
		              childBoardInterface.evaluateBoard();
		              boardInterface.addChild(childBoardInterface);
		              beta = (byte) Math.min(beta, alphaBeta(childBoardInterface, true, currentDepth + 1, alpha, beta));
		              if(alpha >= beta)
		              {
		            	  System.out.println("b < a");
		                  break;
		              }
		  	       }
		          boardInterface.setValue(beta);
		          System.out.println("BETA: " + beta);
		          if(currentDepth > 1)
		          {
		        	  boardInterface.setChildren(null);
		          }
		          return beta;
			  }
		  }
		  else
		  {
			  boardInterface.evaluateBoard();
			  boardInterface.setIsLeaf(true);
		      return boardInterface.getValue();
		  }
		  return -1;	
		/*if(boardInterface.isPlayable() && currentDepth < this.maxDepth)
		{
			ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();
			for(byte[] move: possibleMoves)
			{
				// Duplicates board, makes the next possible moves, evaluates if the game is over or not
				BoardInterface duplicateBoardInterface = new BoardInterface(boardInterface);
				duplicateBoardInterface.makeMove(move[0], move[1]);
				if(currentDepth == this.maxDepth - 1)
				{
					boardInterface.setIsLeaf(true);
				}
				duplicateBoardInterface.evaluateBoard();
			
				boardInterface.addChild(duplicateBoardInterface);
			
				// continues process until all children have been added
				if(!duplicateBoardInterface.isLeaf())
				{
					if(maximize)
					{
						this.alphaBeta(duplicateBoardInterface, false, currentDepth + 1);
					}
					else if(!maximize)
					{
						this.alphaBeta(duplicateBoardInterface, true, currentDepth + 1);
					}
				}
			}
		}
		else
		{
			boardInterface.setIsLeaf(true);
		}

		if(boardInterface.getChildren().size() > 0)
		{
			byte optimalValue = 0;
			if(maximize)
			{
				optimalValue = -50;
				ArrayList<BoardInterface> children = boardInterface.getChildren();
				for(BoardInterface child: children)
				{
					System.out.println(child.getValue());
					optimalValue = (byte)Math.max(optimalValue, child.getValue());
					**if(optimalValue < child.getValue())
					{
						optimalValue = child.getValue();
					}**
				}
			}
			else if(!maximize)
			{
				optimalValue = 50;
				ArrayList<BoardInterface> children = boardInterface.getChildren();
				for(BoardInterface child: children)
				{
					System.out.println(child.getValue());
					optimalValue = (byte)Math.min(optimalValue,  child.getValue());
					**if(optimalValue > child.getValue())
					{
						optimalValue = child.getValue();
					}**
				}
			}
			boardInterface.setValue(optimalValue);
			if(currentDepth > 1)
			{
				boardInterface.setChildren(null);
			}
		}*/

	}
	
	
	
	/**
	 * 	Recursively builds a tree of board interfaces
	 * 	If the board is no longer playable it is marked as a leaf and given a static evaluation
	 * @param rootBoardInterface2
	 */
	
	private void generateChildren(BoardInterface boardInterface, byte currentDepth) 
	{
		if(boardInterface.isPlayable() && currentDepth < this.maxDepth)
		{
			ArrayList<byte[]> possibleMoves = boardInterface.getPossibleMoves();

			for(byte[] move: possibleMoves)
			{
				// Duplicates board, makes the next possible moves, evaluates if the game is over or not
				BoardInterface duplicateBoardInterface = new BoardInterface(boardInterface);
				duplicateBoardInterface.makeMove(move[0], move[1]);
				if(currentDepth == this.maxDepth - 1)
				{
					boardInterface.setIsLeaf(true);
				}
				duplicateBoardInterface.evaluateBoard();
				
				boardInterface.addChild(duplicateBoardInterface);
				/*
				if(currentDepth == 0)
				{
					System.out.println("________________________________________");
				}
				duplicateBoardInterface.displayBoard();
				
				System.out.print(duplicateBoardInterface.getValue() + "\n");
				*/
				
				
				// continues process until all children have been added
				generateChildren(duplicateBoardInterface, (byte) (currentDepth + 1));
				duplicateBoardInterface.setBoard(null);
				duplicateBoardInterface.setPossibleMoves(null);
			}
			boardInterface.setPossibleMoves(null);
			
		}
		else
		{
			boardInterface.setIsLeaf(true);
			boardInterface.setBoard(null);
			boardInterface.setPossibleMoves(null);
		}
		
	}

	/**
	 * 	Recursively runs min max on the tree and returns the optimal value
	 * @param boardInterface current node being investigated
	 * @param depth size of tree
	 * @param maximize true if we are maximizing, false if we are minimizing
	 * @return the optimal value from the list of child board interfaces
	 */
	private byte minMax(BoardInterface boardInterface, int depth, boolean maximize)
	{
		if(depth == 0 ||boardInterface.isLeaf())
		{
			return boardInterface.getValue();
		}
		byte optimalValue = 0;
		if(maximize)
		{
			optimalValue = -50;
			ArrayList<BoardInterface> children = boardInterface.getChildren();
			for(BoardInterface child: children)
			{
				byte value = minMax(child, depth - 1, false);
				if(value > optimalValue)
				{
					optimalValue = value;
				}
			}
			
		}
		else if(!maximize)
		{
			optimalValue = 50;
			ArrayList<BoardInterface> children = boardInterface.getChildren();
			for(BoardInterface child: children)
			{
				byte value = minMax(child, depth - 1, true);
				if(value < optimalValue)
				{
					optimalValue = value;
				}
			}
		}
		return optimalValue;
	}
	
	public byte[] getBestMove2()
	{
		ArrayList<BoardInterface> rootChildren = this.rootBoardInterface.getChildren();
		int numberOfChildren = rootChildren.size();
		System.out.println("number of children: " + numberOfChildren);
		System.out.println("optimal value: " + this.rootBoardInterface.getValue());
		for(int i = 0; i < numberOfChildren; i++)
		{
			if(rootChildren.get(i).getValue() == this.rootBoardInterface.getValue())
			{
				System.out.println( rootBoardInterfaceMoves.get(i)[0] + ", " + rootBoardInterfaceMoves.get(i)[1]);
				return rootBoardInterfaceMoves.get(i);
			}
		}
		System.out.println("ERROR");
		return null;
		
		
	}
	
	
	public byte[] getBestMove()
	{
		ArrayList<BoardInterface> rootChildren = this.rootBoardInterface.getChildren();
		int optimalValue = - 50;
		for(BoardInterface rootChild: rootChildren)
		{
			rootChild.setValue(this.minMax(rootChild, this.depth - 1, false));
			if(rootChild.getValue() > optimalValue)
			{
				optimalValue = rootChild.getValue();
			}
		}
		
		
		
		
		System.out.println("optimal value: " + optimalValue);

		int numberOfChildren = rootChildren.size();
		
		for(int i = 0; i < numberOfChildren; i++)
		{
			if(rootChildren.get(i).getValue() == optimalValue)
			{
				System.out.println( rootBoardInterfaceMoves.get(i)[0] + ", " + rootBoardInterfaceMoves.get(i)[1]);
				return rootBoardInterfaceMoves.get(i);
			}
		}
		System.out.println("ERROR");
		return null;
	}
	
	
}
