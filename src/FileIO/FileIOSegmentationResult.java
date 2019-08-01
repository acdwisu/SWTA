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

import Model.SegmentationResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class FileIOSegmentationResult {
    private final String rootPath = "results/segmentation/";
    
    public static final int SPECIFIER_NAMA_SEGMENTASI = 1;
    public static final int SPECIFIER_NAMA_PELATIHAN = 2;
    public static final int SPECIFIER_NAMA_DATASET = 3;
    
    public void insert(SegmentationResult sr) {
        String dest = rootPath.concat("info/"+ sr.getSegmentationName().concat(".info"));
        
        try {
            FileWriter writer = new FileWriter(new File(dest));
            
            String line = sr.getSegmentationName()+ "," +sr.getTrainName()+ "," +sr.getDatasetName();
            
            writer.write(line);
            
            writer.flush();
            writer.close();
            
            new File(rootPath.concat("image/" +sr.getSegmentationName())).mkdirs();
            new File(rootPath.concat("psps/" +sr.getSegmentationName())).mkdirs();
            
            File overSegmented = new File("results/oversegmented/" +sr.getDatasetName());
            if(!overSegmented.exists())
                overSegmented.mkdirs();
        } catch (IOException ex) {
            Logger.getLogger(FileIOSegmentationResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<SegmentationResult> get(String specifier, int specifierMode) {
        List<SegmentationResult> result = new LinkedList<>();
        
        if(specifierMode == SPECIFIER_NAMA_SEGMENTASI) {
            for(SegmentationResult tr : get()) {
                if(tr.getSegmentationName().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        } else if(specifierMode == SPECIFIER_NAMA_DATASET) {
            for(SegmentationResult tr : get()) {
                if(tr.getDatasetName().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        } else if(specifierMode == SPECIFIER_NAMA_PELATIHAN) {
            for(SegmentationResult tr : get()) {
                if(tr.getTrainName().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        }
        
        return result;
    }
    
    public List<SegmentationResult> get() {
        List<SegmentationResult> results = new LinkedList<>();                
        
        for(File file : new File(this.rootPath+"info/").listFiles()) {
            if(file.getName().toLowerCase().endsWith(".info")) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    
                    String[] line = reader.readLine().split(",");                    
                    
                    results.add(new SegmentationResult(line[0], line[1], line[2]));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return results;
    }
    
    public HashMap<String, String> retrieveImages(String segmentationName) {
        HashMap<String, String> pathImagesPair = new HashMap<>();
        
        String parentFolder = this.rootPath+ "images/" +segmentationName;
        
        for(File file : new File(parentFolder).listFiles()) {
            String fileName = file.getName().toLowerCase();
            
            if(fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || 
                    fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                pathImagesPair.put(fileName.split("\\.")[0], parentFolder+"/"+file.getName());
            }
        }
        
        return pathImagesPair;
    }
    
    public HashMap<String, String> retrieveOversegmented(String datasetName) {
        HashMap<String, String> pathImagesPair = new HashMap<>();
        
        String parentFolder = "results/oversegmented/" +datasetName;
        
        for(File file : new File(parentFolder).listFiles()) {
            String fileName = file.getName().toLowerCase();
            
            if(fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || 
                    fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                pathImagesPair.put(fileName.split("\\.")[0], parentFolder+"/"+file.getName());
            }
        }
        
        return pathImagesPair;
    }
    
    public HashMap<String, String> retrievePSPs(String segmentationName) {
        HashMap<String, String> pathPspsPair = new HashMap<>();
        
        String parentFolder = this.rootPath+ "psps/" +segmentationName;
        
        for(File file : new File(parentFolder).listFiles()) {
            String fileName = file.getName().toLowerCase();
            
            if(fileName.endsWith(".areas")) {
                pathPspsPair.put(fileName.split("\\.")[0], parentFolder+"/"+file.getName());
            }
        }
        
        return pathPspsPair;
    }

    public String getRootPath() {
        return rootPath;
    }
}
