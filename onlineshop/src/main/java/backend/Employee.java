package backend;

import java.util.*;
import javax.cache.Cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.appengine.api.search.*;

@SuppressWarnings("unchecked")
public class Employee {
    private static final Cache CACHE = backend.Cache.Create();
    private static final String INDEX_NAME = "Employee";
    private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
    private static volatile long count = -1L;

    public static model.Employee Get(String email) {
        model.Employee employee = (model.Employee) CACHE.get(email);
        if (employee != null) return employee;
        Document doc = INDEX.get(email);
        if (doc != null) {
            employee = new model.Employee(doc.getId(), doc.getOnlyField("name").getText());
            CACHE.put(email, employee);
        }
        return employee;
    }

    public static synchronized PutResponse Put(model.Employee employee) {
        PutResponse putResponse = INDEX.put(Document.newBuilder()
                .setId(employee.getEmail())
                .addField(Field.newBuilder().setName("name").setText(employee.getName()))
                .build());
        if (putResponse.getResults().get(0).getCode() == StatusCode.OK) count = -1L;
        return putResponse;
    }

    public static synchronized void Delete(String email) {
        INDEX.delete(email);
        CACHE.remove(email);
        count = -1L;
    }

    public static synchronized long Count() {
        if (count == -1L) {
            QueryOptions options = QueryOptions.newBuilder().setNumberFoundAccuracy(25000).build();
            Query query = Query.newBuilder().setOptions(options).build("");
            count = INDEX.search(query).getNumberFound();
        }
        return count;
    }

    public static List<model.Employee> Search() {
        return backend.Employee.Search("", 0, 1000);
    }

    public static List<model.Employee> Search(JsonNode json) {
        return backend.Employee.Search(
                json.has("queryString") ? json.get("queryString").asText() : "",
                json.has("offset") ? json.get("offset").asInt(0) : 0,
                json.has("limit") ? json.get("limit").asInt(1000) : 1000);
    }

    public static List<model.Employee> Search(String queryString, int offset, int limit) {
        List<model.Employee> list = new LinkedList<>();
        if(Count() == 0) return list;

        SortOptions sortOptions =
                SortOptions.newBuilder()
                        .addSortExpression(
                                SortExpression.newBuilder()
                                        .setExpression("name")
                                        .setDirection(SortExpression.SortDirection.ASCENDING)
                                        .setDefaultValue(""))
                        .setLimit(1000)
                        .build();

        QueryOptions queryOptions =
                QueryOptions.newBuilder()
                        .setOffset(offset)
                        .setLimit(limit)
                        .setFieldsToReturn("name")
                        .setSortOptions(sortOptions)
                        .build();

        for(Document doc : INDEX.search(Query.newBuilder().setOptions(queryOptions).build(queryString))) {
            try {
                list.add(new model.Employee(doc.getId(), doc.getOnlyField("name").getText()));
            }
            catch (Exception e) {
                continue;
            }
        }

        return list;
    }

    public static boolean IsEmployee() {
        String email = Auth.GetUser().getEmail();
        Boolean isEmployee = (Boolean) CACHE.get(email);
        if(isEmployee != null) return isEmployee;
        isEmployee = false;
        Document doc = INDEX.get(email);
        if(doc != null) {
            CACHE.put(email, true);
            isEmployee = true;
        }
        return isEmployee;
    }
}