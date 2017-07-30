package board;

import java.util.ArrayList;
import java.util.Arrays;


//-XX:+UseConcMarkSweepGC -XX:+UseParNewGC


public class BoardInterface 
{
	// User input fields
	private int boardSize1;
	private int boardSize2;
	private int inARowToWin;
	private int whoGoesFirst; // 1 if player goes first, otherwise 2

	// Consequential fields
	private int[][] board;
	private int turn;
	
	
	// Derived fields 
	private int value; // How good the given position is
	//private ArrayList<BoardInterface> children; // Board interfaces one move ahead of the current board interface
	private int winner; // 1 if first player wins, 2 if second player wins, 0 otherwise;
	private ArrayList<int[]> possibleMoves; // List of all possible moves 

	private String evaluationType;
	
	private int upperX; // Used to test for locat symmtry
	private int lowerX;
	private int upperY;
	private int lowerY;
		
		/**
		 * 	Constructor for a new, i.e. first, board
		 * @param boardSize1
		 * @param boardSize2
		 */
		public BoardInterface(int boardSize1, int boardSize2, int inARowToWin, int whoGoesFirst)
		{
			// User input fields
			this.boardSize1 = boardSize1;
			this.boardSize2 = boardSize2;
			this.inARowToWin = inARowToWin;
			this.whoGoesFirst =  (whoGoesFirst - 1);
			
			// Consequential fields
			this.board = generateNewBoard();
			this.turn = 0;
			
			// Derived fields
			this.value = 0;
			//this.children = new ArrayList<BoardInterface>();
			this.winner = 0;

			this.evaluationType = "NotEvaluated";

			this.upperX = -1;
			this.lowerX = this.boardSize1;
			this.upperY = -1;
			this.lowerY = this.boardSize2;
		}
		
		/**
		 *  Creates a new 2D board of ints filled with all 0s
		 * @return the newly generated board;
		 */
		private int[][] generateNewBoard() 
		{
			int[][] board = new int[this.boardSize1][this.boardSize2];
			for(int i = 0; i < this.boardSize1; i++)
			{
				for(int j = 0; j < this.boardSize2; j++)
				{
					board[i][j] = 0;
				}
			}
			return board;
		}
		
		
		/**
		 * 	Creates a deep copy of a given board
		 * @param boardToBeCoppied 
		 */
		public BoardInterface(BoardInterface boardInterfaceToBeCoppied)
		{	
			// User input fields
			this.boardSize1 = boardInterfaceToBeCoppied.getBoardSize1();
			this.boardSize2 = boardInterfaceToBeCoppied.getBoardSize2();
			this.inARowToWin = boardInterfaceToBeCoppied.getInARowToWin();
			this.whoGoesFirst = boardInterfaceToBeCoppied.getWhoGoesFirst();
			
			// Consequential fields
			//this.board = copyBoard(boardInterfaceToBeCoppied.getBoard());
			this.board = Arrays.stream(boardInterfaceToBeCoppied.getBoard()).map(int[]::clone).toArray(int[][]::new);
			this.turn = boardInterfaceToBeCoppied.getTurn();
			
			
			// Derived fields
			this.value = boardInterfaceToBeCoppied.getValue();
			this.winner = boardInterfaceToBeCoppied.getWinner();

			this.evaluationType = boardInterfaceToBeCoppied.getEvaluationType();
			this.upperX = boardInterfaceToBeCoppied.getUpperX();
			this.lowerX = boardInterfaceToBeCoppied.getLowerX();
			this.upperY = boardInterfaceToBeCoppied.getUpperY();
			this.lowerY = boardInterfaceToBeCoppied.getLowerY();
			
			
		}

