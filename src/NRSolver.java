import java.util.*;



public class NRSolver implements Solver{
	
	public enum Value {
	    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

	    public String toString() {
	      return Integer.toString(ordinal() + 1);
	    }
	  }

	public String getName()
	{
		return name;
	}
	
	public String getVersion()
	{
		return version;
	}
	
	public String getMessage()
	{
		return msg;
	}
	
	public boolean solve(int matrix[][])
	{
		int col = 0;
		int row = 0;
		int pos = 0;
		boolean done = false;
		Value[] all = Value.values();
		
		
		while(!done)
		{
			done = true;
			
			for(row = 0; row < numRows; row++)
			{
				for(col = 0; col < numCols; col++)
				{
					pos = (row*9) + col;
					
					if(matrix[row][col] != 0)
					{
						possibleNumbers.put(pos, EnumSet.of(all[matrix[row][col] - 1]));
					}
				}
			}
		}
		
		//Place Holder will implement later
		return false;
	}
	
	public void setSolverGui(SolverGui g)
	{
		g.draw();
	}
	
	private final String name = "Nick's Solver";
	private final String version = "0.1";
	private final String msg = "All Done!!";
	private final int numRows = 9;
	private final int numCols = 9;
	private final Map<Integer, EnumSet<Value>> possibleNumbers = new HashMap<Integer, EnumSet<Value>>();
}
