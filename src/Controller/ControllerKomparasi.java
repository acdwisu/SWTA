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
package Controller;

import Embed.EmbedComboModel;
import Embed.EmbedListModel;
import FileIO.FileIOComparationResult;
import FileIO.FileIOSegmentationResult;
import Model.ComparationResult;
import Model.SegmentationResult;
import Processor.ImageProcessor;
import View.MenuKomparasi;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author acdwisu
 */
public class ControllerKomparasi {
    
    private MenuKomparasi menu;
    
    private List<SegmentationResult> listSegmentationResult;
    
    private List<ComparationResult> listComparationResult;    
    
    public ControllerKomparasi(MenuKomparasi menu) {
        this.menu = menu;
        
        this.loadListHasilSegmentasi();
    }
    
    public void responseLoadHasilKomparasi() {
        this.loadListHasilKomparasi();
    }
    
    public void responseDetailHasilKomparasi() {
        this.viewDetailKomparasi();
    }
    
    private void loadListHasilSegmentasi() {
        listSegmentationResult = new FileIOSegmentationResult().get();
        
        List<String> list = new ArrayList<>();
                
        listSegmentationResult.forEach((x) -> {
            list.add(x.getSegmentationName());
        });
        
        EmbedComboModel model = new EmbedComboModel(list);
        
        this.menu.getComboHasilSegmentasi().setModel(model);   
    }
    
    private void loadListHasilKomparasi() {
        listComparationResult = new FileIOComparationResult().get(this.listSegmentationResult.
                get(this.menu.getComboHasilSegmentasi().getSelectedIndex()).getSegmentationName());
        
        List<String> list = new ArrayList<>();
                
        listComparationResult.forEach((x) -> {
            list.add(x.getDataName());
        });
        
        EmbedListModel model = new EmbedListModel(list);
        
        this.menu.getListHasilKomparasi().setModel(model);
    }
    
    private void viewDetailKomparasi() {
        JList<String> list = this.menu.getListHasilKomparasi();
       
        int selectedIndex = list.getSelectedIndex();
        
        if(!list.isSelectionEmpty()) {
            ImageProcessor proc = new ImageProcessor();
            
            JLabel panelCitraAsli = this.menu.getPanelCitraAsli();
            JLabel panelCitraAcuan = this.menu.getPanelCitraAcuan();
            JLabel panelCitraOverSegmented = this.menu.getPanelCitraOverSegmented();
            JLabel panelCitraHasil = this.menu.getPanelCitraHasil();
            
            ComparationResult selectedData = this.listComparationResult.get(selectedIndex);
            
            File fileAsli = new File(selectedData.getPathWordAsli());          
            File fileAcuan = new File(selectedData.getPathWordAcuan());          
            File fileOverSegmented = new File(selectedData.getPathWordOverSegmented());          
            File fileHasil = new File(selectedData.getPathWordHasil());          
            
            BufferedImage imageAsli, imageAcuan, imageOverSegmented, imageHasil;

            if (fileAsli.isFile()) {
                imageAsli = proc.readImage(fileAsli.getPath());
            } else {
                imageAsli = proc.readImage("assets/no_image.bmp");
            }                

            panelCitraAsli.setIcon(new ImageIcon(proc.fitImageToContainer(imageAsli, 
                    panelCitraAsli.getWidth(), panelCitraAsli.getHeight(), 0.8f)));
            
            if (fileAcuan.isFile()) {
                imageAcuan = proc.readImage(fileAcuan.getPath());
            } else {
                imageAcuan = proc.readImage("assets/no_image.bmp");
            }                

            panelCitraAcuan.setIcon(new ImageIcon(proc.fitImageToContainer(imageAcuan, 
                    panelCitraAcuan.getWidth(), panelCitraAcuan.getHeight(), 0.8f)));

            if (fileOverSegmented.isFile()) {
                imageOverSegmented = proc.readImage(fileOverSegmented.getPath());
            } else {
                imageOverSegmented = proc.readImage("assets/no_image.bmp");
            }                

            panelCitraOverSegmented.setIcon(new ImageIcon(proc.fitImageToContainer(imageOverSegmented, 
                    panelCitraOverSegmented.getWidth(), panelCitraOverSegmented.getHeight(), 0.8f)));
            
            if (fileHasil.isFile()) {
                imageHasil = proc.readImage(fileHasil.getPath());
            } else {
                imageHasil = proc.readImage("assets/no_image.bmp");
            }                

            panelCitraHasil.setIcon(new ImageIcon(proc.fitImageToContainer(imageHasil, 
                    panelCitraHasil.getWidth(), panelCitraHasil.getHeight(), 0.8f)));
            
            this.menu.getLabelTotalPSPsAcuan().setText(": " +selectedData.getPspsAcuan());
            this.menu.getLabelTotalPSPs().setText(": " +(selectedData.getPspsBenar()+selectedData.getPspsSalah()));
            this.menu.getLabelPSPsBenar().setText(": " +selectedData.getPspsBenar());
            this.menu.getLabelPSPsSalah().setText(": " +selectedData.getPspsSalah());
            this.menu.getLabelPSPsOver().setText(": " +selectedData.getPspsOver());
            this.menu.getLabelPSPsMiss().setText(": " +selectedData.getPspsMiss());
            this.menu.getLabelPSPsBad().setText(": " +selectedData.getPspsBad());
        }
    }
}
