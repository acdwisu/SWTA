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

import Controller.ControllerPelatihan;
import Processor.ImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestThinning {
    public static void main(String args[]) {
        new TestThinning().run();
    }
    
    private void run(){
        ImageProcessor p = new ImageProcessor();
        
        File folderRootSrc = new File("C:\\Users\\Aha\\Downloads\\j\\IESKarDB-sample\\pspScanned");

        String folderRootDest = "C:\\Users\\Aha\\Downloads\\j\\DBAHCL_thinned";
        
        int availProc = Runtime.getRuntime().availableProcessors();
        
        System.out.println("availProc:"+availProc);
        
        ExecutorService pool = Executors.newFixedThreadPool(availProc);           
        
        for(File file : folderRootSrc.listFiles()) {
            String fileName = file.getName().toLowerCase();            
            
            if(!(fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".png"))) continue;           
            
            pool.execute(new RunnableThinning(file, folderRootDest));                               
        }                                
        
        pool.shutdown();
    }
    
    private class RunnableThinning implements Runnable {
        private File file;
        private String destPath;
        private ImageProcessor p;
        
        public RunnableThinning(File file, String destPath) {
            this.file = file;
            this.destPath = destPath;
            
            p = new ImageProcessor();
        }

        @Override
        public void run() {            
            BufferedImage image = p.readImage(file.getPath());               

            int[][] imageArray = p.imageToArray(image);
            int[][] imageThinned = p.thinning(imageArray);           

            System.out.println(file.getName());
            
            p.writeImage(p.ArrayToImage(imageThinned), 
                    this.destPath+ "\\" +file.getName());
        }
    }
}
