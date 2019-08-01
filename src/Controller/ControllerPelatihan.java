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
package Controller;

import Embed.EmbedComboModel;
import FileIO.FileIODataTrain;
import FileIO.FileIODataset;
import FileIO.FileIOFeatures;
import FileIO.FileIOTrainResult;
import Model.DataTrain;
import Model.Dataset;
import Model.TrainResult;
import Processor.FeatureExtractor.FeatureExtractor;
import Processor.FeatureExtractor.LineSegments.LineSegments;
import Processor.FeatureExtractor.PixelDensity.PixelDensity;
import Processor.FeatureExtractor.WUMI.WUMI;
import Processor.FeatureExtractor.ZoneCharacteristics.ZoneCharacteristics;
import Processor.FeatureExtractor.Zoning.Zoning;
import Processor.ImageProcessor;
import Processor.Learner.RandomForests.RandomForest;
import FileIO.FileIOTree;
import View.MenuKonfigEkstraksiFitur;
import View.MenuPelatihan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;

/**
 *
 * @author acdwisu
 */
public class ControllerPelatihan {

    private MenuPelatihan menu;                
    
    private HashMap<String, Integer> extractionConfig;
    
    public ControllerPelatihan(MenuPelatihan menu) {
        this.menu = menu;        
        
        //set default extraction config
        extractionConfig = new HashMap<>();
        
        this.setConfigExtractionToDefault(0);
        //-----
        
        this.loadListDataset();
    }   
    
    public void responseEditParameterEkstraksi(int tipe) {
        new MenuKonfigEkstraksiFitur(this, tipe, this.extractionConfig).setVisible(true);
    }
    
    public void responseMulaiPelatihan() {
        String trainName = this.menu.getTextNamaPelatihan().getText();
        
        FileIOTrainResult io = new FileIOTrainResult();
        
        boolean trainNameAllowed = io.get(trainName, FileIOTrainResult.SPECIFIER_NAMA_PELATIHAN).size() == 0;
        
        if(trainNameAllowed) {
            this.setEnabilityAllInput(false);
        
            ExecutorService pool = Executors.newFixedThreadPool(2);

            pool.execute(new TrainingSystemRunnable());                       
            
            pool.shutdown();                                              
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, nama tersebut telah digunakan");
        }               
    }
    
    /**
     * 
     */
    private void loadListDataset() {
        FileIODataset fileIODataset = new FileIODataset();
        
        List<Dataset> listDataset = fileIODataset.get(FileIODataset.TRAIN);
        
        List<String> list = new ArrayList<>();
                
        listDataset.forEach((x) -> {
            list.add(x.getNama());
        });
        
        EmbedComboModel model = new EmbedComboModel(list);
        
        this.menu.getComboDataset().setModel(model);                        
    }    
    
    public void setExtractionConfig(HashMap<String, Integer> extractionConfig) {
        this.extractionConfig = extractionConfig;
    }
    
