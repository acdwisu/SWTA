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

import Model.DataTrain;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class FileIOFeatures {
    
    private final String dirRoot;
    
    private final String selectedDataset;
    
    private final String selectedExtractionMethod;
    
    private final String appliedParameters;
    
    private ArrayList<String> featuresRecorded;

    private boolean recordFeatures;
    
    private String fileNameAddition;
    
    /**
     * 
     * @param selectedDataset
     * @param selectedExtractionMethod
     * @param appliedParameters 
     */
    public FileIOFeatures(String selectedDataset, String selectedExtractionMethod, String appliedParameters) {
        this.selectedDataset = selectedDataset;
        this.selectedExtractionMethod = selectedExtractionMethod;
        this.appliedParameters = appliedParameters;
        
        this.dirRoot = "features/".concat(this.selectedDataset);
        
        this.recordFeatures = false;
        this.fileNameAddition = "";
    }
    
    public boolean isFeatureEverExtracted() {
        File file = new File(this.dirRoot.concat("/" +selectedExtractionMethod).concat("_"+appliedParameters).
                concat("_"+this.fileNameAddition+ ".features"));

        return file.exists();
    }
    
    /**
     * 
     * @param data
     * @param features 
     */
    public void storeFeature(DataTrain data, double[] features) {
        String dataPath = "datasets/train/".concat(this.selectedDataset+ "/" +
                data.getKategori()+ "/" +data.getLabel()+ (data.getKelas().equals("") ? "" : "/" +data.getKelas()))
                + "/" +data.getNama().split("\\.")[0].concat("_" +this.selectedExtractionMethod
                        .concat("_" +this.appliedParameters)+ ".feature");
        
        File destFile = new File(dataPath);
        
        try {
            FileWriter writer = new FileWriter(destFile);
            
            String line = "";
            
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(12);
            df.setGroupingUsed(false);
            
            for(double feature : features) {
                line += df.format(feature)+ ",";
            }
            
            line += data.getKategori()+"_" +data.getLabel()+ "-" +data.getKelas();
            
            writer.write(line);
            
            writer.flush();
            writer.close();
            
            if(this.recordFeatures) this.featuresRecorded.add(line);
        } catch (IOException ex) {
            Logger.getLogger(FileIOFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public void mergeStoredFeatures() {
        File root = new File(dirRoot);
        if(!root.exists()) root.mkdir();
        
        File file = new File(this.dirRoot.concat("/" +selectedExtractionMethod+"_").
                concat(appliedParameters+"_"+this.fileNameAddition).concat(".features"));
        
        try {
            FileWriter writer = new FileWriter(file);
            
            for(String record : this.featuresRecorded) {
                writer.write(record+ "\n");
            }
            
            writer.flush();
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(FileIOFeatures.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param recordFeatures 
     */
    public void setRecordFeatures(boolean recordFeatures) {
        this.recordFeatures = recordFeatures;
        
        this.resetFeaturesRecorded();
    }
    
    public void resetFeaturesRecorded() {
        this.featuresRecorded = new ArrayList<>();
    }

    public void setFileNameAddition(String fileNameAddition) {
        this.fileNameAddition = fileNameAddition;
    }
    
}
