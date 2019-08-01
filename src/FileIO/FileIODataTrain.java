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
public class FileIODataTrain {
    
    private String namaDataset;        
    
    private final String rootFolder = "datasets/train/";
    
    //<editor-fold defaultstate="collapsed" desc="Kategori Data Train">
    public final static int KATEGORI_KARAKTER = 1;    
    public final static int KATEGORI_SA = 2;    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Label Data Train">
    public final static int LABEL_KARAKTER_SALAH = 3;    
    public final static int LABEL_KARAKTER_BENAR = 4;    
    public final static int LABEL_SA_BENAR = 5;
    public final static int LABEL_SA_SALAH = 6;
    //</editor-fold>    
    
    public FileIODataTrain() {
        namaDataset = "";
    }

    public void setNamaDataset(String namaDataset) {
        this.namaDataset = namaDataset;
    }
    
    /**
     * 
     * @param label
     * @return 
     */
    public List<DataTrain> get(int label) {
        if(namaDataset.equals("")) return new ArrayList<>();
        
        switch(label) {
            case LABEL_KARAKTER_SALAH:
                return getKarakterSalah();
            case LABEL_KARAKTER_BENAR:
                return getKarakterBenar();
            case LABEL_SA_BENAR:
                return getSABenar();
            case LABEL_SA_SALAH:
                return getSASalah();
            default:
                return new ArrayList<>();
        }
    }
    
    private List<DataTrain> getKarakterSalah() {
        return getKarakterFiles("Salah");                 
    }
    
    private List<DataTrain> getKarakterBenar() {
        return getKarakterFiles("Benar");           
    }

    private List<DataTrain> getSABenar() {
        return getSAFiles("Benar");    
    }   
    
    private List<DataTrain> getSASalah() {
        return getSAFiles("Salah");
    }   
    
