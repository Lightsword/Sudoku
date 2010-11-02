//--------------------------------------------------------------------------------
// Suduko Solver Applet
// Copyright (c) 2005 Jay Allison
//
// Purpose - academic exercise
//
// Java documentation:
//   http://java.sun.com/j2se/1.5.0/docs/api/overview-summary.html
//
//--------------------------------------------------------------------------------

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.lang.System;

//--------------------------------------------------------------------------------
// Still TODO:
// - generate a puzzle (using random number generator?)
//--------------------------------------------------------------------------------

//--------------------------------------------------------------------------------
// an applet that will draw a Suduko board,
// allow the user to fill in a Suduko puzzle,
// and offer a solution to the puzzle
//--------------------------------------------------------------------------------
public class Suduko extends Applet implements ActionListener, KeyListener, FocusListener, SolverGui
{
  //
  // member data
  //

  //----------------------------------
  // * update this when needed *
  //     version history:
  //       0.x   - development
  //       1.0   - applet functional, brute force solver operational
  //       1.1   - major GUI: added marking and clearing of errors
  //       1.1a  - GUI: only allow one integer to be entered
  //       1.1b  - GUI: added setSolverGui() to Solver interface
  //       1.1c  - GUI: added more key handling to cell navigation easier
  //       1.2   - Metrics: measure the execution of solve() in ns
  //       1.2a  - Added David Wallace's solver
  //       1.2b  - Added Brent Priddy's solver
  //       1.2c  - Added final competition solvers
  final static String version ="1.2c";
  //----------------------------------

  // graphical member data
  private TextField board[][];
  private TextField msg;
  private TextArea output;
  private Button solveButton;
  private Button makeButton;
  private Button clearButton;
  private Button validButton;
  private Button loadSampleButton;
  private Button loadSolutionButton;
  private Button compareSolutionButton;
  private Label sampleLabel;
  private Choice sampleChoice;
  private Label radioLabel;
  private Checkbox solverRadio[];
  private CheckboxGroup solverRadioGroup;
  private boolean done;

  // variables for blocking non-numeric entries
  final static String badchars = "`~!@#$%^&*()_+=\\|\"':;?/>.<, 0";
  final static String goodchars = "123456789";
  int maxLength;

  // top, left, bottom, right
  Insets topleft;
  Insets top;
  Insets topright;

  Insets left;
  Insets none;
  Insets right;

  Insets bottomleft;
  Insets bottom;
  Insets bottomright;

  private Panel boardPanel;
  private Panel boardHolderPanel;
  //private Panel msgPanel;
  private Panel buttonPanel;
  private Panel samplePanel;
  private Panel algorithmPanel;

	// analytical member data
  private int matrix[][];
  private int matrixCopy[][];
  private int numSamples;

  // algorithm member data
  private Solver solverArray[];
  private int selectedSolver;
  private int numSolvers;
  private long nanos;

  // samples
  private int sample1[][]   = {
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                                {0,0,0,0,0,0,0,0,0},
                              };

  private int solution1[][] = {
                                {1,2,3,4,5,6,7,8,9},
                                {4,5,6,7,8,9,1,2,3},
                                {7,8,9,1,2,3,4,5,6},
                                {2,1,4,3,6,5,8,9,7},
                                {3,6,5,8,9,7,2,1,4},
                                {8,9,7,2,1,4,3,6,5},
                                {5,3,1,6,4,2,9,7,8},
                                {6,4,2,9,7,8,5,3,1},
                                {9,7,8,5,3,1,6,4,2},
                                };

  private int sample2[][]   = {
                                {0,6,0,1,0,4,0,5,0},
                                {0,0,8,3,0,5,6,0,0},
                                {2,0,0,0,0,0,0,0,1},
                                {8,0,0,4,0,7,0,0,6},
                                {0,0,6,0,0,0,3,0,0},
                                {7,0,0,9,0,1,0,0,4},
                                {5,0,0,0,0,0,0,0,2},
                                {0,0,7,2,0,6,9,0,0},
                                {0,4,0,5,0,8,0,7,0},
                              };

  private int solution2[][] = {
                                {9,6,3,1,7,4,2,5,8},
                                {1,7,8,3,2,5,6,4,9},
                                {2,5,4,6,8,9,7,3,1},
                                {8,2,1,4,3,7,5,9,6},
                                {4,9,6,8,5,2,3,1,7},
                                {7,3,5,9,6,1,8,2,4},
                                {5,8,9,7,1,3,4,6,2},
                                {3,1,7,2,4,6,9,8,5},
                                {6,4,2,5,9,8,1,7,3},
                              };

