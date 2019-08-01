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

import Processor.FeatureExtractor.FeatureExtractor;
import java.util.HashMap;

/**
 *
 * @author acdwisu
 */
public class WUMI extends FeatureExtractor {

    private double[] nilaiWUMI;
    private double[] nilaiMI;
    
    private double[][] fxy_baru;
    private double[][] fxy_biner;
    
    public WUMI(int[][] img) {
        super(new HashMap<>(), img);
    }

    public WUMI() {
        super(new HashMap<>());
    }

    @Override
    public double[] getFeatures() {
        this.ekstraksiWUMI();
        
        super.cleaningNaNFeatures(nilaiWUMI);
        
        return this.nilaiWUMI;
    }        
    
    public void ekstraksiWUMI(){
        int width = img[0].length, height = img.length;
        
        Moment m00, m01, m10, m00_baru, m01_baru, m10_baru;
        nilaiWUMI = new double[8];
        nilaiMI = new double[7];
        fxy_biner = super.intArrayToDoubleArray(img);
        fxy_baru = new double[height][width];
                
        m00 = hitungMomen(0,0,fxy_biner);
        //System.out.println(m00.getMomen());
        m01 = hitungMomen(0,1,fxy_biner);
        //System.out.println(m01.getMomen());
        m10 = hitungMomen(1,0,fxy_biner);
        //System.out.println(m10.getMomen());
        
        double xPusat = hitungMomenPusat(m10, m00);
        double yPusat = hitungMomenPusat(m01, m00);
        
        Moment momen[] = hitungSemuaMomenPusat(xPusat, yPusat,fxy_biner);
        
        //weighting
        WeightingFunction WF = new WeightingFunction();
        WF.setFxyBaru(img, momen, xPusat, yPusat);
        fxy_baru=WF.getFxyBaru();
        
        m00_baru= hitungMomen(0,0,fxy_baru);
        //System.out.println(m00_baru.getMomen());
        m01_baru= hitungMomen(0,1,fxy_baru);
        //System.out.println(m01_baru.getMomen());
        m10_baru= hitungMomen(1,0,fxy_baru);
        //System.out.println(m10_baru.getMomen());
        
        double xPusat_baru = hitungMomenPusat(m10_baru, m00_baru);
        double yPusat_baru = hitungMomenPusat(m01_baru, m00_baru);
        
        Moment momen_baru[] = hitungSemuaMomenPusatBaru(xPusat_baru, yPusat_baru,fxy_baru);
        
        double n[] = normarmalisasiSemuaMomenPusat(momen_baru);
        hitungNilaiWUMI(n);
    }        
    
    private Moment hitungMomen(int p, int q, double[][] fxy){
        int width  = img[0].length, height = img.length;
        
        Moment m = new Moment();
        double sum=0;
        
        for(int x=1; x<height; x++){
            for(int y=1; y<width; y++){
                sum += Math.pow(x,p) * Math.pow(y,q) * fxy[x][y]*fxy_biner[x][y];
            }
        }
        m.setMoment(sum);
        return m;
    }
    
    private Moment hitungMomenBaru(int p, int q, double[][] fxy){
        int width  = img[0].length, height = img.length;
        
        Moment m = new Moment();
        double sum=0;
        
        for(int x=1; x<height; x++){
            for(int y=1; y<width; y++){
                sum += Math.pow(x,p) * Math.pow(y,q) * fxy_biner[x][y];
            }
        }
        
        m.setMoment(sum);
        return m;
    }
 
    private double hitungMomenPusat(Moment m1, Moment m2){
        return (double) m1.getMoment()/m2.getMoment();
    }
    
    private Moment hitungMomenPusat2(double xPusat, double yPusat, int p, int q, double[][] fxy){
        int width  = img[0].length, height = img.length;
        
        Moment m = new Moment();
        double sum=0;
        
        for(int x=1; x<height; x++){
            for(int y=1; y<width; y++){
                sum += Math.pow(x-xPusat, p) * Math.pow(y-yPusat, q) * fxy[x][y];
            }
        }
        
        m.setMoment(sum);
        return m;
    }
    
    private Moment hitungMomenPusat2Baru(double xPusat, double yPusat, int p, int q, double[][] fxy){
        int width  = img[0].length, height = img.length;
        
        Moment m = new Moment();
        double sum=0;
        
        for(int x=1; x<height; x++){
            for(int y=1; y<width; y++){
                sum += Math.pow(x-xPusat, p) * Math.pow(y-yPusat, q) * fxy[x][y] * fxy_biner[x][y];
            }
        }
        
        m.setMoment(sum);
        return m;
    }
    
