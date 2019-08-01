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
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestPoint {
    public static void main(String args[]) {
        new TestPoint().run3();
    }
    
    //test method point.contain
    private void run() {
        List<Point> points = new LinkedList<>();
        
        points.add(new Point(1,2));
        points.add(new Point(2,3));
        points.add(new Point(3,4));
        points.add(new Point(4,5));
        points.add(new Point(5,6));
        
        System.out.println(new ImageProcessor().containPoint(points, new Point(3,2)));
    }    
    
    //test method count neighbour
    private void run2() {
        ImageProcessor p = new ImageProcessor();
        
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testPoint.bmp"));

            int[][] imageArray = p.imageToArray(image);
            
            Point point = new Point(0,1);
            int x = (int) point.getX();
            int y = (int) point.getY();
            int count = p.getNeighbourCount(point, imageArray);
            
            System.out.printf("neighbour count of %d,%d = %d\n", x,y,count);
            
            
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    //test method get neighbour
    private void run3() {
        ImageProcessor p = new ImageProcessor();
        
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testPoint.bmp"));

            int[][] imageArray = p.imageToArray(image);
            
            Point point = new Point(1,1);
            int x = (int) point.getX();
            int y = (int) point.getY();
//            List<Point> points = p.getNeighbourStroke(point, imageArray);

            List<Point> prevPoints = new LinkedList<>();
            prevPoints.add(new Point(0,1));
            
            List<Point> points = p.getNeighbourStroke(point, prevPoints, imageArray);

            int i=1;
            for(Point o : points) {               
                System.out.printf("%d, %d\n", o.getX(), o.getY());
            }                      
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}
