package PartA;

import java.io.Serializable;

/**
 * The class DocData represents the data of each Doc.
 * Using as an object in the Map */

public class DocData implements Serializable {

    private String Headline;
    private int UniqueWords;
    private int TotalWords;
    private int MaxFreq;

    /**
     * Doc data constructor
     * @param h - head line
     * @param u - Unique words
     * @param t - total words
     * @param m - max freq
     */

    public DocData(String h , int u, int t, int m ){
        UniqueWords = u;
        TotalWords = t;
        MaxFreq =m;
        Headline=h;
    }

    public int getTotalWords() {
        return TotalWords;
    }

    public int getMaxFreq() {
        return MaxFreq;
    }

    /**
     *
     * @return DocNum~header~unique words~total words~max freq
     */
    public String toString(){
        return "~"+Headline +"~" + UniqueWords +"~" + TotalWords + "~" + MaxFreq;
    }
}
