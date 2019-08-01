package Processor.Learner.RandomForests;

import Processor.Learner.Tree.DecisionTree;
import FileIO.FileIOTree;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JProgressBar;


public class RandomForest {
	   
    private int forestingMode;
    
    private String knowledgeDir;
    
    /** the number of threads to use when generating the forest */
//    private static final int NUM_THREADS=Runtime.getRuntime().availableProcessors();
    private static final int NUM_THREADS=Runtime.getRuntime().availableProcessors()-1;
    
    /** the number of categorical responses of the traindata (the classes, the "Y" values) - set this before beginning the forest creation */
    public static int C;
    
    /** the number of attributes in the traindata - set this before beginning the forest creation */
    public static int M;
    
    /** Of the M total attributes, the random forest computation requires a subset of them
     * to be used and picked via random selection. "Ms" is the number of attributes in this
     * subset. The formula used to generate Ms was recommended on Breiman's website.
      */
    public static int Ms;//recommended by Breiman: =(int)Math.round(Math.log(M)/Math.log(2)+1);
        
    /** the collection of the forest's decision trees */
    private ArrayList<DecisionTree> trees;
    
    /** the starting time when timing random forest creation */
    private long time_o;
    
    private String duration;
    
    /** the number of trees in this random tree */
    private int numTrees;
    
    /** For progress bar display for the creation of this random forest, this is the amount to update by when one tree is completed */
    private double update;
    
    /** For progress bar display for the creation of this random forest, this records the total progress */
    private double progress;
    
    /** this is an array whose indices represent the forest-wide importance for that given attribute */
    private int[] importances;
    
    /** This maps from a traindata record to an array that records the classifications by the trees where it was a "left out" record (the indices are the class and the values are the counts) */
    private HashMap<int[],int[]> estimateOOB;
    
    /** the total forest-wide error */
    private double error;
    
    /** the thread pool that controls the generation of the decision trees */
    private ExecutorService treePool;
    
    /** the original training traindata matrix that will be used to generate the random forest classifier */
    private ArrayList<ArrayList<String>> traindata;
    
    /** the traindata on which produced random forest will be tested*/
    private ArrayList<ArrayList<String>> testdata;
    
    /** This holds all of the predictions of trees in a Forest */
    private ArrayList<ArrayList<String>> Prediction;
    
    private JProgressBar updateListener;
    
    /**
     * This hold the genres of attributes in the forest
     * 
     * 1 if categ
     * 0 if real
     */
    public ArrayList<Integer> TrainAttributes;
    public ArrayList<Integer> TestAttributes;

    /**
     * Initializes a Breiman random forest creation
     * 
     * @param config
     * @param train
     * @param test
     */
    public RandomForest(HashMap<String, Integer> config, ArrayList<ArrayList<String>> train, ArrayList<ArrayList<String>> test) {
        // TODO Auto-generated constructor stub                     
        forestingMode = 1;
        
        this.traindata=train;
        this.testdata=test;
        
        M=train.get(0).size()-1;                
        
        // calculate classes count
        /*
         * For class-labels 
         */
        HashMap<String, Integer> Classes = new HashMap<>();
        for(ArrayList<String> dp : train){
            String clas = dp.get(dp.size()-1);
            if(Classes.containsKey(clas))
                Classes.put(clas, Classes.get(clas)+1);
            else
                Classes.put(clas, 1);				
        }
        //-----
        
        C=Classes.size();        
        
        this.numTrees=config.get("treeCount");
        
        // interprete Ms type according to selected index at menu's
        int index = config.get("attributeCheckCountType");
        if(index == 0) Ms=(int)Math.round(Math.log(M)/Math.log(2)+1);
        else if(index == 1) Ms=(int) Math.round(Math.sqrt(M));
        //-------
        
        this.TrainAttributes=getAttributes(train);
        this.TestAttributes=getAttributes(test);
        
        trees = new ArrayList<>(numTrees);
        update=100/((double)numTrees);
        progress=0;
        
//        System.out.println("creating "+numTrees+" trees in a random Forest. . . ");
//        System.out.println("total data size is "+train.size());
//        System.out.println("number of attributes "+M);
//        System.out.println("number of selected attributes "+Ms);

        estimateOOB=new HashMap<>(traindata.size());
        Prediction = new ArrayList<>();
    }
    
