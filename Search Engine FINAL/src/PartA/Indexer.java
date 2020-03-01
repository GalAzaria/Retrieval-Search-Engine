package PartA;

import java.io.*;
import java.util.*;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

/**
 * The class receives the parsed texts and builds the indexer and also responsibility to write the post files to the disk */

public class Indexer implements IIndexer {

    private IReadFile MyReadFile;
    private String MyPath;
    private IParse MyParse;
    private int HowManyToReadEachRound;
    private int TotalToRead;
    private int ReadedCounter;
    private Map<String,String> PostMap;
    private MergePostFiles Merge;
    private int PostCounter;
    private int TotalMerges;
    private Map<String, DocData> DocsData;
    private Map<String, TermData> TermsData;
    public static long startTime = System.nanoTime();
    private int WordsCounter;
    private int MostFreq;
    private Map<String , Map<String, Integer>> entitiesPerDoc;
    private Set<String> entitiesSet;
//    private int entitiesMaxFreq ;



    int hara = 0;


    /**
     * Indexer constructor
     * @param rf new read file (as interface)
     * @param path to write the files
     * @param parse new parser
     */
    public Indexer(IReadFile rf, String path, IParse parse){
        MyParse= parse;
        MyPath = path;
        MyReadFile = rf;
        TermsData = new HashMap<>();
        DocsData = new HashMap<>();
        HowManyToReadEachRound = 1;
        TotalToRead = 4;
        TotalMerges = 4;
//        HowManyToReadEachRound = 8;
//        TotalToRead = 1815;
//        TotalMerges=227;
//        ReadedCounter=0;
        Merge = new MergePostFiles(path, TotalMerges, parse.useStemming());
        entitiesPerDoc = new HashMap<>();
//        entitiesMaxFreq =10;
    }

    /**
     * Indexer limited constructor
     * @param path to write the files
     */

    public Indexer(String path){
        MyPath = path;
        TermsData = new HashMap<>();
        DocsData = new HashMap<>();
    }

    public IParse getMyParse() {
        return MyParse;
    }

    public String getMyPath() {
        return MyPath;
    }

    public Set<String> getEntitiesSet() {
        return entitiesSet;
    }

    public void setEntitiesSet(Set<String> entitiesSet) {
        this.entitiesSet = entitiesSet;
    }

    public Map<String, TermData> getTermsData() {
        return TermsData;
    }

    /**
     * the main function of the class, receives the parsed texts and builds the indexer
     * also has the responsibility to write the post files to the disk
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void buildIndexer() throws IOException, InterruptedException {
        PostCounter=0;
        while(ReadedCounter<TotalToRead){
            MyReadFile.readFoldrs(HowManyToReadEachRound);
            Map<String,String>  textOfFile = MyReadFile.getTexts();
            Map<String,String> headlines = MyReadFile.getHeaders();
            PostMap = new TreeMap<>(
                    new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {

                            return o1.toUpperCase().compareTo(o2.toUpperCase());
                        }
                    }
            );
            for(String docNum : textOfFile.keySet()){
                String text = textOfFile.get(docNum);
                MyParse.parsing(text,docNum);
                Map<String, Integer> termsCounter = MyParse.getTermsCounter();
                WordsCounter=0;
                MostFreq=0;
                updatePostMap(termsCounter,docNum);
                DocsData.put(docNum,new DocData(headlines.get(docNum),termsCounter.size(),WordsCounter,MostFreq));
            }

            WritePosting(PostMap, PostCounter);
            PostCounter++;
            ReadedCounter = ReadedCounter+ HowManyToReadEachRound;
        }
        updateEntities(MyParse.getEntities());
        entitiesSet= MyParse.getEntitiesSet();
        WritePosting(PostMap, PostCounter);
        PostCounter++;

        Merge.setTermsData(TermsData);
        for(String docNum : DocsData.keySet()){
            entitiesPerDoc.put(docNum , new HashMap<String, Integer>());
        }
        Merge.startMerge(entitiesPerDoc);
        reduceEntitiesPerDoc();
        writeDictionariesToDisk();
    }

    private void reduceEntitiesPerDoc() {
        for(String docNum : DocsData.keySet()){
            Map<String,Integer> entitiesToReduce = entitiesPerDoc.get(docNum);
            if(entitiesToReduce.size()>5){
                List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer> >(entitiesToReduce.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
                    public int compare(Map.Entry<String, Integer> o1,
                                       Map.Entry<String, Integer> o2)
                    {
                        return (o1.getValue()).compareTo(o2.getValue());
                    }
                });
                HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
                for (int i = 0 ; i<5 ; i++) {
                    Map.Entry<String, Integer> curr = list.get(list.size()-1 - i);
                    temp.put(curr.getKey(), Math.min(10,1 + curr.getValue()/10));
//                    entitiesMaxFreq = Math.max(entitiesMaxFreq,curr.getValue());

                }
                entitiesPerDoc.replace(docNum,temp);
            }
        }
//        for(String docNum : DocsData.keySet()){
//            Map<String,Integer> entities = entitiesPerDoc.get(docNum);
//            for(String entity : entities.keySet()){
//                entities.replace(entity , entities.get(entity)*10/entitiesMaxFreq);
//            }
//        }
    }

    private void updateEntities(HashMap<String, Pair<String, Integer>> entities) {
        PostMap = new TreeMap<>(
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {

                        return o1.toUpperCase().compareTo(o2.toUpperCase());
                    }
                }
        );

        for(String entity: entities.keySet()){
            if(TermsData.containsKey(entity)){
                TermsData.get(entity).AddDoc(entities.get(entity).getValue());
                String oldPost = "";
                oldPost= oldPost + " "+ entities.get(entity).getKey() +"=" +entities.get(entity).getValue();
                PostMap.put(entity,oldPost);
            }
        }
    }

    /**
     * The function delete the all files located in the path
     *
     */