    private void extractingFeaturesOfDataset() {                
        String selectedDataset = (String) this.menu.getComboDataset().getSelectedItem();
        
        String selectedExtractionMethod = (String)this.menu.getComboMetodeEkstraksi().getSelectedItem();
        selectedExtractionMethod = selectedExtractionMethod.split(" ")[0];
        
        String appliedParameter = this.extractionConfig.get("zoneHorizontalCount")+"-"+
                this.extractionConfig.get("zoneVerticalCount")+"-"+
                this.extractionConfig.get("offsetX")+"-"+
                this.extractionConfig.get("offsetY")+"-"+
                this.extractionConfig.get("zoneType");
        
        FileIOFeatures ioFeatures = new FileIOFeatures(selectedDataset, selectedExtractionMethod, appliedParameter);           

        ioFeatures.setFileNameAddition("Karakter");
        if(!ioFeatures.isFeatureEverExtracted()) {

            FileIODataTrain ioData = new FileIODataTrain();

            ImageProcessor proc = new ImageProcessor();
            
            FeatureExtractor fe;
            
            int selectedExtractionMethodIndex = this.menu.getComboMetodeEkstraksi().getSelectedIndex();
            
            if(selectedExtractionMethodIndex == 0 || selectedExtractionMethodIndex == 1)
                fe = new LineSegments(this.extractionConfig);
            else if(selectedExtractionMethodIndex == 2 || selectedExtractionMethodIndex == 3)
                fe = new PixelDensity(this.extractionConfig);
            else if(selectedExtractionMethodIndex == 4 || selectedExtractionMethodIndex == 5)                
                fe = new ZoneCharacteristics(this.extractionConfig);
            else 
                fe = new WUMI();
                
            ioData.setNamaDataset(selectedDataset);

            List<DataTrain> listKarakter = ioData.get(FileIODataTrain.LABEL_KARAKTER_BENAR);

            listKarakter.addAll(ioData.get(FileIODataTrain.LABEL_KARAKTER_SALAH));
            
            List<DataTrain> listSA = ioData.get(FileIODataTrain.LABEL_SA_BENAR);

            listSA.addAll(ioData.get(FileIODataTrain.LABEL_SA_SALAH));

            double update = 100 / (double) (listKarakter.size() + listSA.size()), progress = 0;            
            
            ioFeatures.setRecordFeatures(true);            
            
            for(DataTrain karakter : listKarakter) {
                String imgDir = karakter.getDirektoriOri();
                
                int[][] imgProcessed = proc.thinning(proc.imageToArray(proc.readImage(imgDir)));
                
                fe.setImg(proc.normalizeWordImageSize(imgProcessed));

                ioFeatures.storeFeature(karakter, fe.getFeatures());
                
                this.updateProgress(progress+=update);
            }
                        
            ioFeatures.mergeStoredFeatures();
            ioFeatures.resetFeaturesRecorded();
            
            ioFeatures.setFileNameAddition("SA");
            
            ioFeatures.setRecordFeatures(true);                        
            
            HashMap<String, Integer> configSA = new HashMap();
            
            configSA.put("zoneHorizontalCount", 3);
            configSA.put("zoneVerticalCount", 3);
            configSA.put("offsetX", 0);
            configSA.put("offsetY", 0);
            configSA.put("zoneType", Zoning.STATIC_ZONE);
            
            fe.setConfig(configSA);
            
            for(DataTrain SA : listSA) {
                String imgDir = SA.getDirektoriOri();

                int[][] imgProcessed = proc.thinning(proc.imageToArray(proc.readImage(imgDir)));
                
                fe.setImg(proc.normalizeWordImageSize(imgProcessed));

                ioFeatures.storeFeature(SA, fe.getFeatures());
                
                this.updateProgress(progress+=update);
            }

            ioFeatures.mergeStoredFeatures();
            ioFeatures.resetFeaturesRecorded();
        }
    }
    
    public void setConfigExtractionToDefault(int indexSelected) {
        switch(indexSelected) {
            case 0:
            case 2:
            case 4:
                this.extractionConfig.put("zoneHorizontalCount", 3);
                this.extractionConfig.put("zoneVerticalCount", 3);
                this.extractionConfig.put("offsetX", 0);
                this.extractionConfig.put("offsetY", 0);
                this.extractionConfig.put("zoneType", Zoning.STATIC_ZONE);
                break;
            case 1:
            case 3:
            case 5:
                this.extractionConfig.put("zoneHorizontalCount", 3);
                this.extractionConfig.put("zoneVerticalCount", 3);
                this.extractionConfig.put("offsetX", 3);
                this.extractionConfig.put("offsetY", 3);
                this.extractionConfig.put("zoneType", Zoning.ADAPTIVE_ZONE);
                break;
            case 6:
                this.extractionConfig.put("zoneHorizontalCount", 0);
                this.extractionConfig.put("zoneVerticalCount", 0);
                this.extractionConfig.put("offsetX", 0);
                this.extractionConfig.put("offsetY", 0);
                this.extractionConfig.put("zoneType", 0);
                break;
        }
    }
    
    private void changeProgressInfo(String info) {
        this.menu.getLabelInfoProgress().setText(info);

        updateProgress(0);
    }
    
    public void updateProgress(double progress) {
        this.menu.getProgressPelatihan().setValue((int) Math.round(progress));
    }
    