  private int sample3[][]   = {
                                {4,0,0,0,8,0,1,0,0},
                                {1,0,0,0,0,6,7,0,5},
                                {0,0,0,9,0,0,2,0,0},
                                {0,0,9,4,0,5,6,0,2},
                                {0,0,0,0,0,0,0,0,0},
                                {6,0,1,2,0,8,3,0,0},
                                {0,0,5,0,0,9,0,0,0},
                                {8,0,6,1,0,0,0,0,9},
                                {0,0,2,0,6,0,0,0,7},
                              };

  private int solution3[][] = {
                                {4,9,7,5,8,2,1,6,3},
                                {1,2,8,3,4,6,7,9,5},
                                {5,6,3,9,7,1,2,4,8},
                                {7,3,9,4,1,5,6,8,2},
                                {2,8,4,6,3,7,9,5,1},
                                {6,5,1,2,9,8,3,7,4},
                                {3,4,5,7,2,9,8,1,6},
                                {8,7,6,1,5,3,4,2,9},
                                {9,1,2,8,6,4,5,3,7},
                              };

  private int sample4[][]   = {
                                {0,0,0,7,0,0,0,0,0},
                                {0,8,0,0,4,5,0,0,0},
                                {0,4,7,8,0,0,1,9,0},
                                {3,0,0,4,0,0,7,0,0},
                                {7,0,0,0,0,0,0,0,1},
                                {0,0,5,0,0,7,0,0,2},
                                {0,9,6,0,0,3,2,8,0},
                                {0,0,0,2,8,0,0,7,0},
                                {0,0,0,0,0,6,0,0,0},
                              };

  private int solution4[][] = {
                                {6,3,2,7,1,9,8,5,4},
                                {9,8,1,6,4,5,3,2,7},
                                {5,4,7,8,3,2,1,9,6},
                                {3,2,9,4,5,1,7,6,8},
                                {7,6,4,9,2,8,5,3,1},
                                {8,1,5,3,6,7,9,4,2},
                                {4,9,6,1,7,3,2,8,5},
                                {1,5,3,2,8,4,6,7,9},
                                {2,7,8,5,9,6,4,1,3},
                              };

  //
  // member methods
  //

