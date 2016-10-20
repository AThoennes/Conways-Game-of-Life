import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is where the process of running through
 * generations is done as well as reading the 
 * graph in from compact form and displaying it.
 * Compact form is a binary matrix converted to
 * hexadecimal values so it is easier to type
 * and read. 
 * 
 * The patter tested in this program is the beacon
 * pattern which is written in compact form as:
 * 
 * ...
 * 00000000
 * 00000000
 * 00030000
 * 00030000
 * 0000C000
 * 0000C000
 * 00000000
 * 00000000
 * ...
 * 
 * @author Alex Thoennes
 */
public class GameOfLife 
{
	// these are the two game boards the program will use to create each other
	// I look at one board and use those cells to create the next board
	int [][] board1;
	int [][] board2;

	// number of generations to perform
	int generations;

	// booleans used to help determine which board to modify and which board to use
	private boolean b1; // board1
	private boolean b2; // board2

	/**
	 * Constructor for the game of life that initializes 
	 * the game boards, booleans, generation counter,
	 * and calls the setUp() method.
	 * 
	 * @param rows
	 * @param columns
	 * @throws IOException
	 */
	public GameOfLife(int rows, int columns) throws IOException
	{
		// fill boards with proper amount of rows and columns
		// in this case, they happen to both be 32 X 32 arrays
		board1 = new int [rows][columns];
		board2 = new int [rows][columns];

		// we want to start at board1
		b1 = true;
		b2 = false;

		// generation counter
		generations = 0;

		// fills the board
		setUp();
	}

	/**
	 * This method looks at the text file and 
	 * 
	 * @throws IOException
	 */
	private void setUp() throws IOException
	{
		BufferedReader buffer = new BufferedReader(new FileReader("Game Board"));

		// pre-read the 0th line because it will always be dead
		buffer.readLine();

		// start at the first line because the 0th line will always be dead
		int lineNum = 1;

		while (buffer.ready())
		{
			// keep reading until you hit the last line
			// because that too will always be dead
			if (lineNum < 31)
			{
				// take the line you just read and split it up into an array
				String [] array = buffer.readLine().split("");

				// then take each element in that array and convert it from
				// a string to a hex, then finally to a binary string. You do this
				// because when youfirst read somethinig in, it is a string. 
				for (int i = 0; i < array.length; i ++)
				{
					// convert to hex
					int hex = Integer.parseInt(array[i], 16);
					// convert to binary string
					String binary = Integer.toBinaryString(hex );

					// because some 0's may be lost when converting, this bit of code
					// adds the 0's that were lost back in
					if (binary.length() < 4)
					{
						// find the difference
						int diff = 4 - binary.length();

						// then add that number of 0's to the front of the string
						for (int q = 0; q < diff; q ++)
						{
							binary = "0" + binary;
						}
					}

					// now take the binary string and split it up into an array
					String [] bin = binary.split("");

					// index counter for array bin
					int index = 0;

					// because each hex value is equal to 4 binary digits,
					// you want to start at the last position relative to the ith
					// location of the array

					/* Ex. array = [0,3,0,0,0,0,0,0]
					 *               0 1 2 3 4 5 6 7 
					 * 
					 *  The element at array[0] when converted to binary is 0000 so
					 *  the first four spaces on the line in the matrix will be 0000.
					 *  
					 *  The element at array[1] when converted to binary is 0011 so
					 *  the next four spaces on the same line will be 0011. 
					 *  
					 *  Therefore,the first eight spaces on this line are 00000011.
					 */
					for (int j = i * 4; j < i * 4 + 4; j ++)
					{
						board1[lineNum][j] = Integer.valueOf(bin[index]);

						// increment the counter
						index ++;
					}
				}

				// mark that you have read this line
				lineNum++;
			}
			else
			{
				// exit if there are no more lines to read
				break;
			}
		}

		// close the buffer
		buffer.close();
	}

	/**
	 * Called by the main class because it doe not have access to
	 * wither of the game boards.
	 * 
	 * @param iterations
	 */
	public void playGame(int iterations)
	{
		// iterations is the number of times you want to perform the run method
		// and here we atart on board1
		run(iterations, board1);
	}

