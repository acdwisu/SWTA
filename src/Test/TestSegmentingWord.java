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
package Test;

import Controller.ControllerDataUji;
import Controller.ControllerPelatihan;
import Model.SegmentedWord;
import Processor.FeatureExtractor.FeatureExtractor;
import Processor.FeatureExtractor.LineSegments.LineSegments;
import Processor.FeatureExtractor.PixelDensity.PixelDensity;
import Processor.FeatureExtractor.WUMI.WUMI;
import Processor.FeatureExtractor.ZoneCharacteristics.ZoneCharacteristics;
import Processor.FeatureExtractor.Zoning.Zoning;
import Processor.ImageProcessor;
import Processor.Learner.RandomForests.RandomForest;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author acdwisu
 */
public class TestSegmentingWord {
    
    public static void main(String args[]) {
        new TestSegmentingWord().testPSPseek4();
    }
    
    private void run() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke2.bmp"));

            int[][] imageArray = p.imageToArray(image);                        
            imageArray = p.universeOfDiscourse(imageArray, true, true);
            
            SegmentedWord x = p.subWordSeparation(imageArray);
            
            javax.imageio.ImageIO.write(p.ArrayToImage(x.getSegmentedWord()), 
                    "bmp", new File("segmented-testFollowtroke2.bmp"));
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void run2() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            File folderParent = new File("E:\\Dwi\\New folder\\IESKarDB-sample\\Binary_reversed");
            
