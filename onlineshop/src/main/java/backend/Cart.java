package backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.appengine.api.search.*;
import com.google.cloud.storage.Blob;
import model.CartOut;
import model.ProductCartIn;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Cart {
    private static final String INDEX_NAME = "Cart";
    private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
    private static volatile long countPayment = -1L;
    private static volatile long countUncomfirmed = -1L;
    private static volatile long countComfirmed = -1L;
    private static volatile long lastPayment = 0;
    private static volatile long lastUnconfirmed = 0;
    private static volatile long lastConfirmed = 0;
    private static volatile long lastUpdate = 0;
    private static volatile boolean isSearched = true;

    public static boolean GetIsSearched() {
        return isSearched;
    }

    public static Document GetDocument(String id) {
        return INDEX.get(id);
    }

    public static model.CartDetail Get(String id) {
        return Get(id, INDEX.get(id));
    }

    public static model.CartDetail Get(String id, Document doc) {
        if(doc == null) return null;
        String billImgName = doc.getOnlyField("billImgName").getAtom();
        return new model.CartDetail(
                id,
                doc.getOnlyField("accountId").getAtom(),
                doc.getOnlyField("email").getAtom(),
                doc.getOnlyField("address").getText(),
                doc.getOnlyField("totalPrice").getNumber(),
                ProductCart.GetByCartId(id),
                doc.getOnlyField("confirm").getNumber(),
                billImgName.equals("") ? "" : FileStorage.SignUrl("cart/billImg/" + billImgName),
                doc.getOnlyField("date").getDate().getTime());
    }

    public static long GetLastUpdate() {
        return lastUpdate;
    }

    public static long GetLastPayment() {
        return lastPayment;
    }

    public static long GetLastUnconfirmed() {
        return lastUnconfirmed;
    }

    public static long GetLastConfirmed() {
        return lastConfirmed;
    }

    public static long CountPayment() {
        if(countPayment == -1L) countPayment = Count("confirm: 0 AND billImgName: \"\"");
        return countPayment;
    }

    public static long CountUnconfirmed() {
        if(countUncomfirmed == -1L) countUncomfirmed = Count("confirm: 0 AND NOT billImgName: \"\"");
        return countUncomfirmed;
    }

    public static long CountConfirmed() {
        if(countComfirmed == -1L) countComfirmed = Count("confirm: 1");
        return countComfirmed;
    }

    public static long CountPaymentFromUser(String id) {
        if(countPayment == -1L) countPayment = Count("accountId: " + id + " AND confirm: 0 AND billImgName: \"\"");
        return countPayment;
    }

    public static long CountUnconfirmedFromUser(String id) {
        if(countUncomfirmed == -1L) countUncomfirmed = Count("accountId: " + id + " AND confirm: 0 AND NOT billImgName: \"\"");
        return countUncomfirmed;
    }

    public static long CountConfirmedFromUser(String id) {
        if(countComfirmed == -1L) countComfirmed = Count("accountId: " + id + " AND confirm: 1");
        return countComfirmed;
    }

    public static long Count(String queryString) {
        QueryOptions options = QueryOptions.newBuilder().setNumberFoundAccuracy(25000).build();
        Query query = Query.newBuilder().setOptions(options).build(queryString);
        return INDEX.search(query).getNumberFound();
    }

    public static synchronized boolean Add(model.CartIn cartIn, String accountId, String email) {
        AtomicBoolean editReserved = Product.GetEditReserved();
        if(editReserved.getAndSet(true)) return false;
        if (cartIn.getProducts() == null) {
            editReserved.set(false);
            return false;
        }

        List<ProductCartIn> productCartIns = cartIn.getProducts();
        int length = productCartIns.size();
        double prices[] = new double[length];
        Document docs[] = new Document[length];
        double totalPrice = 0;

        int j = 0;
        for (model.ProductCartIn productCartIn : productCartIns) {
            try {
                final int index = j++;
                String id = productCartIn.getProductId();
                Document doc = Product.GetDocument(id);
                double amount = doc.getOnlyField("amount").getNumber();
                double sold = doc.getOnlyField("sold").getNumber();
                double buyAmount = productCartIn.getAmount();
                if(amount < buyAmount) {
                    editReserved.set(false);
                    return false;
                }
                docs[index] = Product.GetDocumentChangeAmount(id, doc, amount - buyAmount, sold + buyAmount);
                double price = doc.getOnlyField("price").getNumber();
                prices[index] = price;
                totalPrice += price * productCartIn.getAmount();
            }
            catch (Exception e) {
                editReserved.set(false);
                System.out.println(e.toString());
                return false;
            }
        }

        if(Math.abs(totalPrice - cartIn.getTotalPrice()) > 0.00001) {
            editReserved.set(false);
            return false;
        }

        PutResponse putResponse = INDEX.put(Document.newBuilder()
                .addField(Field.newBuilder().setName("accountId").setAtom(accountId))
                .addField(Field.newBuilder().setName("email").setAtom(email))
                .addField(Field.newBuilder().setName("address").setText(cartIn.getAddress()))
                .addField(Field.newBuilder().setName("totalPrice").setNumber(totalPrice))
                .addField(Field.newBuilder().setName("billImgName").setAtom(""))
                .addField(Field.newBuilder().setName("date").setDate(new Date(backend.DateTime.GetCurrentTime())))
                .addField(Field.newBuilder().setName("confirm").setNumber(0))
                        .build());

        if(putResponse.getResults().get(0).getCode() != StatusCode.OK) {
            editReserved.set(false);
            return false;
        }

        lastUpdate = DateTime.GetCurrentTime();
        isSearched = false;
        countPayment = -1L;
        lastPayment  = lastUpdate;

        String cardId = putResponse.getIds().get(0);

        for(int i = 0; i < length; i++) {
            final int index = i;
            model.ProductCartIn productCartIn = productCartIns.get(index);
            ProductCart.Add(cardId, productCartIn.getProductId(), productCartIn.getAmount(), prices[index]);
        }
        Product.PutDocuments(docs);

        editReserved.set(false);
        return true;
    }

    public static List<model.CartOut> Search(JsonNode json) {
        return Search(
                json.has("queryString") ? json.get("queryString").asText() : "",
                json.has("offset") ? json.get("offset").asInt(0) : 0,
                json.has("limit") ? json.get("limit").asInt(1000) : 1000);
    }

    public static List<model.CartOut> Search(String queryString, int offset, int limit) {
        List<model.CartOut> list = new LinkedList<>();

        QueryOptions options = QueryOptions.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setFieldsToReturn("accountId", "email", "address", "totalPrice", "billImgName", "date")
                .build();

        Query query = Query.newBuilder().setOptions(options).build(queryString);

        for(ScoredDocument doc : INDEX.search(query)) {
            String billImgName = doc.getOnlyField("billImgName").getAtom();
            try {
                list.add(new model.CartOut(
                        doc.getId(),
                        doc.getOnlyField("accountId").getAtom(),
                        doc.getOnlyField("email").getAtom(),
                        doc.getOnlyField("totalPrice").getNumber(),
                        billImgName.equals("") ? "" : FileStorage.SignUrl("cart/billImg/" + billImgName),
                        doc.getOnlyField("date").getDate().getTime()
                ));
            }
            catch (Exception e) {
                continue;
            }
        }

        return list;
    }

    public static void Delete(String id) {

    }

    public static void Delete(String id, boolean isConfirm, List<model.ProductCartOut> productCartOuts, String billImgName) {
        for(model.ProductCartOut products : productCartOuts);
        boolean hasBill = billImgName.equals("");
        if(!hasBill) DeleteBillImg(billImgName);

        if(!isConfirm) {
            countComfirmed = -1L;
            lastConfirmed = DateTime.GetCurrentTime();
        }
        else if(hasBill)  {
            countPayment = -1L;
            lastPayment  = DateTime.GetCurrentTime();
        }
        else {
            countUncomfirmed = -1L;
            lastUnconfirmed = DateTime.GetCurrentTime();
        }

        INDEX.delete(id);
    }

    public static void DeleteBillImg(String imgName) {
        FileStorage.Delete("cart/billImg/" + imgName);
    }

    public static Blob UploadBillImg(String imgName, byte[] img, String imgContentType) {
        return FileStorage.Store("cart/billImg/" + imgName, img, imgContentType);
    }

    public static synchronized Blob UpdateBillImgFromId(String id, String accountId, Document doc, byte[] img, String imgContentType) {
        double confirm = doc.getOnlyField("confirm").getNumber();
        if(Math.abs(confirm) > 0.5) return null;
        String imgName = doc.getOnlyField("billImgName").getAtom();
        if(imgName.equals("")) {
            imgName = "img-" + UUID.randomUUID().toString();
            PutResponse putResponse = INDEX.put(Document.newBuilder()
                    .setId(id)
                    .addField(Field.newBuilder().setName("accountId").setAtom(accountId))
                    .addField(Field.newBuilder().setName("email").setAtom(doc.getOnlyField("email").getAtom()))
                    .addField(Field.newBuilder().setName("address").setText(doc.getOnlyField("address").getText()))
                    .addField(Field.newBuilder().setName("totalPrice").setNumber(doc.getOnlyField("totalPrice").getNumber()))
                    .addField(Field.newBuilder().setName("billImgName").setAtom(imgName))
                    .addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
                    .addField(Field.newBuilder().setName("confirm").setNumber(confirm))
                    .build());

            if(putResponse.getResults().get(0).getCode() == StatusCode.OK) {
                if(Math.abs(confirm) < 0.5) {
                    lastUpdate = DateTime.GetCurrentTime();
                    isSearched = false;

                    countPayment = -1L;
                    lastPayment  = lastUpdate;

                    countUncomfirmed = -1L;
                    lastUnconfirmed = lastUpdate;
                }

                return UploadBillImg(imgName, img, imgContentType);
            }
            else return null;
        }

        return UploadBillImg(imgName, img, imgContentType);
    }

    private  static synchronized PutResponse SetComfirmFromDocument(String id, Document doc, double value) {
        return INDEX.put(Document.newBuilder()
                .setId(doc.getId())
                .addField(Field.newBuilder().setName("accountId").setAtom(doc.getOnlyField("accountId").getAtom()))
                .addField(Field.newBuilder().setName("email").setAtom(doc.getOnlyField("email").getAtom()))
                .addField(Field.newBuilder().setName("address").setText(doc.getOnlyField("address").getText()))
                .addField(Field.newBuilder().setName("totalPrice").setNumber(doc.getOnlyField("totalPrice").getNumber()))
                .addField(Field.newBuilder().setName("billImgName").setAtom(doc.getOnlyField("billImgName").getAtom()))
                .addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
                .addField(Field.newBuilder().setName("confirm").setNumber(value))
                .build());
    }

    public static synchronized PutResponse SetComfirm(String id, Document doc, boolean value) {
        boolean confirm = Math.abs(doc.getOnlyField("confirm").getNumber()) > 0.5;
        if(confirm == value) return null;

        countComfirmed = -1L;
        lastConfirmed = DateTime.GetCurrentTime();

        if(doc.getOnlyField("billImgName").getAtom().equals("")) {
            countPayment = -1L;
            lastPayment  = lastConfirmed;
        }
        else {
            countUncomfirmed = -1L;
            lastUnconfirmed = lastConfirmed;
        }

        return SetComfirmFromDocument(id, doc, value ? 1 : 0);
    }

    public static synchronized void Cancel(String id, Document doc, boolean isConfirm) {
        List<model.ProductCartOut> productCartOuts =  ProductCart.GetByCartId(id);
        for(model.ProductCartOut product : productCartOuts) {
            try {
                Product.AddAmountSubtractSold(product.getProductId(), product.getAmount());
            }
            catch (Exception e) {
                continue;
            }
        }

        String billImgName = doc.getOnlyField("billImgName").getAtom();
        if(!isConfirm) {
            countComfirmed = -1L;
            lastConfirmed = DateTime.GetCurrentTime();
        }
        else if(doc.getOnlyField("billImgName").getAtom().equals(""))  {
            countPayment = -1L;
            lastPayment  = DateTime.GetCurrentTime();
        }
        else {
            countUncomfirmed = -1L;
            lastUnconfirmed = DateTime.GetCurrentTime();
        }
        Delete(id, isConfirm, productCartOuts, billImgName);
    }
}