    /**
     * Initializes a Breiman random forest creation
     * 
     * @param config
     * @param train
     */
    public RandomForest(HashMap<String, Integer> config, ArrayList<ArrayList<String>> train) {
        // TODO Auto-generated constructor stub              
        forestingMode = 1;
        
        this.traindata=train;
        
        Collections.shuffle(this.traindata);
        
        M=train.get(0).size()-1;                
        
        // calculate classes count
        /*
         * For class-labels 
         */
        HashMap<String, Integer> Classes = new HashMap<>();
        for(ArrayList<String> dp : train){
            String clas = dp.get(dp.size()-1);
            if(Classes.containsKey(clas))
                Classes.put(clas, Classes.get(clas)+1);
            else
                Classes.put(clas, 1);				
        }
        //-----
        
        C=Classes.size();        
        
        this.numTrees=config.get("treeCount");
        
        // interprete Ms type according to selected index at menu's
        int index = config.get("attributeCheckCountType");
        if(index == 0) Ms=(int)Math.round(Math.log(M)/Math.log(2)+1);
        else if(index == 1) Ms=(int) Math.round(Math.sqrt(M));
        //-------
        
        this.TrainAttributes=getAttributes(train);
        
        trees = new ArrayList<>(numTrees);
        update=100/((double)numTrees);
        progress=0;
        
//        System.out.println("creating "+numTrees+" trees in a random Forest. . . ");
//        System.out.println("total data size is "+train.size());
//        System.out.println("number of attributes "+M);
//        System.out.println("number of selected attributes "+Ms);

        estimateOOB=new HashMap<>(traindata.size());
        Prediction = new ArrayList<>();
    }
    
    public RandomForest(ArrayList<ArrayList<String>> test, String knowledgeDir) {        
        forestingMode = 2;
        
        this.testdata=test;

        this.TestAttributes=getAttributes(test);
        
        trees = new ArrayList<>();
        
        this.knowledgeDir = knowledgeDir;
        
        this.numTrees=0;        
        update=100/((double)numTrees);
        progress=0;
        
        Prediction = new ArrayList<>();
    }
    
    public RandomForest(String knowledgeDir) {       
        forestingMode = 2;
                
        trees = new ArrayList<>();
        
        this.knowledgeDir = knowledgeDir;
        
        this.numTrees=0;        
        update=100/((double)numTrees);
        progress=0;
        
        Prediction = new ArrayList<>();
    }
    
    public void train() {
        startTimer();

        System.out.println("Number of threads started : "+NUM_THREADS);
        System.out.println("Starting trees");
        treePool=Executors.newFixedThreadPool(NUM_THREADS);
    
        if(forestingMode == 1) {        
            for (int t=0;t<numTrees;t++){                
                treePool.execute(new CreateTree(traindata,this,t+1));
            }
        } else if(forestingMode == 2) {
            FileIOTree dt = new FileIOTree();
            
            String[] knowledgeDirs = new File(this.knowledgeDir).list();
            
            int i=1;
            for (String knowledgeFile : knowledgeDirs){
                if(knowledgeFile.endsWith(".tree")) {
                    ArrayList<String[]> knowledge = dt.retrieveKnowledge(this.knowledgeDir.concat("/" +knowledgeFile));
                    
                    treePool.execute(new ReCreateTree(knowledge,this,i));
                    
                    i++;
                }
            }
        }
        
        treePool.shutdown();
        
        try {	         
            treePool.awaitTermination(Long.MAX_VALUE,TimeUnit.SECONDS); //effectively infinity
        } catch (InterruptedException ignored){
            System.out.println("interrupted exception in Random Forests");
        }
        
        duration = timeElapsed(this.time_o);
    }
    
    public void test() {
        if(forestingMode == 1) {
            if(traindata.get(0).size()==testdata.get(0).size())
                evaluateForestWithLabel(trees, traindata, testdata);      
            else if(traindata.get(0).size() == testdata.get(0).size()+1)
                evaluateForestWithoutLabel(trees, testdata);
            else
                System.out.println("Cannt test data");
        } else if(forestingMode == 2) {
            evaluateForestWithoutLabel(trees, testdata);
        }        
    }
    
