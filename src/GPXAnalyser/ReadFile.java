/**
 * Opens a file with only read rights. read the file and returns a string our
 * short.
 *
 * File: ReadFile.java           Author: Hermann Sutter
 * Date: 09.11.2011              Version: 0.3
 *
 * History:
 * Version| Author       | Date       | Changes
 * ----------------------------------------------------------------------------
 * 0.1    | H. Sutter    | 26.11.2011 | first version
 *        |              |            | open and reads file
 * ----------------------------------------------------------------------------
 * 0.2    | H. Sutter    | 03.12.2011 | Reads short of file
 * ----------------------------------------------------------------------------
 * 0.3    | H.Sutter     | 11.12.2011 | javaDoc completed
 * ----------------------------------------------------------------------------
 */

package GPXAnalyser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


public class ReadFile
{
  private RandomAccessFile raf;
  private String strLine;
  
  /**
   * Opens a File with the file name.
   * 
   * @param String with file path
   */
  public ReadFile(String filePath)
  {
	  try
	  {
	    /* Open file */
  	  this.raf = new RandomAccessFile(filePath, "r");
	  }
	  catch(Exception e)
	  {
	    System.err.print("Error: ReadFile(String filePath) Constructor " + e.getMessage() + " (Exception).");
	  }
	}
	
  
  /**
   * Opens a file with File object.
   * 
   * @param File Object
   */
  public ReadFile(File filePath)
  {
    try
    {
      /* Open file */
      this.raf = new RandomAccessFile(filePath, "r");
    }
    catch(Exception e)
    {
      System.err.print("Error: ReadFile(File filePath) Constructor " + e.getMessage() + " (Exception");
    }
  }
	
  
	/**
	 * Save next line in strLine, if file is eof save null strLine and returns the string.
	 * 
	 * @pre valid ReadFile object
	 * @return value of strLine
	 */
	public String nextLine()
	{
	  try
	  {
	    this.strLine = raf.readLine();
	  }
	  catch(IOException e)
	  {
	    System.err.print("Error: ReadFile.nextLine " + e.getMessage() + " (IOException)");
	  }
	  return this.strLine;
	}
	
	/**
	 * Returns last read String of File.
	 * 
	 * @return strLine
	 */
	public String getStrLine()
	{
	  return this.strLine;
	}
	
	/**
	 * Return the file object of type RandomAccessFile.
	 * 
	 * @return raf
	 */
	public RandomAccessFile getFile()
	{
	  return this.raf;
	}
	
	
	/**
	 * This method set
	 */
	public void setFile(RandomAccessFile file){
	  this.raf = file;
	}
	/**
	 * Close the Hgt-File.
	 * 
	 * @pre raf != null
	 * @post raf == null
	 */
	public void fileClose()
	{
	  try
	  {
	    this.raf.close();
	  }
	  catch(IOException e)
	  {
	     System.out.println("Error: ReadFile.fileClose() "+e.getMessage()+" (IOException)");
	  }
	}
	
	/**
	 * Returns the a short value with specific position in file.
	 * 
	 * @param Position of short
	 * @return Altitude in short format
	 */
	public short getShort(int position)
	{
	  short temp = 0;
	  
	  try
	  {
	    this.raf.seek(position);
	    temp = this.raf.readShort();
    }
	  catch (IOException e)
	  {
      System.out.println("Error: ReadFile.getShort() "+e.getMessage()+" (IOException)");
    }
	  
	  return temp;
	}
}
