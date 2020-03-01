package PartA;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * The class receives one text each time and need to parse it to term and counters
 *
 */
public class Parse implements IParse  {

    private Map<String, Integer> TermsCounter;
    private List<String> Terms;
    private int TermsSize;
    private int CounterSkip=0;
    private HashSet<String> StopWords;
    private boolean UseStemming;
    private Stemmer Stm;
    private HashMap<String,String> MonthToNumbers;
    private HashMap <String, Pair<String,Integer>> Entities;
    private HashMap<String, Pair<String,Integer>> TempEntities;
    private Set<String> entitiesSet;
    private Map<String,Integer> AfterNumberMap;
    private String CurrDoc;
    private HashSet<Double> uniqueNum = new HashSet<>();

    public int getUniqueNumSize() {
        return uniqueNum.size();
    }

    /**
     * Parse constructor
     * @param useStemming - Checking if we need to use STM
     * @param stopWorsListPath - Path to the stop word list (Located in the corpus)
     */
    public Parse(Boolean useStemming, String stopWorsListPath){

        entitiesSet = new HashSet<>();
        Entities = new HashMap<>();
        if(useStemming){
            UseStemming = true;
            Stm = new Stemmer();
        }
        else{
            UseStemming = false;
        }
        InitMonthes();
        InitStopWords(stopWorsListPath);
        InitAfterNumberMap();
    }

    public Set<String> getEntitiesSet() {
        return entitiesSet;
    }

    public void setEntitiesSet(Set<String> entitiesSet) {
        this.entitiesSet = entitiesSet;
    }

    private void InitAfterNumberMap() {
        AfterNumberMap = new HashMap<>();
        AfterNumberMap.put("Percent",1);
        AfterNumberMap.put("percent",1);
        AfterNumberMap.put("Percentage",1);
        AfterNumberMap.put("percentage",1);
        AfterNumberMap.put("km",2);
        AfterNumberMap.put("Km",2);
        AfterNumberMap.put("KM",2);
        AfterNumberMap.put("kilometres",2);
        AfterNumberMap.put("Kilometres",2);
        AfterNumberMap.put("kilometers",2);
        AfterNumberMap.put("Kilometers",2);
        AfterNumberMap.put("meter",3);
        AfterNumberMap.put("Meter",3);
        AfterNumberMap.put("meters",3);
        AfterNumberMap.put("Meters",3);
        AfterNumberMap.put("Square",4);
        AfterNumberMap.put("square",4);
        AfterNumberMap.put("Dollars",5);
        AfterNumberMap.put("dollars",5);
        AfterNumberMap.put("million",6);
        AfterNumberMap.put("Million",6);
        AfterNumberMap.put("Millions",6);
        AfterNumberMap.put("millions",6);
        AfterNumberMap.put("M",6);
        AfterNumberMap.put("m",6);
        AfterNumberMap.put("bn",7);
        AfterNumberMap.put("BN",7);
        AfterNumberMap.put("Billion",7);
        AfterNumberMap.put("Billions",7);
        AfterNumberMap.put("billions",7);
        AfterNumberMap.put("billion",7);
        AfterNumberMap.put("Trillion",8);
        AfterNumberMap.put("trillion",8);
        AfterNumberMap.put("Thousand",9);
        AfterNumberMap.put("thousand",9);
        AfterNumberMap.put("thousands",9);
        AfterNumberMap.put("Thousands",9);
        AfterNumberMap.put("pound",10);
        AfterNumberMap.put("Pound",10);
        AfterNumberMap.put("pounds",10);
        AfterNumberMap.put("Pounds",10);
        AfterNumberMap.put("gbp",10);
        AfterNumberMap.put("GBP",10);
    }