    private double normalisasiMomenPusat(Moment mPusat,double m00, int p, int q){
        double normMP=0;
        
        normMP = mPusat.getMoment()/Math.pow(m00, (p+q+2)/2);
        
        return normMP;
    }
    
    private Moment[] hitungSemuaMomenPusat(double xPusat, double yPusat, double[][] fxy){
        Moment m20,m02,m11,m30,m12,m03,m21,m00;   
        
        m20 = hitungMomenPusat2(xPusat, yPusat, 2, 0, fxy);
        m02 = hitungMomenPusat2(xPusat, yPusat, 0, 2, fxy);
        m11 = hitungMomenPusat2(xPusat, yPusat, 1, 1, fxy);
        m30 = hitungMomenPusat2(xPusat, yPusat, 3, 0, fxy);
        m03 = hitungMomenPusat2(xPusat, yPusat, 0, 3, fxy);
        m21 = hitungMomenPusat2(xPusat, yPusat, 2, 1, fxy);
        m12 = hitungMomenPusat2(xPusat, yPusat, 1, 2, fxy);
        m00 = hitungMomenPusat2(xPusat, yPusat, 0, 0, fxy);
        
        
        Moment[] m = new Moment[8];
        m[0] = m20;
        m[1] = m02;
        m[2] = m11;
        m[3] = m30;
        m[4] = m03;
        m[5] = m21;
        m[6] = m12;
        m[7] = m00;
        
        return m;
    }
    
    private Moment[] hitungSemuaMomenPusatBaru(double xPusat, double yPusat, double[][] fxy){
        Moment m20,m02,m11,m30,m12,m03,m21,m00;   
        
        m20 = hitungMomenPusat2Baru(xPusat, yPusat, 2, 0, fxy);
        m02 = hitungMomenPusat2Baru(xPusat, yPusat, 0, 2, fxy);
        m11 = hitungMomenPusat2Baru(xPusat, yPusat, 1, 1, fxy);
        m30 = hitungMomenPusat2Baru(xPusat, yPusat, 3, 0, fxy);
        m03 = hitungMomenPusat2Baru(xPusat, yPusat, 0, 3, fxy);
        m21 = hitungMomenPusat2Baru(xPusat, yPusat, 2, 1, fxy);
        m12 = hitungMomenPusat2Baru(xPusat, yPusat, 1, 2, fxy);
        m00 = hitungMomenPusat2Baru(xPusat, yPusat, 0, 0, fxy);
        
        
        Moment[] m = new Moment[8];
        m[0] = m20;
        m[1] = m02;
        m[2] = m11;
        m[3] = m30;
        m[4] = m03;
        m[5] = m21;
        m[6] = m12;
        m[7] = m00;
        
        return m;
    }
    
    private double[] normarmalisasiSemuaMomenPusat(Moment[] momen){
        Moment mp20,mp02,mp11,mp30,mp12,mp03,mp21,mp00;
        double n20,n02,n11,n30,n03,n12,n21;
        
        double n[] = new double[7];
        
        mp20 = momen[0];
        mp02 = momen[1];
        mp11 = momen[2];
        mp30 = momen[3];
        mp03 = momen[4];
        mp21 = momen[5];
        mp12 = momen[6];
        mp00 = momen[7];
        
        n20 = normalisasiMomenPusat(mp20, mp00.getMoment(), 2, 0);
        n02 = normalisasiMomenPusat(mp02, mp00.getMoment(), 0, 2);
        n11 = normalisasiMomenPusat(mp11, mp00.getMoment(), 1, 1);
        n30 = normalisasiMomenPusat(mp30, mp00.getMoment(), 3, 0);
        n03 = normalisasiMomenPusat(mp03, mp00.getMoment(), 0, 3);
        n12 = normalisasiMomenPusat(mp12, mp00.getMoment(), 1, 2);
        n21 = normalisasiMomenPusat(mp21, mp00.getMoment(), 2, 1);
        
        n[0] = n20;
        n[1] = n02;
        n[2] = n30;

        n[3] = n03;
        n[4] = n21;
        n[5] = n12;
        n[6] = n11;

        
        
        return n;
    }
    
