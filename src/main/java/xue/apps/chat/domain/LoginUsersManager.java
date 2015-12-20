package xue.apps.chat.domain;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginUsersManager {

	private static final Logger logger = LoggerFactory.getLogger(LoginUsersManager.class);

	private static ArrayList<LoginUser> mUsers = new ArrayList<LoginUser>();

	public static void addLoginUser(LoginUser u) {
		mUsers.add(u);
	}

	public static LoginUser getLoginByUserId(long uid) {

		Optional<LoginUser> login = mUsers.stream().filter(u -> u.getId() == uid).findFirst();

		if (login.isPresent())
			return login.get();

		return null;
	}

	public static void removeLoginuser(long uid) {
		logger.info("removeLoginuser");
		// ArrayList<LoginUser> removedList = new ArrayList<LoginUser>();

		mUsers.removeAll(mUsers.stream().filter(u -> u.getId() == uid).collect(Collectors.toList()));
		/*
		 * for (int i = 0; i < mUsers.size(); ++i) { if (mUsers.get(i).getId()
		 * == uid) { removedList.add(mUsers.get(i)); } }
		 */

		// mUsers.removeAll(removedList);
		logger.info("XUE: The count of the login users is " + mUsers.size() + " now!");
	}

	/*
	 * public static void removeSession(String sid) {
	 * logger.info("removeSession");
	 * 
	 * ArrayList<LoginUser> removeList = new ArrayList<LoginUser>();
	 * 
	 * for (int i = 0; i < mUsers.size(); ++i) { if
	 * (mUsers.get(i).checkSessionId(sid)) {
	 * 
	 * logger.info("XUE: remove one session for " + mUsers.get(i).getName());
	 * mUsers.get(i).removeSession(sid);
	 * 
	 * if (mUsers.get(i).getSessionCount() == 0) {
	 * removeList.add(mUsers.get(i)); logger.info("XUE: No session is in " +
	 * mUsers.get(i).getName() + "! remove the user"); } } }
	 * 
	 * mUsers.removeAll(removeList);
	 * 
	 * logger.info("XUE: The count of the login users is " + mUsers.size()); }
	 */

	public static LoginUser getLoginUserBySid(String sid) {

		Optional<LoginUser> login = mUsers.stream().filter(u -> u.checkSessionId(sid)).findFirst();

		if (login.isPresent())
			return login.get();

		return null;
	}
}
