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
 * along with this program; if not, writeSegmentationPoint to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package Controller;

import Embed.EmbedComboModel;
import FileIO.FileIOComparationResult;
import FileIO.FileIODataTest;
import FileIO.FileIODataset;
import FileIO.FileIOOverallResult;
import FileIO.FileIOSegmentationPoints;
import FileIO.FileIOSegmentationResult;
import FileIO.FileIOTrainResult;
import Model.ComparationResult;
import Model.DataTest;
import Model.Dataset;
import Model.OverallResult;
import Model.SegmentationResult;
import Model.TrainResult;
import Processor.FeatureExtractor.FeatureExtractor;
import Processor.FeatureExtractor.LineSegments.LineSegments;
import Processor.FeatureExtractor.PixelDensity.PixelDensity;
import Processor.FeatureExtractor.WUMI.WUMI;
import Processor.FeatureExtractor.ZoneCharacteristics.ZoneCharacteristics;
import Processor.FeatureExtractor.Zoning.Zoning;
import Processor.ImageProcessor;
import Processor.Learner.RandomForests.RandomForest;
import View.MenuSegmentasi;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author acdwisu
 */
public class ControllerSegmentasi {
   
    private final MenuSegmentasi menu;
    
    private List<TrainResult> listTrainResult;

    public ControllerSegmentasi(MenuSegmentasi menu) {
        this.menu = menu;
        
        this.loadListDataset();
        
        this.loadListHasilPelatihan();
    }
    
    public void responseMulaiSegmentasi() {
        String segmentationName = this.menu.getTextNamaSegmentasi().getText();
        
        FileIOSegmentationResult io = new FileIOSegmentationResult();
        
        boolean segmentationNameAllowed = io.get(segmentationName, FileIOSegmentationResult.SPECIFIER_NAMA_SEGMENTASI).size() == 0;
        
        if(segmentationNameAllowed) {
            this.setEnablityAllInput(false);
            
            String trainName = (String) this.menu.getComboHasilPelatihan().getSelectedItem(),
                    datasetName = (String) this.menu.getComboDataset().getSelectedItem();

            SegmentationResult sr = new SegmentationResult(segmentationName, trainName, datasetName);
            
            io.insert(sr);
            
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            ProcessRunnable t = new ProcessRunnable();
            
            pool.execute(t);

            pool.shutdown();                             
        } else {
            JOptionPane.showMessageDialog(this.menu, "Maaf, nama tersebut telah digunakan");
        }
    }
   
    public void responseChangeSelectedEkstraksi() {
        this.loadListHasilPelatihan();
    }
    
    private void loadListDataset() {
        FileIODataset fileIODataset = new FileIODataset();
        
        List<Dataset> listDataset = fileIODataset.get(FileIODataset.TEST);
        
        List<String> list = new ArrayList<>();
                
        listDataset.forEach((x) -> {
            list.add(x.getNama());
        });
        
        EmbedComboModel model = new EmbedComboModel(list);
        
        this.menu.getComboDataset().setModel(model);  
    }
    
    private void loadListHasilPelatihan() {
        String ekstraksiSelected = (String) this.menu.getComboMetodeEkstraksi().getSelectedItem();
        ekstraksiSelected = ekstraksiSelected.split(" ")[0];
        
        this.listTrainResult = new FileIOTrainResult().get(ekstraksiSelected, FileIOTrainResult.SPECIFIER_EKSTRAKSI);
        
        List<String> list = new ArrayList<>();
                
        listTrainResult.forEach((x) -> {
            list.add(x.getNamaPelatihan());
        });
        
        EmbedComboModel model = new EmbedComboModel(list);
        
        this.menu.getComboHasilPelatihan().setModel(model);  
    }
    
