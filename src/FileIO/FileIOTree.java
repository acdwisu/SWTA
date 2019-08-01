package FileIO;

import Controller.ControllerPelatihan;
import Processor.Learner.RandomForests.RandomForest;
import Processor.Learner.Tree.DecisionTree;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileIOTree {
    //method to take the txt fle as input and pass those values to random forests        
    
    public ArrayList<ArrayList<String>> CreateInputCateg(String path){

        ArrayList<ArrayList<String>> DataInput = new ArrayList<ArrayList<String>>();

        BufferedReader BR = null;
                
        try {
            String sCurrentLine;
            
            BR = new BufferedReader(new FileReader(path));

            while ((sCurrentLine = BR.readLine()) != null) {
                ArrayList<Integer> Sp=new ArrayList<Integer>();
                
                int i;
            
                if(sCurrentLine!=null){
                    if(sCurrentLine.indexOf(",")>=0){
                        //has comma

                        sCurrentLine=","+sCurrentLine+",";
                        
                        char[] c =sCurrentLine.toCharArray();
                        
                        for(i=0;i<sCurrentLine.length();i++){
                            if(c[i]==',')
                                Sp.add(i);
                        }
                        
                        ArrayList<String> DataPoint=new ArrayList<String>();
                
                        for(i=0;i<Sp.size()-1;i++){
                            DataPoint.add(sCurrentLine.substring(Sp.get(i)+1, Sp.get(i+1)).trim());
                        }
                        
                        DataInput.add(DataPoint);//System.out.println(DataPoint);
                    } else if(sCurrentLine.indexOf(" ")>=0) {
                        //has spaces
                        sCurrentLine=" "+sCurrentLine+" ";
                        
                        for(i=0;i<sCurrentLine.length();i++){
                            if(Character.isWhitespace(sCurrentLine.charAt(i)))
                                Sp.add(i);
                        }
                        
                        ArrayList<String> DataPoint=new ArrayList<String>();
                        
                        for(i=0;i<Sp.size()-1;i++){
                            DataPoint.add(sCurrentLine.substring(Sp.get(i), Sp.get(i+1)).trim());
                        }
                        
                        DataInput.add(DataPoint);//System.out.println(DataPoint);
                    }
                }
            }
        
//            System.out.println("Input generated");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (BR != null)BR.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
     
        return DataInput;
    }
    
    /**
     * 
     * @param path
     * @return 
     */
    public ArrayList<String[]> retrieveKnowledge(String path) {
       ArrayList<String[]> knowledge = new ArrayList<>();
       
       BufferedReader BR = null;              
            
        try {
            String sCurrentLine;
            
            BR = new BufferedReader(new FileReader(path));
            
            while ((sCurrentLine = BR.readLine()) != null) {
                String[] splitted = sCurrentLine.split(" ");
                
                knowledge.add(splitted);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileIOTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileIOTree.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (BR != null)BR.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
       return knowledge;
    }
    
    public void storeKnowledge(RandomForest rf, String trainName, String tipeRF) {
        String parentPath = "knowledge/"+trainName+ "/"+tipeRF;
        
        File parentFolder = new File(parentPath);
        
        parentFolder.mkdirs();
        
        ArrayList<DecisionTree> trees = rf.getTrees();
        
        int treenum = 1;
        for(DecisionTree tree : trees) {            
            String filePath = parentPath+ "/" +treenum+ ".tree";
            File file = new File(filePath);
            
            String knowledge = tree.toString();
            
            FileWriter writer;
            try {
                writer = new FileWriter(file);
                writer.write(knowledge);
             
                writer.flush();
                writer.close();
                
                treenum++;                  
            } catch (IOException ex) {
                Logger.getLogger(FileIOTree.class.getName()).log(Level.SEVERE, null, ex);
            }                          
        }
    }
    
    public void storeKnowledge(RandomForest rf, String trainName, String tipeRF, ControllerPelatihan cp) {
        String parentPath = "knowledge/"+trainName+ "/"+tipeRF;
        
        File parentFolder = new File(parentPath);
        
        parentFolder.mkdirs();
        
        ArrayList<DecisionTree> trees = rf.getTrees();
        
        double update = 100 / ((double) trees.size()), progress = 0;
        
        int treenum = 1;
        for(DecisionTree tree : trees) {            
            String filePath = parentPath+ "/" +treenum+ ".tree";
            File file = new File(filePath);
            
            String knowledge = tree.toString();
            
            FileWriter writer;
            try {
                writer = new FileWriter(file);
                writer.write(knowledge);
             
                writer.flush();
                writer.close();
                
                treenum++;
                
                progress += update;
                
                cp.updateProgress(progress);
            } catch (IOException ex) {
                Logger.getLogger(ControllerPelatihan.class.getName()).log(Level.SEVERE, null, ex);
            }                          
        }
    }
}