    private void hitungNilaiWUMI(double[] n){
        double n20,n02,n11,n30,n03,n12,n21;
        
        n20 = n[0];
        n02 = n[1];
        n30 = n[2];
        n03 = n[3];
        n21 = n[4];
        n12 = n[5];
        n11 = n[6];
        
        //hitungan geometric
       nilaiMI[0] = n20 + n02;
       nilaiMI[1] = Math.pow(n20-n02,2) + 4 * Math.pow(n11,2);
       nilaiMI[2] = Math.pow(n30 - (3 * n12),2) + Math.pow((3*n21)-n03,2);
       nilaiMI[3] = Math.pow(n30+n12,2) + Math.pow(n21+n03,2);
       nilaiMI[4] = (n30-(3*n12))*(n30 + n12)*(Math.pow(n30+n12,2)- (3*(Math.pow(n21+n03,2))))+((3*n21-n03)*(n21+n03)*(3*(Math.pow(n30+n12,2))-(Math.pow(n21+n03,2))));
       nilaiMI[5] = ((n20-n02)*(Math.pow(n30+n12,2)-Math.pow(n21+n03,2)))+ (4*n11*(n30+n12)*(n21+n03)) ;
       nilaiMI[6] =  ((3*n21-n03)*(n30+n12)*(Math.pow(n30+n12,2)-(3*(Math.pow(n21+n03,2)))))+((3*n21-n03)*(n21+n03)*(3*Math.pow(n30+n12,2)-Math.pow(n21+n03,2)));
    
        
        //hitungan united
        if (nilaiMI[0]!=0) //mencegah nilainya infinite, d dpat dari kondisi pembagi pada rumus UMI 1
        {
         nilaiWUMI[0] = Math.sqrt(nilaiMI[1])/nilaiMI[0];
         if(nilaiWUMI[0]!= nilaiWUMI[0])
         {
             nilaiWUMI[0]=0;
         }
         
        }else {
            nilaiWUMI[0] =  0;
        }
        
        if (nilaiMI[0]*nilaiMI[3]!=0) 
        {
          nilaiWUMI[1] = nilaiMI[5]/(nilaiMI[0]*nilaiMI[3]);
          if(nilaiWUMI[1]!= nilaiWUMI[1])
         {
             nilaiWUMI[1]=0;
         }
        }else {
          nilaiWUMI[1] =  0;
        }
        if (nilaiMI[3]!=0) 
        {
          nilaiWUMI[2] = Math.sqrt(nilaiMI[4])/nilaiMI[3];
          if(nilaiWUMI[2]!= nilaiWUMI[2])
         {
             nilaiWUMI[2]=0;
         }
        }else {
           nilaiWUMI[2] = 0; 
        }
        if (nilaiMI[2]*nilaiMI[3]!=0) 
        {
          nilaiWUMI[3] = nilaiMI[4]/(nilaiMI[2]*nilaiMI[3]);
          if(nilaiWUMI[3]!= nilaiWUMI[3])
         {
             nilaiWUMI[3]=0;
         }
        }else {
            nilaiWUMI[3] = 0;
        }
        
        if (nilaiMI[1]*nilaiMI[2]!=0)
        {
          nilaiWUMI[4] = nilaiMI[0]*nilaiMI[5]/(nilaiMI[1]*nilaiMI[2]);
          if(nilaiWUMI[4]!= nilaiWUMI[4])
         {
             nilaiWUMI[4]=0;
         }
        }else {
          nilaiWUMI[4] = 0;  
        }
        
        if (nilaiMI[5]!=0) 
        {
          nilaiWUMI[5] = (nilaiMI[0]+ Math.sqrt(nilaiMI[1])) * nilaiMI[2]/nilaiMI[5];
          if(nilaiWUMI[5]!= nilaiWUMI[5])
         {
             nilaiWUMI[5]=0;
         }
        }else {
          nilaiWUMI[5]  = 0;
        }
        
        if (nilaiMI[2]*nilaiMI[5]!=0) 
        {
          nilaiWUMI[6] =nilaiMI[0]*nilaiMI[4]/(nilaiMI[2]*nilaiMI[5]);
          if(nilaiWUMI[6]!= nilaiWUMI[6])
         {
             nilaiWUMI[6]=0;
         }
        }else {
          nilaiWUMI[6] = 0;   
        }
        
        if (nilaiMI[4]!=0) {
          nilaiWUMI[7] = (nilaiMI[2]+nilaiMI[3])/ Math.sqrt(nilaiMI[4]);
          if(nilaiWUMI[7]!= nilaiWUMI[7])
         {
             nilaiWUMI[7]=0;
         }
        }else {
             nilaiWUMI[7] = 0;
        }

    }           
}
