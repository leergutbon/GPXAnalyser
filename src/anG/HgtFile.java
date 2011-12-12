/**
 * Opens a hgt file. If there is no hgt file it will be download from
 * www (http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/Eurasia/) and unzip it.
 *
 * File: HgtFile.java           Author: Hermann Sutter
 * Date: 09.11.2011             Version: 0.2
 *
 * History:
 * Version| Author       | Date       | Changes
 * ----------------------------------------------------------------------------
 * 0.1    | H. Sutter    | 26.11.2011 | first version
 *        |              |            | open and reads hgt-file
 * ----------------------------------------------------------------------------
 * 0.2    | H. Sutter    | 03.12.2011 | download and unzip the hgt-files
 * ----------------------------------------------------------------------------
 * 0.3    | H. Sutter    | 11.12.2011 | javaDoc insert
 * ----------------------------------------------------------------------------
 */

package anG;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class HgtFile
{
  private ReadFile readHgtFile;
  private File hgtFile;
  private String fileName;
  
  /**
   * Search for Hgt-File and download one if there is not the right one.
   * Create a object ReadFile, so it can read the 2 byte pares.
   *  
   * @param String filename
   */
  public HgtFile(String fileName)
  {
    this.searchFile(fileName+".hgt");
    this.readHgtFile = new ReadFile(this.hgtFile);
  }

  
  /**
   * Search the hgt-file in hg_files folder, if the file is not found it will be download.
   * 
   * @param name of hgt file
   */
  private void searchFile(String fileName)
  {
    File sFile = new File("hgt_files/");
    File[] fileArray = sFile.listFiles();
    String temp;
    boolean tFlag = false;
    
    /* 
     * if there are no files, begins to search and download the file form
     * the Internet.
     */
    this.fileName = fileName;

    if(fileArray == null)
    {
      downloadFile();
    }
    else
    {
      for(File item: fileArray)
      {
        temp = item.toString().split("/")[1];
        if(temp.equals(fileName))
        {
          this.hgtFile = new File("hgt_files/"+temp);
          tFlag = true;
          break;
        }
      }
    }
    
    if(tFlag == false)
    {
      downloadFile();
    }
  }


  /**
   * Download new data if needed (http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/Eurasia/).
   * With this settings, it can only be used for eurasia. 
   * 
   * @param name of file
   * @post zip file download, unzip and remove zip file
   */
  private void downloadFile()
  {
    URL url;
    HttpURLConnection conn;
    int responseCode, n;
    FileOutputStream os;
    
    try
    {
      /* 
       * opens connection to url
       * url is for eurasia, before change look at the website for other continent
       */
      url = new URL("http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/Eurasia/" + this.fileName + ".zip");
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.connect();
      
      /* path and name of file */
      os = new FileOutputStream("hgt_files/" + this.fileName + ".zip");
      
      /* save the file */
      responseCode = conn.getResponseCode();
      if(responseCode == HttpURLConnection.HTTP_OK)
      { 
        byte tmp_buffer[] = new byte[4096]; 
        InputStream is = conn.getInputStream(); 

        while ((n = is.read(tmp_buffer)) > 0)
        { 
          os.write(tmp_buffer, 0, n); 
          os.flush(); 
        } 
      }
      
      os.close();
    }
    catch(Exception e)
    {
      System.err.println("Error: HgtFile.downloadFile() " + e.getMessage() + " (Exception)");
    }
    
    unzipFile();
  }
  
  
  /**
   * Unzip new hgt file and remove the zip file.
   * 
   * @param file that need to be extracted
   */
  private void unzipFile(){
    try
    {
      BufferedOutputStream dest = null;
      FileInputStream fis = new FileInputStream("hgt_files/"+this.fileName+".zip");
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      
      while((entry = zis.getNextEntry()) != null)
      {
        //System.out.println("Extracting: " +entry);
        int count;
        byte data[] = new byte[2048];
        
        // write the files to the disk
        FileOutputStream fos = new FileOutputStream("hgt_files/"+entry.getName());
        dest = new BufferedOutputStream(fos, 2048);
        while ((count = zis.read(data, 0, 2048)) != -1)
        {
          dest.write(data, 0, count);
        }
       
        dest.flush();
        dest.close();
      }
      zis.close();
    }
    catch(Exception e)
    {
      System.err.println("Error: HgtFile.unzipFile() " + e.getMessage() + " (Exception)");
    }
    
    /* new file assigned to file object hgtFile, for further use */
    this.hgtFile = new File("hgt_files/"+this.fileName);
    
    /* zip file is no longer useful, is deleted */
    new File("hgt_files/"+this.fileName+".zip").delete();
  }


  /**
   * Return name of file as string.
   * 
   * @return filename
   */
  public String getFileName()
  {
    return fileName;
  }

  
  /**
   * Return object ReadFile.
   * 
   * @return object ReadFile
   */
  public ReadFile getHgtFile()
  {
    return this.readHgtFile;
  }
}