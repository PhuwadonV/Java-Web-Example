package backend;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.appengine.api.search.*;
import com.google.appengine.api.search.checkers.SearchApiLimits;
import com.google.cloud.storage.Blob;

public class Product {
	private static final String INDEX_NAME = "Product";
	private static final Index INDEX = SearchServiceFactory.getSearchService().getIndex(IndexSpec.newBuilder().setName(INDEX_NAME).build());
	private static volatile long count = -1L;
	private static volatile long lastUpdate = 0;
	private static AtomicBoolean editReserved = new AtomicBoolean(false);

	public static void PutDocuments(Document[] docs) {
		INDEX.put(docs);
	}

	public static AtomicBoolean GetEditReserved() {
		return editReserved;
	}

	public static long GetLastUpdate() {
		return lastUpdate;
	}

	public static Document GetDocument(String id) {
		return INDEX.get(id);
	}

	public static model.Product Get(Document doc) {
		String imgName = doc.getOnlyField("imgName").getAtom();
		return new model.Product(
				doc.getId(),
				doc.getOnlyField("name").getText(),
				doc.getOnlyField("tags").getText(),
				doc.getOnlyField("description").getText(),
				imgName.equals("") ? "" : FileStorage.SignUrl("product/img/" + doc.getOnlyField("imgName").getAtom()),
				doc.getOnlyField("price").getNumber(),
				doc.getOnlyField("amount").getNumber(),
				doc.getOnlyField("minimumStock").getNumber(),
				doc.getOnlyField("sold").getNumber(),
				doc.getOnlyField("date").getDate().getTime(),
				doc.getOnlyField("updatedTime").getDate().getTime());
	}

	public static model.Product Get(String id) {
		Document doc = INDEX.get(id);
		return doc == null ? null : Get(doc);
	}

	public static synchronized String Add(String name, String tags, String description, byte[] img, String imgContentType, double price, double amount, double minimumStock, double sold, long inputDate) {
		String imgName = img.length == 0 ? "" : "img-" + UUID.randomUUID().toString();
		long currTime = DateTime.GetCurrentTime();
		long time = DateTime.GetDayTime(currTime);
		Date date = new Date(inputDate == 0 ? currTime : (inputDate - DateTime.GetDayTime(inputDate)) + time);

		PutResponse putResponse = INDEX.put(Document.newBuilder()
				.addField(Field.newBuilder().setName("name").setText(name))
				.addField(Field.newBuilder().setName("tags").setText(tags))
				.addField(Field.newBuilder().setName("description").setText(description))
				.addField(Field.newBuilder().setName("imgName").setAtom(imgName))
				.addField(Field.newBuilder().setName("price").setNumber(price))
				.addField(Field.newBuilder().setName("amount").setNumber(amount))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(minimumStock))
				.addField(Field.newBuilder().setName("sold").setNumber(sold))
				.addField(Field.newBuilder().setName("date").setDate(date))
				.addField(Field.newBuilder().setName("time").setNumber(time))
				.addField(Field.newBuilder().setName("updatedTime").setDate(new Date(currTime)))
					.build());

