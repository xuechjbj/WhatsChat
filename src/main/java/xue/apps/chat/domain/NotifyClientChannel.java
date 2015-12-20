package xue.apps.chat.domain;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class NotifyClientChannel {

	public final String mSessionId;
	public final SseEmitter mListeningPort;
	
	public NotifyClientChannel(String sid, SseEmitter emitter){
		mSessionId = sid;
		mListeningPort = emitter;
	}
}
