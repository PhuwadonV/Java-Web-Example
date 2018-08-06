package backend;

import com.google.appengine.api.search.*;

import java.util.LinkedList;
import java.util.List;

public class ProductCart {
    private static final String INDEX_NAME = "ProductCart";
    private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());

    public static PutResponse Add(String cardId, String productId, double amount, double price) {
        return INDEX.put(Document.newBuilder()
                .addField(Field.newBuilder().setName("cardId").setAtom(cardId))
                .addField(Field.newBuilder().setName("productId").setAtom(productId))
                .addField(Field.newBuilder().setName("amount").setNumber(amount))
                .addField(Field.newBuilder().setName("price").setNumber(price))
                .build());
    }

    public static List<model.ProductCartOut> GetByCartId(String id) {
        List<model.ProductCartOut> list = new LinkedList<>();
        for(ScoredDocument doc : INDEX.search("cardId: " + id)) {
            try {
                String productId = doc.getOnlyField("productId").getAtom();
                model.Product product = Product.Get(productId);
                list.add(new model.ProductCartOut(
                        doc.getId(),
                        productId,
                        product == null ? "" : product.getName(),
                        doc.getOnlyField("amount").getNumber(),
                        doc.getOnlyField("price").getNumber()));
            }
            catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    public static void Delete(String id) {
        INDEX.delete(id);
    }
}