		if(putResponse.getResults().get(0).getCode() == StatusCode.OK) {
			count = -1L;
			lastUpdate = currTime;
			if (img.length != 0) FileStorage.StoreAsyn("product/img/" + imgName, img, imgContentType);
			return putResponse.getIds().get(0);
		}
		else return "";
	}

	public static synchronized long Update(String id, String name, String tags, String description, byte[] img, String imgContentType, boolean isNewImg, double price, double amount, double minimumStock, double sold, boolean isNewDate, long date, long updatedTime) {
		if(editReserved.getAndSet(true)) return -1;
		Document doc = INDEX.get(id);
		if(doc == null || doc.getOnlyField("updatedTime").getDate().getTime() != updatedTime) {
			editReserved.set(false);
			return -1;
		}
		long time = DateTime.GetCurrentTime();
		String imgName = doc.getOnlyField("imgName").getAtom();
		if(img.length == 0) {
			if(isNewImg && !imgName.equals("")) {
				DeleteImg(imgName);
				imgName = "";
			}
		}
		else if(imgName.equals("")) imgName = "img-" + UUID.randomUUID().toString();

		long dateBuff = isNewDate ? (date - DateTime.GetDayTime(date)) + DateTime.GetDayTime(DateTime.GetCurrentTime()) : doc.getOnlyField("date").getDate().getTime();

		PutResponse putResponse = INDEX.put(Document.newBuilder()
				.setId(id)
				.addField(Field.newBuilder().setName("name").setText(name))
				.addField(Field.newBuilder().setName("tags").setText(tags))
				.addField(Field.newBuilder().setName("description").setText(description))
				.addField(Field.newBuilder().setName("imgName").setAtom(imgName))
				.addField(Field.newBuilder().setName("price").setNumber(price))
				.addField(Field.newBuilder().setName("amount").setNumber(amount))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(minimumStock))
				.addField(Field.newBuilder().setName("sold").setNumber(sold))
				.addField(Field.newBuilder().setName("date").setDate(new Date(dateBuff)))
				.addField(Field.newBuilder().setName("time").setNumber(DateTime.GetDayTime(dateBuff)))
				.addField(Field.newBuilder().setName("updatedTime").setDate(new Date(time)))
					.build());

		if(putResponse.getResults().get(0).getCode() == StatusCode.OK) {
			count = -1L;
			lastUpdate = time;
			if (img.length != 0) FileStorage.StoreAsyn("product/img/" + imgName, img, imgContentType);
			if(amount >= minimumStock) MinimumStock.Delete(id);
			editReserved.set(false);
			return time;
		}
		else {
			editReserved.set(false);
			return -1;
		}
	}

	public static synchronized void Delete(String id) {
		Document doc = INDEX.get(id);
		if(doc == null) return;
		MinimumStock.Delete(id);
		id = doc.getId();
		DeleteImgFromDocument(doc);
		INDEX.delete(id);
		count = -1L;
		lastUpdate = DateTime.GetCurrentTime();
	}

	public static Blob UpdateImg(String imgName, byte[] img, String imgContentType) {
		return FileStorage.Store("product/img/" + imgName, img, imgContentType);
	}

	public static Future<Blob> UpdateImgAsyn(String imgName, byte[] img, String imgContentType) {
		return FileStorage.StoreAsyn("product/img/" + imgName, img, imgContentType);
	}

	public static synchronized Future<Blob> UpdateImgFromId(String id, byte[] img, String imgContentType) {
		Document doc = INDEX.get(id);
		String imgName = doc.getOnlyField("imgName").getAtom();
		if(imgName.equals("")) {
			imgName = "img-" + UUID.randomUUID().toString();
			PutResponse putResponse = INDEX.put(Document.newBuilder()
					.setId(id)
					.addField(Field.newBuilder().setName("name").setText(doc.getOnlyField("name").getText()))
					.addField(Field.newBuilder().setName("tags").setText(doc.getOnlyField("tags").getText()))
					.addField(Field.newBuilder().setName("description").setText(doc.getOnlyField("description").getText()))
					.addField(Field.newBuilder().setName("imgName").setAtom(doc.getOnlyField(imgName).getAtom()))
					.addField(Field.newBuilder().setName("price").setNumber(doc.getOnlyField("price").getNumber()))
					.addField(Field.newBuilder().setName("amount").setNumber(doc.getOnlyField("amount").getNumber()))
					.addField(Field.newBuilder().setName("minimumStock").setNumber(doc.getOnlyField("minimumStock").getNumber()))
					.addField(Field.newBuilder().setName("sold").setNumber(doc.getOnlyField("sold").getNumber()))
					.addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
					.addField(Field.newBuilder().setName("time").setNumber(doc.getOnlyField("time").getNumber()))
							.build());

			if(putResponse.getResults().get(0).getCode() == StatusCode.OK) return UpdateImgAsyn(imgName, img, imgContentType);
			else return null;
		}

		return UpdateImgAsyn(imgName, img, imgContentType);
	}

	public static void DeleteImg(String imgName) {
		FileStorage.Delete("product/img/" + imgName);
	}

	public static synchronized void DeleteImgFromDocument(Document doc) {
		String imgName = doc.getOnlyField("imgName").getAtom();
		if(imgName.equals("")) return;
		DeleteImg(imgName);
		INDEX.put(Document.newBuilder()
				.setId(doc.getId())
				.addField(Field.newBuilder().setName("name").setText(doc.getOnlyField("name").getText()))
				.addField(Field.newBuilder().setName("tags").setText(doc.getOnlyField("tags").getText()))
				.addField(Field.newBuilder().setName("description").setText(doc.getOnlyField("description").getText()))
				.addField(Field.newBuilder().setName("imgName").setAtom(""))
				.addField(Field.newBuilder().setName("price").setNumber(doc.getOnlyField("price").getNumber()))
				.addField(Field.newBuilder().setName("amount").setNumber(doc.getOnlyField("amount").getNumber()))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(doc.getOnlyField("minimumStock").getNumber()))
				.addField(Field.newBuilder().setName("sold").setNumber(doc.getOnlyField("sold").getNumber()))
				.addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
				.addField(Field.newBuilder().setName("time").setNumber(doc.getOnlyField("time").getNumber()))
						.build());
	}

	public static synchronized void DeleteImgFromId(String id) {
		Document doc = INDEX.get(id);
		if(doc != null)	DeleteImgFromDocument(doc);
	}

	public static List<model.ProductSearch> GetNewest(int offset, int limit) {
		return Search("", offset, limit);
	}

    public static List<model.ProductSearch> GetNewestInStock(int offset, int limit) {
        return Search("NOT amount: 0", offset, limit);
    }

	public static List<model.Product> GetBestSellerAllTime(int offset, int limit) {
		return SearchBestSellerAllTime(offset, limit, "");
	}

	public static List<model.Product> GetBestSeller(long interval, int offset, int limit) {
		return SearchBestSeller(interval, offset, limit, "");
	}

	public static long Count() {
		if(count == -1L){
			QueryOptions options = QueryOptions.newBuilder().setNumberFoundAccuracy(25000).build();
			Query query = Query.newBuilder().setOptions(options).build("");
			count = INDEX.search(query).getNumberFound();
		}
		return count;
	}

	public static long Count(String queryString) {
		QueryOptions options = QueryOptions.newBuilder().setNumberFoundAccuracy(25000).build();
		Query query = Query.newBuilder().setOptions(options).build(queryString);
		return INDEX.search(query).getNumberFound();
	}

	public static List<model.Product> SearchBestSellerAllTime(int offset, int limit, String tags) {
		return SearchBestSeller(tags.equals("") ? "" : "tags: " + tags, offset,  limit);
	}

	public static List<model.Product> SearchBestSeller(long interval, int offset, int limit, String tags) {
		return SearchBestSeller("sold > " + (DateTime.GetCurrentTime() - interval) + (tags.equals("") ? "" : "tags: " + tags), offset,  limit);
	}

	public static List<model.Product> SearchBestSeller(String queryString, int offset, int limit) {
		List<model.Product> list = new LinkedList<>();
		if(Count() == 0) return list;

		SortOptions sortOptions = SortOptions.newBuilder()
				.addSortExpression(
						SortExpression.newBuilder()
								.setExpression("sold")
								.setDirection(SortExpression.SortDirection.DESCENDING)
								.setDefaultValueNumeric(0))
				.setLimit(1000)
				.build();

		QueryOptions options = QueryOptions.newBuilder()
				.setOffset(offset)
				.setLimit(limit)
				.setFieldsToReturn("name", "tags", "description", "imgName", "price", "amount", "minimumStock", "sold", "date", "time", "updatedTime")
				.setSortOptions(sortOptions)
				.build();

		Query query = Query.newBuilder().setOptions(options).build(queryString);

		for(ScoredDocument doc : INDEX.search(query)) {
			try {
				list.add(Get(doc));
			}
			catch(Exception e) {
				continue;
			}
		}

		return list;
	}

	public static List<model.ProductSearch> Search(String queryString, int offset, int limit) {
		List<model.ProductSearch> list = new LinkedList<>();
		if(Count() == 0) return list;

		SortOptions sortOptions = SortOptions.newBuilder()
				.addSortExpression(
						SortExpression.newBuilder()
								.setExpression("date")
								.setDirection(SortExpression.SortDirection.DESCENDING)
								.setDefaultValueDate(SearchApiLimits.MINIMUM_DATE_VALUE))
				.addSortExpression(
						SortExpression.newBuilder()
								.setExpression("time")
								.setDirection(SortExpression.SortDirection.DESCENDING)
								.setDefaultValueNumeric(0))
				.setLimit(1000)
				.build();

		QueryOptions options = QueryOptions.newBuilder()
				.setOffset(offset)
				.setLimit(limit)
				.setFieldsToReturn("name", "imgName", "price", "amount", "sold", "date", "time")
				.setSortOptions(sortOptions)
				.build();

		Query query = Query.newBuilder().setOptions(options).build(queryString);

		for(ScoredDocument doc : INDEX.search(query)) {
			try {
				String imgName = doc.getOnlyField("imgName").getAtom();
				list.add(new model.ProductSearch(
						doc.getId(),
						doc.getOnlyField("name").getText(),
						imgName.equals("") ? "" : FileStorage.SignUrl("product/img/" + doc.getOnlyField("imgName").getAtom()),
						doc.getOnlyField("price").getNumber(),
						doc.getOnlyField("amount").getNumber(),
						doc.getOnlyField("sold").getNumber(),
						doc.getOnlyField("date").getDate().getTime()));
			}
			catch (Exception e) {
				continue;
			}
		}

		return list;
	}

	public static List<model.ProductSearch> Search(JsonNode json) {
		return Search(
				json.has("queryString") ? json.get("queryString").asText() : "",
				json.has("offset") ? json.get("offset").asInt(0) : 0,
				json.has("limit") ? json.get("limit").asInt(1000) : 1000);
	}

	public static synchronized int AddAmount(String id, double amount) {
		if(editReserved.getAndSet(true)) return 1;
		Document doc = INDEX.get(id);
		double oldAmount = doc.getOnlyField("amount").getNumber();
		double result = oldAmount + amount;
		if(result < 0) {
			editReserved.set(false);
			return 1;
		}
		else if(SetAmount(id, doc, result).getResults().get(0).getCode() == StatusCode.OK) {
			if(result >= doc.getOnlyField("minimumStock").getNumber()) MinimumStock.Delete(id);
			editReserved.set(false);
			return 0;
		}
		else {
			editReserved.set(false);
			return -1;
		}
	}

	public static synchronized PutResponse SetAmount(String id, Document doc, double amount) {
		return INDEX.put(Document.newBuilder()
				.setId(id)
				.addField(Field.newBuilder().setName("name").setText(doc.getOnlyField("name").getText()))
				.addField(Field.newBuilder().setName("tags").setText(doc.getOnlyField("tags").getText()))
				.addField(Field.newBuilder().setName("description").setText(doc.getOnlyField("description").getText()))
				.addField(Field.newBuilder().setName("imgName").setAtom(doc.getOnlyField("imgName").getAtom()))
				.addField(Field.newBuilder().setName("price").setNumber(doc.getOnlyField("price").getNumber()))
				.addField(Field.newBuilder().setName("amount").setNumber(amount))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(doc.getOnlyField("minimumStock").getNumber()))
				.addField(Field.newBuilder().setName("sold").setNumber(doc.getOnlyField("sold").getNumber()))
				.addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
				.addField(Field.newBuilder().setName("time").setNumber(doc.getOnlyField("time").getNumber()))
				.addField(Field.newBuilder().setName("updatedTime").setDate(new Date( DateTime.GetCurrentTime())))
				.build());
	}

	public static synchronized int AddAmountSubtractSold(String id, double value) {
		if(editReserved.getAndSet(true)) return 1;
		Document doc = INDEX.get(id);
		double oldAmount = doc.getOnlyField("amount").getNumber();
		double oldSold = doc.getOnlyField("sold").getNumber();

		if(SetAmountAndSold(id, doc, oldAmount + value, oldSold - value).getResults().get(0).getCode() == StatusCode.OK) {
			editReserved.set(false);
			return 0;
		}
		else {
			editReserved.set(false);
			return -1;
		}
	}

	private static PutResponse SetAmountAndSold(String id, Document doc, double amount, double sold) {
		double minimumStock = doc.getOnlyField("minimumStock").getNumber();
		if(amount >= minimumStock) MinimumStock.Delete(id);
		return INDEX.put(Document.newBuilder()
				.setId(id)
				.addField(Field.newBuilder().setName("name").setText(doc.getOnlyField("name").getText()))
				.addField(Field.newBuilder().setName("tags").setText(doc.getOnlyField("tags").getText()))
				.addField(Field.newBuilder().setName("description").setText(doc.getOnlyField("description").getText()))
				.addField(Field.newBuilder().setName("imgName").setAtom(doc.getOnlyField("imgName").getAtom()))
				.addField(Field.newBuilder().setName("price").setNumber(doc.getOnlyField("price").getNumber()))
				.addField(Field.newBuilder().setName("amount").setNumber(amount < 0 ? 0 : amount))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(minimumStock))
				.addField(Field.newBuilder().setName("sold").setNumber(sold < 0 ? 0 : sold))
				.addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
				.addField(Field.newBuilder().setName("time").setNumber(doc.getOnlyField("time").getNumber()))
				.addField(Field.newBuilder().setName("updatedTime").setDate(new Date( DateTime.GetCurrentTime())))
				.build());
	}

	public static synchronized Document GetDocumentChangeAmount(String id, Document doc, double amount, double sold) {
		double minimumStock = doc.getOnlyField("minimumStock").getNumber();
		if(amount < minimumStock) MinimumStock.Add(id);
		return Document.newBuilder()
				.setId(id)
				.addField(Field.newBuilder().setName("name").setText(doc.getOnlyField("name").getText()))
				.addField(Field.newBuilder().setName("tags").setText(doc.getOnlyField("tags").getText()))
				.addField(Field.newBuilder().setName("description").setText(doc.getOnlyField("description").getText()))
				.addField(Field.newBuilder().setName("imgName").setAtom(doc.getOnlyField("imgName").getAtom()))
				.addField(Field.newBuilder().setName("price").setNumber(doc.getOnlyField("price").getNumber()))
				.addField(Field.newBuilder().setName("amount").setNumber(amount))
				.addField(Field.newBuilder().setName("minimumStock").setNumber(minimumStock))
				.addField(Field.newBuilder().setName("sold").setNumber(sold))
				.addField(Field.newBuilder().setName("date").setDate(doc.getOnlyField("date").getDate()))
				.addField(Field.newBuilder().setName("time").setNumber(doc.getOnlyField("time").getNumber()))
				.addField(Field.newBuilder().setName("updatedTime").setDate(new Date( DateTime.GetCurrentTime())))
				.build();
	}
}