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
package Processor.FeatureExtractor.WUMI;

/**
 *
 * @author acdwisu
 */
public class WeightingFunction {
    private Moment[] moments;
    private double[][] fxyBaru;
    
    /**
     * 
     * @return 
     */
    private double RG() {
        return Math.sqrt((moments[0].getMoment() + moments[1].getMoment()) /moments[7].getMoment());
    }
    
    /**
     * 
     * @return 
     */
    private double alphaParameter() {
        double alpha;
        alpha=2/RG();        
        return alpha;
        
    }
    
    /**
     * 
     * @param img
     * @param moment
     * @param xpusat
     * @param ypusat 
     */
    public void setFxyBaru(int[][] img, Moment moment[], double xpusat, double ypusat) {
        
        this.moments = moment;
        
        int width = img[0].length, height = img.length;
        
        fxyBaru = new double[height][width];
        
        for(int x=1; x<height; x++){
            for(int y=1; y<width; y++){
                fxyBaru[x][y] = 1 / (1+ (Math.pow(alphaParameter(),2)*(Math.pow((x-xpusat),2)+Math.pow((y-ypusat),2))));
                //System.out.print((fxyBaru[x][y])+"\t");
            }
            //System.out.println();
        }
        //System.out.println("alpha = "+ alphaParameter());
    }
    
    /**
     * 
     * @return 
     */
    public double[][] getFxyBaru() {
        return fxyBaru;
    }
}
