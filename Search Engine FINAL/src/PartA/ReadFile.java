package PartA;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import PartB.Query;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * The class receives path to the main folder of the corpus and need to separate the docs and save the relevant data of each one
 */
public class ReadFile implements IReadFile {
    private List<String> FilesPath;
    private Map<String,String> TextOfFiles;
    private Map<String, String> HeadersOfFiles;
    private int FinishedCounter;

    /**
     * Read file constructor
     * @param path from where to read the corpus
     */

    public ReadFile(String path){
        FilesPath = new ArrayList<>();
        FinishedCounter =0;
        if(path != null) {
            final File folder = new File(path);
            InitFilesPath(folder);
        }
    }


    private void InitFilesPath(File folder) {
        for(File subFile: folder.listFiles()){
            if(subFile.isFile()){
                FilesPath.add(subFile.getPath());
            }
            else{
                InitFilesPath(subFile);
            }
        }
    }

    /**
     * the main function of the class, receives path to the main folder of the corpus and need to separate the docs and save the relevant data of each one
     * @param howManyToRead how many files to read each round
     * @throws IOException
     */
    @Override
    public void readFoldrs(int howManyToRead) throws IOException {
        TextOfFiles = new HashMap<>();
        HeadersOfFiles = new HashMap<>();
        for (int i = 0; i < howManyToRead && FinishedCounter<FilesPath.size(); i++) {
            loadText(FilesPath.get(FinishedCounter));
            FinishedCounter++;
        }
    }

    private void loadText(String path) throws IOException {
        Document doc = Jsoup.parse(new File(path),"UTF-8");
        Elements elementForEachDoc = doc.getElementsByTag("DOC");
        for(Element currE : elementForEachDoc){
            HeadersOfFiles.put(currE.getElementsByTag("DOCNO").text(),currE.getElementsByTag("TI").text());
            TextOfFiles.put(currE.getElementsByTag("DOCNO").text(),currE.getElementsByTag("TEXT").text());
        }

    }

    /**
     *
     * @return map of the texts files
     */
    @Override
    public Map<String, String> getTexts() {
        return TextOfFiles;
    }

    /**
     *
     * @return a map of the headers of the files
     */

    @Override
    public Map<String, String> getHeaders() {
        return HeadersOfFiles;
    }

//    @Override
//    public Map<String, Query> readQueries(String path) {
//        Map<String,Query> queriesToParse = new HashMap<>();
//        try {
//            Document query = Jsoup.parse(new File(path), "UTF-8");
//            Elements allQueries = query.getElementsByTag("top");
//            for (Element currQ : allQueries) {
//                //insert ID
//                String numQ = currQ.getElementsByTag("num").text();
//                //numQ =numQ.substring(9,hara.length()-1);
//                numQ = numQ.substring(8);
//                String[] t = numQ.split("\\s+");
//                numQ = t[0];
//
//                //insert title
//                String titleQ = currQ.getElementsByTag("title").text();
//
//                //insert desc
//                String descQ = currQ.getElementsByTag("desc").text();
//
//                String[] splitDescQ = descQ.split("\\s+");
//                String newDescQ = "";
//                for(String s : splitDescQ){
//                    if(s.equals("Narrative:")){
//                        break;
//                    }
//                    newDescQ = newDescQ + " " + s;
//                }
//
//                Query newQ = new Query(numQ,titleQ,newDescQ);
//                queriesToParse.put(numQ,newQ);
//
//            }
//        }catch (IOException e){
//            System.out.println("Error");
//        }
//        return queriesToParse;
//    }
}