            for(File file : folderParent.listFiles()) {
                String fileName = file.getName().toLowerCase();
                if(!(fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png"))) continue;
                
                BufferedImage image = javax.imageio.ImageIO.read(file);

                int[][] imageArray = p.imageToArray(image);                        
                imageArray = p.universeOfDiscourse(imageArray, true, true);
                System.out.println(fileName);
                SegmentedWord x = p.subWordSeparation(imageArray);
                
                String folderRootDest = "E:\\Dwi\\New folder\\IESKarDB-sample\\Dots_removed";

                javax.imageio.ImageIO.write(p.ArrayToImage(x.getSegmentedWord()),
                        "bmp", new File(folderRootDest+ "\\" +file.getName()));
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void run3() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke.bmp"));

            int[][] imageArray = p.imageToArray(image);                        
            imageArray = p.universeOfDiscourse(imageArray, true, true);           
            
            List<List<Point>> objects = p.getObjectsFromWord(imageArray); 
            
            List<Integer> dotsObjectsIndex = p.dotsDetector(objects, imageArray);
            
            p.calcAverageDotsDistanceToNearestSubWord(objects, dotsObjectsIndex);
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void runSelectWordObject() {
        File folder = new File("C:\\Users\\user\\Documents\\Binary_reversed");
        
        for(File file : folder.listFiles()) {
            String fileName = file.getName();
            String loweredCase = fileName.toLowerCase();
            
            if(!loweredCase.endsWith(".jpg") && !loweredCase.endsWith(".bmp") && !loweredCase.endsWith(".png")) continue;
            
            System.out.println(fileName);
            
            
        }
    }
    
    private void testPSPseek() {
        ImageProcessor p = new ImageProcessor();
            
        BufferedImage image = p.readImage("segmented-testFollowtroke2.bmp");
        
        List<Integer> psps = p.overSegmentation(image, p.detectSegmentationPoints(p.imageToArray(image)));
        
        
    }
    
    private void testPSPseek2() {
        File folder = new File("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\original");
        ImageProcessor p = new ImageProcessor();
        
        for(File file : folder.listFiles()) {
            String fileName = file.getName();
            String loweredCase = fileName.toLowerCase();
            
            if(!loweredCase.endsWith(".jpg") && !loweredCase.endsWith(".bmp") && !loweredCase.endsWith(".png")) continue;
            
            System.out.println(fileName);
            
            BufferedImage image = p.readImage(file.getPath());
            image = p.ArrayToImage(p.universeOfDiscourse(p.imageToArray(image), true, true));
        
            int[][] imgSeparated = p.separateWord(
                                    p.getObjectsFromWord(
                                            p.imageToArray(image)), 
                                    this.urgenWordObjectIndexManual(fileName), image.getWidth(), image.getHeight());
            
            List<Integer> sps = p.detectSegmentationPoints(imgSeparated);
            
            List<Integer> psps = p.overSegmentation(p.ArrayToImage(imgSeparated), sps);
            
            psps = this.eliminatePspsInLigatures(fileName, psps);
            
            p.writeImage(p.renderWordAccordingToSegmentationPoints(p.ArrayToImage(imgSeparated), sps, psps), 
                    "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\over_segmented2\\"+fileName);
        }
    }
    
    private void testPSPseek3() {
        File file = new File("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\original\\b_J01_018.bmp");
        String fileName = file.getName();
        ImageProcessor p = new ImageProcessor();
        
        BufferedImage image = p.readImage(file.getPath());
        image = p.ArrayToImage(p.universeOfDiscourse(p.imageToArray(image), true, true));

        int[][] imgSeparated = p.separateWord(
                                p.getObjectsFromWord(
                                        p.imageToArray(image)), 
                                this.urgenWordObjectIndexManual(fileName), image.getWidth(), image.getHeight());

        List<Integer> sps = p.detectSegmentationPoints(imgSeparated);

        List<Integer> psps = p.overSegmentation(p.ArrayToImage(imgSeparated), sps);

        psps = this.eliminatePspsInLigatures(fileName, psps);

        p.writeImage(p.renderWordAccordingToSegmentationPoints(p.ArrayToImage(imgSeparated), sps, psps), 
                "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\over_segmented\\"+fileName);       
    }
     
    private class runnableRFValidation implements Runnable {        
        
        private File file;
        private RandomForest rfKarakter, rfSA;        
        private FeatureExtractor extractor;
        
        public runnableRFValidation(File file, RandomForest rfKarakter, RandomForest rfSA, FeatureExtractor extractor) {
            this.file = file;
            this.extractor = extractor;
            this.rfKarakter = rfKarakter;
            this.rfSA = rfSA;
        }
        
        @Override
        public void run() {
            randomForestValidation(file, rfKarakter, rfSA, extractor);
        }
        
    }
    
    private void randomForestValidation(File file, RandomForest rfKarakter, RandomForest rfSA, FeatureExtractor extractor) {
        ImageProcessor p = new ImageProcessor();
        
        String fileName = file.getName();
        String loweredCase = fileName.toLowerCase();              

        BufferedImage image = p.readImage(file.getPath());
        image = p.ArrayToImage(p.universeOfDiscourse(p.imageToArray(image), true, true));

        int[][] imgSeparated = p.separateWord(
                                p.getObjectsFromWord(
                                        p.imageToArray(image)), 
                                this.urgenWordObjectIndexManual(fileName), image.getWidth(), image.getHeight());

        List<Integer> sps = p.detectSegmentationPoints(imgSeparated);

        List<Integer> pspsOS = p.overSegmentation(p.ArrayToImage(imgSeparated), sps);

        pspsOS = this.eliminatePspsInLigatures(fileName, pspsOS);

        // validation of psps by random forests
        List<Integer> pspsV = new LinkedList<>();

        int[][] imgThinned = p.thinning(imgSeparated);            

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
                List<HashMap<String, int[][]>> combinations = p.pspScanning(imgThinned, pspsSubword, j);

                double[] csps = new double[combinations.size()];
                boolean[] validities = new boolean[combinations.size()];

                for(int k=0; k<combinations.size(); k++) {
                    double[] rcFeature, ccFeature, saFeature;

                    //   extract feature of each area
                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("rc")));
                    rcFeature = extractor.getFeatures();

                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("cc")));
                    ccFeature = extractor.getFeatures();

                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("sa")));
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
                        saQuery.add(String.valueOf(saFeature[l]));
                    }

                    query.add(rcQuery);
                    query.add(ccQuery);                        

                    rfKarakter.setTestdata(query);

                    List<HashMap<String, String>> evaluationResultKarakter = rfKarakter.evaluateForestWithoutLabel();

                    query.clear();
                    query.add(saQuery);                        

                    rfSA.setTestdata(query);

                    List<HashMap<String, String>> evaluationResultSA = rfSA.evaluateForestWithoutLabel();

                    rcPrediction = evaluationResultKarakter.get(0).get("prediction");
                    ccPrediction = evaluationResultKarakter.get(1).get("prediction");
                    saPrediction = evaluationResultSA.get(0).get("prediction");

                    double confRc = Double.parseDouble(evaluationResultKarakter.get(0).get("confidence")),
                            confCc = Double.parseDouble(evaluationResultKarakter.get(1).get("confidence")),
                            confSa = Double.parseDouble(evaluationResultSA.get(0).get("confidence"));

                    if(rcPrediction.trim().equalsIgnoreCase("Karakter_Salah")) {
                        Crc = 1 - confRc;
                        Nrc = confRc;
                    } else {
                        Crc = confRc;
                        Nrc = 1-confRc;
                    }

                    if(ccPrediction.trim().equalsIgnoreCase("Karakter_Benar")) {
                        Ncc = 1 - confCc;
                        Ccc = confCc;                            
                    } else {
                        Ncc = confCc;
                        Ccc = 1-confCc;
                    }

                    if(saPrediction.trim().equalsIgnoreCase("SA_Benar")) {
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
                
                System.out.println("j:"+j);
                for(int f:pspsSubword) System.out.println("psps:"+f);
            }  

            pspsSubword.remove(Integer.valueOf(sp1));
            pspsSubword.remove(Integer.valueOf(sp2));

            pspsV.addAll(pspsSubword);
        } 

        p.writeImage(p.renderWordAccordingToSegmentationPoints(p.ArrayToImage(imgSeparated), sps, pspsV), 
                "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\RF_validated_DBAHCL_WUMI\\"+fileName);
        
        System.out.println(fileName);
    }
    
    private void randomForestValidation(File file, RandomForest rfKarakter, RandomForest rfSA, FeatureExtractor extractor, HashMap<String, Integer> config) {
        ImageProcessor p = new ImageProcessor();
        
        String fileName = file.getName();
        String loweredCase = fileName.toLowerCase();              

        BufferedImage image = p.readImage(file.getPath());
        image = p.ArrayToImage(p.universeOfDiscourse(p.imageToArray(image), true, true));

        int[][] imgSeparated = p.separateWord(
                                p.getObjectsFromWord(
                                        p.imageToArray(image)), 
                                this.urgenWordObjectIndexManual(fileName), image.getWidth(), image.getHeight());

        List<Integer> sps = p.detectSegmentationPoints(imgSeparated);

        List<Integer> pspsOS = p.overSegmentation(p.ArrayToImage(imgSeparated), sps);

        pspsOS = this.eliminatePspsInLigatures(fileName, pspsOS);

        // validation of psps by random forests
        List<Integer> pspsV = new LinkedList<>();

        int[][] imgThinned = p.thinning(imgSeparated);            

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
                List<HashMap<String, int[][]>> combinations = p.pspScanning(imgThinned, pspsSubword, j);

                double[] csps = new double[combinations.size()];
                boolean[] validities = new boolean[combinations.size()];

                for(int k=0; k<combinations.size(); k++) {
                    double[] rcFeature, ccFeature, saFeature;

                    //   extract feature of each area
                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("rc")));
                    rcFeature = extractor.getFeatures();