    /**
     * 
     * @param kategori
     * @param label
     * @return 
     */
    private List<DataTrain> getSAFiles(String label) {
        List<DataTrain> listData = new ArrayList<>();
        
        String folder = this.rootFolder+ "/" +this.namaDataset+ "/SA/" +label;
              
        File folderReader = new File(folder);

        if(folderReader.list() != null) {
            
            for(File file : folderReader.listFiles()) {
                if(!file.isFile()) continue;
                
                String fileName = file.getName().toLowerCase();
                
                if(fileName.endsWith(".jpg") || fileName.endsWith(".bmp") 
                        || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {
                    DataTrain data = new DataTrain(file.getName(),
                                                    file.getPath(),
                                                    "SA",
                                                    label,
                                                    "");

                    listData.add(data);
                } else continue;
            }
        } 
        
        return listData;         
    }        
    
    /**
     * 
     * @param kategori
     * @param label
     * @return 
     */
    private List<DataTrain> getKarakterFiles(String label) {
        List<DataTrain> listData = new ArrayList<>();
        
        String folder = this.rootFolder+ "/" +this.namaDataset+ "/Karakter/" +label;
              
        File folderReader = new File(folder);
        
        for(File folderKelas : folderReader.listFiles()) {
            if(!folderKelas.isDirectory()) continue;
            for(File file : folderKelas.listFiles()) {
                if(!file.isFile()) continue;
                
                String fileName = file.getName().toLowerCase();
                
                if(fileName.endsWith(".jpg") || fileName.endsWith(".bmp") 
                        || fileName.endsWith(".png") || fileName.endsWith(".jpeg")) {
                    DataTrain data = new DataTrain(file.getName(),
                                                    file.getPath(),
                                                    "Karakter",
                                                    label,
                                                    folderKelas.getName());

                    listData.add(data);
                } else continue;
            }
        } 
        
        return listData;         
    }        
    
    /**
     * 
     * @param direktori
     * @param kategoriData
     * @return 
     */
    public boolean tambahSA(File direktori) {
        String rootSrc = direktori.toString();
        String rootDest = rootFolder.concat("/" +this.namaDataset+ "/SA");
        
        boolean result = true;
            
        for(File subFolder : direktori.listFiles()) {
            if(!subFolder.isDirectory()) continue;
            for(File file : subFolder.listFiles()) {
                String fileName = file.getName().toLowerCase();
                
                if ((fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg")) || (fileName.endsWith(".bmp")) || fileName.endsWith(".png")) {
                    try {
                        String pathCopyTo = rootDest.concat("/" +subFolder.getName()+ "/" + fileName);
                        String pathCopyFrom = rootSrc.concat("/" +subFolder.getName()+ "/" + fileName);

                        Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                          .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                    }
                    catch (IOException ex) {
                        result = false;

                        Logger.getLogger(FileIODataTest.class.getName()).log(Level.SEVERE, null, ex);                    
                    }
                }
            }
        }
        
        return result;
    }
    
    public boolean tambahSA(File direktori, LoadingProgress loadingProgress) {
        String rootSrc = direktori.toString();
        String rootDest = rootFolder.concat("/" +this.namaDataset+ "/SA");
        
        boolean result = true;
        
        File[] subFolders = direktori.listFiles();
        
        double progress = 0, update = 100 / ((double) subFolders[0].list().length + subFolders[1].list().length);
        
        for(File subFolder : subFolders) {
            if(!subFolder.isDirectory()) continue;
            for(File file : subFolder.listFiles()) {
                String fileName = file.getName().toLowerCase();
                
                if ((fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg")) || (fileName.endsWith(".bmp")) || fileName.endsWith(".png")) {
                    try {
                        String pathCopyTo = rootDest.concat("/" +subFolder.getName()+ "/" + fileName);
                        String pathCopyFrom = rootSrc.concat("/" +subFolder.getName()+ "/" + fileName);

                        Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                          .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                  
                        
                        loadingProgress.updateProgress(progress+=update);
                    }
                    catch (IOException ex) {
                        result = false;

                        Logger.getLogger(FileIODataTest.class.getName()).log(Level.SEVERE, null, ex);                    
                    }
                }
            }
        }
        
        return result;
    }
    
    public boolean tambahKarakter(File direktori, LoadingProgress loadingProgress) {
        String rootSrc = direktori.toString();
        String rootDest = rootFolder.concat("/" +this.namaDataset+ "/Karakter");
        
        boolean result = true;
        
        File[] subFolders = direktori.listFiles();
        
        double progress = 0, update = 100 / ((double) subFolders[0].list().length + subFolders[1].list().length);
        
        for(File subFolder : subFolders) {
            if(!subFolder.isDirectory()) continue;
            for(File folderKelas : subFolder.listFiles()) {
                if(!folderKelas.isDirectory()) continue;
                
                new File(rootDest+ "/" +subFolder.getName()+ "/" +folderKelas.getName()).mkdirs();
                
                for(File file : folderKelas.listFiles()) {
                    String fileName = file.getName().toLowerCase();

                    if ((fileName.endsWith(".jpg")) || (fileName.endsWith(".jpeg")) || (fileName.endsWith(".bmp")) || fileName.endsWith(".png")) {
                        try {                                                        
                            String pathCopyTo = rootDest.concat("/" +subFolder.getName()+ "/" +folderKelas.getName()+ 
                                    "/" + fileName);
                            String pathCopyFrom = rootSrc.concat("/" +subFolder.getName()+ "/" +folderKelas.getName()+ 
                                    "/" + fileName);

                            Files.copy(new File(pathCopyFrom).toPath(), new File(pathCopyTo)
                              .toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });                                            
                        }
                        catch (IOException ex) {
                            result = false;

                            Logger.getLogger(FileIODataTest.class.getName()).log(Level.SEVERE, null, ex);                    
                        }
                    }
                }
                
                loadingProgress.updateProgress(progress+=update);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param data
     * @return 
     */
    public boolean hapusData(DataTrain data) {        
        File file = new File(data.getDirektoriOri());
        
        return file.delete();
    }      
}