    /**
     * Begins the random forest creation and test the random forest
     */
    public void start() {
        this.train();
        
        this.test();
    }         
    
    /**
     * Predicting unlabeled traindata
     * 
     * @param trees
     * @param data
     * @param testdata
     */
   public List<HashMap<String, String>> evaluateForestWithoutLabel(ArrayList<DecisionTree> trees, ArrayList<ArrayList<String>> testdata) {
        // TODO Auto-generated method stub
        ArrayList<HashMap<String, String>> TestResult = new ArrayList<>();
        
//        System.out.println("Predicting Labels now");

        for(ArrayList<String> DP:testdata){
            ArrayList<String> Predict = new ArrayList<String>();

            for(DecisionTree DT:trees){
                Predict.add(DT.evaluate(DP, testdata));
            }
            
            HashMap<String, String> result = new HashMap<>();
            result.put("prediction", modeOfList(Predict));
            result.put("confidence", String.valueOf(this.confidenceOfModeOfList(Predict)));
            
            TestResult.add(result);                        
        }
        
//        for(String res : TestResult) {
//            System.out.println(res);
//        }

        return TestResult;
   }    
   
   public List<HashMap<String, String>> evaluateForestWithoutLabel() {
       return this.evaluateForestWithoutLabel(trees, testdata);
   }
    
    /**
     * Testing the forest using the test-traindata 
     *     
     * @param trees
     * @param train
     * @param test
     */
    public void evaluateForestWithLabel(ArrayList<DecisionTree> trees,ArrayList<ArrayList<String>> train,ArrayList<ArrayList<String>> test){
        int correctness=0;
        
        ArrayList<String> ActualValues = new ArrayList<>();

        for(ArrayList<String> s : test){
            ActualValues.add(s.get(s.size()-1));
        }
        
        int treee=1;
        
        System.out.println("Testing forest now ");

        for(DecisionTree DTC : trees){
            DTC.calculateClasses(train, test, treee);treee++;
            
            if(DTC.predictions!=null)
                Prediction.add(DTC.predictions);
        }
        
        for(int i = 0;i<test.size();i++){
            ArrayList<String> Val = new ArrayList<>();
        
            for(int j=0;j<trees.size();j++){
                Val.add(Prediction.get(j).get(i));
            }
            
            String pred = modeOfList(Val);
            
            if(pred.equalsIgnoreCase(ActualValues.get(i)))
                correctness = correctness +1;            
        }
        
        System.out.println("The Result of Predictions :-");
        System.out.println("Total Cases : "+test.size());
        System.out.println("Total CorrectPredicitions  : "+correctness);
        System.out.println("Forest Accuracy :"+(correctness*100/test.size())+"%");				
    }
    
