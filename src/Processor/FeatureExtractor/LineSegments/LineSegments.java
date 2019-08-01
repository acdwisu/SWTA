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
package Processor.FeatureExtractor.LineSegments;

import Processor.FeatureExtractor.FeatureExtractor;
import Processor.ImageProcessor;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author acdwisu
 */
public class LineSegments extends FeatureExtractor {

    public LineSegments(HashMap<String, Integer> config, int[][] img) {
        super(config, img);
    }

    public LineSegments(HashMap<String, Integer> config) {
        super(config);
    }

    @Override
    public double[] getFeatures() {
        int zoneHorizontalCount = config.get("zoneHorizontalCount"), 
                zoneVerticalCount = config.get("zoneVerticalCount");
        
        LinkedList<Point[]> zonesPosition = getZonesPosition(config);                
        
        int zoneCount = zoneHorizontalCount * zoneVerticalCount;
        
        double[][] features = new double[zoneHorizontalCount * zoneVerticalCount][9];
        
        ImageProcessor proc = new ImageProcessor();                
        
        for(int zoneNumber=0; zoneNumber<zoneCount; zoneNumber++) {            
            Point[] zonePosition = zonesPosition.get(zoneNumber);
            
            int zoneWidth = zonePosition[1].x-zonePosition[0].x+1, zoneHeight = zonePosition[1].y-zonePosition[0].y+1;
            
            int[][] zone = proc.getSubArray(img, zonePosition[0].x, zonePosition[0].y, zoneWidth, zoneHeight);
            
            List<List<Point>> objects = proc.getObjectsFromWord(zone);
            
            LinkedList<List<Point>> segments = new LinkedList<>();           
            
            int intersectionPixelsCount = 0;
            
            for(List<Point> object : objects) {
                int [][] objImg = proc.drawFromPoints(object, zone[0].length, zone.length, true, true);
                
                LinkedList<Point> intersectionPixels = proc.getIntersectionPixelPositions(objImg);
                LinkedList<Point> starterPixels = proc.getStarterPixelPositions(objImg);
                
                intersectionPixelsCount+=intersectionPixels.size();
                
                if(intersectionPixels.size() > 0) {
                    LinkedList<Point> segmentEdges = new LinkedList<>();
                    List<Point> minorStarters = new LinkedList<>();

                    for(Point intersectionPixel : intersectionPixels) {
                        List<Point> neighbours = proc.getNeighbourStrokeNoAdjacent(intersectionPixel, new LinkedList<>(), objImg);

                        for(Point neighbour : neighbours) minorStarters.add(neighbour);                                    
                    }

                    segmentEdges.addAll(starterPixels);
                    segmentEdges.addAll(minorStarters);

                    for(Point intersectionPixel : intersectionPixels) {                               
                        List<Point> neighbours = proc.getNeighbourStrokeNoAdjacent(intersectionPixel, new LinkedList<>(), objImg);

                        for(Point neighbour : neighbours) {
                            if(proc.containPoint(intersectionPixels, neighbour)) 
                                continue;

                            List<Point> segment = new LinkedList<>();                    
                            segment.add(neighbour);                                        

                            List<Point> prevPoint = new LinkedList<>();
                            prevPoint.add(intersectionPixel);
                            prevPoint.addAll(neighbours);

                            List<Point> nextNeighbours = proc.getNeighbourStrokeNoAdjacent(neighbour, prevPoint, objImg);

                            if(nextNeighbours.size() > 0) {
                                Point nextNeighbour = nextNeighbours.get(0);
                                segment.add(nextNeighbour);

                                segment = proc.followStrokeV2(nextNeighbour, objImg, segmentEdges, segment);
                            } else 
                                segment = proc.followStrokeV2(neighbour, objImg, segmentEdges, segment); 

                            segments.add(segment);
                        }
                    }   
                } else {                                        
                    for(Point starterPixel : starterPixels) {
                        List<Point> segment = new LinkedList<>(); 
                        
                        Point neighbour = proc.getNeighbourStrokeNoAdjacent(starterPixel, segment, objImg).get(0);
                        
                        segment = proc.followStrokeV2(neighbour, objImg, starterPixels, segment);
                        
                        segments.add(segment);
                    }
                }
            }            
             
            proc.deleteDuplicateSegments(segments);                                    

            int segmentsSize = segments.size();
            int[] segmentsType = new int[segmentsSize];
            int[] intensityOfSegments = new int[segmentsSize];

            int h=0;
            for(List<Point> segment : segments) {
                int vectorSize = segment.size();
                int[] directionVector = new int[vectorSize];

                directionVector[0] = 2;
                for(int i=1; i<vectorSize; i++) {
                    Point currentPoint = segment.get(i), prevPoint = segment.get(i-1);

                    int direction=5;

                    if(currentPoint.x == prevPoint.x && ((currentPoint.y == prevPoint.y-1) || (currentPoint.y == prevPoint.y+1)))
                        direction=1;
                    else if(currentPoint.y == prevPoint.y && ((currentPoint.x == prevPoint.x-1) || (currentPoint.x == prevPoint.x+1)))
                        direction=3;
                    else if((currentPoint.x == prevPoint.x+1 && currentPoint.y == prevPoint.y+1) || 
                            (currentPoint.x == prevPoint.x-1 && currentPoint.y == prevPoint.y-1))
                        direction=2;                    
                    else if((currentPoint.x == prevPoint.x+1 && currentPoint.y == prevPoint.y-1) || 
                            (currentPoint.x == prevPoint.x-1 && currentPoint.y == prevPoint.y+1))
                        direction=4;

                    else if(currentPoint.x == prevPoint.x && ((currentPoint.y < prevPoint.y) || (currentPoint.y > prevPoint.y)))
                        direction=1;
                    else if(currentPoint.y == prevPoint.y && ((currentPoint.x < prevPoint.x) || (currentPoint.x > prevPoint.x)))
                        direction=3;
                    else if((currentPoint.x > prevPoint.x && currentPoint.y > prevPoint.y) || 
                            (currentPoint.x < prevPoint.x && currentPoint.y < prevPoint.y))
                        direction=2;
                    else if((currentPoint.x > prevPoint.x && currentPoint.y < prevPoint.y) || 
                            (currentPoint.x < prevPoint.x && currentPoint.y > prevPoint.y))
                        direction=4;

                    directionVector[i] = direction;
                }   

                segmentsType[h] = this.classifyLineSegmentType(directionVector);
                intensityOfSegments[h] = segments.size();

                h++;    
            }
            
            HashMap<String, Integer> lineTypesIntensity = this.countOfEachLineType(segmentsType);
            
            HashMap<String, Double> normalizedNumberOfLines = this.calcNormalizeNumberOfLines(lineTypesIntensity);
            HashMap<String, Double> normalizedLengthOfLines = this.calcNormalizeLengthOfLines(segmentsType, 
                    intensityOfSegments, proc.countPixelIntensity(zone));
            
            features[zoneNumber][0] = normalizedNumberOfLines.get("4");
            features[zoneNumber][1] = normalizedNumberOfLines.get("2");
            features[zoneNumber][2] = normalizedNumberOfLines.get("1");
            features[zoneNumber][3] = normalizedNumberOfLines.get("3");
            features[zoneNumber][4] = normalizedLengthOfLines.get("4");
            features[zoneNumber][5] = normalizedLengthOfLines.get("2");
            features[zoneNumber][6] = normalizedLengthOfLines.get("1");
            features[zoneNumber][7] = normalizedLengthOfLines.get("3");
            features[zoneNumber][8] = intersectionPixelsCount;
        }  
        
        double[] res = transform2DTo1DFeatures(features);
        
        super.cleaningNaNFeatures(res);
        
        return res;
    }

