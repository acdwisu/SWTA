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
public class StaticZoning extends Zoning{    

    public StaticZoning(int zoneHorizontalCount, int zoneVerticalCount) {
        super(zoneHorizontalCount, zoneVerticalCount);                
    }

    @Override
    public LinkedList<Point[]> getZonesPosition(int[][] img) {
        LinkedList<Point[]> zonesPosition = new LinkedList<>();
        
        int imgHeight = img.length, imgWidth = img[0].length;
        
        int zoneHeight = imgHeight / zoneVerticalCount, zoneHeightMod = imgHeight % zoneVerticalCount;
        int zoneWidth = imgWidth / zoneHorizontalCount, zoneWidthMod = imgWidth % zoneHorizontalCount;        
        
        int[] zonesHeightAddition = new int[zoneVerticalCount];
        int[] zonesWidthAddition = new int[zoneHorizontalCount];
        
        for(int i=zoneHeightMod, j=0; i>0; i--, j++) {           
            zonesHeightAddition[j]++;
        }
        
        for(int i=zoneWidthMod, j=0; i>0; i--, j++) {
            zonesWidthAddition[j]++;
        }               
        
        for(int h =0; h<zoneVerticalCount; h++) {
            
            int startY;
            int endY;
            
            if(h==0) {
                startY = 0;
                endY = zoneHeight + zonesHeightAddition[h]-1;
            } else {
                int prevEndY = zonesPosition.getLast()[1].y;
                
                startY = prevEndY + 1;
                endY = startY + zoneHeight + zonesHeightAddition[h]-1;
            }
            
            for(int i=0; i<zoneHorizontalCount; i++) {                                                
                int startX;
                int endX;

                if(i==0) {
                    startX = 0;
                    endX = zoneWidth + zonesWidthAddition[i]-1;
                } else {
                    int prevEndX = zonesPosition.getLast()[1].x;

                    startX = prevEndX + 1;
                    endX = startX + zoneWidth + zonesWidthAddition[i]-1;
                }
                
                Point startPoint = new Point(startX,startY);
                Point endPoint = new Point(endX, endY);
                
                Point[] zonePoints = {startPoint, endPoint};
                
                zonesPosition.add(zonePoints);
            }            
        }
        
        return zonesPosition;
    }        
}
