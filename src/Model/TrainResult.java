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
public class TrainResult {
    
    /**  */
    private int id;
    
    /**  */
    private String namaPelatihan;
    
    /**  */
    private String namaDataset;
    
    /**  */
    private String ekstraksi;
    
    /**  */
    private String parameterEkstraksi;
    
    private String parameterRF;
    
    private String akurasiKarakter;
    
    private String akurasiSA;
    
    private String durasiKarakter;
    
    private String durasiSA;

    /**
     * 
     * @param id
     * @param nama
     * @param namaDataset
     * @param fitur
     * @param parameter 
     */
    public TrainResult(int id, String nama, String namaDataset, String ekstraksi, String parameter) {
        this.id = id;
        this.namaPelatihan = nama;
        this.namaDataset = namaDataset;
        this.ekstraksi = ekstraksi;
        this.parameterEkstraksi = parameter;
    }

    public TrainResult(String namaPelatihan, String namaDataset, String ekstraksi, String parameterEkstraksi, 
            String parameterRF, String akurasiKarakter, String akurasiSA, String durasiKarakter, String durasiSA) {
        this.namaPelatihan = namaPelatihan;
        this.namaDataset = namaDataset;
        this.ekstraksi = ekstraksi;
        this.parameterEkstraksi = parameterEkstraksi;
        this.parameterRF = parameterRF;
        this.akurasiKarakter = akurasiKarakter;
        this.akurasiSA = akurasiSA;
        this.durasiKarakter = durasiKarakter;
        this.durasiSA = durasiSA;
    }

    /**
     * 
     * @return 
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @return 
     */
    public String getNamaPelatihan() {
        return namaPelatihan;
    }

    /**
     * 
     * @return 
     */
    public String getNamaDataset() {
        return namaDataset;
    }

    /**
     * 
     * @return 
     */
    public String getEkstraksi() {
        return ekstraksi;
    }

    /**
     * 
     * @return 
     */
    public String getParameterEkstraksi() {
        return parameterEkstraksi;
    }

    public String getParameterRF() {
        return parameterRF;
    }  

    public String getAkurasiKarakter() {
        return akurasiKarakter;
    }

    public String getAkurasiSA() {
        return akurasiSA;
    }

    public String getDurasiKarakter() {
        return durasiKarakter;
    }

    public String getDurasiSA() {
        return durasiSA;
    }

    /**
     * 
     * @param id 
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @param namaPelatihan 
     */
    public void setNamaPelatihan(String namaPelatihan) {
        this.namaPelatihan = namaPelatihan;
    }

    /**
     * 
     * @param namaDataset 
     */
    public void setNamaDataset(String namaDataset) {
        this.namaDataset = namaDataset;
    }

    /**
     * 
     * @param ekstraksi 
     */
    public void setEkstraksi(String ekstraksi) {
        this.ekstraksi = ekstraksi;
    }

    /**
     * 
     * @param parameterEkstraksi 
     */
    public void setParameterEkstraksi(String parameterEkstraksi) {
        this.parameterEkstraksi = parameterEkstraksi;
    }

    public void setParameterRF(String parameterRF) {
        this.parameterRF = parameterRF;
    }

    public void setAkurasiKarakter(String akurasiKarakter) {
        this.akurasiKarakter = akurasiKarakter;
    }

    public void setAkurasiSA(String akurasiSA) {
        this.akurasiSA = akurasiSA;
    }

    public void setDurasiKarakter(String durasiKarakter) {
        this.durasiKarakter = durasiKarakter;
    }

    public void setDurasiSA(String durasiSA) {
        this.durasiSA = durasiSA;
    }
}
