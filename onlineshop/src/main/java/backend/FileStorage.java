package backend;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.google.cloud.storage.*;

public class FileStorage {
	private static final String BLOB_DEV_PATH = GetBolbDevPath();
	private static final String BUCKET_NAME = Resource.Lines("bucket-name.txt").findFirst().get();
	private static final Storage STORAGE = GoogleService.STORAGE;
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

	public static String GetBolbDevPath() {
		try {
			return Resource.Lines("blob-dev-path.txt").findFirst().get().trim();
		}
		catch (Exception e) {
			return "";
		}
	}

	public static final List<Acl> PUBLIC_READ = Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
	
	public static Blob Store(String name, byte[] data, String contentType) {
		return Store(name, data, contentType, PUBLIC_READ);
	}

	public static Blob Store(String name, byte[] data, String contentType, List<Acl> acls) {
		return STORAGE.create(BlobInfo.newBuilder(BUCKET_NAME, BLOB_DEV_PATH + name).setContentType(contentType).setAcl(acls).build(), data);
	}

	public static Future<Blob> StoreAsyn(String name, byte[] data, String contentType) {
		return StoreAsyn(name, data, contentType, PUBLIC_READ);
	}

	public static Future<Blob> StoreAsyn(String name, byte[] data, String contentType, List<Acl> acls) {
		return EXECUTOR_SERVICE.submit(()-> FileStorage.Store(name, data, contentType, acls));
	}
	
	public static boolean Delete(String name) {
		return STORAGE.delete(BlobId.of(BUCKET_NAME, BLOB_DEV_PATH + name));
	}

	public static boolean IsExist(String name) {
		return STORAGE.get(BlobId.of(BUCKET_NAME, BLOB_DEV_PATH + name), Storage.BlobGetOption.fields()).exists();
	}

	public static String SignUrl(String blobName) {
		return STORAGE.signUrl(BlobInfo.newBuilder(BUCKET_NAME, BLOB_DEV_PATH + blobName).build(), 1, TimeUnit.HOURS).toString();
	}
}