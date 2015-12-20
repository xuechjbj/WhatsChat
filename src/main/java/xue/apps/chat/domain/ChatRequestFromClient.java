package xue.apps.chat.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatRequestFromClient {

	private String mSender;
	private String mPeer;
	private String mSessionId;

	private static final Logger logger = LoggerFactory.getLogger(ChatRequestFromClient.class);

	public ChatRequestFromClient() {
		logger.info("ChatRequestFromClient()" + this);
	}

	public void setSender(String snder) {
		logger.info("setSender " + snder + " at " + this);
		mSender = snder;
	}

	public String getSender() {
		return mSender;
	}

	public void setPeer(String peer) {
		logger.info("setPeer " + peer + " at " + this);
		mPeer = peer;
	}

	public String getPeer() {
		return mPeer;
	}

	public void setChatsessionid(String c) {
		logger.info("setChatsessionid " + c + " at " + this);
		mSessionId = c;
	}

	public String getChatsessionid() {
		return mSessionId;
	}
}
