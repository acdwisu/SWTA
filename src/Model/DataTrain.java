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
public class DataTrain {
    
    /**  */
    private String nama;
    
    private String direktoriOri;
    
    /**  */
    private String kategori;
    
    /**  */
    private String label;       
    
    private String kelas;

    public DataTrain(String nama, String direktoriOri, String kategori, String label, String kelas) {
        this.nama = nama;
        this.direktoriOri = direktoriOri;
        this.kategori = kategori;
        this.label = label;  
        this.kelas = kelas;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setDirektoriOri(String direktoriOri) {
        this.direktoriOri = direktoriOri;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getNama() {
        return nama;
    }

    public String getDirektoriOri() {
        return direktoriOri;
    }

    public String getKategori() {
        return kategori;
    }

    public String getLabel() {
        return label;
    }

    public String getKelas() {
        return kelas;
    }
    
}
