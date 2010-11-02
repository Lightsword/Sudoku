//--------------------------------------------------------------------------------
// Suduko Solver Applet Interface
// Copyright (c) 2005 Jay Allison
//
// Purpose - academic exercise
//
//--------------------------------------------------------------------------------

public interface SolverGui
{
  public void draw();                    // allows the solver to draw the board at will
  public void draw(int row, int column); // allows the solver to draw a cell on the board at will
  public void announce(String msg);      // allows the solver to output text to the user
}