    public double[] getFeatures0() {
        int zoneHorizontalCount = config.get("zoneHorizontalCount"), 
                zoneVerticalCount = config.get("zoneVerticalCount");
        
        LinkedList<Point[]> zonesPosition = getZonesPosition(config);                
        
        int zoneCount = zoneHorizontalCount * zoneVerticalCount;
        
        double[][] features = new double[zoneHorizontalCount * zoneVerticalCount][9];
        
        ImageProcessor proc = new ImageProcessor();                
        
        for(int zoneNumber=0; zoneNumber<zoneCount; zoneNumber++) {            
            Point[] zonePosition = zonesPosition.get(zoneNumber);
            
            int zoneWidth = zonePosition[1].x-zonePosition[0].x+1, zoneHeight = zonePosition[1].y-zonePosition[0].y+1;
            
            int[][] zone = proc.getSubArray(img, zonePosition[0].x, zonePosition[0].y, zoneWidth, zoneHeight);
            
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
            
            int segmentsSize = segments.size();
            int[] segmentsType = new int[segmentsSize];
            int[] intensityOfSegments = new int[segmentsSize];
            
            int h=0;
            for(List<Point> segment : segments) {
                int vectorSize = segment.size();
                int[] directionVector = new int[vectorSize];
                
                directionVector[0] = 2;
                for(int i=1; i<vectorSize; i++) {
                    Point currentPoint = segment.get(i), prevPoint = segment.get(i-1);
                    
                    int direction=5;
                    
                    if(currentPoint.x == prevPoint.x && ((currentPoint.y == prevPoint.y-1) || (currentPoint.y == prevPoint.y+1)))
                        direction=1;
                    else if(currentPoint.y == prevPoint.y && ((currentPoint.x == prevPoint.x-1) || (currentPoint.x == prevPoint.x+1)))
                        direction=3;
                    else if((currentPoint.x == prevPoint.x+1 && currentPoint.y == prevPoint.y+1) || 
                            (currentPoint.x == prevPoint.x-1 && currentPoint.y == prevPoint.y-1))
                        direction=2;                    
                    else if((currentPoint.x == prevPoint.x+1 && currentPoint.y == prevPoint.y-1) || 
                            (currentPoint.x == prevPoint.x-1 && currentPoint.y == prevPoint.y+1))
                        direction=4;
                    
                    else if(currentPoint.x == prevPoint.x && ((currentPoint.y < prevPoint.y) || (currentPoint.y > prevPoint.y)))
                        direction=1;
                    else if(currentPoint.y == prevPoint.y && ((currentPoint.x < prevPoint.x) || (currentPoint.x > prevPoint.x)))
                        direction=3;
                    else if((currentPoint.x > prevPoint.x && currentPoint.y > prevPoint.y) || 
                            (currentPoint.x < prevPoint.x && currentPoint.y < prevPoint.y))
                        direction=2;
                    else if((currentPoint.x > prevPoint.x && currentPoint.y < prevPoint.y) || 
                            (currentPoint.x < prevPoint.x && currentPoint.y > prevPoint.y))
                        direction=4;

                    directionVector[i] = direction;
                }   
                
                segmentsType[h] = this.classifyLineSegmentType(directionVector);
                intensityOfSegments[h] = segments.size();
                
                h++;
            }
            
            HashMap<String, Integer> lineTypesIntensity = this.countOfEachLineType(segmentsType);
            
            HashMap<String, Double> normalizedNumberOfLines = this.calcNormalizeNumberOfLines(lineTypesIntensity);
            HashMap<String, Double> normalizedLengthOfLines = this.calcNormalizeLengthOfLines(segmentsType, 
                    intensityOfSegments, proc.countPixelIntensity(zone));
            
            features[zoneNumber][0] = normalizedNumberOfLines.get("4");
            features[zoneNumber][1] = normalizedNumberOfLines.get("2");
            features[zoneNumber][2] = normalizedNumberOfLines.get("1");
            features[zoneNumber][3] = normalizedNumberOfLines.get("3");
            features[zoneNumber][4] = normalizedLengthOfLines.get("4");
            features[zoneNumber][5] = normalizedLengthOfLines.get("2");
            features[zoneNumber][6] = normalizedLengthOfLines.get("1");
            features[zoneNumber][7] = normalizedLengthOfLines.get("3");
            features[zoneNumber][8] = intersectionPixels.size();
        }  
        
        double[] res = transform2DTo1DFeatures(features);
        
        super.cleaningNaNFeatures(res);
        
        return res;
    }
    