  //--------------------------------------------------------------------------------
  // Applet init - like the constructor
  // mainly concerned with laying out the GUI
	//--------------------------------------------------------------------------------
	public void init()
	{
    done = true;
    maxLength = 1;

    setBackground(Color.GRAY);
    setLayout(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0,0,0,0);

    // set quadrant spacing
    int boardInset = 2;
    int buttonInset = 5;

    // build the applet out of smaller panels,
    // which are then built out of the components we need
    boardPanel       = new Panel(new GridBagLayout());
    boardHolderPanel = new Panel(new GridBagLayout());
    //msgPanel         = new Panel(new GridBagLayout());
    buttonPanel      = new Panel(new GridBagLayout());
    samplePanel      = new Panel(new GridBagLayout());
    algorithmPanel   = new Panel(new GridBagLayout());

    boardPanel.setBackground(Color.BLACK);
    boardHolderPanel.setBackground(Color.LIGHT_GRAY);
    //msgPanel.setBackground(Color.LIGHT_GRAY);
    buttonPanel.setBackground(Color.LIGHT_GRAY);
    samplePanel.setBackground(Color.LIGHT_GRAY);
    algorithmPanel.setBackground(Color.LIGHT_GRAY);

    c.fill = GridBagConstraints.NONE;
    c.insets = new Insets(buttonInset, buttonInset, buttonInset, buttonInset);
    boardHolderPanel.add(boardPanel, c);

    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(0,0,0,0);
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    add(boardHolderPanel, c);

    //c.gridy++;
    //add(msgPanel, c);

    c.gridwidth = 1;
    c.gridy++;
    c.insets = new Insets(0,0,buttonInset,0);
    add(buttonPanel, c);

    c.gridx++;
    c.insets = new Insets(buttonInset,buttonInset,buttonInset,0);
    add(samplePanel, c);

    c.gridwidth = 2;
    c.gridx=0;
    c.gridy++;
    c.insets = new Insets(0,0,0,0);
    add(algorithmPanel, c);

    c.gridwidth = 1;

    // allocate the physical and virtual boards
    //  NOTE: when drawn, first index is row & second index is column
    board = new TextField[9][9];
		matrix = new int[9][9];
    matrixCopy = new int[9][9];

    // initialize the matrix (virtual board)
    for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
        matrix[i][j] = 0;
        matrixCopy[i][j] = 0;
			}
		}

    int i = 0, j = 0;
    int h, v;
    //int height = 5, width = 5;
    c.fill = GridBagConstraints.NONE;

    // build the graphical board
    for (i = 0; i < 9; i++)
		{
      for (j = 0; j < 9; j++)
			{
        h = i % 3;
        v = j % 3;

        board[i][j] = new TextField("");
        board[i][j].addKeyListener(this);
        board[i][j].addFocusListener(this);
        board[i][j].setBackground(Color.WHITE);
        //board[j][i].setSize(width, height);
        c.gridx = j;
        c.gridy = i;

        if ((i == 0) || (i == 8))
        {
          if ((j == 0) || (j == 8))
          {
            setInsets(2*boardInset, 2*boardInset);
          }
          else
          {
            setInsets(boardInset, 2*boardInset);
          }
        }
        else if ((j == 0) || (j == 8))
        {
          setInsets(2*boardInset, boardInset);
        }
        else
        {
          setInsets(boardInset, boardInset);
        }

        if ((h==0) && (v==0)) c.insets = topleft;
        if ((h==0) && (v==1)) c.insets = top;
        if ((h==0) && (v==2)) c.insets = topright;
        if ((h==1) && (v==0)) c.insets = left;
        if ((h==1) && (v==1)) c.insets = none;
        if ((h==1) && (v==2)) c.insets = right;
        if ((h==2) && (v==0)) c.insets = bottomleft;
        if ((h==2) && (v==1)) c.insets = bottom;
        if ((h==2) && (v==2)) c.insets = bottomright;

        boardPanel.add(board[i][j], c);
			}
		}

    msg = new TextField("");
    msg.setEditable(false);
    msg.setBackground(Color.WHITE);
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(boardInset, 0, boardInset, 0);
    c.gridx = 0;
    c.gridy++;
    c.gridwidth=2;
    //msgPanel.add(msg, c);
    add(msg, c);

    output = new TextArea(10,40);
    output.setEditable(false);
    output.setBackground(Color.WHITE);
    c.insets = new Insets(boardInset, 0, boardInset, 0);
    c.gridy++;
    //msgPanel.add(output, c);
    add(output, c);

    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth=1;
    // top, left, bottom, right
    c.insets = new Insets(buttonInset*2, buttonInset*2, buttonInset, buttonInset*2);

    // add buttons
		solveButton = new Button("Solve");
    c.gridy++;
    buttonPanel.add(solveButton, c);

    c.insets = new Insets(buttonInset, buttonInset*2, buttonInset, buttonInset*2);
    makeButton = new Button("Generate");
    makeButton.setEnabled(false);
    c.gridy++;
    buttonPanel.add(makeButton, c);

    clearButton = new Button("Clear");
    c.gridy++;
    buttonPanel.add(clearButton, c);

    c.insets = new Insets(buttonInset, buttonInset*2, buttonInset*2, buttonInset*2);
    validButton = new Button("Validate");
    c.gridy++;
    buttonPanel.add(validButton, c);

    // register the applet as a listener with the buttons
    solveButton.addActionListener(this);
    makeButton.addActionListener(this);
    clearButton.addActionListener(this);
    validButton.addActionListener(this);

    c.fill = GridBagConstraints.HORIZONTAL;
    // top, left, bottom, right
    c.insets = new Insets(buttonInset*2, buttonInset*2, buttonInset, 0);

    // add our sample-loading buttons
    sampleLabel = new Label("Sample:");
    ++c.gridy;
    c.gridwidth = 1;
    samplePanel.add(sampleLabel, c);

    sampleChoice = new Choice();
    ++c.gridx;
    c.insets = new Insets(buttonInset*2, 0, buttonInset, buttonInset*2);
    samplePanel.add(sampleChoice, c);

    numSamples = 4;
    for (i = 0; i < numSamples; i++)
    {
      sampleChoice.add(Integer.toString(i+1));
    }

    sampleChoice.select(0);

    loadSampleButton = new Button("Load Sample");
    ++c.gridx;
    //++c.gridy;
    c.gridwidth = 1;
    //c.insets = new Insets(buttonInset, buttonInset*2, buttonInset, buttonInset*2);
    samplePanel.add(loadSampleButton, c);
    loadSampleButton.addActionListener(this);

    loadSolutionButton = new Button("Load Solution");
    ++c.gridy;
    //c.insets = new Insets(buttonInset, buttonInset*2, buttonInset, buttonInset*2);
    samplePanel.add(loadSolutionButton, c);
    loadSolutionButton.addActionListener(this);

    compareSolutionButton = new Button("Compare to Solution");
    ++c.gridy;
    //c.insets = new Insets(buttonInset, buttonInset*2, buttonInset*2, buttonInset*2);
    samplePanel.add(compareSolutionButton, c);
    compareSolutionButton.addActionListener(this);

    // title for the solver section
    radioLabel = new Label("Choose a Solver algorithm:");
    c.insets = new Insets(0, 0, 0, 0);
    c.gridx = 0;
    ++c.gridy;
    c.gridwidth = 3;
    c.insets = none;
    algorithmPanel.add(radioLabel, c);

    selectedSolver = 0;

    //*****************************
    // ADD YOUR Solver Here -
    //  1. increment numSolvers
    //  2. instantiate your Solver as the least entry in the array
    //*****************************
    numSolvers = 4;

    // Jay Allison
    solverArray = new Solver[numSolvers];
    solverArray[0] = new JSolver();
    solverArray[0].setSolverGui(this);

    // David Wallace
    solverArray[1] = new DaveSudukoSolver();
    solverArray[1].setSolverGui(this);

    // Brent Priddy
    solverArray[2] = new PriddyGoodSolver();
    solverArray[2].setSolverGui(this);

    // Andrew Langefeld
    solverArray[3] = new AplSolver();
    solverArray[3].setSolverGui(this);
    
    // Nick Robinson
    
    //*****************************

    // display all of our solver algorithms
    solverRadio = new Checkbox[numSolvers];
    solverRadioGroup = new CheckboxGroup();
    boolean oneTime = true;             // flag to select/check the first solver by default
    for (i = 0; i < numSolvers; i++)
    {
      solverRadio[i] = new Checkbox(solverArray[i].getName() + " (" + solverArray[i].getVersion() + ") ", solverRadioGroup, oneTime);
      ++c.gridy;
      algorithmPanel.add(solverRadio[i], c);
      oneTime = false;
    }

    clearBoard();
	}

	//--------------------------------------------------------------------------------
  // ActionListener interface - event handler
	//--------------------------------------------------------------------------------
	public void actionPerformed(ActionEvent evt)
	{
    if (done)
    {
      done = false;
      // attempt to solve the suduko puzzle
      if (evt.getSource() == solveButton)
      {
        solve();
      }
      // write a puzzle to the board at the user's request
      else if (evt.getSource() == makeButton)
      {
        makeNewPuzzle();
      }
      // clear the board at the user's request
      else if (evt.getSource() == clearButton)
      {
        clearBoard();
      }
      // validate the board at the user's request
      else if (evt.getSource() == validButton)
      {
        validateBoard();
      }
      // load the specified sample
      else if (evt.getSource() == loadSampleButton)
      {
          int i = sampleChoice.getSelectedIndex();
          loadSample(i+1, false);
      }
      // load the specified sample solution
      else if (evt.getSource() == loadSolutionButton)
      {
          int i = sampleChoice.getSelectedIndex();
          loadSample(i+1, true);
      }
      // compare the specified sample solution
      else if (evt.getSource() == compareSolutionButton)
      {
          int i = sampleChoice.getSelectedIndex();
          compareSample(i+1);
      }

      done = true;
    }
	}

	//--------------------------------------------------------------------------------
  // private function - do everything needed to solve the puzzle
	//--------------------------------------------------------------------------------
	private void solve()
	{
    display("Solving...");

		// step 1 - read the user's input
    clearErrors();
    readBoard();

    if (validatePuzzle())
    {
      // step 2 - calculate the solution
      copyMatrix();
      if(calculate())
      {
        // step 3 - write the solution out
        writeBoard();
        if (verifyMatrix())
        {
          if (validateEntireBoard(false))
          {
            displayAppended(" Done - valid solution found in " + buildNanoString(nanos) + " ns.");
            clearErrors();
          }
          else
          {
            displayAppended(" Done with errors - solution is not valid!");
            markErrors(false);
          }
        }
        else
        {
          displayAppended(" Done with errors - solver illegally modified initial puzzle!");
          markErrors(false);
        }
      }
      else
      {
        displayAppended(" Done with errors - solver failed to find a solution!");
        markErrors(false);
      }
    }
    else
    {
      displayAppended(" Cannot solve - puzzle is invalid!");
      markErrors(true);
    }
	}

	//--------------------------------------------------------------------------------
  // private function - make a new puzzle for the user to solve
	//--------------------------------------------------------------------------------
  private void makeNewPuzzle()
  {
    clearBoard();
    display("Unable to generate a puzzle - not implemented!");
  }

	//--------------------------------------------------------------------------------
  // private function - read in the board and store it in the matrix
	//--------------------------------------------------------------------------------
	private void readBoard()
	{
    //display("Reading board...");
    //clearErrors();

		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				// for each square, convert the string into an integer
        try
        {
          matrix[i][j] = Integer.parseInt(board[i][j].getText());
        }
        catch (NumberFormatException e)
        {
          matrix[i][j] = 0;
        }

        if ((matrix[i][j] < 0) || (matrix[i][j] > 9))
        {
          matrix[i][j] = 0;
        }

			}
		}

    //dumpMatrix();

    // get rid of any invalid characters and numbers
    writeBoard();
	}

	//--------------------------------------------------------------------------------
  // private function - calculate the solution
	//--------------------------------------------------------------------------------
  private boolean calculate()
	{
    //display("Calculating solution...");

    boolean rv = false;

    Checkbox selected = solverRadioGroup.getSelectedCheckbox();

    for (int i = 0; i < numSolvers; i++)
    {
      if(solverRadio[i] == selected)
      {
        nanos = 0;
        long nanoStart = System.nanoTime();
        rv = solverArray[i].solve(matrix);
        nanos = System.nanoTime() - nanoStart;

//        if (!solverArray[i].solve(matrix))
//        {
//          displayAppended(" Unable to find solution!");
//        }

        announce(solverArray[i].getMessage());
      }
    }

    return rv;
	}

	//--------------------------------------------------------------------------------
  // private function - write out the matrix to the board
	//--------------------------------------------------------------------------------
	private void writeBoard()
	{
    //display("Writing board...");
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				// for each square, convert the integer into a string
        if (matrix[i][j] != 0)
        {
          board[i][j].setText(Integer.toString(matrix[i][j]));
        }
        else
        {
          board[i][j].setText("");
        }
			}
		}
	}

	//--------------------------------------------------------------------------------
  // private function - write out a value in the matrix to the board
	//--------------------------------------------------------------------------------
  private void writeCell(int i, int j)
	{
    //display("Writing cell...");
    if (matrix[i][j] != 0)
    {
      board[i][j].setText(Integer.toString(matrix[i][j]));
    }
    else
    {
      board[i][j].setText("");
    }
	}

	//--------------------------------------------------------------------------------
  // private function - clear the board
	//--------------------------------------------------------------------------------
  private void clearBoard()
  {
    display("Clearing...");
    output.setText("");
    clearErrors();
    nanos = 0;

    for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				// for each square, convert the integer into a string
        board[i][j].setText("");
        matrix[i][j] = 0;
        matrixCopy[i][j] = 0;
			}
		}

    board[0][0].requestFocus();
    display("Soduko Applet v" + version + " - Ready...");
  }

	//--------------------------------------------------------------------------------
  // private function - validate a given cell:
  //   verify that the value is between 1 & 9
  //   and that the value is not repeated in the column, row, or quadrant
	//--------------------------------------------------------------------------------
  private boolean validateCell(int x, int y, boolean allowZero)
  {
    int x_start = 0;
    int y_start = 0;

    // determine which quadrant we are in horizontally
    switch(x)
    {
      case 0 :
      case 1 :
      case 2 :
        x_start = 0;
        break;
      case 3 :
      case 4 :
      case 5 :
        x_start = 3;
        break;
      case 6 :
      case 7 :
      case 8 :
        x_start = 6;
        break;
    }

    // determine which quadrant we are in vertically
    switch(y)
    {
      case 0 :
      case 1 :
      case 2 :
        y_start = 0;
        break;
      case 3 :
      case 4 :
      case 5 :
        y_start = 3;
        break;
      case 6 :
      case 7 :
      case 8 :
        y_start = 6;
        break;
    }

    // validate the value
    if ((matrix[x][y] < 1) || (matrix[x][y] > 9))
    {
      if ((matrix[x][y] != 0) || ((matrix[x][y] == 0) && (!allowZero)))
      return false;
    }

    // validate the row - fix x, walk y, compare to the rest
    for (int x1 = 0; x1 < 9; x1++)
    {
      if ((matrix[x][y] == matrix[x1][y]) && (x != x1) && (matrix[x][y] != 0))
      {
        return false;
      }
    }

    // validate the column - fix y, walk x, compare to the rest
    for (int y1 = 0; y1 < 9; y1++)
    {
      if ((matrix[x][y] == matrix[x][y1]) && (y != y1) && (matrix[x][y] != 0))
      {
        return false;
      }
    }

    // validate the quadrant
    for (int x1 = x_start; x1 < (x_start + 3); x1++)
    {
      for (int y1 = y_start; y1 < (y_start + 3); y1++)
      {
        if ((matrix[x][y] == matrix[x1][y1]) && (y != y1) && (x != x1) && (matrix[x][y] != 0))
        {
          return false;
        }
      }
    }

    // must be valid if the column, row, and quadrant did not fail
    return true;
  }

	//--------------------------------------------------------------------------------
  // private function to validate the board:
  //  validate each cell
  //--------------------------------------------------------------------------------
  private boolean validateEntireBoard(boolean allowZero)
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (!validateCell(i,j, allowZero))
        {
          // the board is invalid if any cells are invalid
          return false;
        }
      }
    }

    // all of the cells are valid - the board must be valid
    return true;
  }

	//--------------------------------------------------------------------------------
  // private function to validate the board for the user
  //--------------------------------------------------------------------------------
  private void validateBoard()
  {
    display("Validating...");

    clearErrors();
    readBoard();

    if (validateEntireBoard(false))
    {
      displayAppended(" Board is valid.");
    }
    else
    {
      markErrors(true);
      displayAppended(" Board is invalid!");
    }
  }

	//--------------------------------------------------------------------------------
  // private function to validate the puzzle before attempting to solve it
  //--------------------------------------------------------------------------------
  private boolean validatePuzzle()
  {
     return validateEntireBoard(true);
  }

	//--------------------------------------------------------------------------------
  // private function to load a given sample or its solution
  //--------------------------------------------------------------------------------
  private void loadSample(int sample, boolean loadSolution)
  {
    display("Loading Sample " + Integer.toString(sample) + "...");
    clearErrors();

    switch (sample)
    {
      case 1 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (loadSolution) matrix[i][j] = solution1[i][j];
            else              matrix[i][j] = sample1[i][j];
          }
        }
        break;
      case 2 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (loadSolution) matrix[i][j] = solution2[i][j];
            else              matrix[i][j] = sample2[i][j];
          }
        }
        break;
      case 3 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (loadSolution) matrix[i][j] = solution3[i][j];
            else              matrix[i][j] = sample3[i][j];
          }
        }
        break;
      case 4 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (loadSolution) matrix[i][j] = solution4[i][j];
            else              matrix[i][j] = sample4[i][j];
          }
        }
        break;
    }

    writeBoard();

    displayAppended(" Done.");
  }

	//--------------------------------------------------------------------------------
  // private function to compare the board to a given solution
  //--------------------------------------------------------------------------------
  private void compareSample(int sample)
  {
    display("Comparing board to Sample " + Integer.toString(sample) + "...");

    boolean equal = true;

    clearErrors();
    readBoard();

    switch (sample)
    {
      case 1 :
        // sample 1 does not have a unique solution
        // in fact, any valid solution is also valid as the solution to sample 1
        equal = validateEntireBoard(false);
        break;
      case 2 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (matrix[i][j] != solution2[i][j])
            {
              equal = false;
              board[i][j].setBackground(Color.RED);
            }
          }
        }
        break;
      case 3 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (matrix[i][j] != solution3[i][j])
            {
              equal = false;
              board[i][j].setBackground(Color.RED);
            }
          }
        }
        break;
      case 4 :
        for (int i = 0; i < 9; i++)
        {
          for (int j = 0; j < 9; j++)
          {
            if (matrix[i][j] != solution4[i][j])
            {
              equal = false;
              board[i][j].setBackground(Color.RED);
            }
          }
        }
        break;
    }

    if (equal)
    {
      if (sample == 1)
      {
        displayAppended(" Board is a valid solution.");
      }
      else
      {
        displayAppended(" Board matches Solution.");
      }
    }
    else
    {
      displayAppended(" Board does not match Solution.");
    }
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void setInsets(int h, int v)
  {
    // top, left, bottom, right
    topleft  = new Insets(v, h, 0, 0);
    top      = new Insets(v, 0, 0, 0);
    topright = new Insets(v, 0, 0, h);

    left  = new Insets(0, h, 0, 0);
    none  = new Insets(0, 0, 0, 0);
    right = new Insets(0, 0, 0, h);

    bottomleft  = new Insets(0, h, v, 0);
    bottom      = new Insets(0, 0, v, 0);
    bottomright = new Insets(0, 0, v, h);
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void draw()
  {
    clearErrors();
    writeBoard();
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void draw(int row, int column)
  {
    writeCell(row, column);
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void announce(String s)
  {
    output.append(s);
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void dumpMatrix()
  {
    announce("\r\n");
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        announce(Integer.toString(matrix[i][j]) + " ");
      }
      announce("\r\n");
    }
    announce("\r\n");
  }

  //--------------------------------------------------------------------------------
  // make a copy of the matrix so that we can later check to see if the solver
  //  modified any cells it should not have
  //--------------------------------------------------------------------------------
  private void copyMatrix()
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        matrixCopy[i][j] = matrix[i][j];
      }
    }
  }

  //--------------------------------------------------------------------------------
  // check to see if the solver modified any cells it should not have
  //--------------------------------------------------------------------------------
  private boolean verifyMatrix()
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if ((matrixCopy[i][j] != 0) && (matrixCopy[i][j] != matrix[i][j]))
        {
          return false;
        }
      }
    }

    return true;
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void markErrors(boolean allowZero)
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if ( ((matrixCopy[i][j] != 0) && (matrixCopy[i][j] != matrix[i][j])) || (!validateCell(i,j,allowZero)) )
        {
          board[i][j].setBackground(Color.RED);
        }
      }
    }
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void clearErrors()
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        board[i][j].setBackground(Color.WHITE);
      }
    }
    //msg.setText("clearErrors()");
    //announce("clearErrors()\r\n");
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void display(String s)
  {
    msg.setText(s);
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private void displayAppended(String s)
  {
    msg.setText(msg.getText() + s);
  }

  //--------------------------------------------------------------------------------
  // Method to prevent the entry of non-numeric values
  //  this event covers most characters
  //  specifically - block everything but 1-9, jump to the next cell on a 1-9 being entered
  //--------------------------------------------------------------------------------
  public void keyTyped(KeyEvent ev)
  {
    char c = ev.getKeyChar();

//    announce("KeyTyped: " + c + "\r\n");

    if((Character.isLetter(c) && !ev.isAltDown()) || (badchars.indexOf(c) > -1))
    {
      ev.consume();
      return;
    }

    TextField src = null;
    int row = 0, column = 0;

    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (board[i][j] == ev.getSource())
        {
          src = board[i][j];
          row = i;
          column = j;
        }
      }
    }

    // if it is a numeric digit
    boolean moveOn = false;
    if(goodchars.indexOf(c) > -1)
    {
      moveOn = true;
      // if the length of the cell is too long
      if (src.getText().length() >= maxLength)
      {
        // if there is no text selected
        if (src.getSelectionEnd() <= src.getSelectionStart())
        {
          ev.consume();
          moveOn = false;
        }
      }
    }

    // if key is valid, then jump to the next key
    if (moveOn)
    {
      column++;
      if (column > 8)
      {
        column = 0;
        row++;
      }
      if (row > 8)
      {
        row = 0;
      }
      board[row][column].requestFocus();
//      announce("Requesting focus for " + Integer.toString(row) + ":" + Integer.toString(column) + "\r\n");
    }

  }

  //--------------------------------------------------------------------------------
  // Method to prevent the entry of non-numeric values
  //   this event covers non-character keys
  //--------------------------------------------------------------------------------
  public void keyPressed(KeyEvent ev)
  {
    char c = ev.getKeyChar();

//    announce("KeyPressed: " + c + "\r\n");

    if((Character.isLetter(c) && !ev.isAltDown()) || (badchars.indexOf(c) > -1))
    {
      ev.consume();
      return;
    }

    TextField src = null;
    int row = 0, column = 0;

    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (board[i][j] == ev.getSource())
        {
          src = board[i][j];
          row = i;
          column = j;
        }
      }
    }

    // JAY - don't assume that src, row, and column are always valid!

    // if it is a numeric digit
    boolean moveOn = false;
    if(goodchars.indexOf(c) > -1)
    {
      moveOn = true;
      // if the length of the cell is too long
      if (src.getText().length() >= maxLength)
      {
        // if there is no text selected
        if (src.getSelectionEnd() <= src.getSelectionStart())
        {
          ev.consume();
          moveOn = false;
        }
      }
    }

  }

  //--------------------------------------------------------------------------------
  // Method to prevent the entry of non-numeric values
  //   this event covers non-character keys
  //   specifically - handle arrow keys for navigation through board
  //--------------------------------------------------------------------------------
  public void keyReleased(KeyEvent ev)
  {
    char c = ev.getKeyChar();

//    announce("KeyReleased: " + c + "\r\n");

    if((Character.isLetter(c) && !ev.isAltDown()) || (badchars.indexOf(c) > -1))
    {
      ev.consume();
      return;
    }

    TextField src = null;
    int row = 0, column = 0;

    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (board[i][j] == ev.getSource())
        {
          src = board[i][j];
          row = i;
          column = j;
        }
      }
    }

    // JAY - don't assume that src, row, and column are always valid!

    // if it is a numeric digit
    boolean moveOn = false;
    if(goodchars.indexOf(c) > -1)
    {
      moveOn = true;
      // if the length of the cell is too long
      if (src.getText().length() >= maxLength)
      {
        // if there is no text selected
        if (src.getSelectionEnd() <= src.getSelectionStart())
        {
          ev.consume();
          moveOn = false;
        }
      }
    }
    else
    {
      // if key is an arrow, then do its action
      int code = ev.getKeyCode();
      switch (code)
      {
        case KeyEvent.VK_UP :
        case KeyEvent.VK_KP_UP :
          row--;
          if (row < 0)
          {
            row = 8;
          }
          // for up, stay in the same column when wrapping
          board[row][column].requestFocus();
          break;

        case KeyEvent.VK_DOWN :
        case KeyEvent.VK_KP_DOWN :
          row++;
          if (row > 8)
          {
            row = 0;
          }
          // for down, stay in the same column when wrapping
          board[row][column].requestFocus();
          break;

        case KeyEvent.VK_LEFT :
        case KeyEvent.VK_KP_LEFT :
          column--;
          if (column < 0)
          {
            column = 8;
            row--;
          }
          if (row < 0)
          {
            row = 8;
          }
          // for left, jump up a row when wrapping
          board[row][column].requestFocus();
          break;

        case KeyEvent.VK_RIGHT :
        case KeyEvent.VK_KP_RIGHT :
          column++;
          if (column > 8)
          {
            column = 0;
            row++;
          }
          if (row > 8)
          {
            row = 0;
          }
          // for right, jump down a row when wrapping
          board[row][column].requestFocus();
          break;
      }
    }
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void focusGained(FocusEvent ev)
  {
    TextField src = null;
    int row = 0, column = 0;

    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (board[i][j] == ev.getSource())
        {
          src = board[i][j];
          row = i;
          column = j;
        }
      }
    }

    // any time we enter the cell, we need to select all of the text
    // so it is easier to overwrite
    src.selectAll();
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void focusLost(FocusEvent ev)
  {
    readBoard();

    TextField src = null;
    int row = 0, column = 0;

    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (board[i][j] == ev.getSource())
        {
          src = board[i][j];
          row = i;
          column = j;
        }
      }
    }

    // if the value is invalid, jump back to the cell and mark it
    if (validateCell(row,column,true))
    {
      board[row][column].setBackground(Color.WHITE);
      //announce("Cleared error\r\n");
    }
    else
    {
      //ev.consume();
      //board[row][column].requestFocus();
      board[row][column].setBackground(Color.YELLOW);
      //announce("Marked error\r\n");
    }
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  private String buildNanoString(long n)
  {
    String result = new String("");
    long r;
    boolean finished = false;

    while(!finished)
    {

      if (result.length() > 0)
      {
        result = "," + result;
      }

      // first digit
      r = n % 10;
      n = n / 10;
      result = Long.toString(r) + result;

      if (n == 0)
      {
        finished = true;
      }

      if (!finished)
      {
        // second digit
        r = n % 10;
        n = n / 10;
        result = Long.toString(r) + result;

        if (n == 0)
        {
          finished = true;
        }

        if (!finished)
        {
          // third digit
          r = n % 10;
          n = n / 10;
          result = Long.toString(r) + result;

          if (n == 0)
          {
            finished = true;
          }
        }
      }
    };

    return result;
  }

}  // end applet suduko
