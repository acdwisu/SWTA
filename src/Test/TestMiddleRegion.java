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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author acdwisu
 */
public class TestMiddleRegion {
    
    public static void main(String args[]) {
        new TestMiddleRegion().run1();
    }
    
    public void run1() {
        ImageProcessor proc = new ImageProcessor();
        
        String pathSrc = "C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\datasets\\test\\IESKarDB\\processed";
        
        String pathOS = "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\over_segmented2";
        
        String pathDest = "C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\baselined";
        
        File fileSrc = new File(pathSrc);
        
        File fileDest = new File(pathDest);
        
        fileDest.mkdir();
        
        for(File file : fileSrc.listFiles()) {
            String loweredCase = file.getName().toLowerCase();
            
            if(loweredCase.endsWith(".bmp")) {
                try {
                    BufferedImage buff = ImageIO.read(file), buffThinned;
                    
                    int[][] img = proc.imageToArray(buff);
                    img = proc.universeOfDiscourse(img, true, true);
                    
                    HashMap<String, Integer> middleRegion = proc.getMiddleRegion(img);
                    
                    int upperBaseline = middleRegion.get("upperBaseline"),
                            lowerBaseline = middleRegion.get("lowerBaseline");
                    
                    int[][] imgThinned = proc.thinning(img);
                    
                    buffThinned = proc.ArrayToImage(imgThinned);
                    
                    int[][] centerImg = proc.getSubArrayNoCrop(imgThinned, 0, upperBaseline, imgThinned[0].length, 
                            lowerBaseline-upperBaseline);
            
                    int[] mvHistogram = proc.calcModifiedVerticalHistogram(centerImg);

                    HashMap<String, List<Integer>> localMaximaMinima = proc.seekLocalMaximaMinima(mvHistogram);
                    List<Integer> localMaxima = localMaximaMinima.get("localMaxima"), 
                            localMinima = localMaximaMinima.get("localMinima");
                    
                    BufferedImage middleRegioned = proc.renderWordAccordingToBaseline(buffThinned, upperBaseline, lowerBaseline),
                            histogram = proc.renderHistogram(mvHistogram),
                            oversegmented = proc.readImage(pathOS+"\\"+loweredCase);

                    BufferedImage compacted = new BufferedImage(middleRegioned.getWidth(), 
                            buff.getHeight()+middleRegioned.getHeight()+histogram.getHeight()+oversegmented.getHeight()+ 9, 
                            BufferedImage.TYPE_INT_RGB);
                    
                    Graphics g = compacted.createGraphics();
                    
                    g.drawImage(buff, 0, 0, null);

                    g.drawImage(middleRegioned, 0, buff.getHeight()+4, null);

                    g.drawImage(histogram, 0, buff.getHeight()+middleRegioned.getHeight()+8, null);

                    g.drawImage(oversegmented, 0, buff.getHeight()+middleRegioned.getHeight()+histogram.getHeight()+12, null);
                    
                    proc.writeImage(compacted, pathDest.concat("\\"+file.getName()));
                    
                } catch (IOException ex) {
                    Logger.getLogger(TestMiddleRegion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
