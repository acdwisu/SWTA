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

import Model.DataTest;
import View.LoadingProgress;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class FileIODataTest {    
       
    private String namaDataset;
    
    private final String rootFolder = "datasets/test/";
    
    public FileIODataTest() {
        namaDataset = "";
    }

    public void setNamaDataset(String namaDataset) {
        this.namaDataset = namaDataset;
    }
    
    public List<DataTest> get() {
        if(namaDataset.equals("")) return new ArrayList<>();
        
        List<DataTest> listData = new ArrayList<>();
        
        String folderName = rootFolder+ "/" +namaDataset+ "/";
        
        File folderReader = new File(folderName.concat("original"));
        
        if(folderReader.list() != null) {
            
            for(String file : folderReader.list()) {                
                
                String lowerCased = file.toLowerCase();
                if(lowerCased.endsWith(".jpg") || lowerCased.endsWith(".bmp") 
                        || lowerCased.endsWith(".png") || lowerCased.endsWith(".jpeg")) {
                    DataTest data = new DataTest(file, 
                                                folderName+ "/original/" +file,
                                                folderName+ "/processed/" +file,
                                                folderName+ "/segmented/" +file,
                                                folderName+ "/sps/" +file.split("\\.")[0]+ ".sps",
                                                folderName+ "/sps/" +file.split("\\.")[0]+ ".areas");

                    listData.add(data);
                }
            }
        }
        
        return listData;
    }
    
    /**
     * 
     * @param direktori
     * @return 
     */
    public boolean tambahData(File direktori) {
        boolean result = true;
        
        if(namaDataset.equals("")) return false;
        
        String destOri = rootFolder+ "/" +namaDataset+ "/original/";
        String srcOri = direktori.getPath();
        
        String srcProcessed = "temp/word-separated/";
        String destProcessed = rootFolder+ "/" +namaDataset+ "/processed/";
        
        String srcSPs = "temp/sps/";
        String destSPs = rootFolder+ "/" +namaDataset+ "/sps/";
    
        String[] listFiles = direktori.list();
        for (String file : listFiles) {
            String loweredCase = file.toLowerCase();
            if ((loweredCase.endsWith(".jpg")) || (loweredCase.endsWith(".jpeg")) 
                    || (loweredCase.endsWith(".bmp")) || loweredCase.endsWith(".png")) {
                try {
                    String pathCopyTo = destOri.concat(file);
                    String pathCopyFrom = srcOri.concat("/" + file);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    pathCopyTo = destProcessed.concat(file);
                    pathCopyFrom = srcProcessed.concat(file);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    Files.delete(new File(pathCopyFrom).toPath());
                    
                    String spsfile = file.split("\\.")[0].concat(".sps");
                    pathCopyTo = destSPs.concat(spsfile);
                    pathCopyFrom = srcSPs.concat(spsfile);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    Files.delete(new File(pathCopyFrom).toPath());
                }
                catch (IOException ex) {
                    result = false;
                    
                    Logger.getLogger(FileIODataTest.class.getName()).log(Level.SEVERE, null, ex);                    
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param direktori
     * @return 
     */
    public boolean tambahData(File direktori, LoadingProgress loadingProgress) {
        boolean result = true;
        
        if(namaDataset.equals("")) return false;
        
        String destOri = rootFolder+ "/" +namaDataset+ "/original/";
        String srcOri = direktori.getPath();
        
        String srcProcessed = "temp/word-separated/";
        String destProcessed = rootFolder+ "/" +namaDataset+ "/processed/";
        
        String srcSPs = "temp/sps/";
        String destSPs = rootFolder+ "/" +namaDataset+ "/sps/";
    
        String[] listFiles = direktori.list();
        
        double update = 100/(double)listFiles.length, progress=0;
        
        for (String file : listFiles) {
            String loweredCase = file.toLowerCase();
            if ((loweredCase.endsWith(".jpg")) || (loweredCase.endsWith(".jpeg")) 
                    || (loweredCase.endsWith(".bmp")) || loweredCase.endsWith(".png")) {
                try {
                    String pathCopyTo = destOri.concat(file);
                    String pathCopyFrom = srcOri.concat("/" + file);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    pathCopyTo = destProcessed.concat(file);
                    pathCopyFrom = srcProcessed.concat(file);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    Files.delete(new File(pathCopyFrom).toPath());
                    
                    String spsfile = file.split("\\.")[0].concat(".sps");
                    pathCopyTo = destSPs.concat(spsfile);
                    pathCopyFrom = srcSPs.concat(spsfile);

                    Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                      .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    
                    Files.delete(new File(pathCopyFrom).toPath());
                    
                    loadingProgress.updateProgress(progress+=update);
                }
                catch (IOException ex) {
                    result = false;
                    
                    Logger.getLogger(FileIODataTest.class.getName()).log(Level.SEVERE, null, ex);                    
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param fileName
     * @return 
     */
    public boolean hapusData(DataTest data) {
        if(namaDataset.equals("")) return false;
        
        boolean result=false;
        
        //
        String folder = data.getDirektoriOri();
        
        //
        String folderProcessed = this.rootFolder.concat("/" +namaDataset+ "/processed/" +data.getNama());
          
        //        
        String folderSegmentModel = data.getDirekotoriSegmented();
        
        //        
        String folderSps = data.getDirektoriSps();
        
        File folderDeleter = new File(folder);
        File folderProcessedDeleter = new File(folderProcessed);
        File folderSegmentModelDeleter = new File(folderSegmentModel);
        File folderSpsDeleter = new File(folderSps);        
        
        if(folderDeleter.delete()) {
            folderProcessedDeleter.delete();
            folderSegmentModelDeleter.delete();
            folderSpsDeleter.delete();
            result = true;
        }
        
        return result;
    }
}
