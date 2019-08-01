/*
 * Copyright (C) 2018 user
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
package Controller;

import Embed.EmbedListModel;
import FileIO.FileIODataTest;
import FileIO.FileIODataset;
import Model.DataTest;
import Model.Dataset;
import Processor.ImageProcessor;
import View.LoadingProgress;
import View.MenuDataUji;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author acdwisu
 */
public class ControllerDataUji {
    
    /** atribut penampung menu */
    private MenuDataUji menu;
       
    private ControllerModifikasiSP controllerModSP;
    
    /**  */
    private FileIODataset fileIoDataset;
    
    /**  */
    private FileIODataTest fileIoData;
    
    /**  */
    private List<Dataset> listDataset;
    
    /**  */
    private List<DataTest> listData;   
        
    private String selectedDatasetName;
    
    private String selectedDataName;
    
    private LoadingProgress progressViewer;
    
    /**
     * Contructor utama
     */
    private ControllerDataUji() {                
        /* */
        this.fileIoDataset = new FileIODataset();
        this.fileIoData = new FileIODataTest();
        
        /* */
        this.listDataset = new ArrayList<>();
        this.listData = new ArrayList<>();                
    }
    
    /**
     * Contructor saat membuat instance
     * @param menu Menu yang diperlukan 
     */
    public ControllerDataUji(MenuDataUji menu) {
        this();
        
        this.menu = menu;               
        
        /* */
        this.loadListDataset();
    }
    
    /**
     * 
     */
    public void responseExploreDataset() {
        this.exploreDataset();
    }
    
    /**
     * 
     */
    public void responseTambahDataset() {
        this.tambahDataset();
    }
    
    /**
     * 
     */
    public void responseHapusDataset() {
        this.hapusDataset();
    }    
    
    /**
     * 
     */
    public void responseTambahData() {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        pool.execute(new DataAddingRunnable());
        
        pool.shutdown();
    }
    
    /**
     * 
     */
    public void responseHapusData() {
        this.hapusData();
    }
        
    /**
     * 
     */
    public void responsePreviewData() {
        this.previewData();
    }   
    
    /**
     * 
     */
    public void responseModifikasiSP() {
        this.modifikasiSP();
//        new Test.TestShowOverSegmentationImage(new ImageProcessor().
////                readImage("C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\over_segmented\\"+
////                        this.listData.get(this.menu.getListData().getSelectedIndex()).getNama()));
    }   
    
    /**
     * 
     */
    private void loadListDataset() {
        this.listDataset = this.fileIoDataset.get(FileIODataset.TEST);
        
        EmbedListModel model = new EmbedListModel(this.getListNamaFromListDataset(listDataset));
        
        this.menu.getListDataset().setModel(model);                        
    }
    