    /**
     * Testing the forest using the traindata (stratified cross validation)
     *     
     * @param trees
     * @param train     
     * @param print     
     * @return akurasi     
     */
    public String crossValidating(boolean print){
        if(this.forestingMode != 1) {
            String response = "Nope";
            
            if(print) System.out.println(response);
            
            return response;
        }
        
        int kFold = 10;
        
        ArrayList<String> classes = new ArrayList();
        HashMap<String, Integer> classesCount = new HashMap();
        HashMap<String,ArrayList<Integer>> classesIndexLocation = new HashMap();
        
        Collections.shuffle(this.traindata);
      
        int i=0;
        for(ArrayList<String> instance : this.traindata) {
            String c = instance.get(instance.size()-1);
            if(!classes.contains(c))
                classes.add(c);                        
            
            classesCount.put(c, 0);                        
            
            classesIndexLocation.put(c, new ArrayList());
            
            i++;
        }    
        
        i=0;
        for(ArrayList<String> instance : this.traindata) {
            String c = instance.get(instance.size()-1);            
            
            classesCount.put(c, classesCount.get(c)+1);
            
            classesIndexLocation.get(c).add(i);
            
            classesIndexLocation.put(c, classesIndexLocation.get(c));
            
            i++;
        }                               
               
        ArrayList<ArrayList<String>>[] newData = new ArrayList[kFold];
        
        for(int k=0; k<kFold; k++) {
            newData[k] = new ArrayList();

            for(String c : classes) {
                int count = classesCount.get(c);
                int partitionSize = count / kFold;

                ArrayList<Integer> indexes = classesIndexLocation.get(c);

                if(indexes.size() < 1) continue;
                
                int currentPartitionSize;
                if(count < kFold) {
                    currentPartitionSize = 1;
                } else {
                    if(indexes.size() % partitionSize != 0) 
                        currentPartitionSize = partitionSize+1;
                    else
                        currentPartitionSize = partitionSize;
                }
                
                for(int o=0; o<currentPartitionSize; o++) {
                    newData[k].add(this.traindata.get(indexes.get(0)));
                    
                    indexes.remove(0);
                }
                classesIndexLocation.put(c, indexes);
            }                                  
        }
        
        double[] akurasi = new double[10];
        for(int k=0; k<kFold; k++) {
            int correctness=0;            
            
            ArrayList<ArrayList<String>> tempTest, tempTrain = new ArrayList();

            tempTest = newData[k];
            
            for(int l=0; l<kFold; l++) {
                if(l == k) continue;
                
                tempTrain.addAll(newData[l]);
            }
            
            ArrayList<String> ActualValues = new ArrayList<>();

            for(ArrayList<String> s : tempTest){
                ActualValues.add(s.get(s.size()-1));
            }

            int treee=1;

//            System.out.println("Testing forest now ");

            for(DecisionTree DTC : trees){
                DTC.calculateClasses(tempTrain, tempTest, treee);treee++;

                if(DTC.predictions!=null)
                    Prediction.add(DTC.predictions);
            }

            for(int p=0; p<tempTest.size(); p++){
                ArrayList<String> Val = new ArrayList<>();

                for(int j=0;j<trees.size();j++){
                    Val.add(Prediction.get(j).get(p));
                }

                String pred = modeOfList(Val);

                if(pred.split("-")[0].equalsIgnoreCase(ActualValues.get(p).split("-")[0]))
                    correctness = correctness+1;            
            }
            
            akurasi[k] = correctness == 0 ? 0 : correctness*100/tempTest.size();
        }
        
        double akurasiOverall, totalAkurasi=0;
        
        for(double x : akurasi) {
            totalAkurasi+=x;
        }
        
        akurasiOverall = totalAkurasi / kFold * 1.0;
        
        if(print) {
            System.out.println("The Result of Predictions :-");
            System.out.println("Forest Accuracy :"+akurasiOverall+"%");				
        }
        
        return akurasiOverall+"%";
    }
    
    public void evaluateForestWithLabel() {
        this.evaluateForestWithLabel(trees, traindata, testdata);
    }
    
    /**
     * To find the final prediction of the trees
     * 
     * @param predictions
     * @return the mode of the list
     */
    public String modeOfList(ArrayList<String> predictions) {
        // TODO Auto-generated method stub
        String MaxValue = null; 
        
        int MaxCount = 0;
        
        for(int i=0;i<predictions.size();i++){
            int count=0;
            
            for(int j=0;j<predictions.size();j++){
                if(predictions.get(j).trim().equalsIgnoreCase(predictions.get(i).trim()))
                    count++;
                
                if(count>MaxCount){
                    MaxValue=predictions.get(i);
                    MaxCount=count;
                }
            }
        }
        
        return MaxValue;
    }
    
    public double confidenceOfModeOfList(ArrayList<String> predictions) {
        // TODO Auto-generated method stub
        double confidence=0;
        
        String MaxValue = null; 
        
        int MaxCount = 0;
        
        for(int i=0;i<predictions.size();i++){
            int count=0;
            
            for(int j=0;j<predictions.size();j++){
                if(predictions.get(j).trim().equalsIgnoreCase(predictions.get(i).trim()))
                    count++;
                
                if(count>MaxCount){
                    MaxValue=predictions.get(i);
                    MaxCount=count;
                }
            }
        }
        
        confidence = (double) MaxCount / (double) predictions.size();
        
        return confidence;
    }
    
