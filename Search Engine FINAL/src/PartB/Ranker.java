package PartB;

import PartA.DocData;
import PartA.IIndexer;
import PartA.TermData;

import java.io.*;
import java.util.*;

public class Ranker {

    private IIndexer indexer;
    private int termsSize;
    private boolean useStemmer;
    private boolean useSemmantic;
    private double avgDocsSize;
    private double k1;
    private double b;
    private int numOfDocs;
    private Map<String,Double>rankingQuery;
    private Map<String, DocData> docsData;
    private Map<String, TermData> termsData;
    private Set<String> docs;
    private Map<String,Map<String,Integer>> titlePosting;
    private Map<String,Map<String,Integer>> descriptionPosting;
    private semmanticApi api;
    private Set<String> asd;




    public Ranker(IIndexer idx){
        this.k1 = 1.2;
        this.b = 0.4;
        this.indexer = idx;
        this.termsSize = idx.getTermsList().size();
        this.docsData = idx.getDocsData();
        this.numOfDocs = docsData.size();
        this.termsData = idx.getTermsData();
        this.avgDocsSize = calcAVGDocsSize();
        api = new semmanticApi();


    }

    private boolean isEntities(String term){
        return indexer.getEntitiesSet().contains(term);
    }

    public Map<String, Double> ranking(Map<String, Integer> titleMap, Map<String, Integer> descriptionMap) throws IOException {
        rankingQuery = new HashMap<>();
        if(useSemmantic){
            titlePosting = getPostingWithSem(titleMap.keySet());
        }
        else {
            titlePosting = getPosting(titleMap.keySet());
        }
        rankingTitle(titleMap);
        if(descriptionMap!=null){
            descriptionPosting = getPosting(descriptionMap.keySet());
            rankingDescriptionMap(descriptionMap);
        }
        sortQueries();


        return  rankingQuery;
    }



    public void setUseSemmantic(boolean useSemmantic) {
        this.useSemmantic = useSemmantic;
    }

    public void setUseStemmer(boolean useStemmer){
        this.useStemmer = useStemmer;
    }

    private double getScore(Map<String,Integer>query, String docNum, Map<String,Map<String,Integer>> posting) throws IOException {
        double total=0;


        for(String term : query.keySet()){

            if(posting.containsKey(term)&&(posting.get(term)).containsKey(docNum)){
                double idf = calcIdf(foundInDic(term));
                double freq = posting.get(term).get(docNum);
                double docSize = docsData.get(docNum).getTotalWords();
                double toAdd =  query.get(term)*idf*((freq*(k1+1))/(freq+k1*(1-b+b*(docSize/avgDocsSize)))+2);
                if(isEntities(term)){
                    String[] toTerms = term.split("\\s+");
                    int toPow=1;
                    if(toTerms.length>0){
                        toPow = 3*toTerms.length;
                    }
                    toAdd= toPow*toAdd;
                }
                else if(term.equals(term.toUpperCase())){
                    toAdd = 1.5*toAdd;
                }

                total = total +toAdd;
            }
        }
        return total;
    }

    private Map<String, Map<String, Integer>> getPostingWithSem(Set<String> keySet) throws IOException {
        this.docs = new HashSet();
        Map<String,Map<String,Integer>> posting = new HashMap<>();
        for(String term : keySet){
            if(term==null){
                continue;
            }
            HashMap<String,Integer> termToadd = new HashMap<>();
            HashSet<String> synWords = api.getSynByQuery(term);
            synWords.add(term);
            for(String syn : synWords){
                syn = foundInDic(syn);
                if(syn.equals("-1")){
                    continue;
                }
                int lineInPostFile = getlineInPostFile(syn);
                BufferedReader postFile = openFilePost(lineInPostFile);
                if(lineInPostFile<2000000){
                    lineInPostFile = lineInPostFile%200000;
                }
                else {
                    lineInPostFile = lineInPostFile-1800000;
                }
                String termDocCounter= "";
                for(int i = 1; i<lineInPostFile; i ++){
                    postFile.readLine();
                }
                termDocCounter = postFile.readLine();
                String[] splitted = termDocCounter.split("@");
                String[] secSplit = splitted[1].split("\\s+");
                for(int j = 0; j<secSplit.length; j++){
                    String[] curr = secSplit[j].split("=");
                    if(curr.length == 2) {
                        if (termToadd.containsKey(curr[0])) {
                            termToadd.replace(curr[0], termToadd.get(curr[0]) + Integer.parseInt(curr[1]));
                        } else {
                            termToadd.put(curr[0], Integer.parseInt(curr[1]));
                        }
                        docs.add(curr[0]);
                    }
                }
                posting.put(term,termToadd);
            }

        }
        return posting;
    }

