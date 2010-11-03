//--------------------------------------------------------------------------------
// Suduko Solver Applet Interface
// Copyright (c) 2005 Jay Allison
//
// Purpose - academic exercise
//
//--------------------------------------------------------------------------------

public interface Solver
{
  public abstract boolean solve(int matrix[][]);
  public abstract String getName();
  public abstract String getVersion();
  public abstract String getMessage();
  public abstract void setSolverGui(SolverGui g);
}