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
public class DataTest {
    
    private String nama;
    
    private String direktoriOri;
    
    private String direktoriProcessed;
    
    private String direkotoriSegmented;
    
    private String direktoriSps;
    
    private String direktoriPSpAreas;

    public DataTest(String nama, String direktoriOri, String direktoriProcessed, String direkotoriSegmented, 
            String direktoriSPs, String direktoriPSpAreas) {
        this.nama = nama;
        this.direktoriOri = direktoriOri;
        this.direktoriProcessed = direktoriProcessed;
        this.direkotoriSegmented = direkotoriSegmented;
        this.direktoriSps = direktoriSPs;
        this.direktoriPSpAreas = direktoriPSpAreas;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setDirektoriOri(String direktoriOri) {
        this.direktoriOri = direktoriOri;
    }

    public void setDirektoriProcessed(String direktoriProcessed) {
        this.direktoriProcessed = direktoriProcessed;
    }

    public void setDirekotoriSegmented(String direkotoriSegmented) {
        this.direkotoriSegmented = direkotoriSegmented;
    }

    public void setDirektoriSps(String direktoriSps) {
        this.direktoriSps = direktoriSps;
    }

    public void setDirektoriPSpAreas(String direktoriPSpAreas) {
        this.direktoriPSpAreas = direktoriPSpAreas;
    }

    public String getNama() {
        return nama;
    }

    public String getDirektoriOri() {
        return direktoriOri;
    }

    public String getDirektoriProcessed() {
        return direktoriProcessed;
    }

    public String getDirekotoriSegmented() {
        return direkotoriSegmented;
    }

    public String getDirektoriSps() {
        return direktoriSps;
    }

    public String getDirektoriPSpAreas() {
        return direktoriPSpAreas;
    }
}
