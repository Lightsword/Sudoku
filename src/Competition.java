//--------------------------------------------------------------------------------
//
//--------------------------------------------------------------------------------

//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
public class Competition
{
  static Judge judge;

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public static void main(String args[])
  {
    judge = new Judge();

    System.out.println("");
    System.out.println("Suduko Competition Judge v" + judge.getVersion());

    judge.judgeCompetition();

    System.out.println("");
    System.out.println("Done.");
    System.out.println("");
  }
}

//--------------------------------------------------------------------------------
//--------------------------------------------------------------------------------
class Judge implements SolverGui
{
  //---------------------------------
  // Version History:
  // 0.1 - development
  //---------------------------------
  final static String version ="0.1";

  // analytical member data
  private int matrix[][];
  private int matrixCopy[][];
  private int numSamples;

  // algorithm member data
  private Solver solverArray[];
  private int selectedSolver;
  private int numSolvers;
  private long results[][];
  private long min[][];

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

	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
  public void judgeCompetition()
  {
    // allocate storage for puzzles
    matrix = new int[9][9];
    matrixCopy = new int[9][9];

    numSamples = 4;

    // instantiate solvers
    numSolvers = 5;
    solverArray = new Solver[numSolvers];
    int index = 0;
    solverArray[index++] = new JSolver();
    solverArray[index++] = new DaveSudukoSolver();
    solverArray[index++] = new PriddyGoodSolver();
    solverArray[index++] = new AplSolver();
    solverArray[index++] = new NRSolver();

    // allocate storage for results and initialize it
    min = new long[numSolvers][numSamples];
    results = new long[numSolvers][numSamples];
    for (int i = 0; i < numSolvers; i++)
    {
      for (int j = 0; j < numSamples; j++)
      {
        results[i][j] = 0;
      }
    }

    // run each solver through all of the puzzles
    for (index = 0; index < numSolvers; index++)
    {
      solverArray[index].setSolverGui(this);
      judgeSolver(index);
    }

    // display the results
    displayResults();

    // compare the results and pick a winner
    pickWinner();
  }

	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
  private void judgeSolver(int index)
  {
    int numTries = 1000;
    int dotAfter = 100;
    long measurements[] = new long[numTries];
    long nanos = 0, nanoStart = 0;
    boolean rv = true;
    Solver solver = solverArray[index];

    System.out.println("");
    System.out.println("Judging " + solver.getName() + "...");

    for (int i = 0; i < numSamples; i++)
    {
      System.out.print("  Sample " + Integer.toString(i+1));
      for (int j = 0; j < numTries; j++)
      {
        measurements[j] = 0;
      }

      for (int j = 0; j < numTries; j++)
      {
        if ((j % dotAfter) == 0)
        {
          System.out.print(".");
        }

        // set up the puzzle
        loadSample(i, false);
        copyMatrix();

        // solve the puzzle
        nanoStart = System.nanoTime();
        rv = solver.solve(matrix);
        nanos = System.nanoTime() - nanoStart;

        // verify the solution
        if (rv)
        {
          if (!verifyPuzzle())
          {
            System.out.println("ERROR! Solver illegally modified puzzle.");
            rv = false;
            break;
          }
          if (!validateMatrix(false))
          {
            System.out.println("ERROR! Solver did not find a valid solution.");
            rv = false;
            break;
          }
        }
        else
        {
          System.out.println("ERROR! Solver did not find a solution.");
        }

        // store the result
        if (rv)
        {
          measurements[j] = nanos;
        }
        else
        {
          measurements[j] = 0;
        }
      }
      System.out.println("");

      // store the result
      if (rv)
      {
        long average = 0, last = 0;
        min[index][i] = 0x7FFFFFFF;
        for(int k = 0; k < numTries; k++)
        {
          average += measurements[k];
          if (last > average)
          {
            System.out.println("ERROR! Overflow occured when calculating average.");
          }
          last = average;
          if(min[index][i] > measurements[k])
            min[index][i] = measurements[k];
        }
        average = average / numTries;
        results[index][i] = average;
      }
      else
      {
        results[index][i] = 0;
      }
    }
  }

	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
  private void displayResults()
  {
    System.out.println("");
    System.out.println("Results:");
    System.out.println("");
    System.out.format("%-30s", "Solver ");

    for (int i = 0; i < numSamples; i++)
    {
      System.out.format("%12s", "Sample " + Integer.toString(i+1) + " ");
    }
    System.out.println("");

    System.out.format("%-30s", "------ ");

    for (int i = 0; i < numSamples; i++)
    {
      System.out.format("%12s", "-------- ");
    }
    System.out.println("");

    for (int i = 0; i < numSolvers; i++)
    {
      System.out.format("%-30s", solverArray[i].getName() + " ");
      for (int j = 0; j < numSamples; j++)
      {
        System.out.format("%12s", buildNanoString(results[i][j]) + " ");
      }
      System.out.println("");
    }

    System.out.println("");
    System.out.println("Results (min):");
    System.out.println("");
    System.out.format("%-30s", "Solver ");

    for (int i = 0; i < numSamples; i++)
    {
      System.out.format("%12s", "Sample " + Integer.toString(i+1) + " ");
    }
    System.out.println("");

    System.out.format("%-30s", "------ ");

    for (int i = 0; i < numSamples; i++)
    {
      System.out.format("%12s", "-------- ");
    }
    System.out.println("");

    for (int i = 0; i < numSolvers; i++)
    {
      System.out.format("%-30s", solverArray[i].getName() + " ");
      for (int j = 0; j < numSamples; j++)
      {
        System.out.format("%12s", buildNanoString(min[i][j]) + " ");
      }
      System.out.println("");
    }

  }

	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
  private void pickWinner()
  {
  }

	//--------------------------------------------------------------------------------
	//--------------------------------------------------------------------------------
  public String getVersion()
  {
    return version;
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
  // private function to validate the matrix:
  //  validate each cell
  //--------------------------------------------------------------------------------
  private boolean validateMatrix(boolean allowZero)
  {
    for (int i = 0; i < 9; i++)
    {
      for (int j = 0; j < 9; j++)
      {
        if (!validateCell(i,j, allowZero))
        {
          // the matrix is invalid if any cells are invalid
          return false;
        }
      }
    }

    // all of the cells are valid - the matrix must be valid
    return true;
  }

	//--------------------------------------------------------------------------------
  // private function to load a given sample or its solution
  //--------------------------------------------------------------------------------
  private void loadSample(int sample, boolean loadSolution)
  {
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
  private boolean verifyPuzzle()
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

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void draw()
  {
    // do nothing
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void draw(int row, int column)
  {
    // do nothing
  }

  //--------------------------------------------------------------------------------
  //--------------------------------------------------------------------------------
  public void announce(String msg)
  {
    System.out.println(msg);
  }
}
