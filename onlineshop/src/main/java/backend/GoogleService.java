package backend;

import com.google.appengine.api.users.*;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.*;

import java.util.logging.Logger;

public class GoogleService {
	private static final Logger LOGGER = Logger.getLogger(GoogleService.class.getName());
	public static final UserService USER = UserServiceFactory.getUserService();
	public static final Storage STORAGE = CreateStorage();

	private static Storage CreateStorage() {
		try {
			return StorageOptions.newBuilder().setCredentials(ServiceAccountCredentials.fromStream(Resource.GetAsStream("credential.json"))).build().getService();
		}
		catch(Exception e) {
			LOGGER.severe(e.toString());
			return null;
		}
	}
}