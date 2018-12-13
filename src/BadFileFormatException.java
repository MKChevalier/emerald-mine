/**
 * PRA2003, Emerald Mine solution.
 * Author: Cameron Browne (based on code from previous years).
  */
public class BadFileFormatException extends Exception 
{
    private String errorMsg;
    private int row, col;

    /**
     * Constructor.
     */
    public BadFileFormatException(String errorMsg, int row, int col) 
    {
        this.errorMsg = errorMsg;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() 
    {
        String str = "Error: " + errorMsg;

        if (row >= 0)
            str += (", row: " + row);

        if (col >= 0)
            str += (", col:" + col);

        return str;
    }
}

