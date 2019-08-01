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

/**
 *
 * @author acdwisu
 */
public class OverallResult {
    
    private final String segmentationName;
    
    private final int totalPspsAcuan;
    
    private final int totalPsps;
    
    private final int correctPsps;
    
    private final int errorPsps;
    
    private final double percentage;

    public OverallResult(String segmentationName, int totalPspsAcuan, int totalPsps, int correctPsps, int errorPsps) {
        this.segmentationName = segmentationName;
        this.totalPspsAcuan = totalPspsAcuan;
        this.totalPsps = totalPsps;
        this.correctPsps = correctPsps;
        this.errorPsps = errorPsps;
        
        this.percentage = (double) correctPsps / totalPsps;
    }

    public String getSegmentationName() {
        return segmentationName;
    }

    public int getTotalPspsAcuan() {
        return totalPspsAcuan;
    }

    public int getTotalPsps() {
        return totalPsps;
    }

    public int getCorrectPsps() {
        return correctPsps;
    }

    public int getErrorPsps() {
        return errorPsps;
    }

    public double getPercentage() {
        return percentage;
    }
}