    private void trainingSystem() {
        String trainName = this.menu.getTextNamaPelatihan().getText();
        
        HashMap<String, Integer> configRF = new HashMap<>();
        FileIOTree ioTree = new FileIOTree();

        configRF.put("treeCount", (Integer) this.menu.getSpinnerJumlahTree().getValue());
        configRF.put("attributeCheckCountType", this.menu.getComboCountAttributesCheckType().getSelectedIndex());

        this.changeProgressInfo("1/5 (Feature Extraction)");

        this.extractingFeaturesOfDataset();

        // ---
        String selectedDataset = (String) this.menu.getComboDataset().getSelectedItem();

        String selectedExtractionMethod = (String) this.menu.getComboMetodeEkstraksi().getSelectedItem();
        selectedExtractionMethod = selectedExtractionMethod.split(" ")[0];

        String appliedParameter = this.extractionConfig.get("zoneHorizontalCount")+"-"+
                this.extractionConfig.get("zoneVerticalCount")+"-"+
                this.extractionConfig.get("offsetX")+"-"+
                this.extractionConfig.get("offsetY")+"-"+
                this.extractionConfig.get("zoneType");

        String featuresKarakterPath = "features/"+selectedDataset+"/"+selectedExtractionMethod+"_"+
                appliedParameter+"_Karakter.features";
        String featuresSAPath = "features/"+selectedDataset+"/"+selectedExtractionMethod+"_"+
                appliedParameter+"_SA.features";
        // ---

        ArrayList<ArrayList<String>> datasetKarakter = ioTree.CreateInputCateg(featuresKarakterPath)
                , datasetSA = ioTree.CreateInputCateg(featuresSAPath);

        RandomForest rfKarakter = new RandomForest(configRF, datasetKarakter), 
                rfSA = new RandomForest(configRF, datasetSA);

        this.changeProgressInfo("2/5 (Random Forest Karakter training)");
        rfKarakter.setUpdateListener(this.menu.getProgressPelatihan());
        rfKarakter.train();

        this.changeProgressInfo("3/5 (Random Forest SA training)");
        rfSA.setUpdateListener(this.menu.getProgressPelatihan());
        rfSA.train();

        this.changeProgressInfo("4/5 (Random Forest Karakter knowledge storing)");            
        ioTree.storeKnowledge(rfKarakter, trainName, "Karakter");

        this.changeProgressInfo("5/5 (Random Forest SA knowledge storing)");            
        ioTree.storeKnowledge(rfSA, trainName, "SA");      
        
        String dataset = (String) this.menu.getComboDataset().getSelectedItem(), 
                ekstraksi = (String) this.menu.getComboMetodeEkstraksi().getSelectedItem(),
                parameterEkstraksi = this.extractionConfig.get("zoneHorizontalCount")+"-"+
                            this.extractionConfig.get("zoneVerticalCount")+"-"+
                            this.extractionConfig.get("offsetX")+"-"+
                            this.extractionConfig.get("offsetY")+"-"+
                            this.extractionConfig.get("zoneType"),
                parameterRF = ((Integer) this.menu.getSpinnerJumlahTree().getValue())+"-"+
                    (this.menu.getComboCountAttributesCheckType().getSelectedIndex() == 0 ? 
                        ("Ms=(int)Math.round(Math.log(M)/Math.log(2)+1)") : ("Ms=(int) Math.round(Math.sqrt(M))"));
        
        String akurasiKarakter = rfKarakter.crossValidating(true), 
                akurasiSA = rfSA.crossValidating(true);
        
        String durasiKarakter = rfKarakter.getDuration(),
                durasiSA = rfSA.getDuration();
            
        TrainResult tr = new TrainResult(trainName, dataset, ekstraksi.split(" ")[0], parameterEkstraksi, parameterRF, 
                akurasiKarakter, akurasiSA, durasiKarakter, durasiSA);

        new FileIOTrainResult().insert(tr);
        
        this.changeProgressInfo("Selesai!! :3");
        
        this.setEnabilityAllInput(true);
    }
    
    private class TrainingSystemRunnable implements Runnable {

        @Override
        public void run() {
            trainingSystem();
        }
        
    }    
    
    private void setEnabilityAllInput(boolean enabled) {
        this.menu.getButtonMulaiPelatihan().setEnabled(enabled);
        this.menu.getButtonEditParameter().setEnabled(enabled);
        this.menu.getTextNamaPelatihan().setEnabled(enabled);
        this.menu.getComboCountAttributesCheckType().setEnabled(enabled);
        this.menu.getComboDataset().setEnabled(enabled);
        this.menu.getComboMetodeEkstraksi().setEnabled(enabled);
        this.menu.getSpinnerJumlahTree().setEnabled(enabled);
    }
}