    private void InitMonthes() {
        MonthToNumbers = new HashMap<>();
        MonthToNumbers.put("JANUARY","01");
        MonthToNumbers.put("January","01");
        MonthToNumbers.put("JAN","01");
        MonthToNumbers.put("Jan","01");
        MonthToNumbers.put("FEBRUARY","02");
        MonthToNumbers.put("February","02");
        MonthToNumbers.put("FEB","02");
        MonthToNumbers.put("Feb","02");
        MonthToNumbers.put("MARCH","03");
        MonthToNumbers.put("March","03");
        MonthToNumbers.put("MAR","03");
        MonthToNumbers.put("Mar","03");
        MonthToNumbers.put("APRIL","04");
        MonthToNumbers.put("April","04");
        MonthToNumbers.put("APR","04");
        MonthToNumbers.put("Apr","04");
        MonthToNumbers.put("MAY","05");
        MonthToNumbers.put("May","05");
        MonthToNumbers.put("JUN","06");
        MonthToNumbers.put("Jun","06");
        MonthToNumbers.put("JUNE","06");
        MonthToNumbers.put("June","06");
        MonthToNumbers.put("JULY","07");
        MonthToNumbers.put("July","07");
        MonthToNumbers.put("JUL","07");
        MonthToNumbers.put("Jul","07");
        MonthToNumbers.put("AUGUST","08");
        MonthToNumbers.put("August","08");
        MonthToNumbers.put("AUG","08");
        MonthToNumbers.put("Aug","08");
        MonthToNumbers.put("SEPTEMBER","09");
        MonthToNumbers.put("September","09");
        MonthToNumbers.put("SEP","09");
        MonthToNumbers.put("Sep","09");
        MonthToNumbers.put("OCTOBER","10");
        MonthToNumbers.put("October","10");
        MonthToNumbers.put("OCT","10");
        MonthToNumbers.put("Oct","10");
        MonthToNumbers.put("NOV","11");
        MonthToNumbers.put("Nov","11");
        MonthToNumbers.put("NOVEMBER","11");
        MonthToNumbers.put("November","11");
        MonthToNumbers.put("DECEMBER","12");
        MonthToNumbers.put("December","12");
        MonthToNumbers.put("DEC","12");
        MonthToNumbers.put("Dec","12");
    }