    /**
     * 
     */
    private void exploreDataset() {
        JList<String> list = this.menu.getListDataset();
        
        if (!list.isSelectionEmpty()) {
            String datasetName = (String)list.getSelectedValue();

            JOptionPane.showMessageDialog(this.menu, datasetName + " terpilih", "Notice Me !!! ", JOptionPane.INFORMATION_MESSAGE);
      
            this.selectedDatasetName = datasetName;
            this.selectedDataName = "";
            this.fileIoData.setNamaDataset(datasetName);

            this.menu.getLabelBannerDataset().setText("Dataset Explorer (" + datasetName + ")");
            
            this.exploreData();
            
            this.setEnablityAllButton(true);
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari dataset yang tersedia", 
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
    }    
    
    /**
     * 
     */
    private void tambahDataset() {
        String datasetName = JOptionPane.showInputDialog(this.menu, "Nama Dataset Baru", "Tambah Dataset Data Uji", 
                JOptionPane.QUESTION_MESSAGE);
        
        if (datasetName != null) {
            datasetName = datasetName.trim();
            
            if (datasetName.isEmpty()) {
                JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memberikan nama dataset", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean contained = false;
                
                for (Dataset x : this.listDataset) {
                    if (x.getNama().equals(datasetName)) {
                        contained = true;
                    }
                }
                if (contained) {
                    JOptionPane.showMessageDialog(this.menu, "Maaf, nama tersebut tidak dapat digunakan", "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    if (this.fileIoDataset.tambah(datasetName, FileIODataset.TEST)) {                        
                        System.out.println("Dataset " + datasetName + " ditambahkan");
                        
                        this.loadListDataset();
                    }
                    else {
                        JOptionPane.showMessageDialog(this.menu, "Maaf, terjadi kesalahan saat IO\n(pembuatan root folder " 
                                + datasetName + ")", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    /**
     * 
     */
    private void hapusDataset() {
        JList<String> list = this.menu.getListDataset();
        
        if (!list.isSelectionEmpty()) {
            String datasetName = (String)list.getSelectedValue();

            int deny = JOptionPane.showConfirmDialog(this.menu, "Apakah anda yakin akan menghapus\ndataset " + datasetName + " ?", 
                    "Hapus Dataset Data Uji", JOptionPane.ERROR_MESSAGE);
            
            if ((deny == 1) || (deny == -1)) {
                System.out.println("gak jadi hapus " + datasetName);
            } else {
                if(this.fileIoDataset.hapus(datasetName, FileIODataset.TEST)) {
                    JOptionPane.showMessageDialog(menu, "Dataset telah dihapus", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    
                    this.loadListDataset();
                    
                    this.menu.getListData().setModel(new EmbedListModel(new ArrayList<>()));
                    
                    if(this.selectedDatasetName == datasetName) {
                        this.setEnablityAllButton(false);
                        this.setEnablityAllDatasetButton(true);
                    }
                }
                else 
                    JOptionPane.showMessageDialog(menu, "Dataset gagal dihapus", "Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari dataset yang tersedia", "Error", 0);
        }
    }      
    
    /**
     * 
     */
    private void exploreData() {                
        this.loadListData();
    }
    
    /**
     * 
     */
    private void loadListData() {       
        this.listData = this.fileIoData.get();

        EmbedListModel model = new EmbedListModel(this.getListNamaFromListData(listData));
        
        this.menu.getListData().setModel(model);                                
    }
    
    /**
     * 
     */
    private void tambahData() {
        this.setEnablityAllButton(false);
        
        JFileChooser direktoriChooser = new JFileChooser();
    
        direktoriChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        direktoriChooser.setMultiSelectionEnabled(false);
        direktoriChooser.setDialogTitle("Tambah Data  ||  Pilih direktori root data");
        
        if (0 == direktoriChooser.showOpenDialog(this.menu)) {
            File direktori = direktoriChooser.getSelectedFile();
            
            if (direktori.isDirectory()) {
                if (direktori.exists()) {
                    if (this.isFormatDirektoriDataCorrect(direktori)) {
                       
                        this.progressViewer = new LoadingProgress(this.menu);
                        this.progressViewer.setVisible(true);
                        
                        this.progressViewer.changeInfoProgress("1/2 (Preparing Separated Sub-word)");
                        
                        this.renderSeparatedSubWord(direktori);
                        
                        this.progressViewer.changeInfoProgress("2/2 (Saving things)");
                        
                        this.fileIoData.tambahData(direktori, this.progressViewer);
                        
                        this.loadListData();
                        
                        this.progressViewer.updateProgress(100);
                        this.progressViewer.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this.menu, "Maaf, " 
                                +"direktori yang anda masukkan tidak sesuai format", "Warning", 2);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(this.menu, "Maaf, direktori yang anda masukkan tidak exists", "Error", 0);
                }
            } else {
                JOptionPane.showMessageDialog(this.menu, "Maaf, direktori yang anda masukkan tidak valid", "Error", 0);
            }
        }
        
        this.setEnablityAllButton(true);
    }    

    /**
     * 
     */
    private void hapusData() {
        this.setEnablityAllButton(false);
        
        JList<String> list = this.menu.getListData();
        
        if(!list.isSelectionEmpty()) {
            int indexSelected = list.getSelectedIndex();
            DataTest selectedData = listData.get(indexSelected);
            
            if(this.fileIoData.hapusData(selectedData)) {
                JOptionPane.showMessageDialog(menu, "Data telah dihapus", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                this.loadListData();
                
                JLabel panelIconOri = this.menu.getLabelIconOriginal();
                JLabel panelIconSegmented = this.menu.getLabelIconSegmented();
                
                try {
                    BufferedImage noAsset = ImageIO.read(new File("assets/no_image.bmp"));
                    
                    panelIconOri.setIcon(new ImageIcon(noAsset.getScaledInstance(
                            panelIconOri.getWidth(), panelIconOri.getHeight(), 4)));
                    panelIconSegmented.setIcon(new ImageIcon(noAsset.getScaledInstance(
                            panelIconSegmented.getWidth(), panelIconSegmented.getHeight(), 4)));
                } catch (IOException ex) {
                    Logger.getLogger(ControllerDataUji.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else
                JOptionPane.showMessageDialog(menu, "Data gagal dihapus", "Entahlah", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari data yang tersedia", "Error", 0);
        }
        
        this.setEnablityAllButton(true);
    }    
    
    /**
     * 
     */
    private void previewData() {
        JList<String> list = this.menu.getListData();
       
        int selectedIndex = this.menu.getListData().getSelectedIndex();
        
        if(!list.isSelectionEmpty()) {
            JLabel panelIconOri = this.menu.getLabelIconOriginal();
            JLabel panelIconSegmented = this.menu.getLabelIconSegmented();

            DataTest selectedData = this.listData.get(selectedIndex);
            
            File fileOri = new File(selectedData.getDirektoriOri());          
            File fileSegmented = new File(selectedData.getDirekotoriSegmented());          

            ImageProcessor proc = new ImageProcessor();

            BufferedImage imageOri = null, imageSegmented = null;

            if (fileOri.isFile()) {
                imageOri = proc.readImage(fileOri.getPath());
            } else {
                imageOri = proc.readImage("assets/no_image.bmp");
            }                

            panelIconOri.setIcon(new ImageIcon(proc.fitImageToContainer(imageOri, 
                    panelIconOri.getWidth(), panelIconOri.getHeight(), 0.8f)));

            if (fileSegmented.isFile()) {
                imageSegmented = proc.readImage(fileSegmented.getPath());
            } else {
                imageSegmented = proc.readImage("assets/no_image.bmp");
            }                

            panelIconSegmented.setIcon(new ImageIcon(proc.fitImageToContainer(imageSegmented, 
                panelIconSegmented.getWidth(), panelIconSegmented.getHeight(), 0.8f)));             
        }
    }
    
    /**
     * 
     * @param listDataset
     * @return 
     */
    private List<String> getListNamaFromListDataset(List<Dataset> listDataset) {
        List<String> list = new ArrayList<>();
                
        listDataset.forEach((x) -> {
            list.add(x.getNama());
        });
        
        return list;
    }
    
    /**
     * 
     * @param listData
     * @return 
     */
    private List<String> getListNamaFromListData(List<DataTest> listData) {
        List<String> list = new ArrayList<>();
        
        listData.forEach((x) -> {
            list.add(x.getNama());
        });
        
        return list;
    }            
    
    /**
     * 
     * @param direktori
     * @param mode
     * @return 
     */
    private boolean isDirektoriContainImage(File direktori) {
        boolean contain = false;

        String[] listFiles = direktori.list();
        if (listFiles.length > 0) {
            for (String x : listFiles) {
                x = x.toLowerCase();

                if ((x.endsWith(".jpg")) || (x.endsWith(".jpeg")) || (x.endsWith(".bmp")) || (x.endsWith(".png"))) {
                    contain = true;
                    break;
                }
            }
        }
        
        return contain;
    }
    
    /**
     * 
     * @param direktori
     * @return 
     */
    private boolean isFormatDirektoriDataCorrect(File direktori) {
        if(!direktori.isDirectory()) return false;
        return this.isDirektoriContainImage(direktori);
    }   
    
    /**
     * 
     */
    private void modifikasiSP() {
        JList<String> list = this.menu.getListData();
        
        if(!list.isSelectionEmpty()) {
            this.controllerModSP = new ControllerModifikasiSP(this);
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari data yang tersedia", "Error", 0);
        }                
    }
    
    public DataTest getSelectedData() {
        return this.listData.get(this.menu.getListData().getSelectedIndex());
    }
    
    private void setEnablityAllDatasetButton(boolean enabled) {
        this.menu.getButtonExploreDataset().setEnabled(enabled);
        this.menu.getButtonTambahDataset().setEnabled(enabled);
        this.menu.getButtonHapusDataset().setEnabled(enabled);
    }
    
    private void setEnablityAllButton(boolean enabled) {
        this.setEnablityAllDatasetButton(enabled);
        this.menu.getButtonTambahData().setEnabled(enabled);
        this.menu.getButtonHapusData().setEnabled(enabled);
        this.menu.getButtonModifiksiSP().setEnabled(enabled);
    }    
    
    private void renderSeparatedSubWord(File direktori) {
        String dest = "temp/word-separated";
        
        ImageProcessor proc = new ImageProcessor();
        
        String[] listFiles = direktori.list();
        
        double update = 100 / (double) listFiles.length, progress = 0;
        
        for (String file : listFiles) {
            String loweredCase = file.toLowerCase();
            
            if ((loweredCase.endsWith(".jpg")) || (loweredCase.endsWith(".jpeg")) 
                    || (loweredCase.endsWith(".bmp")) || loweredCase.endsWith(".png")) {
                try {
                    BufferedImage buff = ImageIO.read(new File(direktori.getPath().concat("/" +file)));
                    
                    int[][] img = proc.imageToArray(buff);
                    
                    img = proc.universeOfDiscourse(img, true, true);
                    
                    List<List<Point>> objects = proc.getObjectsFromWord(img);
                    
                    List<Integer> wordObjectsIndex = this.urgenWordObjectIndexManual(file);
                    
                    int[][] imgnew = proc.separateWord(objects, wordObjectsIndex, buff.getWidth(), buff.getHeight());
                    
                    int[][] imgtrimmed = proc.universeOfDiscourse(imgnew, true, true);
                    
                    BufferedImage buffnew = proc.ArrayToImage(imgtrimmed);
                    
                    ImageIO.write(buffnew, "bmp", new File(dest.concat("/" +file)));
                    
                    detectSPs(imgtrimmed, file);
                    
                    progress += update;
                    
                    this.progressViewer.updateProgress(progress);
                }
                catch (IOException ex) {                    
                    Logger.getLogger(ControllerDataUji.class.getName()).log(Level.SEVERE, null, ex);                    
                }
            }
        }
    }
    
    private List<Integer> urgenWordObjectIndexManual(String fileName) {
        List<Integer> indexes = new LinkedList<>();
        
        String path = "temp/word-objects.dat";
        
        BufferedReader br = null;
        
        String line;

        try {
            br = new BufferedReader(new FileReader(path));
            
            while((line = br.readLine()) != null) {
                String[] contents = line.split(",");

                if(contents[0].equalsIgnoreCase(fileName)) {
                    String[] indexesTemp = contents[1].split("-");

                    for(String index : indexesTemp) {
                        Integer val = Integer.parseInt(index.trim());
                        indexes.add(val);                        
                    }
                    
                    break;
                }
            }        
        } catch (Exception e) {
            Logger.getLogger(ControllerDataUji.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(ControllerDataUji.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return indexes;
    }
    
    private void detectSPs(int[][] img, String fileName) {
        List<Integer> sps = new ImageProcessor().detectSegmentationPoints(img);
        
        String dest = "temp/sps/" +fileName.split("\\.")[0].concat(".sps");
        
        try {
            FileWriter writer = new FileWriter(new File(dest));
            
            String line = "";
            
            for(int sp : sps) {
                line += sp+ ",";
            }
            
            writer.write(line);
            
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ControllerDataUji.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private class DataAddingRunnable implements Runnable {

        @Override
        public void run() {
            tambahData();
        }
    }    
}
