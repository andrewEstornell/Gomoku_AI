package tree;

import java.util.ArrayList;


import board.BoardInterface;

public class Tree 
{	
	private BoardInterface rootBoardInterface;
	private int depth;
	
	private byte[][] rootBoardInterfaceMoves;
	
	
	
	public Tree(BoardInterface rootBoardInterface)
	{
		this.rootBoardInterface = rootBoardInterface;
		this.rootBoardInterface.isPlayable();
		this.rootBoardInterface.setChildren(new ArrayList<BoardInterface>());
		this.rootBoardInterfaceMoves = this.rootBoardInterface.getPossibleMoves();
		this.depth = (this.rootBoardInterface.getBoardSize1() * this.rootBoardInterface.getBoardSize2()) - this.rootBoardInterface.getTurn();
		this.generateChildren(this.rootBoardInterface);		
	}
	
	/**
	 * 	Recursively builds a tree of board interfaces
	 * 	If the board is no longer playable it is marked as a leaf and given a static evaluation
	 * @param rootBoardInterface2
	 */
	
	private void generateChildren(BoardInterface boardInterface) 
	{
		if(boardInterface.isPlayable())
		{
			byte[][] possibleMoves = boardInterface.getPossibleMoves();

			for(byte[] move: possibleMoves)
			{
				// Duplicates board, makes the next possible moves, evaluates if the game is over or not
				BoardInterface duplicateBoardInterface = new BoardInterface(boardInterface);
				duplicateBoardInterface.makeMove(move[0], move[1]);
				duplicateBoardInterface.evaluateBoard();
				
				boardInterface.addChild(duplicateBoardInterface);
				duplicateBoardInterface.displayBoard();
				System.out.print(duplicateBoardInterface.getValue() + "\n");
				
				
				// continues process until all children have been added
				generateChildren(duplicateBoardInterface);
			}
			
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
				System.out.println( rootBoardInterfaceMoves[i][0] + ", " + rootBoardInterfaceMoves[i][1]);
				return rootBoardInterfaceMoves[i];
			}
		}
		System.out.println("ERROR");
		return null;
	}
	
	
}
