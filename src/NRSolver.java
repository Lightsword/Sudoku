
public class NRSolver implements Solver{

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
		//Place Holder will implement later
		return false;
	}
	
	public void setSolverGui(SolverGui g)
	{
		//do nothing
	}
	
	private final String name = "Nick's Solver";
	private final String version = "0.1";
	private final String msg = "All Done!!";
}
