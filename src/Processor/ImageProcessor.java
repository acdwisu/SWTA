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
package Processor;

import Model.SegmentedWord;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.awt.Point;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

/**
 *
 * @author acdwisu
 */
public class ImageProcessor {
    
    /**
     * Method untuk membaca citra dari path yang disediakan
     * @param path Path dari citra
     * @return hasil dari pembacaan berupa <code> BufferedImage </code>
     */
    public BufferedImage readImage(String path) {
        BufferedImage buff = null;
        
        try {
            buff = ImageIO.read(new File(path));
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return buff;
    }
    
    public void writeImage(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "bmp", new File(path));
        } catch (IOException ex) {
            Logger.getLogger(ImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Method untuk mengkonversi citra dalam representasi <code> BufferedImage </code> ke dalam bentuk array integer
     * dua dimensi (<code> int[][] </code>)
     * @param src 
     * @return 
     */
    public int[][] imageToArrayAndBinarize(BufferedImage src) {
        int[][] result = new int[src.getHeight()][src.getWidth()];
        
        for(int i=0; i<src.getHeight(); i++) {
            for(int j=0; j<src.getWidth(); j++) {
                Color c = new Color(src.getRGB(j, i));
                int warna = c.getRed();
                
                if(warna >= 0 && warna <= 200) result[i][j] = 1;
                else if(warna > 200 && warna <= 255) result[i][j] = 0;
            }
        }
        
        return result;
    }
    
    /**
     * Method untuk mengkonversi citra dalam representasi <code> BufferedImage </code> ke dalam bentuk array integer
     * dua dimensi (<code> int[][] </code>)
     * @param src 
     * @return 
     */
    public int[][] imageToArray(BufferedImage src) {
        int[][] result = new int[src.getHeight()][src.getWidth()];
        
        for(int i=0; i<src.getHeight(); i++) {
            for(int j=0; j<src.getWidth(); j++) {
                Color c = new Color(src.getRGB(j, i));
                int warna = c.getRed();
                
                if(warna == 0) result[i][j] = 1;
                else if(warna == 255) result[i][j] = 0;
            }
        }
        
        return result;
    }

    /**
     * Method untuk mengkonversi citra dari bentuk array dua dimensi (<code> int[][]</code>) ke dalam
     * bentuk <code> BufferedImage </code>
     * @param src
     * @return 
     */
    public BufferedImage ArrayToImage(int[][] src) {
        BufferedImage result = new BufferedImage(src[0].length, src.length, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<result.getHeight(); i++) {
            for(int j=0; j<result.getWidth(); j++) {
                int val = (src[i][j] == 1? 0 : 255);
                int warna = (val << 16) + (val << 8) + val;
                
                result.setRGB(j, i, warna);
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param src
     * @param mode <code> 1 </code> : fit to width/n
     *              <code> 2 </code> : fit to height
     * @param newVal
     * @return 
     */
    public BufferedImage scale(BufferedImage src, int mode, int newVal) {
        return Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, newVal, (BufferedImageOp) null);
    }
    
    /**
     * Method untuk melakukan scaling pada citra dalam bentuk <code> BufferedImage </code> 
     * @param src
     * @param containerWidth
     * @param containerHeight
     * @param scale
     * @return 
     */
    public BufferedImage fitImageToContainer(BufferedImage src, int containerWidth, int containerHeight, float scale) {
        BufferedImage scaled;
        
        int newWidth = Math.round(scale * containerWidth), newHeight = Math.round(scale * containerHeight);
        
        scaled = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, 
            newWidth, (BufferedImageOp) null);

        if(scaled.getHeight() > containerHeight) {
            scaled = Scalr.resize(src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_HEIGHT, 
                newHeight, (BufferedImageOp) null);
        }
        
        BufferedImage result = new BufferedImage(containerWidth, containerHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = result.getGraphics();
        g.fillRect(0, 0, containerWidth, containerHeight);
        g.drawImage(scaled, (containerWidth-scaled.getWidth())/2, (containerHeight-scaled.getHeight())/2, null);
        
        return result;
    }
    
    /**
     * Method untuk mengcopy citra dalam bentuk array integer dua dimensi (<code>int[][]</code>)
     * @param src
     * @return 
     */
    public int[][] copyImage(int[][] src) {
        int width = src[0].length, height = src.length;
        
        int[][] copied = new int[height][width];
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                copied[i][j] = src[i][j];
            }
        }
        
        return copied;
    }
    
    /**
     * Method untuk melakukan konversi citra menjadi citra biner dengan metode thresholding manual
     * @param src
     * @param threshold
     * @return 
     */
    public BufferedImage binarization(BufferedImage src, int threshold) {                
        BufferedImage res;

        int height = src.getHeight(), width = src.getWidth();
            
        res = new BufferedImage(width, height, src.getType());

        int warnaBiner;
        for(short i=0; i<height; i++) {
            for(short j=0; j<width; j++) {
                Color warnaGray = new Color(src.getRGB(j, i));
                if(warnaGray.getRed() < threshold) warnaBiner = (0 << 16) + (0 << 8) + 0;
                else warnaBiner = (255 << 16) + (255 << 8) + 255;
                res.setRGB(j, i, warnaBiner);
            }
        }
        
        return res;
    }

    /**
     * Method untuk mengkonversi citra menjadi citra grayscale
     * @param src
     * @return 
     */
    public BufferedImage grayscale(BufferedImage src) {
        int height = src.getHeight();int width = src.getWidth();

        BufferedImage result = new BufferedImage(width, height, 11);
        
        for (short i = 0; i < height; i = (short)(i + 1)) {
            for (short j = 0; j < width; j = (short)(j + 1)) {
                Color warnaAsli = new Color(src.getRGB(j, i));

                double temp = warnaAsli.getRed() * 0.3D + warnaAsli.getGreen() * 0.59D + warnaAsli.getBlue() * 0.11D;

                int warnaGray = (int)temp;
                warnaGray = (warnaGray << 16) + (warnaGray << 8) + warnaGray;
                result.setRGB(j, i, warnaGray);
            }
        }
        return result;
    }
    
    /**
     * Method untuk melakukan operasi not pada citra biner dalam bentuk <code> int[][] </code> (pembalikan warna)
     * @param src
     * @return 
     */
    public int[][] notOperation(int[][] src) {
        int[][] result = new int[src.length][src[0].length];
        
        for(int i=0; i<src.length; i++) {
            for(int j=0; j<src[0].length; j++) {
                if(src[i][j] == 1) result[i][j] = 0;
                else if(src[i][j] == 0) result[i][j] = 1;
            }
        }
        
        return result;
    }
    
    /**
     * Method untuk membuang space kosong pada citra baik dari sisi horizontal ataupun vertikal (trimming)
     * @param src
     * @return 
     */
    public int[][] universeOfDiscourse(int[][] src, boolean trimXAxis, boolean trimYAxis) {
        int[][] result;

        int furthestX = trimXAxis? furhtestForegroundXSeek(src) : src[0].length-1;
        int nearestX = trimXAxis? nearestForegroundXSeek(src) : 0;
        
        int furthestY = trimYAxis? furhtestForegroundYSeek(src) : src.length-1;
        int nearestY = trimYAxis? nearestForegroundYSeek(src) : 0;

        int newWidth = furthestX - nearestX + 1;
        int newHeight = furthestY - nearestY + 1;
        
        result = this.getSubArray(src, nearestX, nearestY, newWidth, newHeight);

        return result;
    }

    /**
     * Method untuk membagi citra menjadi RC dan CC
     * @param src
     * @return 
     */
    public int[][][] splitCharacterToCCRC(int[][] src) {
        int[][][] result = new int[2][][];
        
        int width = src[0].length;
        int height = src.length;

        int divider = width / 2;
                
        boolean odd = width % 2 == 1; 

        result[0] = src;
        result[1] = this.getSubArray(result[0], 0, 0, (odd ? divider : divider - 1), height);

        return result;
    }
    
    /**
     * 
     * @param src
     * @return 
     */
    public int[][][] splitSAToSABenarSalah(int[][] src) {
        int[][][] result = new int[2][][];
        
        // .........

        return result;
    }
    
    public int[][] normalizeWordImageSize(int[][] src) {
        int widthSrc = src[0].length, heightSrc = src.length;
        
        if(widthSrc > heightSrc) {
            return this.whiteSpaceAdditionBorderVertical(src, (widthSrc-heightSrc)/2);
        } else {
            return this.whiteSpaceAdditionBorderHorizontal(src, (heightSrc-widthSrc)/2);
        }
    }

    /**
     * 
     * @param src
     * @param additionCount
     * @return 
     */
    private int[][] whiteSpaceAdditionBorderVertical(int[][] src, int additionCount) {
        int[][] result = new int[src.length+additionCount*2][src[0].length];
        
        for(int i=0; i<src.length; i++) {
            for(int j=0; j<src[0].length; j++) {
                result[i+additionCount][j] = src[i][j];
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param src
     * @param additionCount
     * @return 
     */
    private int[][] whiteSpaceAdditionBorderHorizontal(int[][] src, int additionCount) {
        int[][] result = new int[src.length][src[0].length+additionCount*2];
        
        for(int i=0; i<src.length; i++) {
            for(int j=0; j<src[0].length; j++) {
                result[i][j+additionCount] = src[i][j];
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param src
     * @param additionCount
     * @return 
     */
    private int[][] whiteSpaceAdditionBorder(int[][] src, int additionCount) {
        int[][] result = new int[src.length+additionCount*2][src[0].length+additionCount*2];
        
        for(int i=0; i<src.length; i++) {
            for(int j=0; j<src[0].length; j++) {
                result[i+additionCount][j+additionCount] = src[i][j];
            }
        }
        
        return result;
    }        
    
    private int[][] whiteSpaceEliminationBorder(int[][] src, int eliminationCount) {
        int[][] result = new int[src.length-eliminationCount*2][src[0].length-eliminationCount*2];
        
        for(int i=0; i<result.length; i++) {
            for(int j=0; j<result[0].length; j++) {
                result[i][j] = src[i+eliminationCount][j+eliminationCount];
            }
        }
        
        return result;
    }
    
    /**
     * Method untuk menerapkan proses thinning pada citra. Metode thinning yang digunakan ialah metode zhang-suen.
     * @param src
     * @return 
     */
    public int[][] thinning(int[][] src) { //<editor-fold defaultstate="collapsed" desc="Thinning method">
        int whitespaceCount = 3;
        
        int[][] result = this.whiteSpaceAdditionBorder(src, whitespaceCount);

        int a, b;
 
        List pointsToChange = new LinkedList();
        boolean hasChange;
 
        do {
 
            hasChange = false;
            for (int y = 1; y + 1 < result.length; y++) {
                for (int x = 1; x + 1 < result[y].length; x++) {
                    a = getA(result, y, x);
                    b = getB(result, y, x);
                    if ( result[y][x]==1 && 2 <= b && b <= 6 && a == 1
                            && (result[y - 1][x] * result[y][x + 1] * result[y + 1][x] == 0)
                            && (result[y][x + 1] * result[y + 1][x] * result[y][x - 1] == 0)) {
                        pointsToChange.add(new Point(x, y));
                        hasChange = true;
                    }
                }
            }
 
            for(int i=0; i<pointsToChange.size(); i++){
            	Point point = (Point)pointsToChange.get(i);
            	result[point.y][point.x] = 0;
            }
             
            pointsToChange.clear();
 
            for (int y = 1; y + 1 < result.length; y++) {
                for (int x = 1; x + 1 < result[y].length; x++) {
                    a = getA(result, y, x);
                    b = getB(result, y, x);
                    if ( result[y][x]==1 && 2 <= b && b <= 6 && a == 1
                            && (result[y - 1][x] * result[y][x + 1] * result[y][x - 1] == 0)
                            && (result[y - 1][x] * result[y + 1][x] * result[y][x - 1] == 0)) {
                        pointsToChange.add(new Point(x, y));
 
                        hasChange = true;
                    }
                }
            }
 
            for(int i=0; i<pointsToChange.size(); i++){
            	Point point = (Point)pointsToChange.get(i);
            	result[point.y][point.x] = 0;
            }
             
            pointsToChange.clear();
 
        } while (hasChange);
 
        return this.whiteSpaceEliminationBorder(result,whitespaceCount);
    }
    //</editor-fold>
    
    /**
     * Method pelengkap dari method thinning
     * @param y
     * @param x
     * @return 
     */    
    private int getA(int[][] src, int y, int x) { //<editor-fold defaultstate="collapsed" desc="getA method">
        int count = 0;
        //p2 p3
        if (src[y - 1][x] == 0 && src[y - 1][x + 1] == 1) {
            count++;
        }
        //p3 p4
        if (src[y - 1][x + 1] == 0 && src[y][x + 1] == 1) {
            count++;
        }
        //p4 p5
        if (src[y][x + 1] == 0 && src[y + 1][x + 1] == 1) {
            count++;
        }
        //p5 p6
        if (src[y + 1][x + 1] == 0 && src[y + 1][x] == 1) {
            count++;
        }
        //p6 p7
        if (src[y + 1][x] == 0 && src[y + 1][x - 1] == 1) {
            count++;
        }
        //p7 p8
        if (src[y + 1][x - 1] == 0 && src[y][x - 1] == 1) {
            count++;
        }
        //p8 p9
        if (src[y][x - 1] == 0 && src[y - 1][x - 1] == 1) {
            count++;
        }
        //p9 p2
        if (src[y - 1][x - 1] == 0 && src[y - 1][x] == 1) {
            count++;
        }
 
        return count;
    }
    //</editor-fold>
    
    /**
     * Method pelengkap dari method thinning
     * @param src
     * @param y
     * @param x
     * @return 
     */    
    protected int getB(int[][] src, int y, int x) {//<editor-fold defaultstate="collapsed" desc="getB method">
        return src[y - 1][x] + src[y - 1][x + 1] + src[y][x + 1]
                + src[y + 1][x + 1] + src[y + 1][x] + src[y + 1][x - 1]
                + src[y][x - 1] + src[y - 1][x - 1];
    }
    //</editor-fold>    
    
    /**
     * Method untuk melakukan proses separasi sub word, dengan memisahkan antara word object dan dot object secara otomatis
     * @param src
     * @return 
     */
    public SegmentedWord subWordSeparation(int[][] src) {
        SegmentedWord segmentedWord = new SegmentedWord();        
        
        List<List<Point>> strokes = this.getObjectsFromWord(src);                
        
        HashMap<String, List<Integer>> wordDotsObject = this.divideToWordDotsIndex(strokes,src);
        
        List<Integer> wordObjectsIndex = wordDotsObject.get("words");
        
        List<Integer> dotsObjectsIndex = wordDotsObject.get("dots");
        
        List<Integer> segmentationPoints = this.seekSegmentationPoints(strokes, wordObjectsIndex);
        
        int[][] img = this.separateWord(strokes, wordObjectsIndex, src[0].length, src.length);
        
        segmentedWord.setOriginalImage(src);
        segmentedWord.setObjectsStroke(strokes);
        segmentedWord.setDotObjectsIndex(dotsObjectsIndex);
        segmentedWord.setWordObjectsIndex(wordObjectsIndex);
        segmentedWord.setSegmentationPoints(segmentationPoints);
        segmentedWord.setSegmentedWord(img);
        
        return segmentedWord;
    }

    /**
     * Method untuk melakukan proses separasi sub word, dengan memisahkan antara word object dan dot object secara manual
     * (word object dan dot object ditentukan secara manual, disebabkan algoritma penentuan secara otomatis kurang mangkus)
     * @param src
     * @return 
     */
    public SegmentedWord subWordSeparation(int[][] src, List<Integer> wordObjectsIndex, List<Integer> dotsObjectsIndex) {
        SegmentedWord segmentedWord = new SegmentedWord();        
        
        List<List<Point>> strokes = this.getObjectsFromWord(src);                
                
        List<Integer> segmentationPoints = this.seekSegmentationPoints(strokes, wordObjectsIndex);
        
        int[][] img = this.universeOfDiscourse(this.separateWord(strokes, wordObjectsIndex, src[0].length, src.length), 
                true, true);
        
        segmentedWord.setOriginalImage(src);
        segmentedWord.setObjectsStroke(strokes);
        segmentedWord.setDotObjectsIndex(dotsObjectsIndex);
        segmentedWord.setWordObjectsIndex(wordObjectsIndex);
        segmentedWord.setSegmentationPoints(segmentationPoints);
        segmentedWord.setSegmentedWord(img);
        
        return segmentedWord;
    }
    
    /**
     * Method untuk melakukan proses separasi sub word, dengan memisahkan antara word object dan dot object secara manual
     * (word object dan dot object ditentukan secara manual, disebabkan algoritma penentuan secara otomatis kurang mangkus)
     * @param src
     * @return 
     */
    public SegmentedWord subWordSeparation(int[][] src, List<Integer> wordObjectsIndex) {
        SegmentedWord segmentedWord = new SegmentedWord();        
        
        List<List<Point>> strokes = this.getObjectsFromWord(src);  
        
        List<Integer> dotsObjectsIndex = new LinkedList<>();
        
        for(int i=0; i<strokes.size(); i++) {
            if(!wordObjectsIndex.contains(i)) {
                dotsObjectsIndex.add(i);
            }
        }

        List<Integer> segmentationPoints = this.seekSegmentationPoints(strokes, wordObjectsIndex);
        
        int[][] img = this.universeOfDiscourse(this.separateWord(strokes, wordObjectsIndex, src[0].length, src.length), 
                true, true);
        
        segmentedWord.setOriginalImage(src);
        segmentedWord.setObjectsStroke(strokes);
        segmentedWord.setDotObjectsIndex(dotsObjectsIndex);
        segmentedWord.setWordObjectsIndex(wordObjectsIndex);
        segmentedWord.setSegmentationPoints(segmentationPoints);
        segmentedWord.setSegmentedWord(img);
        
        return segmentedWord;
    }
    
    /**
     * Method untuk mendapatkan semua object (sekumpulan piksel foreground yang terisolasi) dari suatu citra
     * @param src
     * @return 
     */
    public List<List<Point>> getObjectsFromWord(int[][] src) {
        List<List<Point>> objects = new LinkedList<>();     
        
        if(src.length == 0 || src[0].length == 0) return new LinkedList();
        
        int[][] imgSrc = new int[src.length][src[0].length];
        for(int i=0; i<src.length; i++) 
            for(int j=0; j<src[0].length; j++)
                imgSrc[i][j] = src[i][j];
        
        boolean hasForeground = true;
        while(hasForeground) {
            List<Point> points;
            Point firstPoint = new Point();
            
            hasForeground = false;
            
            //search first point of object, with travel pixels in x axis
            boolean stopLoop = false;
            for(int i=0; i<imgSrc[0].length; i++) {                              
                for(int j=0; j<imgSrc.length; j++) {
                    if(imgSrc[j][i] == 1) {
                        firstPoint = new Point(i,j);
                        stopLoop = true;
                        
                        hasForeground = true;
                        break;
                    }
                }
                
                if(stopLoop) break;
            }
            
            if(!hasForeground) break;
                        
            //follow the stroke(foreground) according to the first point
            List<Point> strokes = new LinkedList<>();
            strokes = this.followStroke(firstPoint, imgSrc, strokes);
            
            //add the strokes gained, to the object list
            objects.add(strokes);
            
            //set the strokes gained to background
            imgSrc = this.setPointsToBackground(strokes, imgSrc);
            
//            //do the triming process to the image that the strokes gained has set to background
//            imgSrc = this.universeOfDiscource(imgSrc, true, true);            
        }
        
        return objects;
    }
    
    /**
     * Method untuk memisahkan antara word object dan dot object
     * @param strokes
     * @return 
     */
    public HashMap<String,List<Integer>> divideToWordDotsIndex(List<List<Point>> strokes, int[][] src) {
        HashMap<String, List<Integer>> result = new HashMap<>();

        List<Integer> wordIndexes = new LinkedList<>(), dotsIndexes;

        dotsIndexes = this.dotsDetector(strokes, src);
        
        for(int i=0; i<strokes.size(); i++) {
            if(dotsIndexes.contains(i)) { 
                continue;
            }
            wordIndexes.add(i);
        }
       
//        System.out.println("word indexes");
//        for(int i : wordIndexes) {
//            System.out.printf("%d", i);
//        }
//        System.out.println("");
//        
//        System.out.println("dots indexes");
//        for(int i : dotsIndexes) {
//            System.out.printf("%d", i);
//        }
//        System.out.println("");

        result.put("words", wordIndexes);
        result.put("dots", dotsIndexes);

        return result;
    }
    
    /**
     * Method untuk mencari lokasi dari titik pisah sub word (segmentation point).
     * @param objects
     * @param wordObjectsIndex
     * @return 
     */
    public List<Integer> seekSegmentationPoints(List<List<Point>> objects, List<Integer> wordObjectsIndex) {
        List<Integer> sps = new LinkedList<>();
        
        int i=0;
        for(int index : wordObjectsIndex) {
            List<Point> wordObject = objects.get(index);
            
            int width = this.getWidthHeightFromStroke(wordObject)[0];
            
            if(i==0) {
                sps.add(width + 1);
            } else {
                sps.add(width + 1 +(sps.get(i-1) + 1));
            }
            i++;
        }
        
        return sps;
    }
    
    /**
     * Method untuk memisahkan sub word dan menyatukannya kembali dalam satu citra
     * @param strokes
     * @param wordObjectsIndex
     * @param widthSource
     * @param heightSource
     * @return 
     */
    public int[][] separateWord(List<List<Point>> strokes, List<Integer> wordObjectsIndex, int widthSource, int heightSource) {       
       List<int[][]> wordsImg = new LinkedList<>();
       
       for(int index : wordObjectsIndex) {
           List<Point> stroke = strokes.get(index);
           wordsImg.add(this.drawFromPoints(stroke, widthSource, heightSource, true, false));
       }
       
       return this.mergeSeparatedWord(wordsImg);
    }
    
    /**
     * Method untuk menyatukan citra sub word yang terpisah
     * @param wordsImg
     * @return 
     */
    public int[][] mergeSeparatedWord(List<int[][]> wordsImgs) {
        int[][] result;

        int highestWord = wordsImgs.get(0).length;
        int totalWidth = 0;

        // search highest word of images and the total width
        for(int[][] wordsImg : wordsImgs) {
            int width = wordsImg[0].length;
            int height = wordsImg.length;
            
            if(height >= highestWord) highestWord = height;

            totalWidth += width;
        }
        
        totalWidth += ((wordsImgs.size()-1) * 3);        
        
        result = new int[highestWord][totalWidth];

        // merge the separated words
        int x=0;
        for(int[][] wordsImg : wordsImgs) {            
            // traverse wordsImg
            for(int i=0; i<wordsImg[0].length; i++, x++) {
                for(int j=0; j<wordsImg.length; j++) {
                    result[j][x] = wordsImg[j][i];   
                }
            }
            x += 3;
        }
       
        return result;
    }
    
    /**
     * Method untuk melakukan deteksi foreground, dengan titik henti piksel background
     * @param point
     * @param src
     * @param strokes
     * @return 
     */
    public List<Point> followStroke(Point point, int[][] src, List<Point> strokes) {              
        List<Point> neighbours = this.getNeighbourStroke(point, strokes, src);
        
        if(strokes.isEmpty())
            strokes.add(point);
        
        //cek jumlah neighbour
        if(!neighbours.isEmpty()) {
            strokes.addAll(neighbours);
            for(Point neighbour : neighbours) {             
                List<Point> nextStrokes = this.followStroke(neighbour, src, strokes);
            }
        }
        
        return strokes;
    }   
    
    /**
     * Method untuk melakukan deteksi foreground, dengan titik henti piksel yang disediakan
     * @param point
     * @param src
     * @param segmentEdges
     * @param strokes
     * @return 
     */
    public List<Point> followStrokeV2(Point point, int[][] src, List<Point> segmentEdges, List<Point> strokes) {                              
        if(strokes.isEmpty())
            strokes.add(point);                             
        
        //cek jumlah neighbour
        if(!this.containPoint(segmentEdges, point)) {
            List<Point> neighbours = this.getNeighbourStrokeSorted(point, strokes, src);
            
            strokes.addAll(neighbours);
            
            if(neighbours.size() > 0)
                this.followStrokeV2(neighbours.get(neighbours.size()-1), src, segmentEdges, strokes);
        }
        
        return strokes;
    }    
    
    /**
     * Method untuk menghapus line segment yang ganda (memiliki elemen yang sama)
     * @param segments 
     */
    public void deleteDuplicateSegments(List<List<Point>> segments) {
        LinkedList<Integer> duplicateIndex = new LinkedList<>();
        
        int i=0;
        for(List<Point> segment : segments) {
            Point start = segment.get(0), end = segment.get(segment.size()-1);
            
            int j=0;
            for(List<Point> segment2 : segments) {
                if(i == j) continue;
                Point start2 = segment2.get(0), end2 = segment2.get(segment2.size()-1);
                
                if((start.equals(start2) && end.equals(end2)) || (start.equals(end2) && end.equals(start2))) {
                    duplicateIndex.add(i);
                    break;
                }   
                
                j++;
            }
            
            i++;
        }
        
        for(int index=segments.size()-1; index>=0; index--) {
            if(duplicateIndex.contains(index)) segments.remove(index);
        }
    }
    
    /**
     * Method untuk mencari jumlah tetangga dari piksel bersangkutan
     * @param point
     * @param src
     * @return 
     */
    public int getNeighbourCount(Point point, int[][] src) {
        int count=0;
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // memastikan jarak penglihatan tetangga tidak melebihi batas dari ukuran citra
        int maksXSeek = (x==src[0].length-1? 0 : 1), minXSeek = (x==0? 0 : -1);
        int maksYSeek = (y==src.length-1? 0 : 1), minYSeek = (y==0? 0 : -1);
        
        for(int i=minYSeek; i<=maksYSeek; i++) {
            for(int j=minXSeek; j<=maksXSeek; j++) {
                if(i == 0 && j == 0) continue;
                if(src[y + i][x + j] == 1) {                    
                    count++;
                }
            }
        }
        
        return count;
    } 
    
    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan
     * @param point
     * @param src
     * @return 
     */
    public List<Point> getNeighbourStroke(Point point, int[][] src) {
        List<Point> strokes = new LinkedList<>();
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // memastikan jarak penglihatan tetangga tidak melebihi batas dari ukuran citra
        int maksXSeek = (x==src[0].length-1? 0 : 1), minXSeek = (x==0? 0 : -1);
        int maksYSeek = (y==src.length-1? 0 : 1), minYSeek = (y==0? 0 : -1);
        
        for(int i=minYSeek; i<=maksYSeek; i++) {
            for(int j=minXSeek; j<=maksXSeek; j++) {
                if(i == 0 && j == 0) continue;
                if(src[y + i][x + j] == 1) {                    
                    strokes.add(new Point(x+j,y+i));
                }
            }
        }       
        
        return strokes;
    }

    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan dengan pengecualian piksel tertentu
     * @param point
     * @param pointsPrev
     * @param src
     * @return 
     */
    public List<Point> getNeighbourStroke(Point point, List<Point> pointsPrev, int[][] src) {
        List<Point> strokes = new LinkedList<>();
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // memastikan jarak penglihatan tetangga tidak melebihi batas dari ukuran citra
        int maksXSeek = (x==src[0].length-1? 0 : 1), minXSeek = (x==0? 0 : -1);
        int maksYSeek = (y==src.length-1? 0 : 1), minYSeek = (y==0? 0 : -1);
        
        for(int i=minYSeek; i<=maksYSeek; i++) {
            for(int j=minXSeek; j<=maksXSeek; j++) {
                
                int checkX = x+j, checkY = y+i;
                
                boolean pointHasPassed = pointHasPassed(new Point(checkX, checkY), pointsPrev);               
                
                if((i == 0 && j == 0) || pointHasPassed) 
                    continue;
                
                if(src[checkY][checkX] == 1) {                    
                    strokes.add(new Point(checkX,checkY));
                }
            }
        }       
        
        return strokes;
    }
    
    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan dengan pengecualian piksel tertentu dan tak ada
     * piksel tetangga yang saling bertetangga satu sama lain. ????? x_x
     * @param point
     * @param pointsPrev
     * @param src
     * @return 
     */
    public List<Point> getNeighbourStrokeNoAdjacent(Point point, List<Point> pointsPrev, int[][] src) {
        // get direct pixel neighbour and diagonal pixel neighbour
        List<Point> directNeighbour = this.getNeighbourDirect(point, pointsPrev, src);
        List<Point> diagonalNeighbour = this.getNeighbourDiagonal(point, pointsPrev, src);
                        
        return this.eliminateAdjacentNeighbourDirectDiagonal(directNeighbour, diagonalNeighbour);
    }
    
    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan dengan pengecualian piksel tertentu, piksel-piksel 
     * tetangga tersebut akan diurutkan dengan prioritas directNeighbour lalu diagonalNeighbour
     * @param point
     * @param pointsPrev
     * @param src
     * @return 
     */
    public List<Point> getNeighbourStrokeSorted(Point point, List<Point> pointsPrev, int[][] src) {                        
        // get direct pixel neighbour and diagonal pixel neighbour
        List<Point> directNeighbour = this.getNeighbourDirect(point, pointsPrev, src);
        List<Point> diagonalNeighbour = this.getNeighbourDiagonal(point, pointsPrev, src);
                        
        return this.sortNeighbourDirectDiagonal(directNeighbour, diagonalNeighbour);
    }
    
    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan dengan pengecualian piksel tertentu, piksel tetangga
     * yang didapatkan hanyalah tetangga yang direct (utara, selatan, timur, dan barat)
     * @param point
     * @param pointsPrev
     * @param src
     * @return 
     */
    public List<Point> getNeighbourDirect(Point point, List<Point> pointsPrev, int[][] src) {
        List<Point> neighbours = new LinkedList<>();
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // memastikan jarak penglihatan tetangga tidak melebihi batas dari ukuran citra
        int maksXSeek = (x==src[0].length-1? 0 : 1), minXSeek = (x==0? 0 : -1);
        int maksYSeek = (y==src.length-1? 0 : 1), minYSeek = (y==0? 0 : -1);        
        
        // traverse direct pixel neighbour and diagonal pixel neighbour
        for(int i=minYSeek; i<=maksYSeek; i++) {            
            for(int j=minXSeek; j<=maksXSeek; j++) {
                
                int checkX = x+j, checkY = y+i;
                
                boolean pointHasPassed = pointHasPassed(new Point(checkX, checkY), pointsPrev);               
                
                if((i == 0 && j == 0) || pointHasPassed) 
                    continue;                
                
                if((j == 0 && i == 1) || (j == 0 && i == -1) || (i == 0 && j == 1) || (i == 0 && j == -1))                                   
                    if(src[checkY][checkX] == 1)         
                        neighbours.add(new Point(checkX,checkY));                    
            }
        }
        
        return neighbours;
    }
    
    /**
     * Method untuk mendapatkan piksel tetangga dari piksel bersangkutan dengan pengecualian piksel tertentu, piksel tetangga
     * yang didapatkan hanyalah tetangga yang diagonal (tenggara, barat daya, barat laut, timur laut) 
     * @param point
     * @param pointsPrev
     * @param src
     * @return 
     */
    public List<Point> getNeighbourDiagonal(Point point, List<Point> pointsPrev, int[][] src) {
        List<Point> neighbours = new LinkedList<>();
        
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // memastikan jarak penglihatan tetangga tidak melebihi batas dari ukuran citra
        int maksXSeek = (x==src[0].length-1? 0 : 1), minXSeek = (x==0? 0 : -1);
        int maksYSeek = (y==src.length-1? 0 : 1), minYSeek = (y==0? 0 : -1);        
        
        // traverse direct pixel neighbour and diagonal pixel neighbour
        for(int i=minYSeek; i<=maksYSeek; i++) {            
            for(int j=minXSeek; j<=maksXSeek; j++) {
                
                int checkX = x+j, checkY = y+i;
                
                boolean pointHasPassed = pointHasPassed(new Point(checkX, checkY), pointsPrev);               
                
                if((i == 0 && j == 0) || pointHasPassed) 
                    continue;                
                
                if((j == -1 && i == 1) || (j == 1 && i == -1) || (i == -1 && j == -1) || (i == 1 && j == 1))                                   
                    if(src[checkY][checkX] == 1)         
                        neighbours.add(new Point(checkX,checkY));                    
            }
        }
        
        return neighbours;
    }
    
    /**
     * Method untuk mengurutkan piksel tetangga dengan prioritas tetangga direct lalu tetangga diagonal
     * @param directNeighbour
     * @param diagonalNeighbour
     * @return 
     */
    public List<Point> sortNeighbourDirectDiagonal(List<Point> directNeighbour, List<Point> diagonalNeighbour) {
       List<Point> neighbours = new LinkedList<>();
       
       List<Integer> neighbourPaired = new LinkedList<>();
       
       if(directNeighbour.isEmpty()) neighbours.addAll(diagonalNeighbour);
       
       for(Point direct : directNeighbour) {
           neighbours.add(direct);
           
           int i=0;           
           for(Point diagonal : diagonalNeighbour) {
               if(neighbourPaired.contains(i)) continue;
               
               if(direct.x == diagonal.x || direct.y == diagonal.y) {
                   neighbours.add(diagonal);
                   neighbourPaired.add(i);
               }
               
               i++;
           }              
       }
       
       return neighbours;
    }
    
    /**
     * Method untuk mengeliminasi piksel tetangga yang saling bertetangga, dengan prioritas tetangga direct (jika tetangga
     * direct dan tetangga diagonal saling bertetangga, maka tetangga diagonal akan dieliminasi)
     * @param directNeighbour
     * @param diagonalNeighbour
     * @return 
     */
    public List<Point> eliminateAdjacentNeighbourDirectDiagonal(List<Point> directNeighbour, List<Point> diagonalNeighbour) {
       List<Point> neighbours = new LinkedList<>();
       
       neighbours.addAll(directNeighbour);              
       
       for(Point diagonal : diagonalNeighbour) {
           
           boolean isAdjacent = false;
           for(Point direct : directNeighbour) {
               if(direct.x == diagonal.x || direct.y == diagonal.y) isAdjacent = true;              
           }
           
           if(!isAdjacent) neighbours.add(diagonal);
       }              
       
       return neighbours;
    }
    
    /**
     * Method untuk mengecek apakah suatu piksel telah dilalui
     * @param point
     * @param pointsPrev
     * @return 
     */
    private boolean pointHasPassed(Point point, List<Point> pointsPrev) {
        return this.containPoint(pointsPrev, point);
    }
    
    /**
     * Method untuk mengecek apakah suatu piksel terdapat dalam suatu kumpulan piksel
     * @param points
     * @param point
     * @return 
     */
    public boolean containPoint(List<Point> points, Point point) {
        for(Point x : points) {
            if(x.x == point.x && x.y == point.y) return true;
        }
        
        return false;
    }   
    
    /**
     * Method untuk menghapus suatu piksel dari kumpulan piksel
     * @param point
     * @param listPoint
     * @return 
     */
    public boolean removePointFromList(Point point, List<Point> listPoint) {
       boolean deleted = false;
       
       int index=0;
       
       for(Point x : listPoint) {
           if(point.x == x.x && point.y == x.y) {
               deleted = true;
               break;
           }
           
           index++;
       }
       
       if(deleted) listPoint.remove(index);
       
       return deleted;
    }
    
    /**
     * 
     * @param points
     * @return 
     */
    public int[] getWidthHeightFromStroke(List<Point> points) {
        int[] result = new int[2];
        
        int maksX = points.get(0).x, minX = points.get(0).x;
        int maksY = points.get(0).y, minY = points.get(0).y;
        
        for(Point point : points) {
            int x = point.x;
            int y = point.y;
            
            if(x > maksX) maksX = x;
            if(x < minX) minX = x;
            
            if(y > maksY) maksY = y;
            if(y < minY) minY = y;
        }
        
        result[0] = maksX - minX +1;
        result[1] = maksY - minY +1;
        
        return result;
    }
    
    /**
     * 
     * @param strokes
     * @param widthSource
     * @param heightSource
     * @return 
     */
    public int[][] drawFromPoints(List<Point> strokes, int widthSource, int heightSource) {
        int[][] img = new int[heightSource][widthSource];
        
        for(Point point : strokes) {
            img[point.y][point.x] = 1;
        }
       
        return img;
    } 
    
    /**
     * 
     * @param strokes
     * @param widthSource
     * @param heightSource
     * @param trimXAxis
     * @param trimYAxis
     * @return 
     */
    public int[][] drawFromPoints(List<Point> strokes, int widthSource, int heightSource, boolean trimXAxis, boolean trimYAxis) {
        int[][] img = new int[heightSource][widthSource];
        
        for(Point point : strokes) {
            img[point.y][point.x] = 1;
        }
        
        img = this.universeOfDiscourse(img, trimXAxis, trimYAxis);
       
        return img;
    }
    
    /**
     * 
     * @param points
     * @param src
     * @return 
     */
    public int[][] setPointsToBackground(List<Point> points, int[][] src) {
        int[][] res = src.clone();
        
        for(Point point : points) {
            res[point.y][point.x] = 0;
        }
        
        return res;
    }
    
    /**
     * 
     * @param src
     * @param objects
     * @param dotsObjectIndex
     * @return 
     */
    public int[][] dotsRemoval(int[][] src, List<List<Point>> objects, List<Integer> dotsObjectIndex, boolean trim) {
        int[][] result = src.clone();
        
        for(int index : dotsObjectIndex) {
            List<Point> dotsObject = objects.get(index);
            
            for(Point point : dotsObject) {
                result[point.y][point.x] = 0;
            }
        }       
        
        if(trim)
            return this.universeOfDiscourse(result, true, true);
        else 
            return result;
    }
    
    /**
     * 
     * @param objects
     * @param src
     * @return 
     */
    public List<Integer> dotsDetector(List<List<Point>> objects, int[][] src) { 
        List<Integer> dotsIndex = new LinkedList<>(), wordsIndex = new LinkedList<>();                
        
        //mencari middle region
        HashMap<String, Integer> middleRegion = this.getMiddleRegion(src);
        
        int upperBaseline = middleRegion.get("upperBaseline"), 
                lowerBaseline = middleRegion.get("lowerBaseline");                

        //mencari object yang berada di luar middle region
        int i=0;
        for(List<Point> object : objects) {
            if(!this.isObjectPartOfArea(object, upperBaseline, lowerBaseline)) {
                dotsIndex.add(i);
            } else {
                wordsIndex.add(i);
            }
            i++;
        }
        
        //mencari nilai density rerata dari object dots
        List<List<Point>> dotsObjects = new LinkedList<>(), wordsObjects = new LinkedList<>();          
        for(int x=0; x<objects.size(); x++) {
            List<Point> object = objects.get(x);
            if(dotsIndex.contains(x)){               
                dotsObjects.add(object);                    
            } else {                
                wordsObjects.add(object);
            }
        }        
        
        double avgDotsDensity = this.calcAverageObjectDensity(dotsObjects);
        double avgWordsDensity = this.calcAverageObjectDensity(wordsObjects);
        
        //merevisi dots index yang didapati sebelumnya, dengan mengecek rerata dari setiap object pada wordsObject, apakah lebih 
        //  mendekati rerata density dotsObject atau mendekati rerata density wordsObject
        for(int index : wordsIndex) {
            List<Point> object = objects.get(index);
            int objectDensity = this.countPixelIntensity(object);
            if(Math.abs(objectDensity - avgDotsDensity) <= Math.abs(objectDensity - avgWordsDensity) ) {
                dotsIndex.add(index);
            }
        }       
        
        //merevisi dots index yang didapati sebelumnya, dengan mengecek rerata dari setiap object pada wordsObject, apakah lebih 
        //  mendekati rerata density dotsObject atau mendekati rerata density wordsObject
        
        return dotsIndex;
    }
    
    /**
     * 
     * @param objects
     * @return 
     */
    public List<Integer> dotsDetector(List<List<Point>> objects) {
        List<Integer> dotsIndex = new LinkedList<>();

        int[] objectsWidth = new int[objects.size()];
                
        double averageCharacterDensity = this.calcAverageObjectDensity(objects);
        
        //menghitung width masing-masing objek
        int i=0;
        for(List<Point> object : objects) {
            objectsWidth[i++] = this.countPixelIntensity(object);       
        }          
        
        //memcari object dengan width kurang dari acd
        i=0;
        for(List<Point> object : objects) {
            if(this.isObjectPartOfArea(object, i, i))
            if(objectsWidth[i] < averageCharacterDensity) 
                dotsIndex.add(i++);
        }
        
        return dotsIndex;
    }
    
    /**
     * 
     * @param objects
     * @return 
     */
    public double calcAverageObjectsWidth(List<List<Point>> objects) {
        double averageCharacterWidth;
        int objectsCount = objects.size();
        int totalWidth = 0;
        int[] objectsWidth = new int[objectsCount];
        
        //menghitung total width
        int i=0;
        for(List<Point> object : objects) {
            objectsWidth[i] = this.getWidthHeightFromStroke(object)[0];
            totalWidth += objectsWidth[i];            
            i++;
        }    
        
        averageCharacterWidth = 1.0 * totalWidth / objectsCount;
        
        return averageCharacterWidth;
    }
    
    /**
     * 
     * @param objects
     * @return 
     */
    public double calcAverageObjectDensity(List<List<Point>> objects) {
        double acd;
        int totalDensity = 0;
        int objectsCount = objects.size();
        int[] objectsDensity = new int[objectsCount];
        
        for(List<Point> object : objects) {
            totalDensity += this.countPixelIntensity(object);
        }
        
        acd = 1.0 * totalDensity / objectsCount;
        
        return acd;
    }
    
    /**
     * 
     * @param objects
     * @param wordIndex
     * @param dotsIndex
     * @return 
     */
    public double calcAverageDotsDistanceToNearestSubWord(List<List<Point>> objects, List<Integer> dotsIndex) {
        double average = Double.NaN;
        
//        List<List<Point>> words=new LinkedList<>(), dots=new LinkedList<>();        
//        
//        //mendapatkan objek word dan dots berdasarkan index
//        int i=0;
//        for(List<Point> object : objects) {            
//            if(dotsIndex.contains(i)) dots.add(object);
//            else words.add(object);
//            i++;
//        }               
//        
//        int[] distance = new int[dots.size()];
//        
//        //travers ke masing-masing object dots
//        for(List<Point> dot : dots) {
//            //travers ke masing-masing object words, untuk dibandingkan ke semua object dots
//            for(List<Point> word : words) {
//                
//            }
//        }
        
        return average;
    }
    
    /**
     * 
     * @param object
     * @return 
     */
    public int countPixelIntensity(List<Point> object) {
        int count = object.size();
        
        return count;
    }
    
    /**
     * 
     * @param src
     * @return 
     */
    public int countPixelIntensity(int[][] src) {
       int count = 0;
       
       for(int i=0; i<src.length; i++) {
           for(int j=0; j<src[0].length; j++) {
               if(src[i][j] == 1) count++;
           }
       }
       
       return count;
    }
    
    /**
     * 
     * @param object
     * @param offsetY1
     * @param offsetY2
     * @return 
     */
    public boolean isObjectPartOfArea(List<Point> object, int offsetY1, int offsetY2) {
       boolean result = false;
        
       for(Point point : object) {
           int y = point.y;

           if(y >= offsetY1 && y <= offsetY2) 
               return true;
       }
       
       return result;
    }
    
    /**
     * 
     * @param src
     * @return 
     */
    public HashMap<String, Integer> getMiddleRegion(int[][] src) {
        HashMap<String, Integer> middleRegion = new HashMap<>();
               
        int[] horizontalHisto = this.calcDensityHorizontalHistogram(src);
               
        int largestDensityIndex=0, upperBaselineIndex=0, lowerBaselineIndex=0;
        List<Integer> upperMaximaMinima = new LinkedList<>(), lowerMaximaMinima = new LinkedList<>();
       
        //mencari largest density
        int maksVal = horizontalHisto[0];
        for(int i=0; i<horizontalHisto.length; i++) {
            int val = horizontalHisto[i];
            if(val >=  maksVal) {
                maksVal = val;
                largestDensityIndex = i;
            }
        }                
        
        HashMap<String, List<Integer>> localMaximaMinima = this.seekLocalMaximaMinima(horizontalHisto);
        List<Integer> localMaxima = localMaximaMinima.get("localMaxima"), 
                localMinima = localMaximaMinima.get("localMinima");

        //mencari upper dan lower localMaxima
        for(int i : localMaxima) {
            if(i < largestDensityIndex) {
                upperMaximaMinima.add(i);
            } else if(i > largestDensityIndex) {
                lowerMaximaMinima.add(i);
            }            
        }
        
        //mencari upper dan lower localMinima
        for(int i : localMinima) {
            if(i < largestDensityIndex) {
                upperMaximaMinima.add(i);
            } else if(i > largestDensityIndex) {
                lowerMaximaMinima.add(i);
            }            
        }
        
        //mencari total dari upper local maxima minima 
        int total = 0;
        for(int i : upperMaximaMinima) {
            total += i;
        }
        upperBaselineIndex = (int) Math.ceil(1.0 * total / upperMaximaMinima.size());
        
        //mencari total dari upper local maxima minima 
        total = 0;
        for(int i : lowerMaximaMinima) {
            total += i;
        }
        
        lowerBaselineIndex = (int) Math.ceil(1.0 * total / lowerMaximaMinima.size());
        
        //evaluasi nilai upperBaselineIndex dan lowerBaselineIndex
        if(upperBaselineIndex == 0) {
            upperBaselineIndex = largestDensityIndex - (int) Math.ceil(largestDensityIndex * 0.2);            
            
        } 
        if(lowerBaselineIndex == 0) {
            lowerBaselineIndex = largestDensityIndex + (int) Math.ceil((horizontalHisto.length-1 - largestDensityIndex) * 0.5);            
        }
        
        middleRegion.put("upperBaseline", upperBaselineIndex);
        middleRegion.put("lowerBaseline", lowerBaselineIndex);
        
        return middleRegion;
    }
    
    /**
     * 
     * @param src
     * @param localMaxima
     * @param localMinima
     * @return 
     */
    public HashMap<String, Integer> getMiddleRegion(int[][] src, List<Integer> localMaxima, List<Integer> localMinima) {
        HashMap<String, Integer> middleRegion = new HashMap<>();
               
        int[] horizontalHisto = this.calcDensityHorizontalHistogram(src);
               
        int largestDensityIndex=0, upperBaselineIndex=0, lowerBaselineIndex=0;
        List<Integer> upperMaximaMinima = new LinkedList<>(), lowerMaximaMinima = new LinkedList<>();
       
        //mencari largest density
        int maksVal = horizontalHisto[0];
        for(int i=0; i<horizontalHisto.length; i++) {
            int val = horizontalHisto[i];
            if(val >=  maksVal) {
                maksVal = val;
                largestDensityIndex = i;
            }
        }                       

        //mencari upper dan lower localMaxima
        for(int i : localMaxima) {
            if(i < largestDensityIndex) {
                upperMaximaMinima.add(i);
            } else if(i > largestDensityIndex) {
                lowerMaximaMinima.add(i);
            }            
        }
        
        //mencari upper dan lower localMinima
        for(int i : localMinima) {
            if(i < largestDensityIndex) {
                upperMaximaMinima.add(i);
            } else if(i > largestDensityIndex) {
                lowerMaximaMinima.add(i);
            }            
        }
        
        //mencari total dari upper local maxima minima 
        int total = 0;
        for(int i : upperMaximaMinima) {
            total += i;
        }
        upperBaselineIndex = (int) Math.ceil(1.0 * total / upperMaximaMinima.size());
        
        //mencari total dari upper local maxima minima 
        total = 0;
        for(int i : lowerMaximaMinima) {
            total += i;
        }
        
        lowerBaselineIndex = (int) Math.ceil(1.0 * total / lowerMaximaMinima.size());
        
        //evaluasi nilai upperBaselineIndex dan lowerBaselineIndex
        if(upperBaselineIndex == 0) {
            upperBaselineIndex = largestDensityIndex - (int) Math.ceil(largestDensityIndex * 0.2);            
            
        } 
        if(lowerBaselineIndex == 0) {
            lowerBaselineIndex = largestDensityIndex + (int) Math.ceil((horizontalHisto.length-1 - largestDensityIndex) * 0.5);            
        }
        
        middleRegion.put("upperBaseline", upperBaselineIndex);
        middleRegion.put("lowerBaseline", lowerBaselineIndex);
        
        return middleRegion;
    }
    
    public List<Integer> seekLocalMinima(int[] histogram) { 
        List<Integer> localMinima = new LinkedList<>();
                       
        for(int i=1; i<histogram.length-1; i++) {                                    
            int val = histogram[i];                                                            
                       
            //perbadingan untuk mendapatkan local minima
            if(val < histogram[i-1]) {
                if(val < histogram[i+1]) {
                    localMinima.add(i);                    
                } 
                //perbandingan untuk mendeteksi kemungkinan adanya local maxima/minima pada serentetan nilai yang sama
                else if(val == histogram[i+1]) {                    
                    int index = this.getLocalMinimaOnSameValueGroup(histogram, i);
                    if(index != -1) {                        
                        localMinima.add(index);
                    }
                }
            } 
        }        
        
        return localMinima;
    }
    
    public List<Integer> seekLocalMaxima(int[] histogram) {
        List<Integer> localMaxima = new LinkedList<>();
                       
        for(int i=1; i<histogram.length-1; i++) {                                    
            int val = histogram[i];                                                            
            
            //perbadingan untuk mendapatkan local maxima
            if(val > histogram[i-1]) {
                if(val > histogram[i+1]) {
                    localMaxima.add(i);
                    continue;
                } 
                //perbandingan untuk mendeteksi kemungkinan adanya local maxima pada serentetan nilai yang sama
                else if(val == histogram[i+1]) {
                    int index = this.getLocalMaximaOnSameValueGroup(histogram, i);
                    if(index != -1) {
                        localMaxima.add(index);
                    }
                }
                continue;                
            }
        }
        
        return localMaxima;
    }
    
    public List<Integer> seekLocalMaximaNoiseEliminated(int[] histogram) {
        List<Integer> localMaxima = new LinkedList<>();
                       
        for(int i=1; i<histogram.length-1; i++) {                                    
            int val = histogram[i];                                                            
            
            //perbadingan untuk mendapatkan local maxima
            if(val > histogram[i-1]) {
                if(val > histogram[i+1]) {
                    localMaxima.add(i);
                    continue;
                } 
                //perbandingan untuk mendeteksi kemungkinan adanya local maxima pada serentetan nilai yang sama
                else if(val == histogram[i+1]) {
                    int index = this.getLocalMaximaOnSameValueGroup(histogram, i);
                    if(index != -1) {
                        localMaxima.add(index);
                    }
                }
                continue;                
            }
        }
        
        double total=0, rerata;
        for(int lm : localMaxima) {
            total+=histogram[lm];
        }
        
        rerata = total/localMaxima.size()*1.0;
        
        List<Integer> toRemove = new LinkedList();
        for(int lm : localMaxima) {
            if(Math.abs(histogram[lm]-rerata) > histogram[lm]) {
                toRemove.add(lm);
            }
        }
        
        for(Integer tr : toRemove) {
            localMaxima.remove(tr);
        }

        return localMaxima;
    }
    
    public HashMap<String, List<Integer>> seekLocalMaximaMinima(int[] histogram) {
        HashMap<String, List<Integer>> result = new HashMap<>();
        
        List<Integer> localMaxima = new LinkedList<>(), localMinima = new LinkedList<>();
                       
        for(int i=1; i<histogram.length-1; i++) {                                    
            int val = histogram[i];                                                            
            
            //perbadingan untuk mendapatkan local maxima
            if(val > histogram[i-1]) {
                if(val > histogram[i+1]) {
                    localMaxima.add(i);
                    continue;
                } 
                //perbandingan untuk mendeteksi kemungkinan adanya local maxima pada serentetan nilai yang sama
                else if(val == histogram[i+1]) {
                    int index = this.getLocalMaximaOnSameValueGroup(histogram, i);
                    if(index != -1) {
                        localMaxima.add(index);
                    }
                }
                continue;                
            } 
            
            //perbadingan untuk mendapatkan local minima
            if(val < histogram[i-1]) {
                if(val < histogram[i+1]) {
                    localMinima.add(i);                    
                } 
                //perbandingan untuk mendeteksi kemungkinan adanya local maxima/minima pada serentetan nilai yang sama
                else if(val == histogram[i+1]) {                    
                    int index = this.getLocalMinimaOnSameValueGroup(histogram, i);
                    if(index != -1) {                        
                        localMinima.add(index);
                    }
                }
            } 
        }        
        
        result.put("localMaxima", localMaxima);
        result.put("localMinima", localMinima);
        
        return result;
    }
    
    /**
     * 
     * @param histogram
     * @param indexStart
     * @return 
     */
    public int getLocalMaximaOnSameValueGroup(int[] histogram, int indexStart) {             
        int lengthSameValue=1;

        int i=indexStart;

        while(i < histogram.length-1 && histogram[i] == histogram[++i]) lengthSameValue++;

        int checkValueIndex = indexStart + lengthSameValue;
        if(checkValueIndex > histogram.length-1) return -1;
        
        if(histogram[indexStart + lengthSameValue] > histogram[indexStart]) return -1;

        int index = indexStart + (lengthSameValue / 2);       

        return index;
    }
    
    /**
     * 
     * @param histogram
     * @param indexStart
     * @return 
     */
    public int getLocalMinimaOnSameValueGroup(int[] histogram, int indexStart) {
        int lengthSameValue=1;

        int i=indexStart;

        while(i < histogram.length-1 && histogram[i] == histogram[++i]) lengthSameValue++;
     
        int checkValueIndex = indexStart + lengthSameValue;
        if(checkValueIndex > histogram.length-1) return -1;
        
        if(histogram[indexStart + lengthSameValue] < histogram[indexStart]) return -1;

        int index = indexStart + (lengthSameValue / 2);       

        return index;       
    }
    
    /**
     * 
     * @param src
     * @return 
     */
    public int[] calcDensityHorizontalHistogram(int[][] src) {
        int[] histogram = new int[src.length];
        
        for(int i=0; i<src.length; i++) {
            for(int j=0; j<src[0].length; j++) {
                if(src[i][j] == 1) histogram[i]++;
                
            }            
        }
        
        return histogram;
    }
    
    public int[] calcVerticalHistogram(int[][] src) {
        int[] histogram = new int[src[0].length];                
        
        for(int j=0; j<src[0].length; j++) {
            for(int i=0; i<src.length; i++) {
                if(src[i][j] == 1) histogram[j]++;                
            }            
        } 
        
        return histogram;
    }
    
    public int[] calcModifiedVerticalHistogram(int[][] src) {
        int[] histogram = new int[src[0].length];
        
        for(int j=0; j<src[0].length; j++) {
            LinkedList<Integer> indexStroke = new LinkedList<>();
            
            for(int i=0; i<src.length; i++) {
                if(src[i][j] == 1) indexStroke.add(i);                
            }            
            
            if(indexStroke.isEmpty()) histogram[j] = -1;
            else {
                int highestStroke = indexStroke.get(0),
                        lowestStroke = indexStroke.get(indexStroke.size()-1);

                histogram[j] = (lowestStroke - highestStroke) + 1; 
            }
        } 
        
        return histogram;
    }
    
    /**
     * 
     * @param src
     * @return 
     */
    private int furhtestForegroundXSeek(int[][] src) {
        int width = src[0].length;
        int height = src.length;
        
        for (int i = width-1; i >= 0; i--) {
            for (int j = 0; j < height; j++) {
                if (src[j][i] == 1) {
                  return i;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param src
     * @return 
     */
    private int furhtestForegroundYSeek(int[][] src) {
        int width = src[0].length;int height = src.length;
       
        for (int i = height-1; i >= 0; i--) {
            for (int j = 0; j < width; j++) {
                if (src[i][j] == 1) {
                  return i;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param src
     * @return 
     */
    private int nearestForegroundXSeek(int[][] src) {
        int width = src[0].length;
        int height = src.length;
        
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (src[j][i] == 1) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param src
     * @return 
     */
    private int nearestForegroundYSeek(int[][] src) {
        int width = src[0].length;int height = src.length;
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (src[i][j] == 1) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @param img
     * @return 
     */
    public LinkedList<Point> getForegroundsPixelLocationFromImage(int[][] img) {
        LinkedList<Point> locations = new LinkedList<>();
        
        for(int i=0; i<img.length; i++) {
            for(int j=0; j<img[0].length; j++) {
                if(img[i][j] == 1) locations.add(new Point(j,i));
            }
        }
        
        return locations;
    }
    
    /**
     * 
     * @param origin
     * @param foregroundCoord
     * @return 
     */
    public double getForegroundPixelDistanceToOrigin(Point origin, Point foregroundCoord) {
       return Math.sqrt(Math.pow(foregroundCoord.x - origin.x, 2) + Math.pow(foregroundCoord.y - origin.y, 2));
    }    
    
    /**
     * 
     * @param img
     * @return 
     */
    public LinkedList<Point> getStarterPixelPositions(final int[][] img) {
       LinkedList<Point> starterPixels = new LinkedList<>(); 
       
       for(int i=0; i<img.length; i++) {
            for(int j=0; j<img[0].length; j++) {
                if(img[i][j] == 1) {    
                    int neighbourCountNoAdjacent = this.getNeighbourStrokeNoAdjacent(new Point(j,i), new LinkedList<>(), img).size();
                    if(neighbourCountNoAdjacent == 1) 
                        starterPixels.add(new Point(j,i));                    
                }
            }
        }
       
       return starterPixels;
    }
    
    /**
     * 
     * @param img
     * @return 
     */
    public LinkedList<Point> getIntersectionPixelPositions(final int[][] img) {
       LinkedList<Point> intersectionPixels = new LinkedList<>(); 
       
       for(int i=0; i<img.length; i++) {
            for(int j=0; j<img[0].length; j++) {
                if(img[i][j] == 1) {    
                    int neighbourCountNoAdjacent = this.getNeighbourStrokeNoAdjacent(new Point(j,i), new LinkedList<>(), img).size();
                    if(neighbourCountNoAdjacent > 2) 
                        intersectionPixels.add(new Point(j,i));                    
                }
            }
        }
       
       return intersectionPixels;
    }
    
    /**
     * 
     * @param src
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 
     */
    public int[][] getSubArray(int[][] src, int x, int y, int width, int height) {        
        int[][] result = new int[height][width];

        if(x > src[0].length-1) x = src[0].length-1;
        if(x < 0) x = 0;
        if(y > src.length-1) y = src.length;
        if(y < 0) y = 0;
        
        if(src.length < height + y) {
            height = src.length - y;
        }
        
        if(src[0].length < width + x) {
            width = src[0].length - x;
        }
        
        
        
        for (int i = 0, a = y; i < height; i++, a++) {

            for (int j = 0, b = x; j < width; j++, b++) {
                result[i][j] = src[a][b];
            }
        }
        return result;
    }     
    
    /**
     * 
     * @param src
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 
     */
    public int[][] getSubArrayNoCrop(int[][] src, int x, int y, int width, int height) {        
        int[][] result = new int[src.length][src[0].length];

        //
        boolean sizeOver = (src.length < height + y) || (src[0].length < width + x);
        
        if(sizeOver) return src;
        
        for (int i = 0, a = y; i < height; i++, a++) {

            for (int j = 0, b = x; j < width; j++, b++) {
                result[a][b] = src[a][b];
            }
        }
        return result;
    }     
    
    /**
     * 
     * @param processedImage
     * @param psps
     * @return 
     */
    public BufferedImage renderWordAccordingToSegmentationPoints(BufferedImage processedImage, List<Integer> psps) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        graphic.setColor(Color.red);
        for(int psp : psps) {
            graphic.fillRect(psp, 0, 1, height);
        }
                               
        return result;
    }
    
    /**
     * 
     * @param processedImage
     * @param sps
     * @param psps
     * @return 
     */
    public BufferedImage renderWordAccordingToSegmentationPoints(BufferedImage processedImage, List<Integer> sps, List<Integer> psps) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        
        graphic.setColor(Color.red);
        for(int psp : psps) {
            graphic.fillRect(psp, 0, 1, height);
        }
        
        graphic.setColor(Color.BLUE);
        for(int sp : sps) {
            graphic.fillRect(sp, 0, 1, height);
        }
                               
        return result;
    }
    
    /**
     * 
     * @param processedImage
     * @param sps
     * @param psps
     * @param upperBaseline
     * @param lowerBaseline
     * @return 
     */
    public BufferedImage renderWordAccordingToSegmentationPoints(BufferedImage processedImage, List<Integer> sps, List<Integer> psps, int upperBaseline, int lowerBaseline) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        
        graphic.setColor(Color.red);
        for(int psp : psps) {
            graphic.fillRect(psp-1, 0, 3, height);
        }
        
        graphic.setColor(Color.yellow);
        for(int sp : sps) {
            graphic.fillRect(sp-1, 0, 3, height);
        }
        
        graphic.setColor(Color.BLUE);        
        graphic.fillRect(upperBaseline-1, 0, 3, height);
        
        graphic.setColor(Color.GREEN);        
        graphic.fillRect(lowerBaseline-1, 0, 3, height);
                               
        return result;
    }
    
    public BufferedImage renderWordAccodringToZones(final BufferedImage image, List<Point[]> zones) {
        int width = image.getWidth(), height = image.getHeight();
        
        BufferedImage result = image.getSubimage(0, 0, width, height);
        
        Graphics g = result.createGraphics();
        
        g.setColor(Color.red);

        for(Point[] zone : zones) {
            g.drawRect(zone[0].x, zone[0].y, zone[1].x - zone[0].x, zone[1].y - zone[0].y);
        }
        
        return result;
    }
    
    /**
     * 
     * @param processedImage
     * @param upperBaseline
     * @param lowerBaseline
     * @return 
     */
    public BufferedImage renderWordAccordingToBaseline(BufferedImage processedImage, int upperBaseline, int lowerBaseline) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        
        graphic.setColor(Color.BLUE);        
        graphic.fillRect(0, upperBaseline-1, width, 3);
        
        graphic.setColor(Color.GREEN);        
        graphic.fillRect(0, lowerBaseline-1, width, 3);
        
        return result;
    }
    
    public BufferedImage renderWordAccordingToPSPArea(BufferedImage processedImage, List<int[]> areas) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        
        graphic.setColor(new Color(0,1,0,.75f));        
        
        for(int[] points : areas) {
            if(points[0] < points[1]) {
                graphic.fillRect(points[0], 0, points[1]-points[0], height);
            } else {
                graphic.fillRect(points[1], 0, points[0]-points[1], height);
            }
        }
        
        return result;
    }
    
    public BufferedImage renderWordAccordingToPSPArea(BufferedImage processedImage, List<Integer> sps, List<int[]> areas) {
        int width = processedImage.getWidth(), height = processedImage.getHeight();
        
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result.setRGB(j, i, processedImage.getRGB(j, i));
            }
        }
        
        Graphics2D graphic = result.createGraphics();
        
        graphic.setColor(new Color(0,1,0,.75f));        
        
        for(int[] points : areas) {
            if(points[0] < points[1]) {
                graphic.fillRect(points[0], 0, points[1]-points[0], height);
            } else {
                graphic.fillRect(points[1], 0, points[0]-points[1], height);
            }
        }
        
        graphic.setColor(Color.RED);
        for(int sp : sps) {
            graphic.fillRect(sp-1, 0, 3, height);
        }
        
        return result;
    }
    
    /**
     * 
     * @param histogram
     * @return 
     */
    public BufferedImage renderHistogram(int[] histogram) {
        
        int max=histogram[0];
        for(int e : histogram) {
            if(e >= max) max = e;
        }
        
        int width = histogram.length, height=max;

        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics g = res.createGraphics();
        g.setColor(Color.WHITE);
        
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.BLUE);
        for(int i=0; i<width; i++) {
            g.drawLine(i, height-1, i, (height-1)-histogram[i]);
        }
        
        return res;
    }
    
    /**
     * 
     * @param img
     * @return 
     */
    public List<Integer> detectSegmentationPoints(int[][] img) {
        LinkedList<Integer> sps = new LinkedList<>();
        
        sps.add(0);        
        
        for(int column=0; column<img[0].length; column++) {
            boolean allBackground = true;
            
            for(int row=0; row<img.length; row++) {
                if(img[row][column] == 1) {
                    allBackground = false;
                    break;
                }
            }
            
            if(allBackground) 
                sps.add(column);
        }
        
        List<Integer> indexToRemove = new LinkedList<>();
        
        for(int i=1; i<sps.size()-1; i++) {            
            if(sps.get(i)-1 == sps.get(i-1) && sps.get(i)+1 == sps.get(i+1)) {                
                indexToRemove.add(i-1);
                indexToRemove.add(i+1);
            }
        }
        
        for(int i=indexToRemove.size()-1; i>=0; i--) {
            sps.remove(indexToRemove.get(i).intValue());
        }
        
        sps.add(img[0].length-1);                
        
        return sps;
    }
    
    /**
     * 
     * @param buffImg
     * @param sps
     * @return 
     */
    public List<Integer> overSegmentation(BufferedImage buffImg, List<Integer> sps) {
        List<Integer> psps = new LinkedList<>();
     
        int[][] img = this.imageToArray(buffImg);                        
        
        HashMap<String, Integer> middleRegion = this.getMiddleRegion(img);
        int upperBaseline = middleRegion.get("upperBaseline");
        int lowerBaseline = middleRegion.get("lowerBaseline");
        
        int[][] imgThinned = this.thinning(img);                
        
        int wordWidth = imgThinned[0].length;
        
        int[][][] wordObjects = new int[sps.size()-1][][];
        
        for(int i=sps.size()-1, j=0; i>=1; i--, j++) {
            int sps1 = sps.get(i), 
                    sps2 = sps.get(i-1);
            
            wordObjects[j] = this.getSubArrayNoCrop(imgThinned, sps2, 0, sps1-sps2, imgThinned.length);
        }
                
        for(int h=0; h<wordObjects.length; h++) {
            List<Integer> pspWord = new LinkedList<>();
                          
            int[][] centerImg = this.getSubArrayNoCrop(wordObjects[h], 0, upperBaseline, wordWidth, lowerBaseline-upperBaseline);
            
            int subWordWidth = this.universeOfDiscourse(centerImg, true, false)[0].length;                        

            int[] mvHistogram = this.calcModifiedVerticalHistogram(centerImg);
                       
            List<Integer> localMaxima = this.seekLocalMaxima(mvHistogram);

            double acw = (double) subWordWidth / (double) localMaxima.size()-1;
            
            int sp1 = sps.get(sps.size()-1-h), 
                    sp2 = sps.get(sps.size()-1-h-1);                
            
            for(int i=localMaxima.size()-1; i>=1; i--) {
                int lMax1 = localMaxima.get(i);
                int lMax2 = localMaxima.get(i-1);

                int distance = lMax1 - lMax2;

                int pspPosition;
                if(distance <= acw) {
                    pspPosition = Math.round(lMax2 + (distance*0.5f));
                } else {
                    pspPosition = Math.round(lMax2 + (distance*0.4f));
                }

                pspWord.add(pspPosition);
            }                        
            
            List<Integer> toRemove = new LinkedList(), toAdd = new LinkedList();
//<editor-fold defaultstate="collapsed" desc="additional technique 'acw'">
            pspWord.add(0,sp1);     
            pspWord.add(sp2);

            for(int i=0; i<pspWord.size()-1; i++) {
                int psp1 = pspWord.get(i);
                int psp2 = pspWord.get(i+1);
                                
                int distance = Math.abs(psp1 - psp2);
                
                if(distance <= acw) {                   
                    pspWord.remove(i+1);
                    i--;
                } 
                else {
                    toAdd.add(psp2 + Math.round(distance*0.6f));
                }
            }                                             
            
            pspWord.addAll(toAdd);
            
            pspWord.remove(Integer.valueOf(sp1));
            pspWord.remove(Integer.valueOf(sp2));                                    
//            
//this.writeImage(this.renderWordAccordingToSegmentationPoints(buffImg, pspWord), "oversegmentation2_"+h+".bmp");            
//</editor-fold>

            
//addition technique : holes detection
            toRemove = new LinkedList<>();
            
            for(int e : pspWord) {
                
                int strokeDetected = 0;
                for(int i=0; i<imgThinned.length; i++) {
                    if(imgThinned[i][e] == 1) strokeDetected++;
                }
                
                if(strokeDetected > 1) toRemove.add(e);
            }            
            
            pspWord.removeAll(toRemove);            

            psps.addAll(pspWord);
        }                
        
        psps = psps.stream().distinct().collect(Collectors.toList());
        
        Collections.sort(psps, Collections.reverseOrder());
        
        return psps;
    }
    
    public List<HashMap<String,int[][]>> pspScanning(int[][] imgThinned, List<Integer> psp, int pspIndex) {                
        List<HashMap<String,int[][]>> result = new LinkedList<>();
        
        int pspCount = psp.size()-pspIndex,
                pspCheckCount = (pspCount >= 4 ? (4) : (pspCount));

//            System.out.println("pspCount:"+pspCount);
//            System.out.println("pspCheckCount:"+pspCheckCount);

        int pspCheck1 = psp.get(pspIndex-1);
        
        for(int i=pspIndex, checked=1; checked<=pspCheckCount; checked++, i++) {
            int pspCheck2 = psp.get(i);
            
//            System.out.println(pspCheck1+" "+pspCheck2);

            HashMap<String, int[][]> area = new HashMap<>();
            
            int[][] areaImg = this.getSubArray(imgThinned, pspCheck2, 0, pspCheck1 - pspCheck2, imgThinned.length);
            
            int[][][] rccc = this.splitCharacterToCCRC(areaImg);
            
            int[][] rc = this.universeOfDiscourse(rccc[1], true, true), 
                    cc = this.universeOfDiscourse(rccc[0], true, true), 
                    sa = this.universeOfDiscourse(
                            this.getSubArray(imgThinned, pspCheck2-5, 0, 10, imgThinned.length), 
                            true, true);
            
            area.put("rc", rc);
            area.put("cc", cc);
            area.put("sa", sa);
            
            result.add(area);
        }
        
        return result;
    }
}
