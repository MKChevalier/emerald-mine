import java.io.IOException;

/**
 * PRA2003: Assignment 3 sample solution.
 * Author: Cameron Browne (based on code from previous years).
 */

/**
 * Run test suite.
 * @author cambolbro
 */
public class Tests 
{
	/**
	 * Test loading the specified file.
	 */
    public static void testLoadFile(final String fileName) 
    {
        try 
        {
            System.out.println("Testing file: " + fileName + "...");
            World world = new World(fileName);
            System.out.println("Successfully loaded world:\n" + world);
        }
        catch (BadFileFormatException e)
        {
            System.out.println(e);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        System.out.println();
    }

    /**
     * Test loading all test files.
     */
    public static void testLoadFiles() 
    {
    	final String[] fileNames = 
    	{
    		"example.txt", "bad-file1.txt", "bad-file2.txt", "bad-file3.txt",
    		               "bad-file4.txt", "bad-file5.txt", "bad-file6.txt",	
    	};   	
    	for (String fileName : fileNames)
    		Tests.testLoadFile(fileName);
     }
}
