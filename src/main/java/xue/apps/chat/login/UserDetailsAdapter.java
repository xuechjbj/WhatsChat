package xue.apps.chat.login;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsAdapter implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mPasswd;
	private String mUsername;
	private String mDispname;
	private long mUid;
	
	public UserDetailsAdapter(String username, String passwd, String dispname, long uid){
		mUsername = username;
		mPasswd = passwd;
		mUid = uid;
		mDispname = dispname;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		HashSet<SimpleGrantedAuthority> authorities =
		        new HashSet<SimpleGrantedAuthority>();
		
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return authorities;
	}

	public void setPassword(String pwd){
		mPasswd = pwd;
	}
	@Override
	public String getPassword() {
		return mPasswd;
	}

	@Override
	public String getUsername() {
		return mUsername;
	}

	public long getUid(){
		return mUid;
	}
	
	public String getDispname(){
		return mDispname;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {

		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
