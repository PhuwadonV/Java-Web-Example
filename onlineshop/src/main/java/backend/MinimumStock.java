package backend;

import com.google.appengine.api.search.*;

import java.util.LinkedList;
import java.util.List;

public class MinimumStock{
    private static final String INDEX_NAME = "MinimumStock";
    private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
    private static volatile long count = -1L;
    private static volatile long lastAdded = 0;
    private static volatile boolean isSearched = true;

    public static long GetLastAdded() {
        return lastAdded;
    }

    public static boolean GetIsSearched() {
        return isSearched;
    }

    public static long Count() {
        if(count == -1L){
            QueryOptions options = QueryOptions.newBuilder().setNumberFoundAccuracy(25000).build();
            Query query = Query.newBuilder().setOptions(options).build("");
            count = INDEX.search(query).getNumberFound();
        }
        return count;
    }

    public static synchronized PutResponse Add(String id) {
        count = -1L;
        lastAdded = DateTime.GetCurrentTime();
        PutResponse putResponse = INDEX.put(Document.newBuilder()
                .setId(id)
                .addField(Field.newBuilder().setName("dummy").setNumber(0))
                .build());
        if(putResponse.getResults().get(0).getCode() == StatusCode.OK) isSearched = false;

        return putResponse;
    }

    public static synchronized void Delete(String id) {
        count = -1L;
        INDEX.delete(id);
    }

    public static List<model.MinimumAmount> Search(int offset, int limit) {
        List<model.MinimumAmount> list = new LinkedList<>();

        QueryOptions options = QueryOptions.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .build();

        Query query = Query.newBuilder().setOptions(options).build("");

        for(ScoredDocument doc : INDEX.search(query)) {
            try {
                String id = doc.getId();
                model.Product product = Product.Get(id);
                list.add(new model.MinimumAmount(id, product.getName(), product.getAmount(), product.getMinimumStock()));
            }
            catch (Exception e) {
                continue;
            }
        }

        isSearched = true;
        return list;
    }
}
