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
import FileIO.FileIOComparationResult;
import FileIO.FileIODataTest;
import FileIO.FileIOOverallResult;
import FileIO.FileIOSegmentationPoints;
import Model.ComparationResult;
import Model.DataTest;
import Model.SegmentedWord;
import Processor.ImageProcessor;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestImageProcessor {
    public static void main(String args[]) {
//        new TestImageProcessor().testKarakterSalahGeneration3();
        new TestImageProcessor().testKarakterSalahGeneration4();
    }
    
    private void run() {
        String filename = "b_a01_017.bmp";
        
        ImageProcessor p = new ImageProcessor();
        
        BufferedImage image = p.readImage("C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\original\\"+filename);

        int[][] imageArray = p.imageToArray(image);
        
        List<Integer> wordsIndex = this.urgenWordObjectIndexManual(filename);

        SegmentedWord sw = p.subWordSeparation(imageArray, wordsIndex);
        
        int[][] i = sw.getSegmentedWord();
        
        for(int j:sw.getSegmentationPoints()) {
            System.out.print(j+",");
        }
//        imageArray = p.thinning(imageArray);
//        imageArray = p.universeOfDiscource(imageArray, true, true);

        p.writeImage(p.ArrayToImage(i), "C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\processed\\"+filename);     
    }        
    
    private void run2() {       
        
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke.bmp"));

            int[][] imageArray = p.imageToArray(image);                       
            
            List<List<Point>> objects = p.getObjectsFromWord(imageArray);                                                
            
            List<Integer> dotsObjectsIndex = p.dotsDetector(objects, imageArray);
            
            for(int i : dotsObjectsIndex) {
                System.out.println("" +i);
            }
            
//            javax.imageio.ImageIO.write(p.ArrayToImage(imageArray), 
//                    "bmp", new File("C:\\Users\\user\\Documents\\NetBeansProjects\\Coba thinning\\A01_001(2).bmp"));
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void readPixelValue() {
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testStrokes.bmp"));

            for(int i=0; i<image.getHeight(); i++) {
                for(int j=0; j<image.getWidth(); j++) {
                    Color c = new Color(image.getRGB(j, i));
                    int merah = c.getRed(), hijau = c.getGreen(), biru = c.getBlue();
                    System.out.printf("%3d|%3d|%3d ", merah, hijau, biru);
                }
                System.out.println("");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void run3() {
        ImageProcessor p = new ImageProcessor();
        
        File parent = new File("C:\\Users\\user\\Documents\\NetBeansProjects\\HWChineseDigit\\testekstraksi");
        File parentDes = new File("C:\\Users\\user\\Documents\\NetBeansProjects\\HWChineseDigit\\testekstraksipro");
        
        for(File folder : parent.listFiles()) {
            File folderDest = new File(parentDes.getPath().concat("\\"+folder.getName()));
            folderDest.mkdir();
        
            for(File file : folder.listFiles()) {
                try {
                    if(!file.getName().toLowerCase().endsWith(".jpg")) continue;
                    
                    BufferedImage image = javax.imageio.ImageIO.read(file);
                    //image = p.grayscale(image);
                    image = p.binarization(image, 127);
                    
                    int[][] imageArray = p.imageToArray(image);
                    imageArray = p.thinning(imageArray);
                    imageArray = p.universeOfDiscourse(imageArray, true, true);
                    
                    List<List<Point>> objects = p.getObjectsFromWord(imageArray);
                                                           
                    int index=0;
                    int temp = objects.get(0).size(), i=0;
                    for(List<Point> object : objects) {
                        if(temp <= object.size()) 
                            index = i;
                        i++;
                    }
                    
                    List<Point> biggestObject = objects.get(index);

                    javax.imageio.ImageIO.write(p.ArrayToImage(p.drawFromPoints(biggestObject, imageArray[0].length, imageArray.length, true, true)), 
                            "bmp", new File(folderDest.getPath().concat("\\"+file.getName())));

                } catch (IOException ex) {
                    Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
                
    }
    
    private void run4() {
        ImageProcessor proc = new ImageProcessor();
        
        String rootSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL\\DB CHARACTERS";
        String rootDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_multiclass";
        
        File src = new File(rootSrc);
        
        for(File subFolder : src.listFiles()) {
            if(!subFolder.isDirectory()) continue;
            
            for(File file : subFolder.listFiles()) {
                String fileName = file.getName().toLowerCase();
                
                if(fileName.endsWith(".jpg") || fileName.endsWith(".bmp") 
                        || fileName.endsWith(".bmp") || fileName.endsWith(".jpeg")) {
                    BufferedImage buff = proc.readImage(file.getPath());

                    buff = proc.binarization(buff, 127);

                    int[][] img = proc.imageToArray(buff);

                    List<List<Point>> objects = proc.getObjectsFromWord(img);

                    int index=0;
                    int temp = objects.get(0).size(), i=0;
                    for(List<Point> object : objects) {
                        if(temp <= object.size()) 
                            index = i;
                        i++;
                    }

                    List<Point> biggestObject = objects.get(index);

                    new File(rootDest+"/"+subFolder.getName()).mkdir();
                    
                    proc.writeImage(proc.ArrayToImage(proc.drawFromPoints(biggestObject, img[0].length, img.length, true, true)), 
                        rootDest.concat("/" +subFolder.getName()+ "/" +fileName.split(".jpg")[0].concat(".bmp")));
                }
            }
        }
    }   
    
    private double[][] imageToMat(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        double[][] imgArr = new double[width][height];
        Raster raster = img.getData();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imgArr[i][j] = raster.getSample(i, j, 0);
            }
        }
        return imgArr;
    }
    
    public void testScale() {
        ImageProcessor proc = new ImageProcessor();
        
        BufferedImage img = proc.readImage("g.bmp");
        
        proc.writeImage(proc.ArrayToImage(proc.imageToArrayAndBinarize(proc.scale(img, 2, 75))), "g1.bmp");
    }
    
    private void testScale2() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_minimized",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_minimized_trimmed_rescaled";
        
        for(File x : new File(pathSrc).listFiles()) {
            if(x.getName().toLowerCase().endsWith(".bmp")) {
                BufferedImage img = proc.readImage(x.getPath());
                
                proc.writeImage(proc.scale(proc.ArrayToImage(proc.universeOfDiscourse(proc.imageToArray(img), true, true)), 2, 75), 
                        pathDest+"/"+x.getName());
            }
        }
    }
    
    private void testTrim() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_trimmed";
        
        for(File x : new File(pathSrc).listFiles()) {
            if(x.getName().toLowerCase().endsWith(".bmp")) {
                BufferedImage img = proc.readImage(x.getPath());
                
                proc.writeImage(proc.ArrayToImage(proc.universeOfDiscourse(proc.imageToArray(img), true, true)), 
                        pathDest+"/"+x.getName());
            }
        }
    }
    
    private void testKarakterSalahGeneration0() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_trimmed",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_splitted_trimmed";
        
        for(File x : new File(pathSrc).listFiles()) {
            if(x.getName().toLowerCase().endsWith(".bmp")) {
                BufferedImage img = proc.readImage(x.getPath());
                
                int newWidth = Math.round(img.getWidth()*0.6f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\1-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\2-"+x.getName());
                
                newWidth = Math.round(img.getWidth()*0.5f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\3-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\4-"+x.getName());
                
                newWidth = Math.round(img.getWidth()*0.3f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\5-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\6-"+x.getName());
            }
        }
    }
    
    private void testKarakterSalahGeneration() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_trimmed_rescaled",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_splitted_trimmed_rescaled";
        
        for(File x : new File(pathSrc).listFiles()) {
            if(x.getName().toLowerCase().endsWith(".bmp")) {
                BufferedImage img = proc.readImage(x.getPath());
                
                int newWidth = Math.round(img.getWidth()*0.6f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\1-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\2-"+x.getName());
                
                newWidth = Math.round(img.getWidth()*0.5f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\3-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\4-"+x.getName());
                
                newWidth = Math.round(img.getWidth()*0.3f);
                
                proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\5-"+x.getName());
                proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\6-"+x.getName());
            }
        }
    }

    private void testKarakterSalahGeneration2() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_trimmed",
                pathComparator = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_minimized_splitted_trimmed_rescaled",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_minimized_splitted_trimmed";

        String[] namesCompareFor = new File(pathComparator).list();
        
        for(File x : new File(pathSrc).listFiles()) {
            if(x.getName().toLowerCase().endsWith(".bmp")) {
                BufferedImage img = proc.readImage(x.getPath());
                
                int newWidth = Math.round(img.getWidth()*0.6f);

                String name;
                
                name = "1-"+x.getName();                
                if(isContain(namesCompareFor, name))
                    proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\"+name);
                
                name = "2-"+x.getName();                
                if(isContain(namesCompareFor, name))
                    proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\"+name);
                
                newWidth = Math.round(img.getWidth()*0.3f);
                
                name = "5-"+x.getName();           
                if(isContain(namesCompareFor, name))
                    proc.writeImage(img.getSubimage(0, 0, newWidth, img.getHeight()), pathDest+"\\"+name);
                
                name = "6-"+x.getName();           
                if(isContain(namesCompareFor, name))
                    proc.writeImage(img.getSubimage(img.getWidth()-newWidth-1, 0, newWidth, img.getHeight()), pathDest+"\\"+name);
            }
        }
    }
    
    private void testKarakterSalahGeneration3() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_splitted_trimmed",
                pathComparator = "C:\\Users\\Aha\\Documents\\data karakter\\Benar",
                pathDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_binarized_minimized_trimmed";

        String[] namesCompareFor = new File(pathComparator).list();
        
        for(File x : new File(pathSrc).listFiles()) {
            if(this.isContainEndsWith__x_x(namesCompareFor,x.getName())) {
                try {
                    Files.copy(x.toPath(), new File(pathDest+"\\"+x.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void testKarakterSalahGeneration4() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Documents\\dataset swta multiclass\\Benar",                
                pathDest = "C:\\Users\\Aha\\Documents\\dataset swta multiclass\\Salah";

        for(File kelas : new File(pathSrc).listFiles()) {
            
            String pathDestLeft = pathDest+"\\left_"+kelas.getName(),
                    pathDestRight = pathDest+"\\right_"+kelas.getName();
            
            new File(pathDestLeft).mkdirs();
            new File(pathDestRight).mkdirs();
            
            for(File file : kelas.listFiles()) {
                if(file.getName().endsWith(".bmp") == false) continue;
                
                BufferedImage img = proc.readImage(file.getPath());
                
                BufferedImage imgLeft = img.getSubimage(0, 0, img.getWidth()/2, img.getHeight()),
                        imgRight = img.getSubimage(img.getWidth()-img.getWidth()/2, 0, img.getWidth()/2, img.getHeight());
                
                proc.writeImage(imgLeft, pathDestLeft+"\\"+file.getName());
                proc.writeImage(imgRight, pathDestRight+"\\"+file.getName());
            }
        }
    }
    
    private boolean isContain(String[] list, String key) {
        for(String x : list) {
            if(x.equalsIgnoreCase(key)) return true;
        }
        
        return false;
    }
    
    private boolean isContainEndsWith__x_x(String[] list, String key) {
        for(String x : list) {
            if(x.endsWith(key)) return true;
        }
        
        return false;
    }
    
    public BufferedImage convertToBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
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
    
    private void testNormalizeImageSize() {
        ImageProcessor proc = new ImageProcessor();
        
        proc.writeImage(proc.ArrayToImage(proc.normalizeWordImageSize(proc.imageToArray(proc.readImage("b_A01_007.bmp")))), "g1.bmp");
    }
    
    private void testMakeFolderMultiClassDatasetTrain() {
        String src = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL\\DB CHARACTERS",
                dest = "C:\\Users\\Aha\\Documents\\dataset swta multiclass\\Benar\\";
        
        File folder = new File(src);
        
        for(File file : folder.listFiles()) {
            if(!file.isDirectory()) continue;
            
            new File(dest.concat(file.getName())).mkdirs();
        }
    }
    
    private void testSegmentKarakterFromAcuanData() {
        String pathDest = "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\Binary_reversed_used_parted";
        
        File dest = new File(pathDest);
        
        if(!dest.exists()) dest.mkdirs();
        
        ImageProcessor proc = new ImageProcessor();
        
        FileIOSegmentationPoints ioSP = new FileIOSegmentationPoints();        
        FileIODataTest fileIOData = new FileIODataTest();                
                
        String namaDataset = "IESKarDB";
        
        fileIOData.setNamaDataset(namaDataset);

        List<DataTest> datas = fileIOData.get();
        
        for(DataTest data : datas) {
            List<int[]> pspAreasAcuan = ioSP.readAreaPSP(data.getDirektoriPSpAreas());
            List<Integer> sps = ioSP.readSegmentationPoint(data.getDirektoriSps());

            for(int[] x : pspAreasAcuan) {
                int x1 = x[0], x2 = x[1];
                int dist = x2-x1;
                int newsp = x1+(dist/2);
                
                sps.add(newsp);
            }
            
            sps.sort(null);
//            for(int sp : sps) System.out.println(sp);
            for(int i=1; i<sps.size(); i++) {
                int x1 = sps.get(i-1), x2 = sps.get(i);
                
                BufferedImage img = proc.readImage(data.getDirektoriProcessed());
                
                proc.writeImage(proc.ArrayToImage(proc.universeOfDiscourse(proc.imageToArray(img.getSubimage(x1, 0, x2-x1, img.getHeight())), true, true)), 
                    pathDest+("/")+data.getNama().split("\\.")[0]+("-")+i+(".bmp"));
                    
//                System.out.println("-----------");
//                System.out.println(data.getNama());
//                System.out.println("img.getWidth() "+img.getWidth()+", img.getHeight() "+img.getHeight()+", +x1+(x2-x1) "+x1+(x2-x1)+", x1 "+x1+ " x2 " +x2);
//                System.out.println("-----------");
            }
        }                
    }
}
