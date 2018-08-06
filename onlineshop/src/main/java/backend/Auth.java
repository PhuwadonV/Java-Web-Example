package backend;

import java.net.*;
import com.google.appengine.api.users.*;

public class Auth {
	private static final UserService USER_SERVICE = GoogleService.USER;
	
    public static boolean IsLoggedIn() {
		return USER_SERVICE.isUserLoggedIn();
    }
	
	public static boolean IsAdmin() {
		return USER_SERVICE.isUserAdmin();
    }
	
	public static User GetUser() {
		return USER_SERVICE.getCurrentUser();
	}
	
	public static String GetID() {
		return GetUser().getUserId();
	}
	
	public static String GetLoginUrl() {
		return USER_SERVICE.createLoginURL("/");
	}
	
	public static String GetLoginUrl(String path) {
		return USER_SERVICE.createLoginURL("/" + path);
	}
	
	public static String GetLogoutUrl() {
		return USER_SERVICE.createLogoutURL("/");
	}
	
	public static String GetLogoutUrl(String path) {
		return USER_SERVICE.createLogoutURL("/" + path);
	}
	
	public static URI GetLoginUri() throws URISyntaxException {
		return new URI(GetLoginUrl());
	}
	
	public static URI GetLoginUri(String path) throws URISyntaxException {
		return new URI(GetLoginUrl(path));
	}
	
	public static URI GetLogoutUri() throws URISyntaxException {
		return new URI(GetLogoutUrl());
	}
	
	public static URI GetLogoutUri(String path) throws URISyntaxException {
		return new URI(GetLogoutUrl(path));
	}

	public static String GetPermission() {
		if(!IsLoggedIn()) return "guest";
		else if(IsAdmin()) return "admin";
		else if(Employee.IsEmployee()) return "employee";
		return "logged-in";
	}

	public static int GetPermissionLevel() {
		if(!IsLoggedIn()) return 0;
		else if(IsAdmin()) return 3;
		else if(Employee.IsEmployee()) return 2;
		return 1;
	}
}