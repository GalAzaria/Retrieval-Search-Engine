package PartA;

import PartB.Query;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;

public interface IReadFile {


    void readFoldrs(int howManyToRead) throws IOException;
    Map<String,String> getTexts();
    Map<String,String> getHeaders();
//    Map<String,Query> readQueries(String path);

}