		/**
		 * 	Checks that a proposed move is valid, if it is valid the move is made and turn in incremented
		 * @param x x coordinate of the proposed move
		 * @param y y coordinate of the proposed move
		 * @return true if move is valid, false otherwise
		 */
		public boolean makeMove(int x, int y) 
		{
			if(this.board[x][y] != 0)
			{
				return false;
			}
			this.board[x][y] =  ((this.turn % 2) + 1);
			this.turn++;
			return true;
		}
		/**
		 * Updates the bound of the symmetry check based on the last valid move
		 * @param x coordinate of last move
		 * @param y coordinate of last move
		 */
		public void updateBounds(int x, int y)
		{
			if(x > this.upperX)
			{
				this.upperX = x;
			}
			if(x < this.lowerX)
			{
				this.lowerX = x;
			}
			if(y > this.upperY)
			{
				this.upperY = y;
			}
			if(y < this.lowerY)
			{
				this.lowerY = y;
			}
			
			while(this.upperX - this.lowerX > this.upperY - this.lowerY)
			{
				if(this.upperY < this.boardSize2 - 1)
				{
					upperY++;
				}
				else
				{
					this.lowerY--;
				}
			}
			while(this.upperX - this.lowerX < this.upperY - this.lowerY)
			{
				if(this.upperX < this.boardSize1 - 1)
				{
					upperX++;
				}
				else
				{
					lowerX--;
				}
			}
			
		}
		
		
		/**
		 * 	Checks that the game is not over
		 * 	Stores all possible moves
		 * @return false if there are no possibleMoves, true if possible moves were generated correctly
		 */
		public boolean isPlayable()
		{
			// Determines if the game if still in a playable state
			this.possibleMoves = new ArrayList<int[]>(60);
			if(this.winner != 0)
			{
				return false;
			}

			
			if(this.turn == 0)
			{
				int[] newMove = {(this.boardSize1/2),  (this.boardSize2/2)}; 
				this.possibleMoves.add(newMove);
				return true;
			}
			
			
			// Generates a list of possible moves based on empty board spaces
			for(int i = 0; i < this.boardSize1; i++)
			{
				for(int j = 0; j < this.boardSize2; j++)
				{
					if(this.board[i][j] == 0)
					{
						// Only care about adjacent squares to places already moved
						if(this.isAdjacentSquare(i, j))
						{
							this.possibleMoves.add(new int [] {i, j});
						}
					}
				}
			}
			return true;
			
		}
		
		/**
		 * Checks if the given square this.board[i][j] is adjacent to a previous move
		 * @param i
		 * @param j
		 * @return true if it is adjacent, false otherwise
		 */
		private boolean isAdjacentSquare(int i, int j) 
		{
			if(i + 1 < this.boardSize1)
			{
				if(this.board[i + 1][j] != 0)
				{
					return true;
				}
				if(j + 1 < this.boardSize2)
				{
					if(this.board[i + 1][j + 1] != 0)
					{
						return true;
					}
				}
				if(j - 1 >= 0)
				{
					if(this.board[i + 1][j - 1] != 0)
					{
						return true;
					}
				}
			}
			if(i - 1 >= 0)
			{
				if(this.board[i - 1][j] != 0)
				{
					return true;
				}
				if(j + 1 < this.boardSize2)
				{
					if(this.board[i - 1][j + 1] != 0)
					{
						return true;
					}
				}
				if(j - 1 >= 0)
				{
					if(this.board[i - 1][j - 1] != 0)
					{
						return true;
					}
				}
			}
			if(j - 1 >= 0)
			{
				if(this.board[i][j - 1] !=0)
				{
					return true;
				}
			}
			if(j + 1 < this.boardSize2)
			{
				if(this.board[i][j + 1] != 0)
				{
					return true;
				}
			}
			return false;
		}


		
		/**
		 * 	Checks if the game is over.	
		 * 	If so, gives a static evaluation, else does nothing.
		 * @return 
		 */
		public int evaluateBoard(int currentDepth, int maxDepth) 
		{
			if(this.winner != 0)
			{
				//this.value = heuristicEvaluation(currentDepth, maxDepth);
				if(this.winner == ((this.whoGoesFirst % 2 ) + 1))
				{
					this.value = (-10000 + currentDepth);
				}
				else if(this.winner == ((this.whoGoesFirst + 1) % 2) + 1)
				{
					this.value = (10000 - currentDepth);
				}
				else if(this.winner == -1)
				{
					this.value = 0; 
					System.out.println("TIE");
				}
				else
				{
					this.value = 0;
					System.out.println("ERROR, maybe");
				}
			}
			else
			{
				
				this.value = this.heuristicEvaluation5(currentDepth, maxDepth, ((this.turn + 1) % 2) + 1)
						   - this.heuristicEvaluation5(currentDepth, maxDepth, ((this.turn % 2) + 1));
				//System.out.println("game still in play");
			}
			return this.value;
		}
		
