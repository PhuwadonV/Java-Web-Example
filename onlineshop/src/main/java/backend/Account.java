package backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.appengine.api.search.*;

@SuppressWarnings("unchecked")
public class Account {
    private static final javax.cache.Cache CACHE = backend.Cache.Create();
    private static final String INDEX_NAME = "Account";
    private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());

    public static PutResponse Put(JsonNode json) {
        model.Account account = Get();
        String id =  account.getId();
        CACHE.remove(id);
        return INDEX.put(Document.newBuilder()
                .setId(id)
                .addField(Field.newBuilder().setName("name").setAtom(json.has("name") ? json.get("name").asText() : account.getName()))
                .addField(Field.newBuilder().setName("phoneNumber").setAtom(json.has("phoneNumber") ? json.get("phoneNumber").asText() : account.getPhoneNumber()))
                .addField(Field.newBuilder().setName("address").setText(json.has("address") ? json.get("address").asText() : account.getAddress()))
                .build());
    }

    public static model.Account Get()
    {
        String id = Auth.GetID();
        model.Account account = (model.Account) CACHE.get(id);
        if(account != null) return account;
        Document doc = INDEX.get(id);
        if(doc != null) {
            account = new model.Account(
                    id,
                    doc.getOnlyField("name").getAtom(),
                    doc.getOnlyField("phoneNumber").getAtom(),
                    doc.getOnlyField("address").getText());
            CACHE.put(id, account);
        }
        return account != null ? account : new model.Account(id, "", "", "");
    }

    public static model.Account Get(String id)
    {
        Document doc = INDEX.get(id);
        return doc == null ? null : new model.Account(
                id,
                doc.getOnlyField("name").getAtom(),
                doc.getOnlyField("phoneNumber").getAtom(),
                doc.getOnlyField("address").getText());
    }
}