    /**
     * This class houses the machinery to generate one decision tree in a thread pool environment.
     *
     */
    private class CreateTree implements Runnable{
        /** the training traindata to generate the decision tree (same for all trees) */
        private final ArrayList<ArrayList<String>> data;
        
        /** the current forest */
        private final RandomForest forest;
        
        /** the Tree number */
        private final int treenum;
        
        /**
         * A default constructor
         */
        public CreateTree(ArrayList<ArrayList<String>> data,RandomForest forest,int num){
            this.data=data;
            this.forest=forest;
            this.treenum=num;
        }
        
        /**
         * Creates the decision tree
         */
        @Override
        public void run() {
            trees.add(new DecisionTree(data, forest, treenum));
            progress+=update;
            
            updateListener(progress);
        }
    }

    /**
     * This class houses the machinery to generate one decision tree in a thread pool environment.
     *
     */
    private class ReCreateTree implements Runnable{
        
        /** the current forest */
        private final RandomForest forest;
        
        /** the Tree number */
        private final int treenum;
        
        /** the stored knowledge **/
        private final ArrayList<String[]> knowledge;
        /**
         * A default constructor
         */
        public ReCreateTree(ArrayList<String[]> knowledge,RandomForest forest,int num){
            this.forest=forest;
            this.treenum=num;
            this.knowledge = knowledge;
        }
        
        /**
         * Creates the decision tree
         */
        @Override
        public void run() {
            trees.add(new DecisionTree(forest, treenum, knowledge));
            progress+=update;
            
            updateListener(progress);
        }
    }
    
    /** start the timer when beginning forest creation */
    private void startTimer(){
        time_o=System.currentTimeMillis();
    }
    
    /**
     * Given a certain time that's elapsed, return a string
     * representation of that time in hr,min,s
     * 
     * @param timeinms	the beginning time in milliseconds
     * @return			the hr,min,s formatted string representation of the time
     */
    private static String timeElapsed(long timeinms){
        double s=(double)(System.currentTimeMillis()-timeinms)/1000;
        
        int h=(int)Math.floor(s/((double)3600));
        
        s-=(h*3600);
        
        int m=(int)Math.floor(s/((double)60));
        
        s-=(m*60);
        
        return ""+h+"hr "+m+"m "+s+"sec";
    }
    
    /**
     * Checks if attribute is categorical or not
     * 
     * @param s
     * @return boolean true if it has an alphabet
     */
    private boolean isAlphaNumeric(String s){
        char c[]=s.toCharArray();
        
        boolean hasalpha=false;
        
        for(int j=0;j<c.length;j++){
            hasalpha = Character.isLetter(c[j]);
            if(hasalpha)
                break;
        }
        
        return hasalpha;
    }
    
    /**
     * Of the attributes selected this function will record the genre of attributes  
     */
    private ArrayList<Integer> getAttributes(List<ArrayList<String>> data){
        ArrayList<Integer> Attributes = new ArrayList<Integer>();
        
        int iter = 0;
        
        ArrayList<String> DataPoint = data.get(iter);
        
        if(DataPoint.contains("n/a") || DataPoint.contains("N/A")){
            iter = iter +1;
            DataPoint = data.get(iter);
        }
        
        for(int i =0;i<DataPoint.size();i++){
            if(isAlphaNumeric(DataPoint.get(i)))
                Attributes.add(1);
            else
                Attributes.add(0);
        }
        
        return Attributes;
    }

    public ArrayList<DecisionTree> getTrees() {
        return trees;
    }

    public String getDuration() {
        return duration;
    }

    public void setTestdata(ArrayList<ArrayList<String>> testdata) {
        this.testdata = testdata;
        this.TestAttributes = this.getAttributes(testdata);
    }

    public double getProgress() {
        return progress;
    }

    public void setUpdateListener(JProgressBar updateListener) {
        this.updateListener = updateListener;
    }
    
    private void updateListener(double progress) {
        if(this.updateListener != null) {
            this.updateListener.setValue((int) Math.round(progress));
        }
    }
}
