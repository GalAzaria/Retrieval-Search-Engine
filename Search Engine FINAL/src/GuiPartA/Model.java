package GuiPartA;
import PartB.Query;
import PartB.Ranker;
import PartB.Searcher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import PartA.*;
import javafx.collections.ObservableMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * The class is responsible on the view controller and act as a mediator
 */
public class Model extends Observable {
    private Indexer idx;
    private Searcher src;
    private Ranker rank;
    public static long startTime = System.nanoTime();
    public static long endTime;


    public Model(){

    }

    /**
     * set index
     * @param index to be set
     */
    public void SetIdx(Indexer index){
        idx = index;
    }

    /**
     * build indexer
     * @throws InterruptedException
     * @throws IOException
     */
    public void selectBuildIndexer() throws InterruptedException, IOException {
        idx.buildIndexer();

    }

    public void selectRunByPath(String path) throws IOException{
        src.handleQueryByPath(path);
    }

    public void initSrc(String path,boolean stm){
        rank = new Ranker(idx);
        IParse prs = new Parse(stm,path+"/stop_words.txt");
        prs.setEntitiesSet(idx.getEntitiesSet());
        src = new Searcher(prs,rank);
    }

    public void setRankIfSemmantic(boolean semmantic){
        rank.setUseSemmantic(semmantic);
    }

    public void setRankIfStemmer(boolean stemmer){
        rank.setUseStemmer(stemmer);
    }


    /**
     * a delete all function, deleting the all files located in the folder
     * @param filePath to delete the files
     */
    public void selectDeleteAll(String filePath){
        if(idx == null){
            idx = new Indexer(filePath);
        }

        idx.deleteAllFiles();
        idx=null;
    }

    /**
     * checking if the folder is empty
     * @param filePath where the files located
     * @param stm checking if to use STM
     * @return True or false (Folder is empty or not)
     */
    public boolean FolderIsEmpty(String filePath, boolean stm){
        String newPath=filePath + "/";
        File file = new File(filePath);
        if(file.isDirectory()){
            if(file.list().length<=0){
                return true;
            }else{
                if(stm){
                    newPath = newPath + "STM-";
                }
                newPath = newPath + "DictionaryFiles";
                File tmpFile = new File(newPath);
                if(tmpFile.exists()){
                    return false;
                }
                else{
                    return true;
                }


            }

        }
        return false;
    }

    public Map<String,Integer>getEntitiesMap(String docid){
        return idx.getEntitiesMap(docid);

    }

    /**
     *
     * @return observable list of terms data
     */
    public ObservableList<TermData> getTermslist(){
        ObservableList<TermData> toReturn = FXCollections.observableArrayList();
        if(idx!=null){
            toReturn.addAll(idx.getTermsList());
        }
        return toReturn;
    }

    /**
     * load the dictionary
     * @param useStem checking if to use STM
     * @return - false if the load failed
     */
    public boolean selectLoad(boolean useStem){
        if(idx !=null) {
            return idx.loadIndex(useStem);

        }
        return false;
    }

    public String printDetails(){
        String str ="";
        str =  idx.getDocsData().size() + " - Documents has been parsed";
        str = str +"\n";
        str = str + idx.getTermsData().size()+" - Different terms in corpus";
        str = str +"\n";
        str = str +idx.getMyParse().getUniqueNumSize()+" - Different nubmers in corpus";
        str = str +"\n";
        str = str + "Final time: "+ startTime + " sec";
        return str;
    }

    public void selectRunByQuery(Query tmpQ) throws IOException {
        HashMap<String,Query> qMap = new HashMap<>();
        qMap.put(tmpQ.getNumber(),tmpQ);
        src.handleQuery(qMap);

    }
    public  Map<String,Map<String,Double>> getRankingMap(){
        return src.getRankingMap();
    }


/*    public ObservableMap<String, ObservableMap<String,Double>> getRanklist(){

       // ObservableMap<String, ObservableMap<String,Double>> toReturn = FXCollections.observableMap();
        if(idx!=null){

        }
        return toReturn;
    }*/


}

