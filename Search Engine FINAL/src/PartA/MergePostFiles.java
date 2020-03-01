package PartA;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class receives the path of all the post files and need to merge them to one */

public class MergePostFiles {

    public Queue<String> QueueToMerge;
    private String Path;
    private int NumOfMerge;
    private Map<String, TermData> TermsData;
    private int LastMergeCounter;
    private boolean isStemmer;
    Map<String , Map<String, Integer>> entitiesPerDoc;
    private BufferedWriter bufferedWriter1;
    private BufferedWriter bufferedWriter2;
    private BufferedWriter bufferedWriter3;
    private BufferedWriter bufferedWriter4;
    private BufferedWriter bufferedWriter5;
    private BufferedWriter bufferedWriter6;
    private BufferedWriter bufferedWriter7;
    private BufferedWriter bufferedWriter8;
    private BufferedWriter bufferedWriter9;
    private BufferedWriter bufferedWriter10;
//    private ExecutorService pool;


    /**
     *
     * @param path where to write to merged post file
     * @param number of merges needed
     * @param isStemmer Checking if we need to use STM
     */
    public MergePostFiles(String path, int number, boolean isStemmer){
        QueueToMerge = new LinkedList<>();
        this.Path = path;
        NumOfMerge = number;
        this.isStemmer = isStemmer;
        LastMergeCounter=0;

 //       this.pool = Executors.newFixedThreadPool(1000);
    }

    /**
     *  the class need to set a new terms data
     * @param TermsData to be set
     */
    public void setTermsData(Map<String, TermData> TermsData){
        this.TermsData = TermsData;
    }


    /**
     * the main function of this class,  receives the path of all the post files and need to merge them to two last files
     * @throws InterruptedException
     */
    public void startMerge(Map<String , Map<String, Integer>> entitiesPerDoc) throws InterruptedException {
        this.entitiesPerDoc = entitiesPerDoc;
        for(int i=0 ; i<NumOfMerge-1; i++){
            if(QueueToMerge.size()>1){
                String p = Path+ "/Mergefile number_"+i +".text";
                mergeTwoFiles(p);
            }
            else{
                //make thread sleep
                //when q>1 wake up
            }

        }
        if(QueueToMerge.size()>1){
            String newPath ="";
            if(isStemmer){
                newPath = Path + "/FinalPostFile-STM";
            }
            else {
                newPath = Path +"/FinalPostFile";
            }
            mergeTwoFinalFiles(newPath);
        }
        else{
            //make thread sleep
            //when q>1 wake up
        }

        File tmpFolder =new File(Path+"/PostFiles");
        tmpFolder.delete();
        File tmpFolderSTM =new File(Path+"/STM-PostFiles");
        tmpFolderSTM.delete();

//        File index = new File(Path +"/PostFiles");
//        index.delete();


    }
    private void writeTodisk(String toWrite) throws IOException {
        if(LastMergeCounter<=200000){
            bufferedWriter1.write(toWrite);
            bufferedWriter1.newLine();
        }
        else if(LastMergeCounter<=400000){
            bufferedWriter2.write(toWrite);
            bufferedWriter2.newLine();
        }
        else if(LastMergeCounter<=600000){
            bufferedWriter3.write(toWrite);
            bufferedWriter3.newLine();

        }
        else if(LastMergeCounter<=800000){
            bufferedWriter4.write(toWrite);
            bufferedWriter4.newLine();

        }
        else if(LastMergeCounter<=1000000){
            bufferedWriter5.write(toWrite);
            bufferedWriter5.newLine();

        }
        else if(LastMergeCounter<=1200000){
            bufferedWriter6.write(toWrite);
            bufferedWriter6.newLine();

        }
        else if(LastMergeCounter<=1400000){
            bufferedWriter7.write(toWrite);
            bufferedWriter7.newLine();

        }
        else if(LastMergeCounter<=1600000){
            bufferedWriter8.write(toWrite);
            bufferedWriter8.newLine();

        }
        else if(LastMergeCounter<=1800000){
            bufferedWriter9.write(toWrite);
            bufferedWriter9.newLine();

        }
        else {
            bufferedWriter10.write(toWrite);
            bufferedWriter10.newLine();

        }
        LastMergeCounter++;
    }