		/**
		 * Need a way to evaluate boards that are not yet won or lost when we reach max depth
		 * @return a heuristic evaluation of the given leaf board
		 */
		public int heuristicEvaluation(int currentDepth, int maxDepth)
		{
			//// ADD CHECK FOR BOTH PLAYERS THREATS/////////
			int value = 0;
			int player1 = (((this.turn + 1) % 2) + 1);
			int player2 = ((this.turn % 2) + 1);
			
			
			
			// Diamond structure
			for(int i = 0; i < this.boardSize1; i++)
			{
				for(int j = 0; j < this.boardSize2; j++)
				{

					if(this.board[i][j] == 0)
					{
						
						value += this.YinARow(i, j, player1) - this.YinARow(i, j, player2) + this.XinARow(i, j, player1) - this.XinARow(i, j, player2) 
							   + this.XeqYinARow(i, j, player1) - this.XeqYinARow(i, j, player2) + this.XeqNegYinARow(i, j, player1) - this.XeqNegYinARow(i, j, player2);
						
						
					}
				}
			}
			if(((this.whoGoesFirst) % 2) == (this.turn) % 2)
			{
				value = value * (-1);
			}
			//this.displayBoard();
			//System.out.println("H value:^^ " + value);
			return value;
		}

		
		private int heuristicEvaluation5(int currentDepth, int maxDepth, int player)
		{	
			
			int value = 0;
			// Check for non boundary threats
			for(int i = 1; i < this.boardSize1 - 1; i++)
			{
				for(int j = 1; j < this.boardSize1 - 1; j++)
				{
					if(this.board[i][j] == player)
					{
						if(this.board[i - 1][j] == 0 && this.board[i + 1][j] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(i + k < this.boardSize1)
								{
									if(this.board[i + k][j] == player)
									{
										value++;
									}
									if(this.board[i + k][j] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						else if(this.board[i + 1][j] == 0 && this.board[i - 1][j] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(i - k > -1)
								{
									if(this.board[i - k][j] == player)
									{
										value++;
									}
									else if(this.board[i - k][j] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						if(this.board[i][j - 1] == 0 && this.board[i][ j + 1] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(j + k < this.boardSize2)
								{
									if(this.board[i][j + k] == player)
									{
										value++;
									}
									else if(this.board[i][j + k] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						else if(this.board[i][j + 1] == 0 && this.board[i][j - 1] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(j - k > -1)
								{
									if(this.board[i][j - k] == player)
									{
										value++;
									}
									else if(this.board[i][j - k] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						if(this.board[i - 1][j - 1] == 0 && this.board[i + 1][j + 1] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(i + k < this.boardSize1 && j + k < this.boardSize2)
								{
									if(this.board[i + k][j + k] == player)
									{
										value++;
									}
									else if(this.board[i + k][j + k] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						else if(this.board[i + 1][j + 1] == 0 && this.board[i - 1][j - 1] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(i - k > -1 && j - k > -1)
								{
									if(this.board[i - k][j - k] == player)
									{
										value++;
									}
									else if(this.board[i - k][j - k] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						if(this.board[i -1][j + 1] == 0 && this.board[i + 1][j - 1] == player)
						{
							value++;
							for(int k = 2; k < this.inARowToWin; k++)
							{
								if(i + k < this.boardSize1 && j - k > -1)
								{
									if(this.board[i + k][j - k] == player)
									{
										value++;
									}
									else if(this.board[i + k][j - k] == 0)
									{
										value++;
										break;
									}
									else
									{
										break;
									}
								}
								else
								{
									break;
								}
							}
						}
						
					}
				}
			}
			
			return value;
		}
		
		private int YinARow(int i, int j, int player)
		{
			int player1 = player;
			
			int counterY = 0;
			
			for(int k = 1; k < this.inARowToWin; k++)
			{
				if(i + k < this.boardSize1)
				{
					if(this.board[i + k][j] == player1)
					{
						counterY++;
					}
					else if(this.board[i + k][j] == 0)
					{
						counterY++;
						break;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			if(counterY > 1)
			{
				return counterY;
			}
			
			return 0;
		}
		private int XinARow(int i, int j, int player)
		{
			int player1 = player;
			
			int counterY = 0;
			
			for(int k = 1; k < this.inARowToWin; k++)
			{
				if(j + k < this.boardSize2)
				{
					if(this.board[i][j + k] == player1)
					{
						counterY++;
					}
					else if(this.board[i][j + k] == 0)
					{
						counterY++;
						break;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			if(counterY > 1)
			{
				return counterY;
			}
			return 0;
		}
		private int XeqYinARow(int i, int j, int player)
		{	
			int player1 = player;
		
			int counterXeqY = 0;
			for(int k = 1; k < this.inARowToWin; k++)
			{
				if(i + k < this.boardSize2 && j - k > -1)
				{
					if(this.board[i + k][j - k] == player1)
					{
						counterXeqY++;
					}
					else if(this.board[i + k][j - k] == 0)
					{
						counterXeqY++;
						break;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			if(counterXeqY > 1)
			{
				return counterXeqY;
			}
			return 0;
		}
		private int XeqNegYinARow(int i, int j, int player)
		{
			int player1 = player;
			
			int counterXeqNegY = 0;
			for(int k = 1; k < this.inARowToWin; k++)
			{
				if(i + k < this.boardSize1 && j + k < this.boardSize2)
				{
					if(this.board[i + k][j + k] == player1)
					{
						counterXeqNegY ++;
					}
					else if(this.board[i + k][j + k] == 0)
					{
						counterXeqNegY++;
						break;
					}
					else
					{
						break;
					}
				}
				else
				{
					break;
				}
			}
			if(counterXeqNegY > 1)
			{
				return counterXeqNegY;
			}
			return 0;
		}
		
		
		/**
		 * 	checks for three in a row, four different ways,  starting at board[0][0] 
		 * 		Sets winner = the character of th player if the game is over
		 * @return true if a player has won the game, false otherwise
		 */
		public boolean hasWon()
		{
			if(this.winner != 0)
			{
				return true;
			}
			int playersCharacter =  (((this.turn + 1) % 2) + 1);
			for(int i = 0; i < this.boardSize1; i++)
			{
				for(int j = 0; j < this.boardSize2; j++)
				{
					if(this.board[i][j] == playersCharacter)
					{
						int k = 1;
						while(i + k < this.boardSize1 && this.board[i + k][j] == playersCharacter)
						{
							if(k == this.inARowToWin - 1)
							{
								this.winner = playersCharacter;
								return true;
							}
							k++;
						}
						k = 1;
						while(j + k < this.boardSize2 && this.board[i][j + k] == playersCharacter)
						{
							if(k == this.inARowToWin - 1)
							{
								this.winner = playersCharacter;
								return true;
							}
							k++;
						}
						k = 1;
						while(i + k < this.boardSize1 && j + k < this.boardSize2 && this.board[i + k][j + k] == playersCharacter)
						{
							if(k == this.inARowToWin - 1)
							{
								this.winner = playersCharacter;
								return true;
							}
							k++;
						}
						k = 1;
						while(i + k < this.boardSize1 && j - k >= 0 && this.board[i + k][j - k] == playersCharacter)
						{
							if(k == this.inARowToWin - 1)
							{
								this.winner = playersCharacter;
								return true;
							}
							k++;
						}
					}
				}
			}
			if(this.turn == this.boardSize1 * this.boardSize2)
			{
				this.winner = -1;
				return true;
			}
			return false;
		}
		
		/**
		 * 	Prints the board to the console
		 */
		public void displayBoard()
		{
			for(int i = 0; i < this.boardSize1; i++)
			{
				for(int j = 0; j < this.boardSize2; j++)
				{
					System.out.print("|" + (char)(-(7*this.board[i][j]) + 95));
				}
				System.out.print("|\n");
			}
		}
		
		
		
		/**
		 * 	Compares two boards to see if they are exactly equal, or a trivial reflection or rotation of one another
		 * @param otherBoardInterface
		 * @return true if they are equal up to symmetry, false otherwise
		 */
		public boolean isEqualUpToSymmetry(BoardInterface otherBoardInterface)
		{
		
			int[][] otherBoard = otherBoardInterface.getBoard();
			
			int otherUpperX = otherBoardInterface.getUpperX();
			int otherLowerX = otherBoardInterface.getLowerX();
			int otherUpperY = otherBoardInterface.getUpperY();
			int otherLowerY = otherBoardInterface.getLowerY();
			
			if(this.upperX - this.lowerX != otherUpperX - otherLowerX || this.upperY - this.lowerY != otherUpperY - otherLowerY)
			{
				return false;
			}
			// Used to test if boards are equal up to symmetries of rectangles
			// Rotations
			boolean syme = true;
			boolean sym90 = true;
			boolean sym180 = true;
			boolean sym270 = true;
			// Flips
			boolean symX = true;
			boolean symY = true;
			boolean symXeqY = true;
			boolean symXeqNegY = true;
			
			
			int maxSize = this.upperX - this.lowerX;
			
			
			if(this.boardSize1 == this.boardSize2)
			{
				for(int i = 0; i <= maxSize; i++)
				{
					for(int j = 0; j <= maxSize; j++)
					{
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[i + otherLowerX][j + otherLowerY])
						{
							syme = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[otherUpperY - j][i + otherLowerX])
						{
							sym90 = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[otherUpperX - i][otherUpperY - j])
						{
							sym180 = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[j + otherLowerY][otherUpperX - i])
						{
							sym270 = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[otherUpperX - i][j + otherLowerY])
						{
							symX = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[i + otherLowerX][otherUpperY - j])
						{
							symY = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[j + otherLowerY][i + otherLowerX])
						{
							symXeqY = false;
						}
						if(this.board[i + this.lowerX][j + this.lowerY] != otherBoard[otherUpperY - j][otherUpperX - i])
						{
							symXeqNegY = false;
						}
						if(!syme && !sym90 && !sym180 && !sym270 && !symX && !symY && !symXeqY && !symXeqNegY)
						{
							
							return false;
						}
					}
				}
			}
			else
			{
				for(int i = 0; i < this.boardSize1; i++)
				{
					for(int j = 0; j < this.boardSize2; j++)
					{
						if(this.board[i][j] != otherBoard[this.boardSize1 - 1 - i][this.boardSize2 - 1 - j])
						{
							sym180 = false;
						}
						if(this.board[i][j] != otherBoard[this.boardSize1 - 1- i][j])
						{
							symX = false;
						}
						if(this.board[i][j] != otherBoard[i][this.boardSize2 - 1 - j])
						{
							symY = false;
						}
						if(!sym180 && !symX && !symY)
						{
							return false;
						}
					}
				}
			}
			return true;
		}
		
		
		
		
		
		
		
		
		
		
		
		public int getBoardSize2() {return boardSize2;}
		public void setBoardSize2(int boardSize2) {this.boardSize2 = boardSize2;}
		
		public int getBoardSize1() {return boardSize1;}
		public void setBoardSize1(int boardSize1) {this.boardSize1 = boardSize1;}
		
		public int[][] getBoard() {return board;}
		public void setBoard(int[][] board) {this.board = board;}
		
		public int getWinner() {return this.winner;}
		public void setWinner(int winner) {this.winner = winner;}

		public int getTurn() {return this.turn;}
		public void setTurn(int turn) {this.turn = turn;}
		
		//public ArrayList<BoardInterface> getChildren() {return this.children;}
		//public void setChildren(ArrayList<BoardInterface> children) {this.children = children;}
		
		public void setPossibleMoves(ArrayList<int[]> possibleMoves) {this.possibleMoves = possibleMoves;}
		public ArrayList<int[]> getPossibleMoves() { return this.possibleMoves;}
		
		public int getInARowToWin() {return this.inARowToWin;}
		public void setInARowToWin(int inARowToWin) {this.inARowToWin = inARowToWin;}
				
		public int getValue() {return this.value;}
		public void setValue(int value) {this.value = value;}

		public int getWhoGoesFirst() {return whoGoesFirst;}
		public void setWhoGoesFirst(int whoGoesFirst) {this.whoGoesFirst = whoGoesFirst;}

		public String getEvaluationType() {return this.evaluationType;}

		public void setEvaluationType(String evaluationType) {
			this.evaluationType = evaluationType;
		}

		

		public int getUpperX() {return this.upperX;}
		public void setUpperX(int upperX) {this.upperX = upperX;}

		public int getLowerX() {return this.lowerX;}
		public void setLowerX(int lowerX) {this.lowerX = lowerX;}

		public int getUpperY() {return this.upperY;}
		public void setUpperY(int upperY) {this.upperY = upperY;}

		public int getLowerY() {return this.lowerY;}
		public void setLowerY(int lowerY) {this.lowerY = lowerY;}





		
}


