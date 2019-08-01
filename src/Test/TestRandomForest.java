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

import Controller.ControllerPelatihan;
import Processor.Learner.RandomForests.RandomForest;
import Processor.Learner.Tree.DecisionTree;
import FileIO.FileIOTree;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author acdwisu
 */
public class TestRandomForest {
    public static void main(String[] args){
        new TestRandomForest().run1();
    }
    
    private void run1() {
        System.out.println("Random-Forest with Categorical support");
        System.out.println("Now Running");
        /*
         * data has to be separated by either ',' or ' ' only...
         */
        int categ=-1;
        String traindata,testdata;
        if(categ>0){
            traindata="KDDTrainSmall.txt";
            testdata="KDDTestSmall.txt";
        }else if(categ<0){
//            traindata="Data.txt";
//            testdata="Test.txt";
//            traindata="irisTrain.txt";
            traindata="C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\recognitionTest\\with thinning\\featuresSZZC-4x4-train.txt";
            testdata="C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\recognitionTest\\with thinning\\featuresSZZC-4x4-test.txt";
        }else{
            traindata="KDDTrain+.txt";
            testdata="KDDTest+.txt";
        }

        FileIOTree DT = new FileIOTree();
        ArrayList<ArrayList<String>> Train = DT.CreateInputCateg(traindata);
        ArrayList<ArrayList<String>> Test = DT.CreateInputCateg(testdata);
        /*
         * For class-labels 
         */
        HashMap<String, Integer> Classes = new HashMap<String, Integer>();
        for(ArrayList<String> dp : Train){
            String clas = dp.get(dp.size()-1);
            if(Classes.containsKey(clas))
                Classes.put(clas, Classes.get(clas)+1);
            else
                Classes.put(clas, 1);				
        }

// -------------------------------------------------------------------------------------------------        
        
        int numTrees=50;
        int M=Train.get(0).size()-1;
        int Ms = (int)Math.round(Math.log(M)/Math.log(2)+1);
//        int Ms = (int) Math.round(Math.sqrt(M));
        int C = Classes.size();
        HashMap<String, Integer> config = new HashMap<>();
        config.put("treeCount", numTrees);
        config.put("attributeCheckCountType", 0);
        
        RandomForest RFC = new RandomForest(config, Train, Test);
        RFC.train();       
        
//        List<HashMap<String,String>> results = RFC.evaluateForestWithoutLabel();      
        
//        for(HashMap<String,String> result : results) {
//            System.out.print("prediction : " +result.get("prediction"));
//            System.out.println("\tconfidence : " +result.get("confidence"));
//        }

        RFC.evaluateForestWithLabel();

// ----------------------------------------------------------------------------------------------------------------
        
//        RandomForest RFC = new RandomForest(Test, "C:\\Users\\Aha\\Documents\\NetBeansProjects\\SWTA\\knowledge\\TRAIN_WUMI_DBAHCL_500\\Karakter");
//        RFC.train();
//        
//        RFC.setTestdata(Test);
//        
//        List<HashMap<String,String>> results = RFC.evaluateForestWithoutLabel();
//        
//        for(HashMap<String,String> result : results) {
//            System.out.print("prediction : " +result.get("prediction"));
//            System.out.println("\tconfidence : " +result.get("confidence"));
//        }

// --------------------------------------------------------------------------------------------------------------

//        HashMap<String, Integer> config = new HashMap<>();
//        config.put("treeCount", 1);
//        config.put("attributeCheckCountType", 0);
//        
//        RandomForest RFC = new RandomForest(config, Train);
//        RFC.train();       
//        
//        String parentPath = "knowledge/iris";
//        
//        File parentFolder = new File(parentPath);
//        
//        parentFolder.mkdir();
//        
//        ArrayList<DecisionTree> trees = RFC.getTrees();
//        
//        int treenum = 1;
//        for(DecisionTree tree : trees) {            
//            String filePath = parentPath+ "/" +treenum+ ".tree";
//            File file = new File(filePath);
//            
//            String knowledge = tree.toString();
//            
//            FileWriter writer;
//            try {
//                writer = new FileWriter(file);
//                writer.write(knowledge);
//             
//                writer.flush();
//                writer.close();
//                
//                treenum++;
//            } catch (IOException ex) {
//                Logger.getLogger(ControllerPelatihan.class.getName()).log(Level.SEVERE, null, ex);
//            }                          
//        }
    }   
}
