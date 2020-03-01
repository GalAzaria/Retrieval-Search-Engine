package PartA;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface IIndexer {

    public void buildIndexer() throws IOException, InterruptedException;

    public void deleteAllFiles();
    IParse getMyParse();

     Map<String, TermData> getTermsData();
    String getMyPath();

    public boolean loadIndex(boolean toAtem);

    public Collection<TermData> getTermsList();

    Map<String,DocData> getDocsData();

    public Set<String> getEntitiesSet();

    public void setEntitiesSet(Set<String> entitiesSet);
}
