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

import Processor.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestHistogram {
    public static void main(String args[]) {
        new TestHistogram().run3();
    }
    
    private void run() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke.bmp"));

            int[][] imageArray = p.imageToArray(image);                        
        
            int[] histogram = p.calcDensityHorizontalHistogram(imageArray);
            
            HashMap<String, List<Integer>> localMaximaMinima = p.seekLocalMaximaMinima(histogram);
            List<Integer> localMaxima = localMaximaMinima.get("localMaxima");
            List<Integer> localMinima = localMaximaMinima.get("localMinima");
            
//            System.out.println("local maxima");
//            for(int i : localMaxima) System.out.println(i);
//            
//            System.out.println("local minima");
//            for(int i : localMinima) System.out.println(i);
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void run2() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke.bmp"));

            int[][] imageArray = p.imageToArray(image);                        
        
            int[] histogram = p.calcDensityHorizontalHistogram(imageArray);
            
            HashMap<String, Integer> middleRegion = p.getMiddleRegion(imageArray);
            
            int upperBaseline = middleRegion.get("upperBaseline");
            int lowerBaseline = middleRegion.get("lowerBaseline");
            
            System.out.println("upperBaseline " +upperBaseline);
            System.out.println("lowerBaseline " +lowerBaseline);
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void run3() {
        try {
            ImageProcessor p = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testStrokes3_segmented_word.bmp"));

            int[][] imageArray = p.imageToArray(image);                        
        
            int[] histogram = p.calcModifiedVerticalHistogram(imageArray);
            
            int i=0;
            for(int v : histogram) {
                System.out.println(i+" : " +v);
                i++;
            }
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
