package xue.apps.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ContactPerson {

	private long mPersonId;
	private String mUsername;
	private String mIconUrl;
	private String mExtraInfo;
	
	@JsonIgnore
	private LoginUser mLoginUser;
	
	public ContactPerson(long id, String name, String icon){
		mPersonId = id;
		mUsername = name;
		mIconUrl = icon;
		
		mLoginUser = LoginUsersManager.getLoginByUserId(mPersonId);
	}
	
	public String getName(){
		return mUsername;
	}
	
	@JsonIgnore
	public LoginUser getLoginUser(){
		return mLoginUser;
	}
	
	public void setLoginUser(LoginUser u){
		mLoginUser = u;
	}
	
	public String getIconUrl(){
		return mIconUrl;
	}
	
	public long getPersonId(){
		return mPersonId;
	}
	
	public String getStatus(){
		if(mLoginUser != null)
	        return "online";
		else
			return "offline";
	}
	
	public String getExtraInfo(){
		return mExtraInfo;
	}
	
	public void setExtraInfo(String extraInfo){
		mExtraInfo = extraInfo;
	}
	
}
