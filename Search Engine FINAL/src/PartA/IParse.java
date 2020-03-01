package PartA;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface IParse {

    void parsing(String text , String docNum);
    Map<String, Integer> getTermsCounter();
//    public void InitStopWords(String path);
    boolean useStemming();
    HashMap<String, Pair<String,Integer>> getEntities();
    public int getUniqueNumSize();
    Set<String> getEntitiesSet();
    void setEntitiesSet(Set<String> entitiesSet);

    }