	/**
	 * This is where the main process of determining whether 
	 * a cell lives or not is done.
	 * 
	 * @param iterations
	 * @param array
	 */
	private void run(int iterations, int [][] array)
	{
		// Print the initial board
		System.out.println("\nGeneration " + generations);
		displayBoard(array);
		displayCompactBoard(array);

		// perform the game until the number of desired generations has been reached
		while (generations < iterations)
		{
			// for every cell
			for (int i = 0; i < currentBoard().length; i ++)
			{
				for (int j = 0; j < currentBoard().length; j ++)
				{
					// reinforces the idea of a dead border (no cells can live on the border, 
					// the other option is to wrap the matrix like a map in a civ game)
					if (atBorder(currentBoard(), i, j))
					{
						// nextBoard() and currentBoard() are dependent on the booleans b1 and b2
						nextBoard()[i][j] = 0;
					}
					else
					{
						// if you've found a live cell
						if (alive(currentBoard(), i, j))
						{
							// check to see if the cell has 2 or 3 live neighbors
							if (checkNeighbors(currentBoard(), i, j) == 2 || checkNeighbors(currentBoard(), i, j) == 3)
							{
								// stay alive if it does
								nextBoard()[i][j] = 1;
							}
							else
							{
								// otherwise it dies
								nextBoard()[i][j] = 0;
							}
						}
						// if you found a dead cell
						else if (!alive(currentBoard(), i, j))
						{
							// count the number of live neighbors
							if (checkNeighbors(currentBoard(), i, j) == 3)
							{
								// if the number is exactly 3, then the current cell
								// comes to life
								nextBoard()[i][j] = 1;
							}
						}
					}
				}
			}

			// display the board you have just created
			System.out.println("\nGeneration: " + generations);
			displayBoard(nextBoard());
			displayCompactBoard(nextBoard());

			if (b1)
			{
				// switch to board two and move on to
				// the next generation
				b1 = false;
				b2 = true;
				generations ++;
			}
			else if (b2)
			{
				// otherwise switch to board 1 and 
				// move on to the next generation
				b1 = true;
				b2 = false;

				generations ++;
			}
		}
	}

	/**
	 * Looks at the two boolean values and determines 
	 * which board is the current board
	 * 
	 * @return
	 */
	private int [][] currentBoard()
	{
		if (b1)
		{
			// on board1
			return board1;
		}
		else
		{
			// on board2
			return board2;
		}
	}

	/**
	 * Returns the board that you want to modify based
	 * off the cells in your current board
	 * 
	 * @return
	 */
	private int [][] nextBoard()
	{
		// if you're on board1
		// you want to modify board2
		if (b1)
		{
			return board2;
		}
		else
		{
			// otherwise modify board1
			return board1;
		}
	}

	/**
	 *  This method looks at the cells around your current cell
	 *  to determine how many live neighbors it has
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return numAlive
	 */
	private int checkNeighbors(int [][] array, int i , int j)
	{
		// number of live neighboring cells
		int numAlive = 0;

		// use ternarys and set the min/maxRow and min/maxCol
		// to the proper values only if they are not in the dead border
		int minRow = (i != 0) ? i - 1 : 0;
		int maxRow = (i != array.length - 1) ? i + 1: 0;

		int minCol = (j != 0) ? j - 1 : 0;
		int maxCol = (j != array.length - 1) ? j + 1: 0;

		// iterates over the neghiboring cells and counts how many are alive
		for (int row = minRow; row <= maxRow; row++) 
		{
			for (int column = minCol; column <= maxCol; column++) 
			{
				if (array[row][column] == 1 && !(row == i && column == j)) 
				{
					numAlive++;
				}
			}
		}

		// finally return the number of live cells
		return numAlive;
	}

	/**
	 * Used to check if the current cell is at the border
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean atBorder(int [][] array, int i, int j)
	{
		// check if the cell is at the border
		if (i == array.length - 1 || i == 0 || j == 0 || j == array.length - 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Check if the current cell is alive or dead
	 * 
	 * @param array
	 * @param i
	 * @param j
	 * @return
	 */
	private boolean alive(int [][] array, int i, int j)
	{
		// current cell 
		if (array[i][j] == 0)
		{
			return false;
		}
		else 
		{
			return true;
		}
	}

	/**
	 * Displays the matrix with 0's turned to .'s
	 * and 1's turned to *'s
	 * 
	 * @param array
	 */
	private void displayBoard(int [][] array)
	{
		for (int i = 0; i < array.length; i ++)
		{
			for (int j = 0; j < array.length; j ++)
			{
				if (array[i][j] == 0)
				{
					System.out.print(".");
				}
				else
				{
					System.out.print("*");
				}
			}
			System.out.println();
		}
	}

	/**
	 * This method looks at the binary matrix and for every 4
	 * elements it reads in, it is then converted to hexadecimal
	 * and printed to provide a more compact form of output
	 * 
	 * @param array
	 */
	private void displayCompactBoard(int [][] array)
	{
		// num will contain the four elements
		String num = "";

		// for every space in board
		for (int row = 0; row < array.length; row ++)
		{
			for (int column = 0; column < array.length; column ++)
			{
				// add the next element
				num = num + array[row][column];
				
				// if your length is 4
				if (num.length() == 4)
				{
					// convert it to hexadecimal
					int binary = Integer.parseInt(String.valueOf(num), 2);
					String hex = Integer.toHexString(binary);

					// print out the hex value
					System.out.print(hex);

					// then reset num
					num = "";
				}
			}
			System.out.println();
		}
	}
}
