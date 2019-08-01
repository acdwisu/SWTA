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

import Model.Dataset;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author acdwisu
 */
public class FileIODataset {
    
    private final String rootFolder = "datasets";
    
    public final static int TRAIN = 1;
    public final static int TEST = 2;
    
    public FileIODataset(){
        
    }    
    
    public List<Dataset> get(int tipe) {        
        
        switch(tipe) {
            case TRAIN:
                return this.getTrainDataset();
            case TEST:
                return this.getTestDataset();                
            default:
                return new LinkedList<>();
        }                        
    }        
    
    /**
     * 
     * @param nama
     * @param tipe
     * @return 
     */
    public boolean tambah(String nama, int tipe) {
        boolean result;
        
        switch(tipe) {
            case TRAIN:
                result = this.tambahDatasetTrain(nama);
                break;
            case TEST:
                result = this.tambahDatasetTest(nama);
                break;
            default:
                result = false;
        }
        
        return result;
    }
    
    /**
     * 
     * @param nama
     * @param tipe
     * @return 
     */
    public boolean hapus(String nama, int tipe) {
        boolean result;
        
        switch(tipe) {
            case TRAIN:
                result = this.hapusDatasetTrain(nama);
                break;
            case TEST:
                result = this.hapusDatasetTest(nama);
                break;
            default:
                result = false;
        }
        
        return result;
    }
    
    /**
     * 
     * @param nama
     * @return 
     */
    private boolean tambahDatasetTrain(String nama) {
        boolean result;
        
        //
        String folder = this.rootFolder.concat("/train/" +nama);                
        
        File folderMaker = new File(folder);
        
        result = folderMaker.mkdirs();
        
        String[] kategori = {"Karakter", "SA"};
        String[][] label = {{"Benar", "Salah"}, {"Benar", "Salah"}};
        
        if(result) {                        
            for(int i=0; i<kategori.length; i++) {
                folderMaker = new File(folder.concat("/" +kategori[i]));
                
                result = folderMaker.mkdir();
                
                if(!result) {                
                    break;
                }
                
                for(int j=0; j<label[i].length; j++) {
                    folderMaker = new File(folder.concat("/" +kategori[i]+ "/" +label[i][j]));

                    result = folderMaker.mkdir();

                    if(!result) {                
                        break;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param nama
     * @return 
     */
    private boolean tambahDatasetTest(String nama) {
        boolean result;
        
        //
        String folder = this.rootFolder.concat("/test/" +nama+ "/original");   
        
        //
        String folderProcessed = this.rootFolder.concat("/test/" +nama+ "/processed");
          
        //        
        String folderSegmentModel = this.rootFolder.concat("/test/" +nama+ "/segmented");
        
        //        
        String folderSps = this.rootFolder.concat("/test/" +nama+ "/sps");
        
        File folderMaker = new File(folder);
        File folderProcessedMaker = new File(folderProcessed);
        File folderSegmentModelMaker = new File(folderSegmentModel);
        File folderSpsMaker = new File(folderSps);
        
        result = folderMaker.mkdirs() && folderProcessedMaker.mkdirs() && 
                folderSegmentModelMaker.mkdirs() && folderSpsMaker.mkdirs();
        
        return result;        
    }
    
    /**
     * 
     * @param nama
     * @return 
     */
    private boolean hapusDatasetTrain(String namaDataset) {
        boolean result;
        
        result = this.deleteDataTrainFiles(namaDataset);
        
        return result;
    }
    
    /**
     * 
     * @param nama
     * @return 
     */
    private boolean hapusDatasetTest(String nama) {
        boolean result = true;
        
        //
        String folder = this.rootFolder.concat("/test/" +nama+ "/original");   
        
        //
        String folderProcessed = this.rootFolder.concat("/test/" +nama+ "/processed");
          
        //        
        String folderSegmentModel = this.rootFolder.concat("/test/" +nama+ "/segmented");
        
        //        
        String folderSps = this.rootFolder.concat("/test/" +nama+ "/sps");
        
        File folderDeleter = new File(folder);
        File folderProcessedDeleter = new File(folderProcessed);
        File folderSegmentModelDeleter = new File(folderSegmentModel);
        File folderSpsDeleter = new File(folderSps);
        
        if(this.deleteDataTestFiles(folderDeleter) && this.deleteDataTestFiles(folderProcessedDeleter)
                && this.deleteDataTestFiles(folderSegmentModelDeleter) && this.deleteDataTestFiles(folderSpsDeleter)) {
            result = folderDeleter.getParentFile().delete();
        } else result = false;
        
        return result;
    }

    private boolean deleteDataTrainFiles(String namaDataset) {
        boolean a = this.deleteDataKarakterFiles(namaDataset), b = this.deleteDataSAFiles(namaDataset),
                c = new File(this.rootFolder+ "/train/" +namaDataset).delete();       
        System.out.println("a "+a+ " b " +b+ " c "+c);
        
        return a && b && c;
    }
    
    /**
     * 
     * @param folderDeleter
     * @return 
     */
    private boolean deleteDataKarakterFiles(String namaDataset) {        
        File parent = new File(this.rootFolder+ "/train/" +namaDataset+ "/Karakter");

        //traverse folder label
        for(File folder : parent.listFiles()) {            
            for(File folderKelas : folder.listFiles()) {                
                //traverse files inside
                for(File file : folderKelas.listFiles()) {
                    if(!file.delete()) return false;
                }     
                if(!folderKelas.delete()) return false;
            }
            if(!folder.delete()) return false;            
        }

        return parent.delete();
    }
    
    private boolean deleteDataSAFiles(String namaDataset) {                
        File parent = new File(this.rootFolder+ "/train/" +namaDataset+ "/SA");               
        
        //traverse folder label
        for(File folder : parent.listFiles()) {
            //traverse files inside
            for(File file : folder.listFiles()) {
                if(!file.delete()) return false;
            }     
            if(!folder.delete()) return false;            
        }

        return parent.delete();
    }
    
    /**
     * 
     * @param folderDeleter
     * @return 
     */
    private boolean deleteDataTestFiles(File folderDeleter) {
        boolean result = false;
        
        File[] files = folderDeleter.listFiles();

        boolean emptyFolder = files.length <= 0;

        if(emptyFolder) {
            result = folderDeleter.delete();
        } else {
            boolean allFileDeleted = true;

            for(File file : files) {
                result = file.delete();

                if(!result) {
                    allFileDeleted = false;
                    break;
                }
            }
            if(allFileDeleted) {
                result = folderDeleter.delete();
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @return 
     */
    private List<Dataset> getTrainDataset() {
        List<Dataset> list = new LinkedList<>();
        
        String folderPath = this.rootFolder.concat("/train");

        File folder = new File(folderPath);
        
        if(folder.listFiles().length > 0) {
            for(File subFolder : folder.listFiles()) {
                if(subFolder.isDirectory()) {
                    list.add(new Dataset(subFolder.getName()));
                }
            }
        }
        
        return list;
    }
    
    /**
     * 
     * @return 
     */
    private List<Dataset> getTestDataset() {
        List<Dataset> list = new LinkedList<>();
        
        String folder = this.rootFolder.concat("/test");
        
        for(File subFolder : new File(folder).listFiles()) {
            if(subFolder.isDirectory()) {
                list.add(new Dataset(subFolder.getName()));
            }
        }
        
        return list;
    }
}
