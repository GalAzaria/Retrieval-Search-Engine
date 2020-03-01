package PartB;


import PartA.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Searcher {

    private IParse myParser;
    private Ranker myRanker;
    private Map<String, Map<String,Double>> rankingMap; //qid, docNum, rank
    public static long startTime = System.nanoTime();


    public Searcher(IParse parse, Ranker rank){
        myParser = parse;
        myRanker = rank;
        rankingMap = new HashMap<>();
    }

    public void handleQueryByPath(String path)throws IOException{
        handleQuery(initQueries(path));
    }

    private Map<String,Query> initQueries(String path) {
        Map<String,Query> queriesToParse = new HashMap<>();
        try {
            Document query = Jsoup.parse(new File(path + "\\queries.txt"), "UTF-8");
            Elements allQueries = query.getElementsByTag("top");
            for (Element currQ : allQueries) {
                //insert ID
                String numQ = currQ.getElementsByTag("num").text();
                //numQ =numQ.substring(9,hara.length()-1);
                numQ = numQ.substring(8);
                String[] t = numQ.split("\\s+");
                numQ = t[0];

                //insert title
                String titleQ = currQ.getElementsByTag("title").text();

                //insert desc
                String descQ = currQ.getElementsByTag("desc").text();

                String[] splitDescQ = descQ.split("\\s+");
                String newDescQ = "";
                for(String s : splitDescQ){
                    if(s.equals("Narrative:")){
                        break;
                    }
                    newDescQ = newDescQ + " " + s;
                }
                newDescQ = newDescQ.substring(11);

                Query newQ = new Query(numQ,titleQ,newDescQ);
                queriesToParse.put(numQ,newQ);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return queriesToParse;
    }

    public void handleQuery(Map<String,Query> queries) throws IOException {
        for(String qId: queries.keySet()){
            long start = System.currentTimeMillis();
            Query query = queries.get(qId);
            Map<String,Double> currentRankingMap;
            myParser.parsing(query.getTitle(),null);
            Map<String,Integer> titleMap = myParser.getTermsCounter();

            if(query.getDescription()!=null){
                myParser.parsing(query.getDescription(),null);
                Map<String,Integer> descriptionMap = myParser.getTermsCounter();
                currentRankingMap = myRanker.ranking(titleMap,descriptionMap);
            }
            else{
                currentRankingMap = myRanker.ranking(titleMap,null);
            }
            rankingMap.put(qId,currentRankingMap);
        }

        writeRankToDisk();
        writeTrec_Eval();

    }

    public Map<String, Map<String, Double>> getRankingMap() {
        return rankingMap;
    }

    private void writeRankToDisk() {
        String path = myRanker.getIndexer().getMyPath(); // ?
        File docsFile = new File(path + "/Ranks.txt");
        try {
            BufferedWriter docsdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docsFile.getPath()), "UTF-8"), 262144);
            for(String qid : rankingMap.keySet()){
                String toAdd = qid + "@";
                for(String docNum : rankingMap.get(qid).keySet()){
                    toAdd = toAdd + docNum + "="+ rankingMap.get(qid).get(docNum)+ ",";
                }
                docsdWriter.write(toAdd);
                docsdWriter.newLine();
            }
            docsdWriter.close();



        }catch (FileNotFoundException e ){

        }catch (UnsupportedEncodingException e){

        }catch (IOException e){

        }

    }





    private void writeTrec_Eval() {
        String path = myRanker.getIndexer().getMyPath(); // ?
        File docsFile = new File(path + "/TREC_EVAL.txt");
        try {

            BufferedWriter docsdWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docsFile.getPath()), "UTF-8"), 262144);
            for(String qid : rankingMap.keySet()) {
                for (String docNum : rankingMap.get(qid).keySet()) {
                    String toAdd = qid + " 0 " + docNum + " 1 " + "42.38 mt";
                    docsdWriter.write(toAdd);
                    docsdWriter.newLine();
                }
            }
            docsdWriter.close();



        }catch (FileNotFoundException e ){

        }catch (UnsupportedEncodingException e){

        }catch (IOException e){

        }

    }
}
