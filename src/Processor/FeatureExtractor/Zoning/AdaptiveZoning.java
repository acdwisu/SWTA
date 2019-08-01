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
package Processor.FeatureExtractor.Zoning;

import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author acdwisu
 */
public class AdaptiveZoning extends Zoning {

    private final int rentangX;
    
    private final int rentangY;

    public AdaptiveZoning(int zoneHorizontalCount, int zoneVerticalCount, int rentangX, int rentangY) {
        super(zoneHorizontalCount, zoneVerticalCount);
        
        this.rentangX = rentangX;
        this.rentangY = rentangY;
    }       

    @Override
    public LinkedList<Point[]> getZonesPosition(int[][] img) {        
        StaticZoning sz = new StaticZoning(this.zoneHorizontalCount, this.zoneVerticalCount);
        
        LinkedList<Point[]> zonesPosition = sz.getZonesPosition(img);
        
        for(Point[] zonePosition : zonesPosition) {            
            int zoneWidth = zonePosition[1].x-zonePosition[0].x+1, zoneHeight = zonePosition[1].y-zonePosition[0].y+1;
            
            int dx = 0, dy = 0;    
            int pixelIntensityOnZone=0;
            
            for(int i=-rentangX; i<=rentangX; i++) {                
                for(int j=-rentangY; j<=rentangY; j++) {                    
                    int trialX = zonePosition[0].x + i, trialY = zonePosition[0].y + j;
                            
                    if(trialX < 0 || trialY < 0 || trialX > img[0].length || trialY > img.length) continue;
                    
                    int[][] zone = proc.getSubArray(img, trialX, trialY, zoneWidth, zoneHeight);
                    
                    int temp = proc.countPixelIntensity(zone);
                    
                    if(temp > pixelIntensityOnZone) {
                        pixelIntensityOnZone = temp;
                        dx = i;
                        dy = j;
                    }
                }
            }
            
            //adjust correction   
            zonePosition[0].x += dx;
            zonePosition[0].y += dy;
            zonePosition[1].x += dx;
            zonePosition[1].y += dy;            
        }                
        
        return zonesPosition;
    }
}