    /**
     * 
     * @param directionVectors
     * @return 
     */
    private int classifyLineSegmentType(int[] directionVectors) {       
       int[] intensity = new int[4];
       
       for(int direction : directionVectors) {
           intensity[direction-1]++;
       }
       
       int indexModus=0;
       int temp=intensity[0];
       
       for(int i=1; i<4; i++) {
           if(temp > intensity[i]) {
               indexModus = i;
               temp = intensity[i];
           }
       }
       
       return indexModus+1;
    }
    
    /**
     * 
     * @param lineTypes
     * @return 
     */
    private HashMap<String, Integer> countOfEachLineType(int[] lineTypes) {
        int size = lineTypes.length;
        
        HashMap<String, Integer> counts = new HashMap<>();
        
        for(int i=1; i<=4; i++) {
            String key = Integer.toString(i);
            
            counts.put(key, 0);
        }
        
        for(int i=0; i<size-1; i++) {
            String key = Integer.toString(lineTypes[i]);
            int prevVal = counts.get(key);
            
            counts.put(key, prevVal+1);
        }
        
        return counts;
    }
    
    /**
     * 
     * @param lineTypeCounts
     * @return 
     */
    private HashMap<String, Double> calcNormalizeNumberOfLines(HashMap<String, Integer> lineTypeCounts) {
        HashMap<String, Double> result = new HashMap<>();
        
        for(int i=1; i<=4; i++) {
            String key = Integer.toString(i);
            double val = 1 - (( (lineTypeCounts.get(key)*1.0) / 10) * 2);
            
            result.put(key, val);
        }
        
        return result;
    }
    
    /**
     * 
     * @param linesType
     * @param intensityOfSegments
     * @param intensityOfZone
     * @return 
     */
    private HashMap<String, Double> calcNormalizeLengthOfLines(int[] linesType, int[] intensityOfSegments, int intensityOfZone) {
        HashMap<String, Double> result = new HashMap<>();
        
        int[] intensityOnLineType = new int[4];
        
        for(int i=0; i<linesType.length; i++) {
            intensityOnLineType[linesType[i]-1] += intensityOfSegments[i];
        }
        
        for(int i=1; i<=4; i++) {
            String key = Integer.toString(i);
            double val = (intensityOnLineType[i-1]*1.0) / intensityOfZone;
            
            result.put(key, val);
        }
        
        return result;
    }
    
}
