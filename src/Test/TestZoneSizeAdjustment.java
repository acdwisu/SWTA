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

import java.awt.Point;
import java.util.LinkedList;

/**
 *
 * @author acdwisu
 */
public class TestZoneSizeAdjustment {
 
    
    public static void main(String args[]) {
        new TestZoneSizeAdjustment().run();
    }
    
    public void run() {
        
        int[][] img = {
            {1,2,3,4,5,6,7,8,9,10,11,12},
            {10,11,12,13,14,15,16,1,2,3,4,17},
            {18,19,20,1,2,22,23,3,4,5,6,7},
            {12,13,14,15,16,17,1,2,3,4,5,6},
        };
        
        int zoneHorCount = 2, zoneVerCount = 2;       
        
        int zoneHeight = img.length / zoneVerCount, zoneHeightMod = img.length % zoneVerCount;
        int zoneWidth = img[0].length / zoneHorCount, zoneWidthMod = img[0].length % zoneHorCount;        
        
        int[] zonesHeightAddition = new int[zoneVerCount];
        int[] zonesWidthAddition = new int[zoneHorCount];
        
        for(int i=zoneHeightMod, j=0; i>0; i--, j++) {           
            zonesHeightAddition[j]++;
        }
        
        for(int i=zoneWidthMod, j=0; i>0; i--, j++) {
            zonesWidthAddition[j]++;
        }
        
        LinkedList<Point[]> zones = new LinkedList<>();
        
        for(int h =0; h<zoneVerCount; h++) {
            
            int startY;
            int endY;
            
            if(h==0) {
                startY = 0;
                endY = zoneHeight + zonesHeightAddition[h]-1;
            } else {
                int prevEndY = zones.getLast()[1].y;
                
                startY = prevEndY + 1;
                endY = startY + zoneHeight + zonesHeightAddition[h]-1;
            }
            
            for(int i=0; i<zoneHorCount; i++) {                                                
                int startX;
                int endX;

                if(i==0) {
                    startX = 0;
                    endX = zoneWidth + zonesWidthAddition[i]-1;
                } else {
                    int prevEndX = zones.getLast()[1].x;

                    startX = prevEndX + 1;
                    endX = startX + zoneWidth + zonesWidthAddition[i]-1;
                }
                
                Point startPoint = new Point(startX,startY);
                Point endPoint = new Point(endX, endY);
                
                Point[] zonePoints = {startPoint, endPoint};
                
                zones.add(zonePoints);
            }            
        }                 
        
        int i=1;
        for(Point[] points : zones) {
            System.out.println("zone "+i);
            for(Point point : points) {
                System.out.printf("%d, %d\n", point.x, point.y);
            }
            i++;
        }
    } 
}
