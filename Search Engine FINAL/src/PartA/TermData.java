package PartA;

import java.io.Serializable;

/**
 * The class represents the data of each term
 * Using as an object in the Map
 */
public class TermData implements Serializable {


    private String Term;
    private int DocsCounter;
    private int TotalAppearance;
    private int PointerLine;
    private int MaxApperance;

    /**
     * Term data limited constructor
     * @param term term to work on
     */
    TermData(String term){
        Term = term;
        DocsCounter = 0;
        TotalAppearance = 0;
        MaxApperance=0;

    }

    /**
     * term data constructor
     * @param term
     * @param docsCounter
     * @param totalA - total appearance
     * @param MaxApperance
     */

    TermData(String term,int docsCounter, int totalA, int MaxApperance){
        Term = term;
        DocsCounter = docsCounter;
        TotalAppearance = totalA;
        this.MaxApperance = MaxApperance;
    }
    TermData(String term,int docsCounter, int totalA,int pointerline, int MaxApperance){
        Term = term;
        DocsCounter = docsCounter;
        TotalAppearance = totalA;
        PointerLine = pointerline;
        this.MaxApperance = MaxApperance;
    }
    TermData(String term,int numApperance){
        Term=term;
        DocsCounter=1;
        TotalAppearance=numApperance;
        MaxApperance=numApperance;
    }


    /**
     * Add doc function
     * @param numOfApperance in doc
     */
    public void AddDoc(int numOfApperance){
        TotalAppearance = TotalAppearance+numOfApperance;
        DocsCounter++;
        if(numOfApperance>MaxApperance){
            MaxApperance=numOfApperance;
        }
    }

    /**
     * Getters and setters -
     */


    public String getTerm() {
        return Term;
    }

    public int getTotalAppearance(){
        return this.TotalAppearance;
    }

    public int getDocsCounter() {
        return DocsCounter;
    }


    public void setPointerLine(int pl){
        this.PointerLine = pl;
    }
    public int getMaxApperance(){
        return MaxApperance;
    }

    public int getPointerLine() {
        return PointerLine;
    }

    /**
     *
     * @return term ~ doc counter ~ total app ~ pointer to line in the post file ~ max app
     */
    public String toString(){
        return "~"+ DocsCounter+"~"+ TotalAppearance+"~"+ PointerLine+"~"+ MaxApperance;
    }

}