    private void segmenting() {
        String namaSegmentasi = this.menu.getTextNamaSegmentasi().getText();
        
        ImageProcessor proc = new ImageProcessor();
        
        int indexSelectedTrain = this.menu.getComboHasilPelatihan().getSelectedIndex();
        TrainResult trainRes = this.listTrainResult.get(indexSelectedTrain);
        
        RandomForest rfKarakter = new RandomForest("knowledge/" +trainRes.getNamaPelatihan()+ "/Karakter"), 
                rfSA = new RandomForest("knowledge/" +trainRes.getNamaPelatihan()+ "/SA");
        
        this.changeProgressInfo("1/4 (Retrieve knowledge Random Forests Karakter)");
        
        rfKarakter.train();
        rfKarakter.setUpdateListener(this.menu.getProgressSegmentasi());
        
        this.changeProgressInfo("2/4 (Retrieve knowledge Random Forests SA)");
        
        rfSA.train();

        rfSA.setUpdateListener(this.menu.getProgressSegmentasi());
        
        String selectedDataset = (String) this.menu.getComboDataset().getSelectedItem();        
        
        String selectedExtractionMethod = trainRes.getEkstraksi();
        
        String[] parameterEkstraksi = trainRes.getParameterEkstraksi().split("-");
        
        HashMap<String, Integer> configKarakter = new HashMap<>();
        
        configKarakter.put("zoneHorizontalCount", Integer.parseInt(parameterEkstraksi[0]));
        configKarakter.put("zoneVerticalCount", Integer.parseInt(parameterEkstraksi[1]));
        configKarakter.put("offsetX", Integer.parseInt(parameterEkstraksi[2]));
        configKarakter.put("offsetY", Integer.parseInt(parameterEkstraksi[3]));
        configKarakter.put("zoneType", Integer.parseInt(parameterEkstraksi[4]));
        
        HashMap<String, Integer> configSA = new HashMap();
            
        configSA.put("zoneHorizontalCount", 3);
        configSA.put("zoneVerticalCount", 3);
        configSA.put("offsetX", 0);
        configSA.put("offsetY", 0);
        configSA.put("zoneType", Zoning.STATIC_ZONE);                        
        
        FeatureExtractor extractor;
        
        switch(selectedExtractionMethod) {
            case "SZLS":
            case "AZLS":
                extractor = new LineSegments(configKarakter);
                break;
            case "SZPD":
            case "AZPD":
                extractor = new PixelDensity(configKarakter);
                break;
            case "SZZC":
            case "AZZC":
                extractor = new ZoneCharacteristics(configKarakter);
                break;            
            case "WUMI":
            default:
                extractor = new WUMI();
                break;
        }
        
        FileIODataTest fileIOData = new FileIODataTest();
        FileIOSegmentationPoints fileIOSP = new FileIOSegmentationPoints();
        
        fileIOData.setNamaDataset(selectedDataset);
        
        List<DataTest> datas = fileIOData.get();
        
        this.changeProgressInfo("3/4 (PSPs gain by Over-segmentation & RF validation)");
        
        double update = 100 / (double)datas.size(), progress = 0;      

        for(DataTest data : datas) {
            BufferedImage image = proc.readImage(data.getDirektoriProcessed());

            List<Integer> sps = new FileIOSegmentationPoints().readSegmentationPoint(data.getDirektoriSps());
            
            // psps result by over-segmentation
            List<Integer> pspsOS = proc.overSegmentation(image, sps);
            
            // eliminate psps in ligatures object (ignores the ligatures :v)
            pspsOS = this.eliminatePspsInLigatures(data.getNama(), pspsOS);
                        
            // validation of psps by random forests
            List<Integer> pspsV = new LinkedList<>();
            
            int[][] imgThinned = proc.thinning(proc.imageToArray(image));            
            
            // travers all sp to get the psps of each subword
            for(int i=sps.size()-1; i>=1; i--) {
                int sp1= sps.get(i),
                        sp2 = sps.get(i-1);
                
                List<Integer> pspsSubword = new LinkedList<>();
                
                for(int e : pspsOS) {
                    if(e < sp1 && e > sp2) {
                        pspsSubword.add(e);
                    }
                }
                
                pspsSubword.add(0,sp1);
                pspsSubword.add(sp2);                               
                
                // traverse all subword psps
                for(int j=1; j<pspsSubword.size()-1; j++) {
                    //   generate areas by scanning each psps then combine those
                    List<HashMap<String, int[][]>> combinations = proc.pspScanning(imgThinned, pspsSubword, j);
                    
                    double[] csps = new double[combinations.size()];
                    boolean[] validities = new boolean[combinations.size()];
                    
                    for(int k=0; k<combinations.size(); k++) {
                        double[] rcFeature, ccFeature, saFeature;
                        
                        extractor.setConfig(configKarakter);
                        
                        //   extract feature of each area
                        extractor.setImg(proc.normalizeWordImageSize(combinations.get(k).get("rc")));
                        rcFeature = extractor.getFeatures();
                        
                        extractor.setImg(proc.normalizeWordImageSize(combinations.get(k).get("cc")));
                        ccFeature = extractor.getFeatures();
                        
                        extractor.setConfig(configSA);
                        
                        extractor.setImg(proc.normalizeWordImageSize(combinations.get(k).get("sa")));
                        saFeature = extractor.getFeatures();
                        
                        //   rf evaluation   
                        String rcPrediction, ccPrediction, saPrediction;
                        double Crc, Nrc, Ccc, Ncc, Csa, Nsa;
                        double csp, isp;
                        
                        //      make test query
                        ArrayList<ArrayList<String>> query = new ArrayList<>();

                        ArrayList<String> rcQuery = new ArrayList<>(), ccQuery = new ArrayList<>(),
                                saQuery = new ArrayList<>();
                        
                        for(int l=0; l<rcFeature.length; l++) {
                            rcQuery.add(String.valueOf(rcFeature[l]));
                            ccQuery.add(String.valueOf(ccFeature[l]));
                        }
                        
                        query.add(rcQuery);
                        query.add(ccQuery);                        
                        
                        rfKarakter.setTestdata(query);
                        
                        List<HashMap<String, String>> evaluationResultKarakter = rfKarakter.evaluateForestWithoutLabel();
                                    
                        query.clear();
                        
                        for(int l=0; l<saFeature.length; l++) {                            
                            saQuery.add(String.valueOf(saFeature[l]));
                        }
                        
                        query.add(saQuery);                        
                        
                        rfSA.setTestdata(query);
                        
                        List<HashMap<String, String>> evaluationResultSA = rfSA.evaluateForestWithoutLabel();
                        
                        rcPrediction = evaluationResultKarakter.get(0).get("prediction");
                        ccPrediction = evaluationResultKarakter.get(1).get("prediction");
                        saPrediction = evaluationResultSA.get(0).get("prediction");
                        
                        double confRc = Double.parseDouble(evaluationResultKarakter.get(0).get("confidence")),
                            confCc = Double.parseDouble(evaluationResultKarakter.get(1).get("confidence")),
                            confSa = Double.parseDouble(evaluationResultSA.get(0).get("confidence"));
                        
                        if(rcPrediction.trim().split("-")[0].equalsIgnoreCase("Karakter_Salah")) {
                            Crc = 1 - confRc;
                            Nrc = confRc;
                        } else {
                            Crc = confRc;
                            Nrc = 1-confRc;
                        }
                        
                        if(ccPrediction.trim().split("-")[0].equalsIgnoreCase("Karakter_Benar")) {
                            Ncc = confCc;
                            Ccc = 1-confCc;
                        } else {
                            Ncc = 1 - confCc;
                            Ccc = confCc;
                        }
                        
                        if(saPrediction.trim().split("-")[0].equalsIgnoreCase("SA_Benar")) {
                            Csa = confSa;
                            Nsa = 1-confSa;
                        } else {
                            Csa = 1 - confSa;
                            Nsa = confSa;
                        }
                        
                        csp = Csa + Ccc + (1 - Nrc);
                        isp = Nsa + Ncc + Crc;                                                
                        
                        boolean validity = csp > isp ? true : false;
                        
                        csps[k] = csp;
                        validities[k] = validity;
                    }
                    
                    if(validities[0] == false) {
                        pspsSubword.remove(j);
                        j--;
                    } else {
                        double maks = csps[0], maksIndex=0;
                        
                        for(int k=0; k<csps.length; k++) {
                            if(csps[k] > maks) {
                                maks = csps[k];
                                maksIndex = k;
                            }
                        }
                        
                        if(maksIndex != 0) {
                            pspsSubword.remove(j);
                            j--;
                        }
                    }
                }  
                
                pspsSubword.remove(Integer.valueOf(sp1));
                pspsSubword.remove(Integer.valueOf(sp2));
                
                pspsV.addAll(pspsSubword);
            }                                      
            
            fileIOSP.writeSegmentationPoint("results/segmentation/psps/"+namaSegmentasi+"/"+
                    data.getNama().split("\\.")[0].concat(".psps"), pspsV);
            
            proc.writeImage(proc.renderWordAccordingToSegmentationPoints(image, sps, pspsV), 
                    "results/segmentation/image/"+namaSegmentasi+"/"+data.getNama());
            
            String pathOverSegmented = "results/oversegmented/"+selectedDataset+"/"+data.getNama();
            if(!new java.io.File(pathOverSegmented).exists())
                proc.writeImage(proc.renderWordAccordingToSegmentationPoints(image, sps, pspsOS),
                    pathOverSegmented);
            
            this.updateProgress(progress+=update);
        }
    }
    