    public IIndexer getIndexer() {
        return indexer;
    }

    private Map<String, Map<String, Integer>> getPosting(Set<String> keySet) throws IOException {
        this.docs = new HashSet();
        Map<String,Map<String,Integer>> posting = new HashMap<>();
        String currTerm = "";
        for(String t : keySet){
            currTerm = foundInDic(t);
            if(currTerm.equals("-1")){
                continue;
            }
                int lineInPostFile = getlineInPostFile(currTerm);
                BufferedReader postFile = openFilePost(lineInPostFile);
                if(lineInPostFile<2000000){
                    lineInPostFile = lineInPostFile%200000;
                }
                else {
                    lineInPostFile = lineInPostFile-1800000;
                }
                String termDocCounter= "";
                if(postFile == null){
                    System.out.println("null 456456");
                }
                for(int i = 1; i<lineInPostFile; i ++){
                    postFile.readLine();
                }
                termDocCounter = postFile.readLine();


                String[] splitted = termDocCounter.split("@");
                String[] secSplit = splitted[1].split("\\s+");
                if(!splitted[0].equals(foundInDic(t))){
                    System.out.println("errorrrrrrrrrrrrrr    " + splitted[0] + "!=" +currTerm);
                }
                Map<String,Integer> docToadd = new HashMap<>();
                for(int j = 0; j<secSplit.length; j++){
                    String[] curr = secSplit[j].split("=");
                    if(curr.length == 2) {
                        docToadd.put(curr[0], Integer.parseInt(curr[1]));
                        docs.add(curr[0]);
                    }
                }
                posting.put(t,docToadd);

            }

//        }
        return posting;
    }

    private double calcIdf(String term) {

        int n = termsData.get(term).getDocsCounter();
        return Math.log10((numOfDocs - n + 0.5)/(n+0.5));
    }

    private void sortQueries() {
        List<Map.Entry<String, Double> > list = new LinkedList<>(rankingQuery.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double> >() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        rankingQuery= new LinkedHashMap<String, Double>();
        int toAdd = Math.min(50,list.size());
        for(int i=0; i<toAdd; i++){
            Map.Entry aa = list.get(i);
            rankingQuery.put((String) aa.getKey(), (Double) aa.getValue());
        }
    }

    private void rankingDescriptionMap(Map<String,Integer> descQ) throws IOException {

        for(String docNum: docs ){
            double score = 0.3 * getScore(descQ,docNum,descriptionPosting);
            if(rankingQuery.containsKey(docNum)){
                rankingQuery.replace(docNum,rankingQuery.get(docNum)+score);
            }
            else{
                rankingQuery.put(docNum,score);
            }

        }

    }


    private void rankingTitle(Map<String,Integer> titleQ) throws IOException{

        for(String docNum: docs ){
            double score = 0.7 * getScore(titleQ,docNum,titlePosting);
            if(rankingQuery.containsKey(docNum)){
                rankingQuery.replace(docNum,rankingQuery.get(docNum)+score);
            }
            else{
                rankingQuery.put(docNum,score);
            }

        }

    }




    private double calcAVGDocsSize(){
        double total=0;
        for(DocData doc: docsData.values()){
            total=total+doc.getTotalWords();
        }
        return (total/numOfDocs);
    }

    private int getlineInPostFile(String term){
        return this.indexer.getTermsData().get(term).getPointerLine();
    }
    private BufferedReader openFilePost(int line){

        String path = indexer.getMyPath();
        if(useStemmer){
            path = path + "\\FinalPostFile-STM";
        }
        else{
            path = path + "\\FinalPostFile";
        }
        if(line<=200000){
            path = path+1;
        }
        else if(line<=400000){
            path = path+2;
        }
        else if(line<=600000){
            path = path+3;

        }
        else if(line<=800000){
            path = path+4;

        }
        else if(line<=1000000){
            path = path+5;

        }
        else if(line<=1200000){
            path = path+6;

        }
        else if(line<=1400000){
            path = path+7;

        }
        else if(line<=1600000){
            path = path+8;

        }
        else if(line<=1800000){
            path = path+9;

        }
        else {
            path = path+10;

        }
        path = path + ".txt";

        try {
            return new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
        }catch (IOException e){
            System.out.println("not found  " + path);
            return null;
        }
    }

    private String foundInDic(String term){
        String termLowerCase = term.toLowerCase();
        String termUpperCase = term.toUpperCase();
        if(termsData.containsKey(term)){
            return term;
        }
        if (termsData.containsKey(termLowerCase)) {
            return termLowerCase;
        }
        if (termsData.containsKey(termUpperCase)) {
            return termUpperCase;
        }
        return  "-1";
    }



}
