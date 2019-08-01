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
import FileIO.FileIODataTrain;
import FileIO.FileIODataset;
import Model.DataTrain;
import Model.Dataset;
import Processor.ImageProcessor;
import View.LoadingProgress;
import View.MenuDataLatih;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.JPanel;

/**
 *
 * @author acdwisu
 */
public class ControllerDataLatih {
    
    /** atribut penampung menu */
    private MenuDataLatih menu;
       
    /**  */
    private FileIODataset fileIoDataset;
    
    /**  */
    private FileIODataTrain fileIoData;
    
    /**  */
    private List<Dataset> listDataset;
    
    /**  */
    private List<DataTrain> listKarakter;   
    
    /**  */
    private List<DataTrain> listSA; 
    
    private String selectedDatasetName;
    
    private String selectedDataName;
    
    private LoadingProgress progressViewer;
    
    /**
     * Contructor utama
     */
    private ControllerDataLatih() {                
        /* */
        this.fileIoDataset = new FileIODataset();
        this.fileIoData = new FileIODataTrain();
        
        /* */
        this.listDataset = new ArrayList<>();
        this.listKarakter = new ArrayList<>();                
        this.listSA = new ArrayList<>();                
    }
    
    /**
     * Contructor saat membuat instance
     * @param menu Menu yang diperlukan 
     */
    public ControllerDataLatih(MenuDataLatih menu) {
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
    public void responseTambahKarakter() {
        if(!this.menu.getButtonTambahKarakter().isEnabled()) return;
        
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        pool.execute(new DataAddingRunnable(DataAddingRunnable.DATA_KARAKTER));
        
        pool.shutdown();
    }
    
    /**
     * 
     */
    public void responseTambahSA() {
        if(!this.menu.getButtonTambahSA().isEnabled()) return;
        
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        
        pool.execute(new DataAddingRunnable(DataAddingRunnable.DATA_SA));
        
        pool.shutdown();
    }
    
    /**
     * 
     */
    public void responseHapusKarakter() {
        if(!this.menu.getButtonHapusKarakter().isEnabled()) return;
        
        this.hapusKarakter();
    }
    
    public void responseHapusSA() {
        if(!this.menu.getButtonHapusSA().isEnabled()) return;
        
        this.hapusSA();
    }
    
    /**
     * 
     */
    public void responsePreviewKarakter() {
        this.previewKarakter();
    }   
    
    /**
     * 
     */
    public void responsePreviewSA() {
        this.previewSA();
    }   
    
    /**
     * 
     */
    private void loadListDataset() {
        this.listDataset = this.fileIoDataset.get(FileIODataset.TRAIN);
        
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
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari dataset yang tersedia", 
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        this.setEnablityAllButton(true);
    }    
    
    /**
     * 
     */
    private void tambahDataset() {
        String datasetName = JOptionPane.showInputDialog(this.menu, "Nama Dataset Baru", "Tambah Dataset Data Latih", 
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
                    if (this.fileIoDataset.tambah(datasetName, FileIODataset.TRAIN)) {                        
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
                    "Hapus Dataset Data Latih", JOptionPane.ERROR_MESSAGE);
            
            if ((deny == 1) || (deny == -1)) {
                System.out.println("gak jadi hapus " + datasetName);
            } else {
                if(this.fileIoDataset.hapus(datasetName, FileIODataset.TRAIN)) {
                    JOptionPane.showMessageDialog(menu, "Dataset telah dihapus", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
                    
                    this.loadListDataset();
                    
                    this.menu.getListKarakter().setModel(new EmbedListModel(new ArrayList<>()));
                    this.menu.getListSA().setModel(new EmbedListModel(new ArrayList<>()));
                    
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
        this.loadListKarakter();
        this.loadListSA();
    }
    
    /**
     * 
     */
    private void loadListKarakter() {       
        this.listKarakter = this.fileIoData.get(FileIODataTrain.LABEL_KARAKTER_BENAR);

        this.listKarakter.addAll(this.fileIoData.get(FileIODataTrain.LABEL_KARAKTER_SALAH));

        EmbedListModel model = new EmbedListModel(this.getListNamaFromListData(listKarakter));
        
        this.menu.getListKarakter().setModel(model);                                
    }
    
    /**
     * 
     */
    private void loadListSA() {
        this.listSA = this.fileIoData.get(FileIODataTrain.LABEL_SA_BENAR);
        this.listSA.addAll(this.fileIoData.get(FileIODataTrain.LABEL_SA_SALAH));
        
        EmbedListModel model = new EmbedListModel(this.getListNamaFromListData(listSA));
        
        this.menu.getListSA().setModel(model);                        
    }
        
    /**
     * 
     */
    private void tambahKarakter() {
        this.setEnablityAllButton(false);
        
        JFileChooser direktoriChooser = new JFileChooser();
    
        direktoriChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        direktoriChooser.setMultiSelectionEnabled(false);
        direktoriChooser.setDialogTitle("Tambah Data Karakter  ||  Pilih direktori root data karakter");
        
        if (0 == direktoriChooser.showOpenDialog(this.menu)) {
            File direktori = direktoriChooser.getSelectedFile();
            
            if (direktori.isDirectory()) {
                if (direktori.exists()) {
                    if (this.isFormatDirektoriKarakterCorrect(direktori)) {
                        
                        this.progressViewer = new LoadingProgress(this.menu);
                        this.progressViewer.setVisible(true);
                        
                        this.progressViewer.changeInfoProgress("Saving things");
                        
                        this.fileIoData.tambahKarakter(direktori, this.progressViewer);
                        
                        this.progressViewer.updateProgress(100);
                        
                        this.loadListKarakter();
                        
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
    private void tambahSA() {
        this.setEnablityAllButton(false);
                
        JFileChooser direktoriChooser = new JFileChooser();
    
        direktoriChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        direktoriChooser.setMultiSelectionEnabled(false);
        direktoriChooser.setDialogTitle("Tambah Data SA  ||  Pilih direktori root data SA");
        
        if (0 == direktoriChooser.showOpenDialog(this.menu)) {
            File direktori = direktoriChooser.getSelectedFile();
            
            if (direktori.isDirectory()) {
                if (direktori.exists()) {
                    if (this.isFormatDirektoriSACorrect(direktori)) {
                        
                        this.progressViewer = new LoadingProgress(this.menu);
                        this.progressViewer.setVisible(true);
                        
                        this.progressViewer.changeInfoProgress("saving things");
                        
                        this.fileIoData.tambahSA(direktori, this.progressViewer);                                                
                        
                        this.loadListSA();
                        
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
    private void hapusKarakter() {
        this.setEnablityAllButton(false);
        
        JList<String> list = this.menu.getListKarakter();
        
        if(!list.isSelectionEmpty()) {
            int indexSelected = list.getSelectedIndex();
            DataTrain selectedData = listKarakter.get(indexSelected);
            
            if(this.fileIoData.hapusData(selectedData)) {
                JOptionPane.showMessageDialog(menu, "Data telah dihapus", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                this.loadListKarakter();
            } else
                JOptionPane.showMessageDialog(menu, "Data gagal dihapus", "Entahlah", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, anda harus memilih salah satu dari data yang tersedia", "Error", 0);
        }
        
        this.setEnablityAllButton(true);
    }
    
    private void hapusSA() {
        this.setEnablityAllButton(false);
        
        JList<String> list = this.menu.getListSA();
        
        if(!list.isSelectionEmpty()) {
            int indexSelected = list.getSelectedIndex();
            DataTrain selectedData = listSA.get(indexSelected);
            
            if(this.fileIoData.hapusData(selectedData)) {
                JOptionPane.showMessageDialog(menu, "Data telah dihapus", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                this.loadListSA();
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
    private void previewKarakter() {
        ImageProcessor proc = new ImageProcessor();
        
        JList<String> list = this.menu.getListKarakter();
       
        int selectedIndex = this.menu.getListKarakter().getSelectedIndex();
        
        if(!list.isSelectionEmpty()) {
            JLabel panelIcon = this.menu.getLabelIconPreview();

            File file = new File(this.listKarakter.get(selectedIndex).getDirektoriOri());          
            
            BufferedImage image;

            if (file.isFile()) {
                image = proc.readImage(file.getPath());
            } else {
                image = proc.readImage("assets/no_image.bmp");
            }                

            panelIcon.setIcon(new ImageIcon(proc.
                    fitImageToContainer(image, panelIcon.getWidth(), panelIcon.getHeight(), 0.6f)));                        
        }
    }

    private void previewSA() {
        ImageProcessor proc = new ImageProcessor();
        
        JList<String> list = this.menu.getListSA();
       
        int selectedIndex = this.menu.getListSA().getSelectedIndex();
        
        if(!list.isSelectionEmpty()) {
            JLabel panelIcon = this.menu.getLabelIconPreview();

            File file = new File(this.listSA.get(selectedIndex).getDirektoriOri());          
            
            BufferedImage image;

            if (file.isFile()) {
                image = proc.readImage(file.getPath());
            } else {
                image = proc.readImage("assets/no_image.bmp");
            }                

            panelIcon.setIcon(new ImageIcon(proc.
                    fitImageToContainer(image, panelIcon.getWidth(), panelIcon.getHeight(), 0.6f)));            
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
    private List<String> getListNamaFromListData(List<DataTrain> listData) {
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

                if ((x.endsWith(".jpg")) || (x.endsWith(".jpeg")) || (x.endsWith(".bmp") || (x.endsWith(".png")))) {
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
    private boolean isFormatDirektoriSACorrect(File direktori) {
        boolean correct = true;

        File[] parentFolders = direktori.listFiles();
        if(parentFolders.length == 2) {
            if(direktori.isDirectory()) {
                int i=1;
                for(File parentFolder : parentFolders) {
                    if( (i == 1 && parentFolder.getName().equals("Benar")) || (i==2 && parentFolder.getName().equals("Salah")) ) {
                        if(parentFolder.isDirectory()) {
                            if(!this.isDirektoriContainImage(parentFolder)) {
                                correct = false;
                            }
                        } else correct = false;
                    } else correct = false;
                    i++;
                }
            } else {
                correct = false;
            }
        } else correct = false;
        
        return correct;
    }
    
    /**
     * 
     * @param direktori
     * @return 
     */
    private boolean isFormatDirektoriKarakterCorrect(File direktori) {
        boolean correct = true;

        File[] parentFolders = direktori.listFiles();
        if(parentFolders.length == 2) {
            if(direktori.isDirectory()) {
                int i=1;
                for(File parentFolder : parentFolders) {
                    if( (i == 1 && parentFolder.getName().equals("Benar")) || (i==2 && parentFolder.getName().equals("Salah")) ) {
                        if(parentFolder.isDirectory()) {
                            for(File folderKelas : parentFolder.listFiles()) {
                                if(folderKelas.isDirectory()) {
                                    if(!this.isDirektoriContainImage(folderKelas)) {
                                        correct = false;
                                    }
                                } else correct = false;
                            }
                        } else correct = false;
                    } else correct = false;
                    i++;
                }
            } else {
                correct = false;
            }
        } else correct = false;
        
        return correct;
    }
        
    private void setEnablityAllDatasetButton(boolean enabled) {
        this.menu.getButtonExploreDataset().setEnabled(enabled);
        this.menu.getButtonTambahDataset().setEnabled(enabled);
        this.menu.getButtonHapusDataset().setEnabled(enabled);
    }
    
    private void setEnablityAllButton(boolean enabled) {
        this.setEnablityAllDatasetButton(enabled);
        this.menu.getButtonHapusKarakter().setEnabled(enabled);
        this.menu.getButtonHapusSA().setEnabled(enabled);        
        this.menu.getButtonTambahKarakter().setEnabled(enabled);
        this.menu.getButtonTambahSA().setEnabled(enabled);
    }       
    
    private class DataAddingRunnable implements Runnable {

        public static final int DATA_KARAKTER=1;
        
        public static final int DATA_SA=2;
        
        private final int DataAddingMode;

        public DataAddingRunnable(int DataAddingMode) {
            this.DataAddingMode = DataAddingMode;
        }
        
        @Override
        public void run() {
            switch(this.DataAddingMode) {
                case DATA_KARAKTER :
                    tambahKarakter();
                    break;
                case DATA_SA :
                    tambahSA();
                    break;
            }
        }
    }    
}
