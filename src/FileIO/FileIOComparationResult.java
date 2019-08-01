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

import Model.ComparationResult;
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
public class FileIOComparationResult {
    private final String rootPath = "results/comparation/";
    
    public void create(String segmentationName) {
        new File(this.rootPath.concat(segmentationName)).mkdirs();
    }
    
    public void insert(ComparationResult cr) {
        String dest = rootPath.concat(cr.getSegmentationName()+ "/" +cr.getDataName().concat(".comparation"));
        
        try {
            FileWriter writer = new FileWriter(new File(dest));
            
            String line = cr.getDataName()+","+cr.getSegmentationName()+","+cr.getPspsAcuan()+","+cr.getPspsTerdeteksi()+","
                    +cr.getPspsBenar()+","+cr.getPspsSalah()+","+cr.getPspsOver()+","+cr.getPspsMiss()+","+cr.getPspsBad()+","
                    +cr.getPathWordAsli()+","+cr.getPathWordAcuan()+","+cr.getPathWordOverSegmented()+","+cr.getPathWordHasil();

            writer.write(line);
            
            writer.flush();
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FileIOSegmentationResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<ComparationResult> get(String segmentationName) {
        List<ComparationResult> results = new LinkedList<>();                
        
        for(File file : new File(this.rootPath+segmentationName).listFiles()) {
            if(file.getName().toLowerCase().endsWith(".comparation")) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    
                    String[] line = reader.readLine().split(",");                    
                    
                    results.add(new ComparationResult(line[0], line[1], Integer.parseInt(line[2]), Integer.parseInt(line[3]),
                            Integer.parseInt(line[4]), Integer.parseInt(line[5]), Integer.parseInt(line[6]), 
                            Integer.parseInt(line[7]), Integer.parseInt(line[8]), line[9], line[10], line[11], line[12]));                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileIOComparationResult.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return results;
    }
}