    @Override
    public void deleteAllFiles() {
        try {
            FileUtils.cleanDirectory(new File(MyPath));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * the function need to load the whole dictionary from the post files
     * @param toStem - Checking if we need to use STM
     * @return false in case the folders does'nt exists
     */
    @Override
    public boolean loadIndex(boolean toStem) {
        String newPath;
        if(toStem){
            newPath = MyPath +"/STM-DictionaryFiles";
            File theDir = new File(newPath);
            if (!theDir.exists()) {
                return false;
            }
        }
        else {
            newPath = MyPath +"/DictionaryFiles";
            File theDir = new File(newPath);
            if (!theDir.exists()) {
                return false;
            }
        }

        try {

            File load1 = new File(newPath+"/DocsDicFile.txt");
            File load2 = new File(newPath+"/TermsDicFile.txt");
            File load3 = new File(newPath+"/EntitiesFile.txt");
            File load4 = new File(newPath+"/EntitiesFile1.txt");


            BufferedReader bufferReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(load1), "UTF-8"));
            BufferedReader bufferReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(load2), "UTF-8"));
            BufferedReader bufferReader3 = new BufferedReader(new InputStreamReader(new FileInputStream(load3), "UTF-8"));
            BufferedReader bufferReader4 = new BufferedReader(new InputStreamReader(new FileInputStream(load4), "UTF-8"));

            entitiesSet = new HashSet<>();
            while (bufferReader4.ready()){
                entitiesSet.add(bufferReader4.readLine());
            }

            entitiesPerDoc = new HashMap<>();
            while(bufferReader3.ready()){

                String newLine = bufferReader3.readLine();
                String[] split1 = newLine.split("@");
                HashMap<String, Integer> insideMap = new HashMap<>();
                if(split1.length>1) {
                    String[] split2 = split1[1].split(",");

                    for (String ent : split2) {
                        String[] lastSplit = ent.split("=");
                        insideMap.put(lastSplit[0], Integer.parseInt(lastSplit[1]));
                    }
                }

                entitiesPerDoc.put(split1[0],insideMap);
            }


            while(bufferReader1.ready()){
                String newLine = bufferReader1.readLine();
                String[] tmpStr = newLine.split("~",20);
//                if(tmpStr.length==5){
//                    DocsData.put(tmpStr[0],new DocData(tmpStr[1], Integer.parseInt(tmpStr[2]),Integer.parseInt(tmpStr[3]),Integer.parseInt(tmpStr[4])));
//                }
//                else {
//                    System.out.println(tmpStr[0]);
//                }
                DocsData.put(tmpStr[0],new DocData(tmpStr[1], Integer.parseInt(tmpStr[2]),Integer.parseInt(tmpStr[3]),Integer.parseInt(tmpStr[4])));
            }


