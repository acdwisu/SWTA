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
public class ComparationResult {
    
    private String dataName;
    
    private String segmentationName;
    
    private int pspsAcuan;
    
    private int pspsTerdeteksi;
    
    private int pspsBenar;
    
    private int pspsSalah;
    
    private int pspsOver;
    
    private int pspsMiss;
    
    private int pspsBad;
    
    private String pathWordAsli;
    
    private String pathWordAcuan;
    
    private String pathWordOverSegmented;
    
    private String pathWordHasil;

    public ComparationResult(String dataName, String segmentationName, int pspsAcuan, int pspsTerdeteksi, 
            int pspsBenar, int pspsSalah, int pspsOver, int pspsMiss, int pspsBad, String pathWordAsli,
            String pathWordAcuan, String pathWordOverSegmented, String pathWordHasil) {
        this.dataName = dataName;
        this.segmentationName = segmentationName;
        this.pspsAcuan = pspsAcuan;
        this.pspsTerdeteksi = pspsTerdeteksi;
        this.pspsBenar = pspsBenar;
        this.pspsSalah = pspsSalah;
        this.pspsOver = pspsOver;
        this.pspsMiss = pspsMiss;
        this.pspsBad = pspsBad;
        this.pathWordAsli = pathWordAsli;
        this.pathWordAcuan = pathWordAcuan;
        this.pathWordOverSegmented = pathWordOverSegmented;
        this.pathWordHasil = pathWordHasil;        
    }

    public String getDataName() {
        return dataName;
    }

    public String getSegmentationName() {
        return segmentationName;
    }

    public int getPspsAcuan() {
        return pspsAcuan;
    }

    public int getPspsTerdeteksi() {
        return pspsTerdeteksi;
    }

    public int getPspsBenar() {
        return pspsBenar;
    }

    public int getPspsSalah() {
        return pspsSalah;
    }

    public int getPspsOver() {
        return pspsOver;
    }

    public int getPspsMiss() {
        return pspsMiss;
    }

    public int getPspsBad() {
        return pspsBad;
    }

    public String getPathWordAsli() {
        return pathWordAsli;
    }

    public String getPathWordAcuan() {
        return pathWordAcuan;
    }

    public String getPathWordOverSegmented() {
        return pathWordOverSegmented;
    }

    public String getPathWordHasil() {
        return pathWordHasil;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public void setSegmentationName(String segmentationName) {
        this.segmentationName = segmentationName;
    }

    public void setPspsAcuan(int pspsAcuan) {
        this.pspsAcuan = pspsAcuan;
    }

    public void setPspsTerdeteksi(int pspsTerdeteksi) {
        this.pspsTerdeteksi = pspsTerdeteksi;
    }

    public void setPspsBenar(int pspsBenar) {
        this.pspsBenar = pspsBenar;
    }

    public void setPspsSalah(int pspsSalah) {
        this.pspsSalah = pspsSalah;
    }

    public void setPspsOver(int pspsOver) {
        this.pspsOver = pspsOver;
    }

    public void setPspsMiss(int pspsMiss) {
        this.pspsMiss = pspsMiss;
    }

    public void setPspsBad(int pspsBad) {
        this.pspsBad = pspsBad;
    }

    public void setPathWordAsli(String pathWordAsli) {
        this.pathWordAsli = pathWordAsli;
    }

    public void setPathWordAcuan(String pathWordAcuan) {
        this.pathWordAcuan = pathWordAcuan;
    }

    public void setPathWordOverSegmented(String pathWordOverSegmented) {
        this.pathWordOverSegmented = pathWordOverSegmented;
    }

    public void setPathWordHasil(String pathWordHasil) {
        this.pathWordHasil = pathWordHasil;
    }
    
}
