package xue.apps.chat.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNTS")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private long id;
	
	@Column(name = "USERNAME", nullable = false)
	private String userName;
	@Column(name = "PASSWORD", nullable = false)
	private String password;
	@Column(name = "DISPNAME", nullable = false)
	private String dispName;
	@Column(name = "PORTRAIT_URL", nullable = false)
	private String portraitUrl;
	
	protected Account() {}
	
	/*public Account(long userId, String name, String pwd, String dispName, String portrait){
		this.id = userId;
		this.userName = name;
		this.password = pwd;
		this.dispName = dispName;
		this.portraitUrl = portrait;
	}*/
	
	public Account(String name, String pwd, String email, String portrait){
		//this.userId = userId;
		this.userName = email;
		this.password = pwd;
		this.dispName = name;
		this.portraitUrl = portrait;
	}

	public long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDispName() {
		return dispName;
	}

	public void setDispName(String dispName) {
		this.dispName = dispName;
	}

	public String getPortraitUrl() {
		return portraitUrl;
	}

	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}
}
