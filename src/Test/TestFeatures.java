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

import Processor.FeatureExtractor.FeatureExtractor;
import Processor.FeatureExtractor.LineSegments.LineSegments;
import Processor.FeatureExtractor.PixelDensity.PixelDensity;
import Processor.FeatureExtractor.WUMI.WUMI;
import Processor.FeatureExtractor.ZoneCharacteristics.ZoneCharacteristics;
import Processor.FeatureExtractor.Zoning.Zoning;
import Processor.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author acdwisu
 */
public class TestFeatures {
    public static void main(String[] args) {
        new TestFeatures().run1();
    }
    
    private void run1() {
        ImageProcessor proc = new ImageProcessor();

        String[] files = {"obj3psp1area0.bmp", "obj3psp1area1.bmp", "obj3psp1area2.bmp", "obj3psp1area3.bmp"};

        HashMap<String, Integer> config = new HashMap<>();
                
        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
        
//            FeatureExtractor e = new WUMI();
        FeatureExtractor e = new LineSegments(config);
        
        for(String file : files) {
            BufferedImage image = proc.readImage(file);                       
            
            int[][] img = proc.imageToArray(image);

            e.setImg(img);
            
            double[] features = e.getFeatures();

            for(double p : features) {
                System.out.print(p+",");
            }
            System.out.println("");
        }
        
    }
    
    public void run2() {
        BufferedImage buff = null;
        
        File parent = new File("C:\\Users\\user\\Documents\\NetBeansProjects\\HWChineseDigit\\testekstraksi\\waw");
        
        ImageProcessor proc = new ImageProcessor();
        
        for(File file : parent.listFiles()) {
            try {
                if(!file.toString().toLowerCase().contains(".jpg")) continue;
                
                buff = ImageIO.read(file);
                
                int[][] img = proc.imageToArray(buff);
                int[][] thinned = proc.thinning(img);
                
                HashMap<String, Integer> config = new HashMap<>();
                
                config.put("zoneHorizontalCount", 3);
                config.put("zoneVerticalCount", 3);
                config.put("zoneType", Zoning.STATIC_ZONE);
                
                FeatureExtractor pd = new PixelDensity(config, thinned);
                
                double[] features = pd.getFeatures();
                
                for(double feature : features) {
                    System.out.print(feature+ ", ");
                }
                
                System.out.println(file.getParentFile().getName());
            } catch (IOException ex) {
                Logger.getLogger(TestFeatures.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void run3() {
        BufferedImage buff = null;
        
        File parent = new File("C:\\Users\\Aha\\Documents\\datasets swta\\karakter\\Benar");
        
        List<List<String>> featuresList = new LinkedList();
        
        ImageProcessor proc = new ImageProcessor();
        
        HashMap<String, Integer> config = new HashMap<>();

        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
        
        FeatureExtractor pd = new PixelDensity(config);
        
        for(File file : parent.listFiles()) {
            List<String> f = new LinkedList();
                    
            if(!file.toString().toLowerCase().contains(".bmp")) continue;

            buff = proc.readImage(file.getPath());

            int[][] img = proc.imageToArray(buff);
            int[][] thinned = proc.normalizeWordImageSize(proc.thinning(img));

                        pd.setImg(thinned);
            double[] features = pd.getFeatures();

            for(double feature : features) {
                f.add(String.valueOf(feature));
            }

            f.add("Karakter_Benar");                
            
            featuresList.add(f);
        }
        
        parent = new File("C:\\Users\\Aha\\Documents\\datasets swta\\karakter\\Salah");
        
        for(File file : parent.listFiles()) {
            List<String> f = new LinkedList();
            
            if(!file.toString().toLowerCase().contains(".bmp")) continue;

            buff = proc.readImage(file.getPath());

            int[][] img = proc.imageToArray(buff);
            int[][] thinned = proc.normalizeWordImageSize(proc.thinning(img));       

            pd.setImg(thinned);
            double[] features = pd.getFeatures();

            for(double feature : features) {
                f.add(feature+ "");
            }

            f.add("Karakter_Salah");                
            
            featuresList.add(f);
        }
        
////        Collections.shuffle(featuresList);
        
        for(List<String> x : featuresList) {
            for(String y : x) {
                System.out.print(x);
            }
            System.out.println("");
        }
    }
    
    public void run4() {
        BufferedImage buff = null;
        
        File parent = new File("C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_multiclass");
        
        ImageProcessor proc = new ImageProcessor();
        
        HashMap<String, Integer> config = new HashMap<>();

        config.put("zoneHorizontalCount", 3);
        config.put("zoneVerticalCount", 3);
        config.put("offsetX", 3);
        config.put("offsetY", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
//        config.put("zoneType", Zoning.ADAPTIVE_ZONE);

        FeatureExtractor extractor;
        
        extractor = new LineSegments(config);
//        extractor = new PixelDensity(config);
//        extractor = new ZoneCharacteristics(config);
//        extractor = new WUMI();
        
        for(File folder : parent.listFiles()) {
            if(!folder.isDirectory()) continue;
            for(File file : folder.listFiles()) {
                if(!file.getName().toLowerCase().endsWith(".bmp")) continue;
                buff = proc.readImage(file.getPath());

                int[][] img = proc.imageToArray(buff);

                extractor.setImg(img);

                double[] features = extractor.getFeatures();

                for(double feature : features) {
                    System.out.print(feature+ ",");
                }

                System.out.println(folder.getName());
            }
        }
    }
    
    public void run5() {
        BufferedImage buff = null;
        
        File parent = new File("C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_multiclass");
        
        ImageProcessor proc = new ImageProcessor();
        
        HashMap<String, Integer> config = new HashMap<>();

        config.put("zoneHorizontalCount", 4);
        config.put("zoneVerticalCount", 4);
        config.put("offsetX", 3);
        config.put("offsetY", 3);
        config.put("zoneType", Zoning.STATIC_ZONE);
//        config.put("zoneType", Zoning.ADAPTIVE_ZONE);

        FeatureExtractor extractor;
        
//        extractor = new LineSegments(config);
//        extractor = new PixelDensity(config);
        extractor = new ZoneCharacteristics(config);
//        extractor = new WUMI();
        
        List<List<String>> list = new LinkedList();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(12);
        df.setGroupingUsed(false);
        
        for(File folder : parent.listFiles()) {
            if(!folder.isDirectory()) continue;
            for(File file : folder.listFiles()) {
                List<String> query = new LinkedList();
                
                if(!file.getName().toLowerCase().endsWith(".bmp") && !file.getName().toLowerCase().endsWith(".png")) continue;
                buff = proc.readImage(file.getPath());

//                int[][] img = proc.imageToArray(buff);

                int[][] img = proc.thinning(proc.imageToArray(buff));
                
                extractor.setImg(proc.normalizeWordImageSize(img));
                
//                extractor.setImg(img);

                double[] features = extractor.getFeatures();

                for(double feature : features) {
                    query.add(df.format(feature).concat(","));
//                    System.out.print(feature+ ",");
                }

                query.add(folder.getName());
//                System.out.println(folder.getName());

                list.add(query);
            }
        }
        
        Collections.shuffle(list);
        
        for(List<String> query : list) {
            for(String x : query) {
                System.out.print(x);
            }
            System.out.println("");
        }
    }
    
    private void testNull() {
        double d = Double.NaN;
        
        System.out.println(Double.isNaN(d));
    }
}
