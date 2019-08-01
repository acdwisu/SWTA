/*
 * Copyright (C) 2018 acdwisu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package FileIO;

import Model.OverallResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class FileIOOverallResult {
    private final String rootPath = "results/overall/";
    
    public void insert(OverallResult or) {
        String dest = rootPath.concat(or.getSegmentationName().concat(".info"));
        
        try {
            FileWriter writer = new FileWriter(new File(dest));
            
            String line = or.getSegmentationName()+","+or.getTotalPspsAcuan()+","+or.getTotalPsps()+","+or.getCorrectPsps()
                    +","+or.getErrorPsps()+","+or.getPercentage();
            
            writer.write(line);
            
            writer.flush();
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FileIOSegmentationResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public OverallResult get(String segmentationName) {
        OverallResult result = null;
                
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(this.rootPath.concat(segmentationName+".info"))));

            String[] line = reader.readLine().split(",");                    

            result = new OverallResult(line[0], Integer.parseInt(line[1]), Integer.parseInt(line[2]), 
                    Integer.parseInt(line[3]), Integer.parseInt(line[4]));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }
}
