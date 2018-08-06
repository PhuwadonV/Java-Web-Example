package backend;

import com.google.appengine.api.search.*;

public class DataManager {
    private static final SearchService SEARCH_SERVICE = SearchServiceFactory.getSearchService();

    public static void Delete(String indexName, String id) {
        Index index = SEARCH_SERVICE.getIndex(IndexSpec.newBuilder().setName(indexName).build());
        index.delete(id);
    }
}