//p.writeImage(p.renderWordAccodringToZones(p.ArrayToImage(p.normalizeWordImageSize(p.normalizeWordImageSize(combinations.get(k).get("rc")))), extractor.getZonesPosition(config)), "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\test_zoned\\rc-"+fileName);
                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("cc")));
                    ccFeature = extractor.getFeatures();
//p.writeImage(p.renderWordAccodringToZones(p.ArrayToImage(p.normalizeWordImageSize(p.normalizeWordImageSize(combinations.get(k).get("cc")))), extractor.getZonesPosition(config)), "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\test_zoned\\cc-"+fileName);
                    extractor.setImg(p.normalizeWordImageSize(combinations.get(k).get("sa")));
                    saFeature = extractor.getFeatures();
//p.writeImage(p.renderWordAccodringToZones(p.ArrayToImage(p.normalizeWordImageSize(p.normalizeWordImageSize(combinations.get(k).get("sa")))), extractor.getZonesPosition(config)), "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\test_zoned\\sa-"+fileName);
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
                        saQuery.add(String.valueOf(saFeature[l]));
                    }

                    query.add(rcQuery);
                    query.add(ccQuery);                        

                    rfKarakter.setTestdata(query);

                    List<HashMap<String, String>> evaluationResultKarakter = rfKarakter.evaluateForestWithoutLabel();

                    query.clear();
                    query.add(saQuery);                        

                    rfSA.setTestdata(query);

                    List<HashMap<String, String>> evaluationResultSA = rfSA.evaluateForestWithoutLabel();

                    rcPrediction = evaluationResultKarakter.get(0).get("prediction");
                    ccPrediction = evaluationResultKarakter.get(1).get("prediction");
                    saPrediction = evaluationResultSA.get(0).get("prediction");

                    double confRc = Double.parseDouble(evaluationResultKarakter.get(0).get("confidence")),
                            confCc = Double.parseDouble(evaluationResultKarakter.get(1).get("confidence")),
                            confSa = Double.parseDouble(evaluationResultSA.get(0).get("confidence"));

                    if(rcPrediction.trim().equalsIgnoreCase("Karakter_Salah")) {
                        Crc = 1 - confRc;
                        Nrc = confRc;
                    } else {
                        Crc = confRc;
                        Nrc = 1-confRc;
                    }

                    if(ccPrediction.trim().equalsIgnoreCase("Karakter_Benar")) {
                        Ncc = 1 - confCc;
                        Ccc = confCc;                            
                    } else {
                        Ncc = confCc;
                        Ccc = 1-confCc;
                    }

                    if(saPrediction.trim().equalsIgnoreCase("SA_Benar")) {
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
                
                System.out.println("j:"+j);
                for(int f:pspsSubword) System.out.println("psps:"+f);
            }  

            pspsSubword.remove(Integer.valueOf(sp1));
            pspsSubword.remove(Integer.valueOf(sp2));

            pspsV.addAll(pspsSubword);
        } 

        p.writeImage(p.renderWordAccordingToSegmentationPoints(p.ArrayToImage(imgSeparated), sps, pspsV), 
                "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\RF_validated_DBAHCL_SZZC\\"+fileName);
        
        System.out.println(fileName);
    }
    
    private void testPSPseek4() {
        File folder = new File("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\u-dont-say-2\\original");
        
        HashMap<String, Integer> config = new HashMap<>();
        
        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("offsetX", 3);
        config.put("offsetY", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
//        config.put("zoneType", Zoning.ADAPTIVE_ZONE);
        
//        FeatureExtractor extractor = new WUMI();
//        FeatureExtractor extractor = new ZoneCharacteristics(config);
        FeatureExtractor extractor = new PixelDensity(config);
//        FeatureExtractor extractor = new LineSegments(config);
        
        RandomForest rfKarakter = new RandomForest("knowledge/szpd_u-dont-say-2_30/Karakter"), 
                rfSA = new RandomForest("knowledge/szpd_u-dont-say-2_30/SA");
        
        rfKarakter.train();
        rfSA.train();
                
        for(File file : folder.listFiles()) {
            String fileName = file.getName();
            String loweredCase = fileName.toLowerCase();  
        
            if(!loweredCase.endsWith(".jpg") && !loweredCase.endsWith(".bmp") && !loweredCase.endsWith(".png")) continue;
            
            this.randomForestValidation(file, rfKarakter, rfSA, extractor);
        }
                
    }
    
    private void testPSPseek5() {
        File folder = new File("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\original");
        
        HashMap<String, Integer> config = new HashMap<>();
        
        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("offsetX", 3);
        config.put("offsetY", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
        
//        FeatureExtractor extractor = new WUMI();
        FeatureExtractor extractor = new ZoneCharacteristics(config);
//        FeatureExtractor extractor = new PixelDensity(config);
//        FeatureExtractor extractor = new LineSegments(config);
        
        RandomForest rfKarakter = new RandomForest("knowledge/TRAIN_Dataset-latih1_AZZC_100/Karakter"), 
                rfSA = new RandomForest("knowledge/TRAIN_Dataset-latih1_AZZC_100/SA");
        
        rfKarakter.train();
        rfSA.train();
        
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());       
        
        for(File file : folder.listFiles()) {
            String fileName = file.getName();
            String loweredCase = fileName.toLowerCase();  
        
            if(!loweredCase.endsWith(".jpg") && !loweredCase.endsWith(".bmp") && !loweredCase.endsWith(".png")) continue;
            
            pool.execute(new runnableRFValidation(file,rfKarakter,rfSA,extractor));
        }
        
        pool.shutdown();
    }
    
    private void testClassifiyPSPArea() {
        File folder = new File("C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\as dataset\\Salah");
        
        ImageProcessor proc = new ImageProcessor();
        
        HashMap<String, Integer> config = new HashMap<>();
        
        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("offsetX", 3);
        config.put("offsetY", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
//        config.put("zoneType", Zoning.ADAPTIVE_ZONE);
        
//        FeatureExtractor extractor = new WUMI();
//        FeatureExtractor extractor = new ZoneCharacteristics(config);
//        FeatureExtractor extractor = new PixelDensity(config);
        FeatureExtractor extractor = new LineSegments(config);
        
        RandomForest rfKarakter = new RandomForest("knowledge/TRAIN_SZLS_Dataset-latih1_100/Karakter");
        
        rfKarakter.train();
            
        for(File file : folder.listFiles()) {
            if(!file.getName().endsWith(".bmp")) continue;
            
            double[] features;
            
            extractor.setImg(proc.imageToArray(proc.readImage(file.getPath())));
            
            features = extractor.getFeatures();
            
            //      make test query
            ArrayList<ArrayList<String>> query = new ArrayList<>();

            ArrayList<String> ccQuery = new ArrayList<>();

            for(int l=0; l<features.length; l++) {
                ccQuery.add(String.valueOf(features[l]));
            }

            query.add(ccQuery);

            rfKarakter.setTestdata(query);

            List<HashMap<String, String>> evaluationResultKarakter = rfKarakter.evaluateForestWithoutLabel();
            
            System.out.println(file.getName()+ ": ");
            System.out.println("prediction:" +evaluationResultKarakter.get(0).get("prediction"));
            System.out.println("confidence:" +evaluationResultKarakter.get(0).get("confidence"));
        }
                
    }
    
    private void testPSPScanning() {
        ImageProcessor p = new ImageProcessor();
        
        String fileName = "b_A01_002.bmp";
        
        BufferedImage buff = p.readImage("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\processed\\"+fileName);
                
        int[][] img = p.imageToArray(buff),
                imgthinned = p.thinning(img);
        
        List<Integer> sps = p.detectSegmentationPoints(img);

        List<Integer> pspsOS = p.overSegmentation(p.ArrayToImage(img), sps);
               
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
            System.out.println("pspsSubword.size():"+pspsSubword.size());
            // traverse all subword psps
            for(int j=1; j<pspsSubword.size()-1; j++) {
                //   generate areas by scanning each psps then combine those
                System.out.println("j:"+j);
                List<HashMap<String, int[][]>> combinations = p.pspScanning(imgthinned, pspsSubword, j);

                for(int k=0; k<combinations.size(); k++) {
                    System.out.println("height:"+combinations.get(k).get("sa").length);
                    System.out.println("width:"+combinations.get(k).get("sa")[0].length);
                    System.out.println("------------");
                    p.writeImage(p.ArrayToImage(combinations.get(k).get("cc")), "obj"+i+"psp"+j+"area"+k+".bmp");
                }

            }
            System.out.println("=================");
        }
    }
    
     private void testPSPScanning2() {
        ImageProcessor p = new ImageProcessor();
        
        String dest = "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\pspScanned";
        
        for(File file : new File("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\processed").listFiles()) {
            String fileName = file.getPath();
            
            System.out.println(file.getName());
            
            BufferedImage buff = p.readImage(fileName);

            int[][] img = p.imageToArray(buff),
                    imgthinned = p.thinning(img);

            List<Integer> sps = p.detectSegmentationPoints(img);

            List<Integer> pspsOS = p.overSegmentation(p.ArrayToImage(img), sps);

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
                    List<HashMap<String, int[][]>> combinations = p.pspScanning(imgthinned, pspsSubword, j);

                    for(int k=0; k<combinations.size(); k++) {
                        p.writeImage(p.ArrayToImage(combinations.get(k).get("cc")), dest+"\\"+file.getName()+"-obj"+i+
                                "-psp"+j+"-area"+k+".bmp");
                    }

                }
                System.out.println("=================");
            }
        }
    }
    
    private void testObjectCount() {
        File folder = new File("C:\\Users\\Aha\\Downloads\\j\\DBAHCL_Binarized");
        ImageProcessor p = new ImageProcessor();
        
        for(File file : folder.listFiles()) {
            String fileName = file.getName();
            String loweredCase = fileName.toLowerCase();
            
            if(!loweredCase.endsWith(".jpg") && !loweredCase.endsWith(".bmp") && !loweredCase.endsWith(".png")) continue;
            
            System.out.println(fileName);
            
            BufferedImage image = p.readImage(file.getPath());
            
            int[][] img = p.imageToArray(image);
            
            List<List<Point>> objects = p.getObjectsFromWord(img);
            
            if(objects.size() > 1) {
                p.writeImage(image, 
                    "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\multi_object\\"+fileName);    
            }
                                    
        }
    }
    
    List<Integer> urgenWordObjectIndexManual(String fileName) {
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
}