    private void mergeTwoFinalFiles(String pathTowrite) {
        try {
            String path1 = QueueToMerge.poll();
            String path2 = QueueToMerge.poll();
            File file1 = new File(path1);
            File file2 = new File(path2);
            File mergeFile1 = new File(pathTowrite+1+ ".txt");
            File mergeFile2 = new File(pathTowrite+2+ ".txt");
            File mergeFile3 = new File(pathTowrite+3+ ".txt");
            File mergeFile4 = new File(pathTowrite+4+ ".txt");
            File mergeFile5 = new File(pathTowrite+5+ ".txt");
            File mergeFile6 = new File(pathTowrite+6+ ".txt");
            File mergeFile7 = new File(pathTowrite+7+ ".txt");
            File mergeFile8 = new File(pathTowrite+8+ ".txt");
            File mergeFile9 = new File(pathTowrite+9+ ".txt");
            File mergeFile10 = new File(pathTowrite+10+ ".txt");
            BufferedReader bufferReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "UTF-8"));
            BufferedReader bufferReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(path2), "UTF-8"));
            bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile1.getPath()), "UTF-8"), 262144);
            bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile2.getPath()), "UTF-8"), 262144);
            bufferedWriter3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile3.getPath()), "UTF-8"), 262144);
            bufferedWriter4 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile4.getPath()), "UTF-8"), 262144);
            bufferedWriter5 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile5.getPath()), "UTF-8"), 262144);
            bufferedWriter6 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile6.getPath()), "UTF-8"), 262144);
            bufferedWriter7 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile7.getPath()), "UTF-8"), 262144);
            bufferedWriter8 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile8.getPath()), "UTF-8"), 262144);
            bufferedWriter9 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile9.getPath()), "UTF-8"), 262144);
            bufferedWriter10 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile10.getPath()), "UTF-8"), 262144);

            String term1Line="";
            String term2Line="";

            boolean hasMore;
            if(bufferReader1.ready() && bufferReader2.ready()){
                hasMore = true;
                term1Line = bufferReader1.readLine();
                term2Line = bufferReader2.readLine();
            }
            else {
                hasMore=false;
            }


            while(hasMore){

                String[] tmpTerms1 = term1Line.split("@");
                String term1 = tmpTerms1[0];

                String[] tmpTerms2 = term2Line.split("@");
                String term2 = tmpTerms2[0];


                int result = CompareTwoTerms(term1,term2);


                String toWrite = "";
                if(result==0){
                    if((term1.charAt(0) >='a' && term1.charAt(0) <='z')||(term2.charAt(0) >='a' && term2.charAt(0) <='z')){
                        toWrite = term1.toLowerCase() +"@"+ tmpTerms1[1]+ " " + tmpTerms2[1];
                        if(TermsData.containsKey(term1.toLowerCase())){
                            TermsData.get(term1.toLowerCase()).setPointerLine(LastMergeCounter);
                            writeTodisk(toWrite);
                        }

                    }
                    else {
                        if((term1.charAt(0) >='A' && term1.charAt(0) <='Z')&&(term2.charAt(0) >='A' && term2.charAt(0) <='Z')){
                            updateEntitiesPerDoc(term1.toUpperCase() , toWrite);
                        }
                        toWrite = term1 +"@"+ tmpTerms1[1]+ " " +tmpTerms2[1];
                        if(TermsData.containsKey(term1)){
                            TermsData.get(term1).setPointerLine(LastMergeCounter);
                            writeTodisk(toWrite);
                        }
                    }
                    if(bufferReader1.ready() && bufferReader2.ready()){
                        term1Line = bufferReader1.readLine();
                        term2Line = bufferReader2.readLine();
                    }
                    else {
                        hasMore=false;
                    }

                }
                else if(result > 0){
                    if(term2.charAt(0) >='A' && term2.charAt(0) <='Z'){
                        updateEntitiesPerDoc(term2.toUpperCase() , tmpTerms2[1]);
                    }
                    if(TermsData.containsKey(term2)){
                        TermsData.get(term2).setPointerLine(LastMergeCounter);
                        writeTodisk(term2Line);
                    }
                    if(bufferReader2.ready()){
                        term2Line = bufferReader2.readLine();
                    }
                    else {
                        hasMore=false;
                        if(TermsData.containsKey(term1)){
                            TermsData.get(term1).setPointerLine(LastMergeCounter);
                            writeTodisk(term1Line);
                        }
                    }

                }
                else{
                    if(term1.charAt(0) >='A' && term1.charAt(0) <='Z'){
                        updateEntitiesPerDoc(term1.toUpperCase() , tmpTerms1[1]);
                    }
                    if(TermsData.containsKey(term1)){
                        TermsData.get(term1).setPointerLine(LastMergeCounter);
                        writeTodisk(term1Line);
                    }
                    if(bufferReader1.ready()){
                        term1Line = bufferReader1.readLine();
                    }
                    else {
                        hasMore=false;
                        if(TermsData.containsKey(term2)){
                            TermsData.get(term2).setPointerLine(LastMergeCounter);
                            writeTodisk(term2Line);
                        }
                    }

                }
            }
            while(bufferReader1.ready()){
                term1Line = bufferReader1.readLine();
                String[] tmpTerms1 = term1Line.split("@");
                String term1 = tmpTerms1[0];
                if(TermsData.containsKey(term1)){
                    TermsData.get(term1).setPointerLine(LastMergeCounter);
                    writeTodisk(term1Line);
                }
            }
            while(bufferReader2.ready()){
                term2Line = bufferReader2.readLine();
                String[] tmpTerms2 = term2Line.split("@");
                String term2 = tmpTerms2[0];
                if(TermsData.containsKey(term2)){
                    TermsData.get(term2).setPointerLine(LastMergeCounter);
                    writeTodisk(term2Line);
                }
            }


            bufferedWriter1.close();
            bufferedWriter2.close();
            bufferedWriter3.close();
            bufferedWriter4.close();
            bufferedWriter5.close();
            bufferedWriter6.close();
            bufferedWriter7.close();
            bufferedWriter8.close();
            bufferedWriter9.close();
            bufferedWriter10.close();
            bufferReader1.close();
            bufferReader2.close();
            file1.delete();
            file2.delete();
            QueueToMerge.add(pathTowrite);


        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void updateEntitiesPerDoc(String entities , String toUpdate) {
        String[] split = toUpdate.split("\\s+");
        for(int j = 0; j<split.length; j++){
            String[] curr = split[j].split("=");
            if(curr.length==2){
                entitiesPerDoc.get(curr[0]).put(entities,Integer.parseInt(curr[1]));
            }

        }
    }

    private void mergeTwoFiles(String pathTowrite) {
        try {
            String path1 = QueueToMerge.poll();
            String path2 = QueueToMerge.poll();
            File file1 = new File(path1);
            File file2 = new File(path2);
            File mergeFile = new File(pathTowrite);
            BufferedReader bufferReader1 = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "UTF-8"));
            BufferedReader bufferReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(path2), "UTF-8"));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mergeFile.getPath()), "UTF-8"), 262144);

            String term1Line="";
            String term2Line="";

            boolean hasMore;
            if(bufferReader1.ready() && bufferReader2.ready()){
                hasMore = true;
                term1Line = bufferReader1.readLine();
                term2Line = bufferReader2.readLine();
            }
            else {
                hasMore=false;
            }


            while(hasMore){

                String[] tmpTerms1 = term1Line.split("@");
                String term1 = tmpTerms1[0];

                String[] tmpTerms2 = term2Line.split("@");
                String term2 = tmpTerms2[0];


                int result = CompareTwoTerms(term1,term2);

                String toWrite = "";
                if(result==0){
                    if((term1.charAt(0) >='a' && term1.charAt(0) <='z')||(term2.charAt(0) >='a' && term2.charAt(0) <='z')){
                        toWrite = term1.toLowerCase() +"@"+ tmpTerms1[1] + " "+tmpTerms2[1];
                    }
                    else {
                        toWrite = term1 +"@"+ tmpTerms1[1]+" " + tmpTerms2[1];
                    }
                    bufferedWriter.write(toWrite);
                    bufferedWriter.newLine();

                    if(bufferReader1.ready() && bufferReader2.ready()){
                        term1Line = bufferReader1.readLine();
                        term2Line = bufferReader2.readLine();
                    }
                    else {
                        hasMore=false;
                    }

                }
                else if(result > 0){
                    bufferedWriter.write(term2Line);
                    bufferedWriter.newLine();
                    if(bufferReader2.ready()){
                        term2Line = bufferReader2.readLine();
                    }
                    else {
                        hasMore=false;
                        bufferedWriter.write(term1Line);
                        bufferedWriter.newLine();
                    }

                }
                else{
                    bufferedWriter.write(term1Line);
                    bufferedWriter.newLine();
                    if(bufferReader1.ready()){
                        term1Line = bufferReader1.readLine();
                    }
                    else {
                        hasMore=false;
                        bufferedWriter.write(term2Line);
                        bufferedWriter.newLine();
                    }

                }
            }
            while(bufferReader1.ready()){
                bufferedWriter.write(bufferReader1.readLine());
                bufferedWriter.newLine();
            }
            while(bufferReader2.ready()){
                bufferedWriter.write(bufferReader2.readLine());
                bufferedWriter.newLine();
            }


            bufferedWriter.close();
            bufferReader1.close();
            bufferReader2.close();
            file1.delete();
            file2.delete();
            QueueToMerge.add(pathTowrite);


        }
        catch (IOException e){
            e.printStackTrace();
        }

    }


    private int CompareTwoTerms(String term1, String term2){
        term1 = term1.toLowerCase();
        term2 = term2.toLowerCase();

        return term1.compareTo(term2);

       // return 0;
    }
}
