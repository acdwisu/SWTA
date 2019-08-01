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
import Processor.FeatureExtractor.WUMI.WUMI;
import Processor.FeatureExtractor.Zoning.Zoning;
import Processor.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestWUMI {
    public static void main(String[] args) {
        new TestWUMI().run1();
    }
    
    private void run1() {
        try {
            ImageProcessor proc = new ImageProcessor();
            
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testStarters.bmp"));

            int[][] img = proc.thinning(proc.universeOfDiscourse(proc.imageToArray(image), true, true));
            
//            FeatureExtractor wumi = new WUMI(img);

            
//            double[] wumiFeatures = wumi.getFeatures();

            HashMap<String, Integer> config = new HashMap<>();
                
            config.put("zoneHorizontalCount", 1);
            config.put("zoneVerticalCount", 1);
            config.put("zoneType", Zoning.STATIC_ZONE);

            FeatureExtractor ex = new LineSegments(config, img);
            
            double[] features = ex.getFeatures();
            
            for(double x : features) {
                System.out.println(x);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(TestWUMI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
