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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestLoop {
    public static void main(String args[]) {
        new TestLoop().testrename();
    }
    
    private void run() {
        for(int i=1; i<100; i++) {
            boolean stopLoop=false;
            System.out.printf("%d\n", i);
            
            for(char j='a'; j<'z'; j++) {
                if(i==50) {
                    stopLoop = true;
                    break;
                }
                System.out.printf("%c", j);
            }
            System.out.println("");
            if(stopLoop) break;
        }
    }
    
    private void r() {
        //System.out.println("klklkl.yuyuyu".split("\\.")[0]);
        
        List<Integer> k = new LinkedList<>();
        
        k.add(3);
        k.add(1);
        k.add(10);
        k.add(2);
        
        k.sort(null);
        
        Collections.shuffle(k);
        
        for(int x : k) {
            System.out.println(x);
        }
        
        
    }
    
    private void testrename() {
        String pathBenar = "C:\\Users\\Aha\\Documents\\data karakter\\Benar",
                pathSalah = "C:\\Users\\Aha\\Documents\\data karakter\\Salah";
        
        for(File x : new File(pathSalah).listFiles()) {
            if(x.getName().endsWith(".bmp")) {
                try {
                    Files.move(x.toPath(), new File(pathSalah.concat("\\K-S_"+x.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(TestLoop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
