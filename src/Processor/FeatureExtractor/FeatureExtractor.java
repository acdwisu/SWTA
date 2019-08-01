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
package Processor.FeatureExtractor;

import Processor.FeatureExtractor.Zoning.AdaptiveZoning;
import Processor.FeatureExtractor.Zoning.StaticZoning;
import Processor.FeatureExtractor.Zoning.Zoning;
import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author acdwisu
 */
public abstract class FeatureExtractor {        
    protected HashMap<String, Integer> config;
    protected int[][] img;

    public FeatureExtractor(HashMap<String, Integer> config, int[][] img) {
        this.config = config;
        this.img = img;
    }     

    public FeatureExtractor(HashMap<String, Integer> config) {
        this.config = config;
    }

    public FeatureExtractor() {
    }
    
    public abstract double[] getFeatures();       
        
    protected double[][] intArrayToDoubleArray(int[][] src) {
        int width  = src[0].length, height = src.length;
        
        double[][] result = new double[height][width];
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {
                result[i][j] = (double) src[i][j];
            }
        }
        
        return result;
    }
    
    protected double[] transform2DTo1DFeatures(final double[][] src) {
        int height = src.length, width = src[0].length;
        
        double[] result = new double[height * width];
        
        int index = 0;
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {                
                result[index] = src[i][j];                                
                
                index++;
            }
        }
        
        return result;
    }
    
    protected void cleaningNaNFeatures(double[] features) {        
        for(int i=0; i<features.length; i++) {
            if(Double.isNaN(features[i])) features[i] = 0;
            
        }
    }
    
    public LinkedList<Point[]> getZonesPosition(HashMap<String, Integer> config) {
        int zoneHorizontalCount = config.get("zoneHorizontalCount"), 
                zoneVerticalCount = config.get("zoneVerticalCount"),
                zoneType = config.get("zoneType");
        
        Zoning zoning;
        
        switch(zoneType) {
            case Zoning.STATIC_ZONE:
                zoning = new StaticZoning(zoneHorizontalCount, zoneVerticalCount);                
                break;
            case Zoning.ADAPTIVE_ZONE:
                int offsetX = config.get("offsetX"),
                        offsetY = config.get("offsetY");
                
                zoning = new AdaptiveZoning(zoneHorizontalCount, zoneVerticalCount, offsetX, offsetY);                
                break;
            default:
                zoning = new StaticZoning(zoneHorizontalCount, zoneVerticalCount);                
        }        
        
        return zoning.getZonesPosition(img);
    }

    public void setImg(int[][] img) {
        this.img = img;
    }

    public void setConfig(HashMap<String, Integer> config) {
        this.config = config;
    }
}
