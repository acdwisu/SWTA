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
package Controller;

import Embed.EmbedListModel;
import FileIO.FileIOSegmentationPoints;
import Model.DataTest;
import Model.SegmentedWord;
import Processor.ImageProcessor;
import View.MenuModifikasiSP;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author acdwisu
 */
public class ControllerModifikasiSP {
    
    private ControllerDataUji controllerDataUji;
    
    private final MenuModifikasiSP menu;
    
    private final DataTest data;
    
    private BufferedImage processedImage = null, originalImage = null;
    
    private final SegmentedWord word;
    
    private List<int[]> pspAreas;        
    
    public ControllerModifikasiSP(ControllerDataUji controllerDataUji) {
        this.controllerDataUji = controllerDataUji;
        
        data = this.controllerDataUji.getSelectedData();
        
        ImageProcessor proc = new ImageProcessor();
        
        processedImage = proc.readImage(data.getDirektoriProcessed());         
        
        originalImage = proc.readImage(data.getDirektoriOri());
        
        pspAreas = new FileIOSegmentationPoints().readAreaPSP(data.getDirektoriPSpAreas());
        
        menu = new MenuModifikasiSP(this);
        
        menu.setVisible(true);        
        
        loadListPSPAreas(true);
        
        word = new SegmentedWord();                   
    }
    
    public void loadListPSPAreas(boolean fromFile) {
        JList<String> listPSPs = this.menu.getListPSPAreas();
        
        if(fromFile)
            pspAreas = new FileIOSegmentationPoints().readAreaPSP(data.getDirektoriPSpAreas());
        
        List<String> spstemp = new LinkedList<>();
        
        for(int[] pspArea : pspAreas) {
            spstemp.add(pspArea[0]+"-"+pspArea[1]);
        }
        
        EmbedListModel model = new EmbedListModel(spstemp);
        listPSPs.setModel(model);
    }
    
    public void responseSimpan() {
        simpanAreaPSP();
    }
    
    public void responseTambahAreaPSP() {
        tambahAreaPSP();
    }
    
    public void responseHapusAreaPSP() {
        hapusAreaPSP();
    }
    
    public void responseOkTambahAreaPSP() {
        tambahAreaPSPOk();
    }
    
    private void simpanAreaPSP() {
        new FileIOSegmentationPoints().writeAreaPSP(data.getDirektoriPSpAreas(), this.pspAreas);
        
        List<Integer> sps = new FileIOSegmentationPoints().readSegmentationPoint(data.getDirektoriSps());

        ImageProcessor proc = new ImageProcessor();

        proc.writeImage(proc.renderWordAccordingToPSPArea(this.processedImage, sps, this.pspAreas), data.getDirekotoriSegmented());

        this.menu.getImageViewerProcessed().setIcon(new ImageIcon(proc.renderWordAccordingToPSPArea(this.processedImage, sps, this.pspAreas)));
        
        JOptionPane.showMessageDialog(menu, "PSP Areas telah disimpan");
    }
    
    private void tambahAreaPSP() {
        this.setEnablityMainButtons(false);
        
        List<Integer> sps = new FileIOSegmentationPoints().readSegmentationPoint(data.getDirektoriSps());
        
        JButton button = this.menu.getBtnPSpAreaOk();
        JSlider slider1 = this.menu.getSliderPoint1(),
                slider2 = this.menu.getSliderPoint2();
        
        button.setEnabled(true);
        slider1.setEnabled(true);  
        slider2.setEnabled(true);                
        
        slider1.setMaximum(processedImage.getWidth()-1);
        slider1.setMinimum(0);
        slider1.addChangeListener((ChangeEvent e) -> {
            int point1 = slider1.getValue(), point2 = slider2.getValue();
            
            List<int[]> pspstemp = new LinkedList<>();
            
            for(int[] psp:pspAreas) {
                pspstemp.add(psp);
            }
            
            pspstemp.add(new int[]{point1, point2});
            
            this.menu.getImageViewerProcessed().setIcon(new ImageIcon(new ImageProcessor().
                    renderWordAccordingToPSPArea(this.processedImage, sps, pspstemp)));
        });
        
        slider2.setMaximum(processedImage.getWidth()-1);
        slider2.setMinimum(0);
        slider2.addChangeListener((ChangeEvent e) -> {
            int point1 = slider1.getValue(), point2 = slider2.getValue();
            
            List<int[]> pspstemp = new LinkedList<>();
            
            for(int[] psp:pspAreas) {
                pspstemp.add(psp);
            }
            
            pspstemp.add(new int[]{point1, point2});
            
            this.menu.getImageViewerProcessed().setIcon(new ImageIcon(new ImageProcessor().
                    renderWordAccordingToPSPArea(this.processedImage, sps, pspstemp)));
        });
        
    }
    
    private void hapusAreaPSP() {
        JList<String> list = this.menu.getListPSPAreas();
        
        if(!list.isSelectionEmpty()) {
            List<Integer> sps = new FileIOSegmentationPoints().readSegmentationPoint(data.getDirektoriSps());

            ImageProcessor proc = new ImageProcessor();
        
            int index = list.getSelectedIndex();

            this.pspAreas.remove(index);

            this.loadListPSPAreas(false);
            
            this.menu.getImageViewerProcessed().setIcon(new ImageIcon(proc.renderWordAccordingToPSPArea(this.processedImage, sps, this.pspAreas)));
        }
    }
    
    private void tambahAreaPSPOk() {
        JSlider slider1 = this.menu.getSliderPoint1(), slider2 = this.menu.getSliderPoint2();
        
        int point1 = slider1.getValue(), point2 = slider2.getValue();                        
        
        if(point1 >= point2) {
            JOptionPane.showMessageDialog(menu, "Maaf, point 1 harus berada di belakang point2, sebaliknya ...");
        } else {
            pspAreas.add(new int[]{point1, point2});

            this.loadListPSPAreas(false);

            this.setEnablityMainButtons(true);

            this.menu.getBtnPSpAreaOk().setEnabled(false);
            slider1.setEnabled(false);
            slider1.setValue(0);
            slider2.setEnabled(false);
            slider2.setValue(0);
        }
    }                        

    public DataTest getData() {
        return data;
    }

    public List<int[]> getPspAreas() {
        return pspAreas;
    }
    
    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public BufferedImage getProcessedImage() {
        return processedImage;
    }
    
    private void setEnablityMainButtons(boolean enbaled) {
        this.menu.getButtonHapusAreaPSp().setEnabled(enbaled);
        this.menu.getButtonSimpan().setEnabled(enbaled);
        this.menu.getButtonTambahAreaPSp().setEnabled(enbaled);
    }      
}
