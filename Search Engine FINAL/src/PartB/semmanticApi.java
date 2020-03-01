package PartB;


import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class semmanticApi {

    private String URL = "https://api.datamuse.com/words?rel_syn=";
    private OkHttpClient httpClient;
    private HashSet<String> synWordsQuery = new HashSet<>();


    public semmanticApi() {
        httpClient = new OkHttpClient();

    }

    public HashSet<String>getSynByQuery(String term){

        connect(term);
        return synWordsQuery;
    }


    private void connect(String term){
        synWordsQuery = new HashSet<>();
        String tmpURL = URL + term;
        Request request = new Request.Builder().url(tmpURL).build();
        org.json.simple.parser.JSONParser json = new org.json.simple.parser.JSONParser();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();

        } catch (Exception e) {
            System.out.println("The computer is offline, using Word2Vec instead of the datamuse API.");
            try {
                useSynOffline(term);
            }catch (IllegalArgumentException es){
                return;
            }
        }
        Object object = null;
        try {
            try {
                //the object includes data from json object after parsering it
                object = json.parse(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(object!=null){
            Object[] parsed_json = ((JSONArray) object).toArray();
            String resWord = "";
            //for each word we will take 4 syn words.
            int i = 0;
            for(Object o : parsed_json){
                if(i==2){
                    break;
                }
                resWord = (String) ((JSONObject) o).get("word");
                synWordsQuery.add(resWord);

                i++;
            }
        }
    }

    private void useSynOffline(String term){
        try{
            Word2VecModel model = Word2VecModel.fromBinFile(new File("word2vec.c.output.model.txt"));
            com.medallia.word2vec.Searcher searcher = model.forSearch();

            int numOfResultLimi = 10;

            List<com.medallia.word2vec.Searcher.Match> matches = searcher.getMatches(term,numOfResultLimi);
            for(com.medallia.word2vec.Searcher.Match match : matches){
                match.match();
            }

        }
//        catch (IllegalArgumentException e){
//            return;
//        }
        catch (IOException e){
            return;
        }
        catch (com.medallia.word2vec.Searcher.UnknownWordException e){
            return;
        }
    }

}
