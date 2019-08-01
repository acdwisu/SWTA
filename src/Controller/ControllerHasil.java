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
import FileIO.FileIOOverallResult;
import FileIO.FileIOSegmentationResult;
import FileIO.FileIOTrainResult;
import Model.OverallResult;
import Model.SegmentationResult;
import Model.TrainResult;
import View.MenuHasil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author acdwisu
 */
public class ControllerHasil {   
    private final MenuHasil menu;
    
    private List<SegmentationResult> listSegmentationResult;
    
    public ControllerHasil(MenuHasil menu) {
        this.menu = menu;
        
        this.loadListHasilSegmentasi();
    }
    
    public void responseLihatDetails() {                 
        SegmentationResult selectedSegmentationResult = this.listSegmentationResult.get(this.menu.getComboHasilSegmentasi().
                getSelectedIndex());
        
        OverallResult overallResult = new FileIOOverallResult().get(selectedSegmentationResult.getSegmentationName());
        
        String selectedTrainName = selectedSegmentationResult.getTrainName();
        
        TrainResult trainResult =  new FileIOTrainResult().get(selectedTrainName, FileIOTrainResult.SPECIFIER_NAMA_PELATIHAN).get(0);
        
        String[] paramEkstraksi = trainResult.getParameterEkstraksi().split("-");
        String paramZoneType = (Integer.parseInt(paramEkstraksi[4]) == 1 ? ("Static Zoning") : ("Adaptive Zoning")),
                paramZoneHorCount = paramEkstraksi[0],
                paramZoneVerCount = paramEkstraksi[1],
                paramOffsetX = paramEkstraksi[2],
                paramOffsetY = paramEkstraksi[3];

        String[] paramRF = trainResult.getParameterRF().split("-");
        String paramTreeCount = paramRF[0],
                paramAttrCheckCount = paramRF[1];
        
        String info =
                "Detil Segmentasi\n\n"+
                "  Nama segmentasi : "+selectedSegmentationResult.getSegmentationName()+"\n"+
                "  Dataset digunakan : "+selectedSegmentationResult.getDatasetName()+"\n"+
                "  Hasil pelatihan digunakan : "+selectedSegmentationResult.getTrainName()+"\n"+
                "------------------------------------------------------\n\n\n"+                
                "Detil Pelatihan\n\n"+
                "  Nama pelatihan : "+trainResult.getNamaPelatihan()+"\n"+
                "  Dataset digunakan : "+trainResult.getNamaDataset()+"\n"+
                "  Metode Ekstraksi digunakan : "+trainResult.getEkstraksi()+"\n"+
                "    Parameter\n"+
                "      Zone type : "+paramZoneType+"\n"+
                "      Zone horizontal count : "+paramZoneHorCount+"\n"+
                "      Zone vertical count : "+paramZoneVerCount+"\n"+
                "      Offset X : "+paramOffsetX+"\n"+
                "      Offset Y : "+paramOffsetY+"\n"+
                "  Random Forests (RF)\n"+
                "    Parameter\n"+
                "      Tree Count : "+paramTreeCount+"\n"+
                "      Attribute Check Count Mode : "+paramAttrCheckCount+"\n"+
                "    RF Karakter\n"+
                "      Akurasi Pelatihan : "+trainResult.getAkurasiKarakter()+"\n"+
                "      Durasi Pelatihan : "+trainResult.getDurasiKarakter()+"\n"+
                "    RF SA(Segmentation Area)\n"+
                "      Akurasi Pelatihan : "+trainResult.getAkurasiSA()+"\n"+
                "      Durasi Pelatihan : "+trainResult.getDurasiSA()+"\n"+
                "------------------------------------------------------\n\n\n"+
                "Detil Komparasi\n\n"+
                "  Total PSPs terdeteksi : "+overallResult.getTotalPsps()+"\n"+
                "  Jumlah PSPs benar : "+overallResult.getCorrectPsps()+"\n"+
                "  Jumlah PSPs salah : "+overallResult.getErrorPsps()+"\n"+
                "  Akurasi : "+overallResult.getPercentage()+"\n"+
                "------------------------------------------------------";             
        
        this.menu.getInfoHasilSegmentasi().setText(info);
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
}
