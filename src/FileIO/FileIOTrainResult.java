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

import Model.TrainResult;
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
public class FileIOTrainResult {
    
    private final String rootPath = "knowledge/";
    
    public static final int SPECIFIER_EKSTRAKSI = 1;
    public static final int SPECIFIER_NAMA_PELATIHAN = 2;
    public static final int SPECIFIER_NAMA_DATASET = 3;
    
    public void insert(TrainResult tr) {
        String dest = rootPath.concat(tr.getNamaPelatihan().concat(".info"));
        
        try {
            FileWriter writer = new FileWriter(new File(dest));
            
            String line = tr.getNamaPelatihan()+ "," +tr.getNamaDataset()+ "," +tr.getEkstraksi()+ "," 
                    +tr.getParameterEkstraksi()+ "," +tr.getParameterRF()+ "," +tr.getAkurasiKarakter()+ ","
                    +tr.getAkurasiSA()+ "," +tr.getDurasiKarakter()+ "," +tr.getDurasiSA();
            
            writer.write(line);
            
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<TrainResult> get(String specifier, int specifierMode) {
        List<TrainResult> result = new LinkedList<>();
        
        if(specifierMode == SPECIFIER_NAMA_PELATIHAN) {
            for(TrainResult tr : get()) {
                if(tr.getNamaPelatihan().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        } else if(specifierMode == SPECIFIER_NAMA_DATASET) {
            for(TrainResult tr : get()) {
                if(tr.getNamaDataset().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        } else if(specifierMode == SPECIFIER_EKSTRAKSI) {
            for(TrainResult tr : get()) {
                if(tr.getEkstraksi().equalsIgnoreCase(specifier)) {
                    result.add(tr);
                }
            }
        }
        
        return result;
    }
    
    public List<TrainResult> get() {
        List<TrainResult> result = new LinkedList<>();
        
        for(File file : new File(this.rootPath).listFiles()) {
            if(file.getName().toLowerCase().endsWith(".info")) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    
                    String[] line = reader.readLine().split(",");                    
                    
                    result.add(new TrainResult(line[0], line[1], line[2], line[3], line[4], 
                            line[5], line[6], line[7], line[8]));
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(FileIOTrainResult.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return result;
    }
}
