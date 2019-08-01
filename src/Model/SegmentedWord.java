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
package Model;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author acdwisu
 */
public class SegmentedWord {
    
    /**  */
    private int[][] originalImage;
    
    /**  */
    private List<List<Point>> objectsStroke;
    
    /**  */
    private List<Integer> segmentationPoints;
    
    /**  */
    private List<Integer> prospectiveSP;    
    
    /**  */
    private List<Integer> dotObjectsIndex;
    
    /**  */
    private List<Integer> wordObjectsIndex;
    
    /**  */
    private int[][] segmentedWord;

    public SegmentedWord() {
        originalImage = null;
        
        objectsStroke = new LinkedList<>();
                
        segmentationPoints = new LinkedList<>();
        
        prospectiveSP = new LinkedList<>();
        
        dotObjectsIndex = new LinkedList<>();
                
        segmentedWord = null;
    }

    public void setOriginalImage(int[][] originalImage) {
        this.originalImage = originalImage;
    }

    public void setObjectsStroke(List<List<Point>> objectsStroke) {
        this.objectsStroke = objectsStroke;
    }

    public void setSegmentationPoints(List<Integer> segmentationPoints) {
        this.segmentationPoints = segmentationPoints;
    }

    public void setProspectiveSP(List<Integer> prospectiveSP) {
        this.prospectiveSP = prospectiveSP;
    }

    public void setSegmentedWord(int[][] segmentedWord) {
        this.segmentedWord = segmentedWord;
    }

    public void setDotObjectsIndex(List<Integer> dotObjectsIndex) {
        this.dotObjectsIndex = dotObjectsIndex;
    }

    public void setWordObjectsIndex(List<Integer> wordObjectsIndex) {
        this.wordObjectsIndex = wordObjectsIndex;
    }

    public int[][] getOriginalImage() {
        return originalImage;
    }

    public List<List<Point>> getObjectsStroke() {
        return objectsStroke;
    }

    public List<Integer> getSegmentationPoints() {
        return segmentationPoints;
    }

    public List<Integer> getProspectiveSP() {
        return prospectiveSP;
    }

    public int[][] getSegmentedWord() {
        return segmentedWord;
    }

    public List<Integer> getDotObjectsIndex() {
        return dotObjectsIndex;
    }

    public List<Integer> getWordObjectsIndex() {
        return wordObjectsIndex;
    }
}