    private void InitStopWords(String path) {
        StopWords = new HashSet<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader fileInputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader fileReader = new BufferedReader(fileInputStreamReader);
            while (fileReader.ready()) {
                String toAdd = fileReader.readLine();
                StopWords.add(toAdd);
                StopWords.add(toAdd+".");
            }
            fileReader.close();
            fileInputStreamReader.close();
            fileInputStream.close();
        }
        catch (IOException e){
            System.out.println("There is no stop word list");
        }
        StopWords.add("-");
        StopWords.add("%");
        StopWords.add("$");
    }

    /**
     * The main function of this class, receives one text each time and need to parse it to term and counters
     * @param text - to be parsed
     * @param docNum - text doc Num - have to be unique
     */
    @Override
    public void parsing(String text,String docNum) {
        CurrDoc = docNum;
        TermsCounter = new HashMap<>();
        TempEntities = new HashMap<>();
        text = text.replaceAll(",|\\(|\\)|'|\"|`|\\{|}|\\[|]|\\\\|#|--|\\+|---|&|\\.\\.\\.|\\.\\.|\\||=|>|<|//|", "");
        Terms = new ArrayList(Arrays.asList(text.split("\\n|\\s+|\\t|;|\\?|!|:|@|\\[|]|\\(|\\)|\\{|}|_|\\*")));
        TermsSize = Terms.size();
        for(int i=0 ; i<TermsSize; i++){
            if(CounterSkip>0){
                CounterSkip--;
                continue;
            }
            String term = Terms.get(i);
            if(StopWords.contains(deleteDot(term)) || term.length()==0){
                continue;
            }
            if(i<TermsSize-2 && Terms.get(i+1).equals("-")){
                addMakafTerm(term, i);
                continue;
            }
            if(isNumber(term)){
                handleNumberTerm(term,i);
            }
            else{
                handleTerm(deleteDot(term),i);
            }
        }
        if(CurrDoc!=null) {
            for(String entity:TempEntities.keySet() ){
                entitiesSet.add(entity);
            }
            Entities.putAll(TempEntities);


        }
    }




    private void handleTerm(String term, int i) {
        if(term.length()==0){
            return;
        }
        if(MonthToNumbers.containsKey(term) && i<TermsSize-1 && isIntNumber(deleteDot(Terms.get(i+1)))){
            CounterSkip=1;
            String toAdd="";
            String next = deleteDot(Terms.get(i+1));
            int tmpNum = Integer.parseInt(next);
            if(tmpNum < 10){
                next = "0" + next;
            }
            if(tmpNum <=31 && tmpNum > 0) {
                toAdd = toAdd + MonthToNumbers.get(term) + "-" + next;
                addTermQuick(toAdd);
                return;
            }
            else{
                toAdd = toAdd + next + "-" + MonthToNumbers.get(term);
                addTermQuick(toAdd);
                return;
            }
        }
        if(term.toLowerCase().equals("between")&& i<Terms.size()-2 && isNumber(Terms.get(i+1))&& isNumber(Terms.get(i+2))){
            addTerm(term + " "+ Terms.get(i+1) + " "+ Terms.get(i+2));
            CounterSkip = 2 ;
            return;
        }
        if(isStartWithUpper(term)){
            if(handleEntities(term,i)){
                return;
            }
            if(!StopWords.contains(term.toLowerCase())){
                addTermUpper(term);
            }
        }
        else{
            if (term.length() > 1 && term.charAt(0) == '$' && isNumber(term.substring(1))){
                double number = Double.parseDouble(term.substring(1));
                if(i<TermsSize-1 && AfterNumberMap.containsKey(deleteDot(Terms.get(i+1)))){
                    int next = AfterNumberMap.get(deleteDot(Terms.get(i+1)));
                    if(next==6){
                        addTermQuick("" + number+" M Dollars");
                        CounterSkip=1;
                        return;
                    }
                    if(next==7){
                        number = number * 1000;
                        addTermQuick("" + number + " M Dollars");
                        CounterSkip=1;
                        return;
                    }

                }
                if(number<1000000){
                    addTermQuick(term+ " Dollars");
                }
                else {
                    number = number/1000000;
                    addTermQuick("" + number +" M Dollars");
                }
            }
            else{
                addTermLower(term);
            }

        }

    }

    private boolean handleEntities(String term,int i) {
        if(i<TermsSize-1 && isStartWithUpper(Terms.get(i+1))){
            if(i<TermsSize-2 && isStartWithUpper(Terms.get(i+2))){
                if(i<TermsSize-3 && isStartWithUpper(Terms.get(i+3))){
                    String entity = term + " " + Terms.get(i+1) + " " + Terms.get(i+2) + " " + Terms.get(i+3);
                    entity = entity.toUpperCase();
                    if(entitiesSet.contains(entity)){
                        addTermQuick(entity);
                        CounterSkip=3;
                        return true;
                    }
                    else{
                        addEntitiesToTemp(entity);
                    }
                }
                String entity = term + " " + Terms.get(i+1) + " " + Terms.get(i+2);
                entity = entity.toUpperCase();
                if(entitiesSet.contains(entity)){
                    addTermQuick(entity);
                    CounterSkip=2;
                    return true;
                }
                else{
                    addEntitiesToTemp(entity);
                }
            }
            String entity = term + " " + Terms.get(i+1);
            entity = entity.toUpperCase();
            if(entitiesSet.contains(entity)){
                addTermQuick(entity);
                CounterSkip=1;
                return true;
            }
            addEntitiesToTemp(entity);
            return false;
        }
        return false;
    }

    private void addEntitiesToTemp(String entity){

        entity = entity.toUpperCase();
        if(TempEntities.containsKey(entity)){
            TempEntities.put(entity,new Pair<>(CurrDoc,TempEntities.get(entity).getValue()+1));
        }
        else{
            TempEntities.put(entity,new Pair<>(CurrDoc,1));
        }
    }


    private boolean isIntNumber(String term){
        if(term.length()>0 && Character.isDigit(term.charAt(0))){
            try {
                Integer.parseInt(term);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }
        return false;
    }

    private void handleNumberTerm(String term, int i) {
        if(i<TermsSize-1){
            String next = deleteDot(Terms.get(i+1));
            if(AfterNumberMap.containsKey(next)){
                int afterNumber = AfterNumberMap.get(next);
                double number;
                switch (afterNumber){
                    case 1:
                        term = term + '%';
                        addTermQuick(term);
                        CounterSkip=1;
                        break;
                    case 2:
                        term = term+" KM";
                        addTermQuick(term);
                        CounterSkip = 1;
                        break;
                    case 3:
                        term = term+" Meter";
                        addTermQuick(term);
                        CounterSkip = 1;
                        break;
                    case 4:
                        if(i<TermsSize-2){
                            String NextNextTerm = deleteDot(Terms.get(i+2));
                            if(NextNextTerm.equals("meter") || NextNextTerm.equals("meters")|| NextNextTerm.equals("Meters") || NextNextTerm.equals("Meter")){
                                term = term + " Square Meter";
                                addTermQuick(term);
                                CounterSkip =2;
                                break;
                            }
                            else if(NextNextTerm.equals("kilometers")|| NextNextTerm.equals("kilometres")){
                                term = term + " Square KM";
                                addTermQuick(term);
                                CounterSkip =2;
                                break;
                            }
                            else{
                                addTermNumber(term);
                                break;
                            }
                        }
                    case 5:
                        number = Double.parseDouble(term);
                        if(number<1000000){
                            term = term + " Dollars";
                            addTermQuick(term);
                            CounterSkip=1;
                            break;
                        }
                        else{
                            number= number/1000000;
                            term = "" + number + " M Dollars";
                            addTermQuick(term);
                            CounterSkip =1;
                            break;
                        }
                    case 6:
                        term = term + " M";
                        if(i<TermsSize-2){
                            String nextNext = deleteDot(Terms.get(i+2));
                            if(nextNext.equals("dollars")||nextNext.equals("Dollars")){
                                term = term + " Dollars";
                                addTermQuick(term);
                                CounterSkip=2;
                                break;
                            }
                            if(nextNext.equals("U.S.")){
                                term = term + " Dollars";
                                addTermQuick(term);
                                CounterSkip=3;
                                break;
                            }
                            if(nextNext.equals("pounds")){
                                term = term +" Pounds";
                                addTermQuick(term);
                                CounterSkip=2;
                                break;
                            }
                        }
                        addTermQuick(term);
                        CounterSkip =1;
                        break;
                    case 7:
                        if(i<TermsSize-2){
                            String nextNext = deleteDot(Terms.get(i+2)).toLowerCase();
                            if(nextNext.equals("dollars")){
                                number = Double.parseDouble(term);
                                number = number *1000;
                                term = "" + number +" M Dollars";
                                addTermQuick(term);
                                CounterSkip=2;
                                break;
                            }
                            if(nextNext.equals("U.S.")){
                                number = Double.parseDouble(term);
                                number = number *1000;
                                term = "" + number +" M Dollars";
                                addTermQuick(term);
                                CounterSkip=3;
                                break;
                            }
                            if(nextNext.equals("pounds")){
                                number = Double.parseDouble(term);
                                number = number *1000;
                                term = "" + number +" M Pounds";
                                addTermQuick(term);
                                CounterSkip=2;
                                break;
                            }
                        }
                        term = term+" B";
                        addTermQuick(term);
                        CounterSkip=1;
                        break;
                    case 8:
                        if(i<TermsSize-2 && Terms.get(i+2).equals("U.S.")){
                            number = Double.parseDouble(term) * 1000000;
                            term ="" + number + " M Dollars";
                            addTermQuick(term);
                            CounterSkip = 3;
                            break;
                        }
                        number = Double.parseDouble(term) * 1000;
                        term = "" + number+" B";
                        addTermQuick(term);
                        CounterSkip=1;
                        break;
                    case 9:
                        addTermQuick( term +" K");
                        CounterSkip=1;
                        break;
                    case 10:
                        addTermQuick(getFixedNumber(Double.parseDouble(term)) + " Pounds");
                        CounterSkip=1;
                        break;
                }

            }
            else if(MonthToNumbers.containsKey(next)){
                term = MonthToNumbers.get(next) +"-"+term;
                addTermQuick(term);
                CounterSkip=1;
            }
            else if(isFraction(next)){
                term = term + " " + next;
                CounterSkip=1;
                if(i<TermsSize-2 && (Terms.get(i+2).equals("Dollars")||Terms.get(i+2).equals("dollars"))){
                    term = term + " " + "Dollars";
                    CounterSkip=2;
                }
                addTermQuick(term);
            }
            else{
                addTermNumber(term);
            }
        }
        else{
            addTermNumber(term);
        }
    }

    private boolean isFraction(String term) {
        ArrayList<String> temp = new ArrayList<String>(Arrays.asList(term.split("/")));
        if(temp.size()==2 && isNumber(temp.get(0)) && isNumber(temp.get(1))){
            return true;
        }

        return false;
    }

    private void addTermNumber(String term) {
        Double number = Double.parseDouble(term);
        if(number<1000){
            addTermQuick(term);
        }
        else{
            addTermQuick(getFixedNumber(number));
        }
    }

    private String getFixedNumber(double number){
        double newNumber;
        String newTerm="";
        if(number<1000000){
            newNumber = number/1000;
            newTerm =newNumber + "K";
            return newTerm;
        }
        else if(number<1000000000){
            newNumber = number/1000000;
            newTerm =newNumber + "M";
            return newTerm;
        }
        else{
            newNumber = number/1000000000;
            newTerm = "" + newNumber + "B";
            return newTerm;
        }
    }

    private void addTermQuick(String term){
        if(UseStemming){
            term=stemming(term);
        }
        if(TermsCounter.containsKey(term)){
            TermsCounter.replace(term,TermsCounter.get(term)+1);
        }
        else{
            TermsCounter.put(term,1);
        }
    }

    private void addMakafTerm(String term, int i) {

        if(i<TermsSize-4 && Terms.get(i+3).equals("-")){
            String toAdd = term + "-" + Terms.get(i+2) + "-" + deleteDot(Terms.get(i+4));
            addTerm(toAdd);
            CounterSkip=1;
            return;
        }
        String toAdd = term + "-" + Terms.get(i+2);
        addTerm(toAdd);
        CounterSkip=1;
    }

    private void addTermUpper(String term){
        if(UseStemming){
            term=stemming(term);
        }
        String Upper = term.toUpperCase();
        String Lower = term.toLowerCase();
        if(TermsCounter.containsKey(Lower)){
            TermsCounter.replace(Lower,TermsCounter.get(Lower)+1);
        }
        else if(TermsCounter.containsKey(Upper)){
            TermsCounter.replace(Upper,TermsCounter.get(Upper)+1);
        }
        else {
            TermsCounter.put(Upper,1);
        }
    }

    private void addTermLower(String term){
        if(UseStemming){
            term=stemming(term);
        }
        String Upper = term.toUpperCase();
        String Lower = term.toLowerCase();
        if(TermsCounter.containsKey(Lower)){
            TermsCounter.replace(Lower,TermsCounter.get(Lower)+1);
        }
        else if(TermsCounter.containsKey(Upper)){
            TermsCounter.put(Lower,TermsCounter.get(Upper)+1);
            TermsCounter.remove(Upper);
        }
        else{
            TermsCounter.put(Lower,1);
        }
    }

    private void addTerm(String term) {
        if(UseStemming){
            term=stemming(term);
        }
        if(TermsCounter.containsKey(term)){
            TermsCounter.replace(term,TermsCounter.get(term)+1);
        }
        else {
            String Upper = term.toUpperCase();
            String Lower = term.toLowerCase();
            if(!isStartWithUpper(term)){
                if(TermsCounter.containsKey(Lower)){
                    TermsCounter.replace(Lower,TermsCounter.get(Lower)+1);
                }
                else if(TermsCounter.containsKey(Upper)){
                    TermsCounter.put(Lower,TermsCounter.get(Upper)+1);
                    TermsCounter.remove(Upper);
                }
                else{
                    TermsCounter.put(Lower,1);
                }
            }
            else{
                if(TermsCounter.containsKey(Lower)){
                    TermsCounter.replace(Lower,TermsCounter.get(Lower)+1);
                }
                else if(TermsCounter.containsKey(Upper)){
                    TermsCounter.replace(Upper,TermsCounter.get(Upper)+1);
                }
                else {
                    TermsCounter.put(Upper,1);
                }
            }
        }
    }

    private boolean isStartWithUpper(String term){
        if(term.length()>0  && term.charAt(0) >='A' && term.charAt(0) <='Z'){
            return  true;
        }
        return false;
    }

    private String stemming(String term) {
        for(int i=0 ; i<term.length(); i++){
            Stm.add(term.charAt(i));
        }
        Stm.stem();
        return Stm.toString();

    }



    private String deleteDot(String term) {
        if (term.length()==0){
            return term;
        }
        String toReturn = term;
        if(term.charAt(term.length()-1)=='.' && !term.equals("U.S.")){
            toReturn = term.substring(0,term.length()-1);
        }
        if(term.charAt(0)=='.' || term.charAt(0)=='/'){
            toReturn = term.substring(1);
        }
        return toReturn;
    }

    private boolean isNumber(String term) {
        if(term.length()>0 && Character.isDigit(term.charAt(0))&& Character.isDigit(term.charAt(term.length()-1))){
            try {
                double d = Double.parseDouble(term);
                uniqueNum.add(d);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }
        return false;
//        try {
//            Double.parseDouble(term);
//            return true;
//        } catch(NumberFormatException e){
//            return false;
//        }
    }

    /**
     *
     * @return a map of the term and counter
     */
    @Override
    public Map<String, Integer> getTermsCounter() {
        return TermsCounter;
    }

    /**
     *
     * @return if we need to use STM
     */
    @Override
    public boolean useStemming() {
        return UseStemming;
    }

    /**
     *
     * @return Hash map of entities
     */
    @Override
    public HashMap<String, Pair<String, Integer>> getEntities() {
        return Entities;
    }
}
