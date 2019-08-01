/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import View.*;

/**
 *
 * @author acdwisu
 */
public class ControllerHome {
    
    // <editor-fold defaultstate="collapsed" desc="Kode menu">
    
    /** Referensi kode menu Data Latih */
    public static final int MENU_DATA_LATIH = 1;
    
    /** Referensi kode menu Data Uji */
    public static final int MENU_DATA_UJI = 2;
    
    /** Referensi kode menu Pelatihan */
    public static final int MENU_PELATIHAN = 3;
    
    /** Referensi kode menu Segmentasi */
    public static final int MENU_SEGMENTASI = 4;
    
    /** Referensi kode menu Komparasi */
    public static final int MENU_KOMPARASI = 5;
    
    /** Referensi kode menu Hasil */
    public static final int MENU_HASIL = 6;
    // </editor-fold>
    
    /**
     * Method untuk menampilkan menu sesuai dengan tombol yang di-click
     * @param menu Merupakan kode dari mene. Kode direferensikan kepada atribut dari kelas <code> ControllerHome </code>
     */
    public void openMenu(int menu) {
        
        switch(menu) {
            case MENU_DATA_LATIH :
                new MenuDataLatih().setVisible(true);
                break;
            case MENU_DATA_UJI :
                new MenuDataUji().setVisible(true);
                break;
            case MENU_PELATIHAN :
                new MenuPelatihan().setVisible(true);
                break;
            case MENU_SEGMENTASI :
                new MenuSegmentasi().setVisible(true);
                break;
            case MENU_KOMPARASI :
                new MenuKomparasi().setVisible(true);
                break;
            case MENU_HASIL :
                new MenuHasil().setVisible(true);
                break;
        }
    }
}