            while(bufferReader2.ready()){
                String newLine = bufferReader2.readLine();
                String[] tmpStr = newLine.split("~",20);
                if(tmpStr.length == 5) {
                    TermsData.put(tmpStr[0], new TermData(tmpStr[0], Integer.parseInt(tmpStr[1]), Integer.parseInt(tmpStr[2]), Integer.parseInt(tmpStr[3]), Integer.parseInt(tmpStr[4])));
                }
                else{
                    TermsData.put(tmpStr[1], new TermData(tmpStr[1], Integer.parseInt(tmpStr[2]), Integer.parseInt(tmpStr[3]), Integer.parseInt(tmpStr[4]), Integer.parseInt(tmpStr[5])));
                }
//                if(tmpStr.length==5){
//                    TermsData.put(tmpStr[0],new TermData(tmpStr[0], Integer.parseInt(tmpStr[1]),Integer.parseInt(tmpStr[2]),Integer.parseInt(tmpStr[3]),Integer.parseInt(tmpStr[4])));
//                }
//                else {
//                    System.out.println(tmpStr[0]);
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
        return true;
    }

    /**
     *
     * @return collection of all term data
     */

    @Override
    public Collection<TermData> getTermsList() {
        return TermsData.values();
    }

    @Override
    public Map<String, DocData> getDocsData() {
        return DocsData;
    }


    private void writeDictionariesToDisk() {
        String newPath ="";
        if(MyParse.useStemming()){
            newPath = MyPath +"/STM-DictionaryFiles";
        }
        else {
            newPath = MyPath +"/DictionaryFiles";
        }

        File theDir = new File(newPath);
        if (!theDir.exists()) {
            theDir.mkdir();
        }
        else{
            theDir.delete();
            theDir.mkdir();
        }
        File docsFile = new File(newPath + "/DocsDicFile.txt");
        File termsFile = new File(newPath + "/TermsDicFile.txt");
        File entitiFile = new File(newPath + "/EntitiesFile.txt");
        File entitiFile2 = new File(newPath + "/EntitiesFile1.txt");

        try {

     //       FileWriter fileWriter1 = new FileWriter(docsFile);
            BufferedWriter docsdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docsFile.getPath()), "UTF-8"), 262144);
  //          FileWriter fileWriter2 = new FileWriter(termsFile);
            BufferedWriter termWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(termsFile.getPath()), "UTF-8"), 262144);

            BufferedWriter entFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entitiFile.getPath()), "UTF-8"), 262144);
