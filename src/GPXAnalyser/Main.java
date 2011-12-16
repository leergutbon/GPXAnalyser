/* Opens a hgt file. If there is no hgt file it will be downloaded from
 * www (http://dds.cr.usgs.gov/srtm/version2_1/SRTM3/Eurasia/) and unzip it.
 *
 * File: Main.java              Author: Hermann Sutter
 * Date: 09.11.2011             Version: 0.2
 *
 * History:
 * Version| Author       | Date       | Changes
 * ----------------------------------------------------------------------------
 * ----------------------------------------------------------------------------
 */

package GPXAnalyser;

public class Main {
  public static void main(String args[]){
    ReadFile file;
    AnalyseGPX coordinates;
    
    /* scan for input file */
    file = new ReadFile(args[0]);
    coordinates = new AnalyseGPX();
    coordinates.analyseCoordinates(file);
    
    file.fileClose();
    
    //Gui fenster = new Gui();
  }/* end main(String args[]) */
}
