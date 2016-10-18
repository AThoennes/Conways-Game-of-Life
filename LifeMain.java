import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Conway's Game of Life
 * 
 * @author Alex Thoennes
 * 
 * Rules:
 * 1) Any live cell with 2 or 3 live neighbors styas alive. (stable)
 * 2) Any live cell with more than 3 or less than 2 live neighbors dies. (overpopulation/ underpopulation)
 * 3) Any dead cell with exactly 3 live neighbors comes alive. (repopulation)
 * 
 * This version utilizes the dead border approach.
 * 
 */
public class LifeMain 
{
	public static void main(String[] args) throws IOException 
	{
		// create new game of life witht eh number of rows and 
		// columns that were read in from the file
		GameOfLife life = new GameOfLife(rows(), columns());
		
		// pass in the number of generations you want to run
		life.playGame(10);
	}

	/**
	 * Counts the number of rows in the compact textFile
	 * to be used in the creation of the game boards.
	 * 
	 * @return rows
	 * @throws IOException
	 */
	private static int rows() throws IOException
	{
		int rows = 0;
		
		BufferedReader buffer = new BufferedReader(new FileReader("Game Board"));
		
		while (buffer.ready())
		{
			// read the next line and add 1 to rows
			buffer.readLine();
			rows ++;
		}
		
		// close the buffer
		buffer.close();
		
		return rows;
	}
	
	/**
	 * Counts the number of elements in one line and then 
	 * multiplies that number by 4
	 * 
	 * @return columns
	 * @throws IOException
	 */
	private static int columns() throws IOException
	{
		int columns = 0;
		
		BufferedReader buffer = new BufferedReader(new FileReader("Game Board"));
		
		if (buffer.ready())
		{
			// splite the line that was read into a string array with the delimiter ""
			String [] array = buffer.readLine().split("");
			
			// for every element in the array
			for (String string: array) 
			{
				columns ++;
			}
		}
		
		// close the buffer
		buffer.close();
		
		// multiply by 4 because every hex value on one line
		// represents 4 numbers in binary
		return columns * 4;
	}
}