//            PrintWriter out = new PrintWriter(new FileWriter(docsFile, true));
//            PrintWriter out2 = new PrintWriter(new FileWriter(termsFile, true));

            BufferedWriter entFile2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(entitiFile2.getPath()), "UTF-8"), 262144);
            for(String ent: entitiesSet){
                entFile2.write(ent);
                entFile2.newLine();
            }

            for(String docNum : entitiesPerDoc.keySet()){
                String toAdd = docNum + "@";
                for(String ent : entitiesPerDoc.get(docNum).keySet()){
                    toAdd = toAdd + ent + "="+ entitiesPerDoc.get(docNum).get(ent)+ ",";
                }
                entFile.write(toAdd);
                entFile.newLine();
            }

            for(String term : TermsData.keySet()){
                TermData curr = TermsData.get(term);
                String toAdd = term+curr.toString();
                termWriter.write(toAdd);
                termWriter.newLine();
            }
            for (String DocNum : DocsData.keySet()) {
                docsdWriter.write(DocNum+DocsData.get(DocNum).toString());
                docsdWriter.newLine();
            }
            docsdWriter.close();
 //           fileWriter1.close();
            termWriter.close();
  //          fileWriter2.close();
            entFile.close();
            entFile2.close();

        }
         catch (IOException e) {
            e.printStackTrace();

        }

    }
    public Map<String, Integer> getEntitiesMap(String docid) {
        return entitiesPerDoc.get(docid);
    }


    private void WritePosting(Map<String, String> postMap, int i) {
        String newPath ="";
        if(MyParse.useStemming()){
            newPath = MyPath +"/STM-PostFiles";
        }
        else {
            newPath = MyPath +"/PostFiles";
        }
        File theDir = new File(newPath);
        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }else{
            theDir.delete();
            theDir.mkdir();
        }

        File log = new File(newPath +"/Postmap"+i+".PostFile");
        try{
            if(log.exists()==false){
                log.createNewFile();
            }
            PrintWriter out = new PrintWriter(new FileWriter(log, true));

            for(String term : postMap.keySet()) {
                out.append( term +"@"+ postMap.get(term) +"\n");
            }
            Merge.QueueToMerge.add(newPath +"/Postmap"+i+".PostFile");
            out.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void updatePostMap(Map<String, Integer> termsCounter, String docNum) {
        for(String term:termsCounter.keySet()){
            int apperanceNum = termsCounter.get(term);
            WordsCounter = WordsCounter+ apperanceNum;
            if(apperanceNum>MostFreq){
                MostFreq = apperanceNum;
            }
            String Upper = term.toUpperCase();
            String Lower = term.toLowerCase();
            String oldPost;

            if(term.charAt(0) >='a' && term.charAt(0) <='z'){
                if(TermsData.containsKey(Lower)){
                    TermsData.get(Lower).AddDoc(apperanceNum);
                    if(PostMap.containsKey(Lower)){
                        oldPost = PostMap.get(Lower);
                        oldPost= oldPost + " "+ docNum +"=" +apperanceNum;
                        PostMap.replace(Lower,oldPost);
                    }
                    else{
                        oldPost = docNum +"=" + apperanceNum;
                        PostMap.put(Lower,oldPost);
                    }
                }
                else if(TermsData.containsKey(Upper)){
                    TermsData.put(Lower, new TermData(Lower,TermsData.get(Upper).getDocsCounter(),TermsData.get(Upper).getTotalAppearance(),TermsData.get(Upper).getMaxApperance()));
                    TermsData.remove(Upper);
                    TermsData.get(Lower).AddDoc(apperanceNum);
                    if(PostMap.containsKey(Upper)){
                        oldPost = PostMap.get(Upper);
                        oldPost = oldPost + " "+ docNum +"=" + apperanceNum;
                        PostMap.remove(Upper);
                        PostMap.put(Lower,oldPost);
                    }
                    else{
                        oldPost = docNum +"=" + termsCounter.get(term);
                        PostMap.put(term,oldPost);
                    }
                }
                else{
                    TermsData.put(Lower,new TermData(Lower,apperanceNum));
                    oldPost = docNum +"=" + apperanceNum;
                    PostMap.put(Lower,oldPost);
                }
            }
            else if(term.charAt(0) >='A' && term.charAt(0) <='Z'){
                if(TermsData.containsKey(Upper)){
                    TermsData.get(Upper).AddDoc(apperanceNum);
                    if(PostMap.containsKey(Upper)){
                        oldPost = PostMap.get(Upper)+ " "+ docNum +"=" + apperanceNum;
                        PostMap.replace(Upper,oldPost);
                    }
                    else{
                        oldPost =  docNum +"=" + apperanceNum;
                        PostMap.put(Upper,oldPost);
                    }
                }
                else if(TermsData.containsKey(Lower)){
                    TermsData.get(Lower).AddDoc(apperanceNum);
                    if(PostMap.containsKey(Lower)){
                        oldPost = PostMap.get(Lower);
                        oldPost= oldPost + " "+ docNum +"=" + apperanceNum;
                        PostMap.replace(Lower,oldPost);
                    }
                    else{
                        oldPost =  docNum +"=" + apperanceNum;
                        PostMap.put(Lower,oldPost);
                    }
                }
                else{
                    TermsData.put(term,new TermData(term,apperanceNum));
                    oldPost = docNum +"=" + apperanceNum;
                    PostMap.put(term,oldPost);
                }
            }
            else{
                if(!TermsData.containsKey(term)){
                    TermsData.put(term,new TermData(term,apperanceNum));
                    oldPost =docNum +"=" + apperanceNum;
                    PostMap.put(term,oldPost);
                }
                else{
                    TermsData.get(term).AddDoc(apperanceNum);
                    if(PostMap.containsKey(term)){
                        oldPost = PostMap.get(term)+ " "+ docNum +"=" + apperanceNum;
                        PostMap.replace(term,oldPost);
                    }
                    else {
                        oldPost =docNum +"=" + apperanceNum;
                        PostMap.put(term,oldPost);
                    }
                }
            }
        }
    }

}
