
MERGE INTO ACCOUNTS
  USING (VALUES(1, 'admin', '123', 'Admin', '/img/person1.png'),
               (2, 'bill', '123', 'Bill', '/img/person2.png')) 
         AS vals(id, username, passwd, dispname, url) ON (ACCOUNTS.USERNAME=vals.username)
  WHEN NOT MATCHED THEN INSERT VALUES vals.id, vals.username, vals.passwd, vals.dispname, vals.url;
  
MERGE INTO SESSIONS
  USING (VALUES(1, 1, 1449368922594, 1),
               (2, 1, 1449368922594, 2)) 
         AS vals(id, chatId, createTime, peerId) ON (SESSIONS.CHAT_ID=vals.chatId)
  WHEN NOT MATCHED THEN INSERT VALUES vals.id, vals.chatId, vals.createTime, vals.peerId;
  
MERGE INTO MESSAGES
  USING (VALUES(1, 1, 1, 'Hello! How is going', 1449368926594),
               (2, 1, 2, 'Yes! How is going!!', 1449368929594)) 
         AS vals(id, chatId, speakerId, msg, createTime) ON (MESSAGES.CHAT_ID=vals.chatId)
  WHEN NOT MATCHED THEN INSERT VALUES vals.id, vals.chatId, vals.speakerId, vals.msg, vals.createTime;