    private void comparing() {     
        FileIOSegmentationPoints ioSP = new FileIOSegmentationPoints();
        FileIOComparationResult ioCR = new FileIOComparationResult();
        FileIOOverallResult ioOR = new FileIOOverallResult();
        FileIODataTest fileIOData = new FileIODataTest();                
        
        String namaSegmentasi = this.menu.getTextNamaSegmentasi().getText();                
        
        String pathHasilPSPs = "results/segmentation/psps/"+namaSegmentasi+"/";        
        
        String namaDataset = (String) this.menu.getComboDataset().getSelectedItem();
        
        fileIOData.setNamaDataset(namaDataset);

        List<DataTest> datas = fileIOData.get();
        
        ioCR.create(namaSegmentasi);
        
        double update = 100 / (double)datas.size(), progress = 0; 
        
        this.changeProgressInfo("4/4 (comparing sementation results)");
        
        int totalPsps = 0, totalPspsAcuan = 0;
        
        int overallPspsBenar = 0, overallPspsSalah = 0;
        for(DataTest data : datas) {
            List<int[]> pspAreasAcuan = ioSP.readAreaPSP(data.getDirektoriPSpAreas());
            List<Integer> pspsUji = ioSP.readSegmentationPoint(pathHasilPSPs.concat(data.getNama().split("\\.")[0]+".psps"));

            List<Integer> tempPspsUji = new LinkedList<>();
            
            for(int e : pspsUji) {
                tempPspsUji.add(e);
            }
            
            int correctPsp = 0, errorPsp = 0;
            int missedPsp = 0, badPsp = 0, overPsp = 0;
            for(int[] points : pspAreasAcuan) {
                
                boolean pspOnAreaAcuan = false;
                int indexPspFoundOnArea=-1;
                
                if(tempPspsUji.size() > 0) {
                    for(int i=0; i<tempPspsUji.size(); i++) {
                        if(tempPspsUji.get(i) >= points[0] && tempPspsUji.get(i) <= points[1]) {
                            if(pspOnAreaAcuan) overPsp++;
                            else {
                                indexPspFoundOnArea = i;
                                pspOnAreaAcuan = true;
                            }

                        } 
                    }
                } else missedPsp++;
                
                if(pspOnAreaAcuan) {
                    correctPsp++;
                    tempPspsUji.remove(indexPspFoundOnArea);
                }
                
                totalPspsAcuan++;
            }
            
            badPsp = tempPspsUji.size();
            
            errorPsp += missedPsp + badPsp + overPsp;
            
            ioCR.insert(new ComparationResult(data.getNama().split("\\.")[0], namaSegmentasi, pspAreasAcuan.size(),
                    pspsUji.size(), correctPsp, errorPsp, overPsp, missedPsp, badPsp, data.getDirektoriOri(),
                    data.getDirekotoriSegmented(), "results/oversegmented/"+namaDataset+"/"+data.getNama(),
                    "results/segmentation/image/"+namaSegmentasi+"/"+data.getNama()));
            
            overallPspsBenar += correctPsp;
            overallPspsSalah += errorPsp;
            
            this.updateProgress(progress+=update);
        }
        
        totalPsps = overallPspsBenar + overallPspsSalah;
        
        ioOR.insert(new OverallResult(namaSegmentasi, totalPspsAcuan, totalPsps, overallPspsBenar, overallPspsSalah));
        
        this.changeProgressInfo("Selesai :3");
    }
    
