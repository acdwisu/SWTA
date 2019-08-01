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
package Processor.FeatureExtractor.PixelDensity;

import Processor.FeatureExtractor.FeatureExtractor;
import Processor.ImageProcessor;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author acdwisu
 */
public class PixelDensity extends FeatureExtractor {

    public PixelDensity(HashMap<String, Integer> config, int[][] img) {
        super(config, img);
    }

    public PixelDensity(HashMap<String, Integer> config) {
        super(config);
    }
    
    @Override
    public double[] getFeatures() {
        int zoneHorizontalCount = config.get("zoneHorizontalCount"), 
                zoneVerticalCount = config.get("zoneVerticalCount");

        LinkedList<Point[]> zonesPosition = getZonesPosition(config);                
        
        int zoneCount = zoneHorizontalCount * zoneVerticalCount;
        
        double[] features = new double[zoneCount];
        
        ImageProcessor proc = new ImageProcessor();
        
        for(int zoneNumber=0; zoneNumber<zoneCount; zoneNumber++) {            
            Point[] zonePosition = zonesPosition.get(zoneNumber);
            
            int zoneWidth = zonePosition[1].x-zonePosition[0].x+1, zoneHeight = zonePosition[1].y-zonePosition[0].y+1;
            
            int[][] zone = proc.getSubArray(img, zonePosition[0].x, zonePosition[0].y, zoneWidth, zoneHeight);
            
            int pixelIntensityOnZone = proc.countPixelIntensity(zone);
            
            double zoneDensity = pixelIntensityOnZone*1.0 / (zoneWidth * zoneHeight);
            
            features[zoneNumber] = zoneDensity;
        }
        
        super.cleaningNaNFeatures(features);
        
        return features;    
    }          
}
