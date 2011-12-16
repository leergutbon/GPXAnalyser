/**
 * Opens a hgt file. If there is no hgt file it will be downloaded from
 * this url http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/Eurasia/ and
 * unzip it.
 *
 * File: Analyse.java           Author: Hermann Sutter
 * Date: 09.11.2011             Version: 0.2
 *
 * History:
 * Version| Author       | Date       | Changes
 * ----------------------------------------------------------------------------
 * 0.1    | H. Sutter    | 26.11.2011 | first version
 *        |              |            | writes latitude and longitude in
 *        |              |            | arrayList
 * ----------------------------------------------------------------------------
 * 0.2    | H. Sutter    | 03.12.2011 | insert altitude in arrayList;
 *        |              |            | bug in calculate the position of short
 *        |              |            | in hgt-file
 * ----------------------------------------------------------------------------
 */

package GPXAnalyser;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AnalyseGPX
{
  private final double NORTH = 0.0;
  private final double EAST = 1.0;
  private final double SOUTH = 2.0;
  private final double WEST = 3.0;
  private final int OFFSET = 1201;
  
  private ArrayList<double[]> coordinates;
  
  
  /**
   * Standard constructor
   */
  public AnalyseGPX(){}
  
  
  /**
   * Search for latitude and longitude and save them in the ArrayList.
   * Every element of the arrayList is double array with two elements.
   * Latitude: -180.0 (E) <= value < 180.0 (E)
   * Longitude: -90.0 (S) <= value <= 90.0 (N)
   *  
   * @param object ReadFile, with buffered contend of file 
   * @return arrayList with all coordinates of the way points
   */
  public void analyseCoordinates(ReadFile file)
  {
    String strLine;
    String temp[];
    double lonLatAlt[] = new double[7];

    /* arrayList for save the coordinates */
    this.coordinates = new ArrayList<double[]>();
    
    while((strLine = file.nextLine()) != null)
    {
      /* search only track points */
      if(strLine.indexOf("trkpt") != -1){
        /* split string, element 1 and 3 are coordinates */
        temp = strLine.split("\"");
        
        /* 
         * gpx standard have no geographic direction
         * it use for latitude -90 to 90 degree and for longitude -180 to 180 degree
         * convert to geographic direction with north, south, west and east direction
         * this need to be used for the hgt files
         * 
         * latitude second element
         */
        if(Double.parseDouble(temp[1])<0)
        {
          lonLatAlt[0] = SOUTH;
          lonLatAlt[1] = Double.parseDouble(temp[1]) * -1;
        }
        else
        {
          lonLatAlt[0] = NORTH;
          lonLatAlt[1] = Double.parseDouble(temp[1]);
        }
        
        /* longitude third element */
        if(Double.parseDouble(temp[3])<0)
        {
          lonLatAlt[2] = WEST;
          lonLatAlt[3] = Double.parseDouble(temp[3]) * -1;
        }
        else
        {
          lonLatAlt[2] = EAST;
          lonLatAlt[3] = Double.parseDouble(temp[3]);
        }

        /* write double array in arrayList */
        coordinates.add(lonLatAlt);
        
        /* important, array can add new values in list */
        lonLatAlt = new double[7];
      }
    }

    /*file.fileClose();
    int i;
    for(i=0; i<coordinates.size(); i++){
      System.out.println("lat: "+coordinates.get(i)[1]+" lon: "+coordinates.get(i)[3]);
    }*/
    
    this.setAltitude();
  }/* end searchCoordinates() */

  
  /**
   * Searches height data in file.
   * If the file with this data is unavailable, it will be downloaded.
   * 
   * @pre arrayList coordinates have no altitude
   * @post arrayList coordinates have altitude from gpx-files
   */
  private void setAltitude()
  {
    ArrayList<double []> tempCoordinates = new ArrayList<double[]>();
    double coordinate[];
    int i, lat, lon, pos;
    short altitude = 0;
    HgtFile hgtFile = null;
    
    String formatCoordinate = null;
    DecimalFormat format = new DecimalFormat();
    format.setMinimumIntegerDigits(3);

    /* 
     * get every coordinate and write high data in double array
     * the new double array would be written back in arrayList
     */
    System.out.println("Elements: "+coordinates.size()+"\n");
    
    for(i=0; i<coordinates.size(); i++)
    {
      try
      {
        coordinate = this.coordinates.get(i);

        /* makes a string of coordinates, need to find the hgt-file */
        if(coordinate[0] == NORTH)
        {
          formatCoordinate = "N";
        }
        else
        {
          formatCoordinate = "S";
        }
        formatCoordinate += String.valueOf(Double.valueOf(coordinate[1]).intValue());
        if(coordinate[2] == EAST)
        {
          formatCoordinate += "E";
        }
        else
        {
          formatCoordinate += "W";
        }
        formatCoordinate += format.format(Double.valueOf(coordinate[3]).intValue());
        
        /* 
         * if no file is open, then it will be open,
         * else it will be checked if the right file is still open then nothing will be done.
         * When the the wrong file is open, then it will be opened.
         */
        if(hgtFile == null)
        {
          hgtFile = new HgtFile(formatCoordinate);
          //hgtFile = new HgtFile("N50E009");
        }
        else if(!hgtFile.getFileName().equals(formatCoordinate+".hgt"))
        {
          hgtFile = new HgtFile(formatCoordinate);
          //hgtFile = new HgtFile("N50E009");
        }
        
        /* calculate the coordinate for the altitude, in hgt file 
         * -1 because the first element is 0
         */
        lat = Math.round(Math.round(((coordinate[1] - Double.valueOf(coordinate[1]).intValue()) * OFFSET)));
        lon = Math.round(Math.round(((coordinate[3] - Double.valueOf(coordinate[3]).intValue()) * OFFSET)));
        //lat = Math.round(Math.round(0.498056 * OFFSET));
        //lon = Math.round(Math.round(0.937778 * OFFSET));
        /* calculate the position of altitude in hgt-file */ 
        pos = ((lat * 1201) + lon) * 2;
        
        /* gets the latitude for the coordinate */
        altitude = hgtFile.getHgtFile().getShort(pos);
        
        /* debug information */
        System.out.printf("%s%f  %s%f \t x_lat: %d y_lon: %d pos: %d \t alt: %d\t\t%d;%d\t%s\n"
                          , coordinate[0]==NORTH ? "N" : "S", coordinate[1]
                          , coordinate[2]==EAST ? "E" : "W", coordinate[3], lat, lon, pos, altitude
                          , lat*1202*2, lon*2, hgtFile.getFileName());
        
        /* saves the new coordinate in temp arraylist, then it can check the next coordinate */
        tempCoordinates.add(i, coordinate);
        coordinate = null;
      }
      catch(IndexOutOfBoundsException e)
      {
        System.out.println("Error: Analyse.setAltitude() " + e.getMessage() + " (IndexOutOfBoundsException).");
      }
      catch(NullPointerException e)
      {
        System.out.println("Error: Analyse.setAltitude() " + e.getMessage() + " (NullPointerException).");
      }
    }
    
    /* arrayList gets the new coordinates */
    this.coordinates = tempCoordinates;
    hgtFile.getHgtFile().fileClose();
  }

  
  /**
   * Returns the double array from ArrayList from specific position in list.
   * 
   * @param index
   * @return double array with coordinates and latitude
   */
  public double[] getCoordinates(int index)
  {
    return coordinates.get(index);
  }
}