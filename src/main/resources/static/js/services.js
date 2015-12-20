angular.module('starter.services', [])

.factory('Chats', function() {
    var RESTServer = '';//http://192.168.0.6:8080';


    var chatId;
    var title;
    var history = [];
    var onNewMsgCallback = null;
    var onSessionChange = null;
    var onNewInvitesComing = null;
    var self = this;
    var invitesData = [];
    var chatSessionDetails = {};
    var xsrfToken = null;


    var authenticated = false;
    var loginid;
    var username;
    var evtSource = null;
    var chatSessions = [];

    var _onopen = function(e) {
        console.log('Contact EventSource Open:' + e.data);
    };
    var _onmessage = function(e) {
        console.log('Contact EventSource recv:' + e.data);
        var obj = JSON.parse(e.data);
        if (obj.type == 2) {
            self.addHistory(obj.message);
            if (onNewMsgCallback != null) {
                onNewMsgCallback(obj.message);
            }
        } else if (obj.type == 1) {
            replace = false;
            for (var i = 0; i < chatSessions.length; i++) {
                if (chatSessions[i].chatId === obj.session.chatId) {
                    chatSessions[i] = obj.session;
                    replace = true;
                    break;
                }
            }
            if (replace == false) {
                chatSessions = chatSessions.concat(obj.session);
            }
            if (onSessionChange != null) {
                onSessionChange();
            }
        } else if (obj.type == 3) {
            replace = false;
            for (var i = 0; i < invitesData.length; i++) {
                if (invitesData[i].inviteId === obj.chatInvite.inviteId) {
                    if(obj.chatInvite.resp == -2){
                        invitesData.splice(i, 1);
                    }
                    else{
                        invitesData[i] = obj.chatInvite;
                    }
                    
                    replace = true;
                    break;
                }
            }
            if (replace == false) {
                invitesData = invitesData.concat(obj.chatInvite);
            }
            
            if (onNewInvitesComing != null)
                onNewInvitesComing();
        }
    };
    var _onerror = function(e) {
        console.log('Contact EventSource Error:' + e.data);
        evtSource.close();
    };

    var clearAll = function() {
        chatId = null;
        title = null;
        history = [];
        //var onNewMsgCallback = null;

        chatSessionDetails = {};

        authenticated = false;
        loginid = null;
        username = null;
        if (evtSource != null) {
            evtSource.close();
            evtSource = null;
        }

        chatSessions = [];
    }

    var self = {
        getLoginURL: function() {
            return RESTServer + '/login';
        },
        getServerURL: function() {
            return RESTServer;
        },
        getLaunchChatURL: function() {
            return RESTServer + '/launchchat';
        },
        needUpdate: function(id) {
            var cs = chatSessionDetails[id];
            if (typeof cs == 'undefined' || cs == null) {
                return true;
            }
            return false;
        },

        enterChatId: function(id) {
            var cs = chatSessionDetails[id];
            if (typeof cs == 'undefined' || cs == null) {
                chatId = id;
            } else {
                chatId = cs.chatId;
                title = cs.title;
                history = cs.history;
                //onNewMsgCallback = cs.onNewMsgCallback;
            }
        },
        getChatId: function() {
            return chatId;
        },
        leaveChatId: function() {
            var cs = {};
            cs.chatId = chatId;
            cs.title = title;
            cs.history = history;
            //cs.onNewMsgCallback = onNewMsgCallback;
            chatSessionDetails[chatId] = cs;
            //chatId = null; Don't clean up chatId in order to reload fron cache
            //onNewMsgCallback = null;
            history = [];
            title = null;
        },

        getTitle: function() {
            return title;
        },
        setTitle: function(t) {
            title = t;
        },
        getChatSessions: function() {
            return chatSessions;
        },
        setChatSessions: function(data) {
            authenticated = true;
            loginid = data.loginid;
            username = data.username;
            chatSessions = data.contacts;
            var listeningUrl = RESTServer + "/listening/" + loginid;
            console.log("Subscrible login user " + loginid + " on " + listeningUrl);
            evtSource = new EventSource(listeningUrl, {
                withCredentials: true
            });
            evtSource.onopen = _onopen;
            evtSource.onmessage = _onmessage;
            evtSource.onerror = _onerror;
        },
        getLoginId: function() {
            return loginid;
        },
        getUsername: function() {
            return username;
        },

        setNewMsgCallback: function(newMsgCB) {
            onNewMsgCallback = newMsgCB;

        },
        setSessionStatusCallback: function(sessionChangeCB) {
            onSessionChange = sessionChangeCB;
        },

        getHistory: function() {
            return history;
        },
        addHistory: function(data) {
            history = history.concat(data);
        },
        setUnAuthenticated: function() {
            authenticated = false;
            clearAll();
        },
        isAuthenticated: function() {
            return authenticated;
        },

        getChatInvites: function() {
            return invitesData;
        },
        setNewInvitesComingCallback: function(cb) {
            onNewInvitesComing = cb;
        },
        getInviteRespURL: function() {
            return RESTServer + '/invitesResp';
        },

        remove: function(chat) {
            chats.splice(chats.indexOf(chat), 1);
        },

        getSendMessageURL: function() {
            return RESTServer + '/postchat';
        },

        getLogoutURL: function() {
            return RESTServer + '/logout';
        },

        getSignupURL: function() {
            return RESTServer + '/signup';
        },

        getNewContactReqURL: function() {
            return RESTServer + '/inviteChat'
        },
        getUpdateChatURL: function() {
            return RESTServer + '/updateReadStatus';
        },
        getRESTServer: function(){
            return RESTServer;
        },
        setXSRFToken: function(xsrf){
            xsrfToken = xsrf;
        },
        getXSRFToken: function(){
            return xsrfToken;
        }

    };

    return self;
});