    private List<Integer> eliminatePspsInLigatures(String dataName, List<Integer> psps) {      
        String path = "temp/ligatures.dat";
        
        BufferedReader br = null;
        
        String line;

        try {
            br = new BufferedReader(new FileReader(path));
            
            while((line = br.readLine()) != null) {
                String[] contents = line.split(",");

                if(contents[0].equalsIgnoreCase(dataName)) {
                    List<Integer> toRemove = new LinkedList<>();
                    
                    String[] areas = contents[1].split("-");

                    int border1 = Integer.parseInt(areas[0]),
                            border2 = Integer.parseInt(areas[1]);
                    
                    for(Integer psp : psps) {
                        if(psp <= border2 && psp >= border1) {
                            toRemove.add(psp);                            
                        }
                    }
                    
                    for(Integer psp : toRemove) {
                        psps.remove(psp);
//                        System.out.println(psp);
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
        
        return psps;
    }
           
    private void changeProgressInfo(String info) {      
        this.menu.getLabelInfo().setText(info);
        
        updateProgress(0);
    }
    
    private void updateProgress(double progress) {
        this.menu.getProgressSegmentasi().setValue((int) Math.round(progress));
    }
    
    private class ProcessRunnable implements Runnable {

        @Override
        public void run() {
            Thread t1 = new Thread(new SegmentasiRunnable());
            
            t1.start();
            
            try {
                t1.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControllerSegmentasi.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            comparing();    
            
            setEnablityAllInput(true);
        }
        
    }
    
    private class SegmentasiRunnable implements Runnable {

        @Override
        public void run() {
            segmenting();
        }
        
    }
    
    private void setEnablityAllInput(boolean enabled) {
        this.menu.getButtonMulaiSegmentasi().setEnabled(enabled);
        this.menu.getComboDataset().setEnabled(enabled);
        this.menu.getComboHasilPelatihan().setEnabled(enabled);
        this.menu.getComboMetodeEkstraksi().setEnabled(enabled);
        this.menu.getTextNamaSegmentasi().setEnabled(enabled);
    }
}
