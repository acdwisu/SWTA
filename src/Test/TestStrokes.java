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
public class TestStrokes {
    public static void main(String args[]) {
        new TestStrokes().run4();
    }
    
    private void run() {
        ImageProcessor p = new ImageProcessor();
        
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testStrokes3.bmp"));

            int[][] imageArray = p.imageToArray(image);
            
            Point point = new Point(27,109);
            int x = point.x;
            int y = point.y;

            List<Point> strokes = new LinkedList<>();
                  
            strokes = p.followStroke(point, imageArray, strokes);  
            
            int[][] imageArrayMod = p.setPointsToBackground(strokes, imageArray);
            
            imageArrayMod = p.universeOfDiscourse(imageArray, true, true);
            
            javax.imageio.ImageIO.write(p.ArrayToImage(imageArrayMod), 
                    "bmp", new File("testDrawStroke.bmp"));
            
//            for(Point j : strokes) {
//                System.out.printf("%d, %d \n", j.getX(), j.getY());
//            }
                          
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }                
    }    
    
    private void run2() {
        List<Point> points1 = new LinkedList<>(), points2 = new LinkedList<>();
        
        points1.add(new Point(1,2));
        points1.add(new Point(2,3));
        points1.add(new Point(3,4));
        points1.add(new Point(4,5));
        points1.add(new Point(5,6));
        
        points2.add(new Point(6,7));
        points2.add(new Point(7,8));
        points2.add(new Point(8,9));
        points2.add(new Point(9,10));
        points2.add(new Point(10,11));
        
        List<Point> merged = new LinkedList<>();
        merged.addAll(points1);
        merged.addAll(points2);
        
        for(Point p : merged) {
            System.out.printf("%d, %d\n", p.x, p.y);
        }
    }
    
    private void run3() {
        ImageProcessor p = new ImageProcessor();
        
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testFollowStroke.bmp"));

            int[][] imageArray = p.imageToArray(image);
            
            List<List<Point>> objects = p.getObjectsFromWord(imageArray);                        
            
            int i=1;
            for(List<Point> object : objects) {
                javax.imageio.ImageIO.write(p.ArrayToImage(p.drawFromPoints(object, imageArray[0].length, imageArray.length, true, true)), 
                    "bmp", new File("testDrawStroke" +(i++)+ ".bmp"));
//                System.out.println("------------");
//                for(Point g : object) {
//                    System.out.printf("x : %d, y: %d\n", g.x, g.y);
//                }
//
//                System.out.println("------------");

//                System.out.printf("Character Density method 1 : %d\n", p.countPixelIntensity(object));
//                System.out.printf("Character Density method 2 : %d\n", p.countPixelIntensity(p.drawFromPoints(object, 
//                        imageArray[0].length, imageArray.length, true, true)));
            }
                          
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }           
    }
    
    private void run4() {
        ImageProcessor proc = new ImageProcessor();
        
        try {
            BufferedImage image = javax.imageio.ImageIO.read(
                    new File("testStarters.bmp"));

            int[][] zone = proc.imageToArray(image);
            
            LinkedList<Point> intersectionPixels = proc.getIntersectionPixelPositions(zone);
            LinkedList<Point> starterPixels = proc.getStarterPixelPositions(zone);
            
            LinkedList<List<Point>> segments = new LinkedList<>();
            
            LinkedList<Point> segmentEdges = new LinkedList<>();
            
            List<Point> minorStarters = new LinkedList<>();
            for(Point intersectionPixel : intersectionPixels) {
                List<Point> neighbours = proc.getNeighbourStrokeNoAdjacent(intersectionPixel, new LinkedList<>(), zone);
                
                for(Point neighbour : neighbours) minorStarters.add(neighbour);                                    
            }
            
            segmentEdges.addAll(starterPixels);
            segmentEdges.addAll(minorStarters);
            
            for(Point intersectionPixel : intersectionPixels) {                               
                List<Point> neighbours = proc.getNeighbourStrokeNoAdjacent(intersectionPixel, new LinkedList<>(), zone);

                for(Point neighbour : neighbours) {
                    if(proc.containPoint(intersectionPixels, neighbour)) 
                        continue;

                    List<Point> segment = new LinkedList<>();                    
                    segment.add(neighbour);                                        
                    
                    List<Point> prevPoint = new LinkedList<>();
                    prevPoint.add(intersectionPixel);
                    prevPoint.addAll(neighbours);
                    
                    List<Point> nextNeighbours = proc.getNeighbourStrokeNoAdjacent(neighbour, prevPoint, zone);
                    
                    if(nextNeighbours.size() > 0) {
                        Point nextNeighbour = nextNeighbours.get(0);
                        segment.add(nextNeighbour);
                        
                        segment = proc.followStrokeV2(nextNeighbour, zone, segmentEdges, segment);
                    } else 
                        segment = proc.followStrokeV2(neighbour, zone, segmentEdges, segment);                             
                    
                    segments.add(segment);
                }
            }   
            
            proc.deleteDuplicateSegments(segments);
            
//            int i=0;
//            for(Point intersectionPixel : intersectionPixels) {       
//                System.out.println(++i);
//                
//                System.out.println(intersectionPixel.toString());
//                
//                System.out.println("--------");
//            }
            
            int i=1;
            for(List<Point> segment : segments) {
                System.out.println("Segment "+i);
                
                for(Point point : segment) {
                    System.out.printf("%3d, %3d\n", point.x,point.y);                    
                }
                
                System.out.println("--------------");
                i++;
            }
                          
        } catch (IOException ex) {
            Logger.getLogger(TestImageProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }           
                
    }    
}
