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
 * along with this program; if not, writeSegmentationPoint to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package FileIO;

import Controller.ControllerDataUji;
import Controller.ControllerModifikasiSP;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class FileIOSegmentationPoints {
       
    public void writeSegmentationPoint(String path, List<Integer> psps) {
        try {
            PrintWriter writer = new PrintWriter(new File((path)));

            String line = "";
            
            for(int psp : psps) {
                line = line.concat(psp+ ",");             
            }

            writer.print(line);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Logger.getLogger(FileIOSegmentationPoints.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public List<Integer> readSegmentationPoint(String path) {
        List<Integer> psps = new LinkedList<>();                
        
        BufferedReader buff = null;
        
        if(new File(path).exists()) {
            try {
                buff = new BufferedReader(new FileReader(path));

                String line;

                line = buff.readLine();

                if(line != null) {
                    String[] pspsTemp = line.split(",");

                    for(String psp : pspsTemp) {
                        Integer val = Integer.parseInt(psp.trim());
                        
                        psps.add(val);                        
                    }
                }
                
                buff.close();
            } catch (Exception e) {
                Logger.getLogger(FileIOSegmentationPoints.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        
        return psps;
    }
    
    public void writeAreaPSP(String path, List<int[]> pspAreas) {
        try {
            PrintWriter writer = new PrintWriter(new File((path)));

            String line = "";
            
            for(int[] pspArea : pspAreas) {
                line = line.concat(pspArea[0]+ "-" +pspArea[1]+ ",");             
            }

            writer.print(line);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Logger.getLogger(FileIOSegmentationPoints.class.getName()).log(Level.SEVERE, null, e);
        }
    } 
    
    public List<int[]> readAreaPSP(String path) {
        List<int[]> pspAreas = new LinkedList<>();                
        
        BufferedReader buff = null;
        
        if(new File(path).exists()) {
            try {
                buff = new BufferedReader(new FileReader(path));

                String line;

                line = buff.readLine();

                if(line != null) {
                    String[] pspAreasTemp = line.split(",");

                    for(String pspAreaTemp : pspAreasTemp) {
                        String points[] = pspAreaTemp.split("-");
                        
                        int[] val = {Integer.parseInt(points[0]), Integer.parseInt(points[1]) };
                        
                        pspAreas.add(val);                        
                    }
                }
                
                buff.close();
            } catch (Exception e) {
                Logger.getLogger(FileIOSegmentationPoints.class.getName()).log(Level.SEVERE, null, e);
            } 
        }
        
        return pspAreas;
    }
}
