angular.module('starter.controllers', [])
    .controller('LoginCtrl', function($scope, $http, $ionicPopup, $state, $cookies, Chats) {

        $scope.$on('$ionicView.enter', function(e) {
            console.log("LoginCtrl onEnter Authenticated=" + Chats.isAuthenticated() + ",scope id=" + $scope.$id);
            if (Chats.isAuthenticated()) {
                $http.get(Chats.getLogoutURL());
                Chats.setUnAuthenticated();
            }
        });

        $scope.user = {};
        //$scope.user.email="";
        //$scope.user.passwoard = "";
        var userCancel = false;
        var popupConnectDiag = null;

        $scope.login = function() {
            userCancel = false;

            doLoginByEmail(closePopupDialog);

            popupConnectDiag = $ionicPopup.show({
                title: 'Login ...',
                template: '<div align="center"><i class="center fa fa-spinner fa-spin fa-3x"></i></div>',
                buttons: [{
                    text: 'Cancel'
                }]
            });
            popupConnectDiag.then(function(res) {
                if (res) {
                    console.log('user Cancel it');
                    userCancel = true;
                }
            });
        };

        var closePopupDialog = function() {
            if (popupConnectDiag == null) return;
            popupConnectDiag.close();
            popupConnectDiag = null;
        };

        var doLoginByEmail = function(dismissDiag) {
            var credentials = "username=" + $scope.user.email + "&password=" + $scope.user.password;
            var token = $cookies.getAll(); //get('XSRF-TOKEN');
            $http.post(Chats.getLoginURL(), credentials, {
                    //headers: { 'Content-Type': 'application/json'}
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
                        'Accept': 'application/json'
                    }
                })
                .success(function(data, status, headers, config) {

                    if (userCancel) return;
                    console.log("Received the number of contact=" + data.length);
                    //Chats.setAuthenticated(true);
                    Chats.setChatSessions(data);
                    dismissDiag();
                    $state.go('tab.contacts');
                }).error(function(data) {
                    console.log($cookies.get('JSESSIONID'));
                    dismissDiag();
                    var alertPopup = $ionicPopup.alert({
                        title: 'Login failed!',
                        template: 'Please check your credentials!'
                    });
                });
            //console.log("send out username:password"+credentials);
        };

        $scope.signup = function() {
            userCancel = false;

            doSignup();

            popupConnectDiag = $ionicPopup.show({
                title: 'Sign up ...',
                template: '<div align="center"><i class="center fa fa-spinner fa-spin fa-3x"></i></div>',
                buttons: [{
                    text: 'Cancel'
                }]
            });
            popupConnectDiag.then(function(res) {

                console.log('user Cancel it');
                userCancel = true;

            });
        };

        var doSignup = function() {
            var credentials = "username=" + $scope.user.signup.username +
                "&email=" + $scope.user.signup.email +
                "&password=" + $scope.user.signup.password;

            $http.post(Chats.getSignupURL(), credentials, {
                    //headers: { 'Content-Type': 'application/json'}
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
                        'Accept': 'application/json'
                    }
                })
                .success(function(data, status, headers, config) {
                    afterSignupHandler(data);

                }).error(function(data) {
                    afterSignupHandler(false);

                });
        };

        var afterSignupHandler = function(success) {
            if (!userCancel) {
                closePopupDialog();
            }

            if (success == true) {
                var alertPopup = $ionicPopup.alert({
                    title: 'Signup Successfully!',
                    template: 'You can login now!'
                });
            } else {
                var alertPopup = $ionicPopup.alert({
                    title: 'Signup failed!',
                    template: 'Please change your username and Email!'
                });
            }

        };
    })

.controller('ChatsCtrl', function($scope, $http, $state, $ionicPopup, Chats) {
    // With the new view caching in Ionic, Controllers are only called
    // when they are recreated or on app start, instead of every page change.
    // To listen for when this page is active (for example, to refresh data),
    // listen for the $ionicView.enter event:
    //
    $scope.$on('$ionicView.enter', function(e) {
        //console.log("$ionicView.enter");
        console.log("Chat Contact. enter scope id=" + $scope.$id);
        if (!Chats.isAuthenticated()) {
            $state.go("login");
        }
    });


    $scope.serverUrl = Chats.getServerURL();
    $scope.chatSessions = Chats.getChatSessions();

    var onSessionStatusChange = function() {
        $scope.chatSessions = Chats.getChatSessions();
        $scope.$apply();

        console.log("onSessionStatusChange, scope id=" + $scope.$id);
    };

    Chats.setSessionStatusCallback(onSessionStatusChange);


    var onNewInvitesComing = function() {
        //$scope.chatInvites = Chats.getChatInvites();
        $scope.$apply();
    }
    $scope.hasNewInvites = function() {
        var invts = Chats.getChatInvites();
        if (invts != null && invts.length > 0)
            return true;
        return false;
    };
    Chats.setNewInvitesComingCallback(onNewInvitesComing);

    console.log("load chat contacts:" + $scope.chatSessions.length);
    $scope.remove = function(chat) {
        Chats.remove(chat);
    };
    $scope.launchchat = function(chatid) {
        if (typeof chatid == 'undefined' || chatid == null || chatid == "") {
            console.log("Launch chat which id is null");
            return;
        }
        Chats.enterChatId(chatid);
        $state.go("tab.chat-detail");
    };
    $scope.getStatusClass = function(status) {
        if (status == 'online') {
            return 'fa fa-circle online';
        } else {
            return 'fa fa-circle offline';
        }
    };
    $scope.getBoldClass = function(readDone) {
        if (readDone == 'false') {
            return "bold-text";
        }
        return "";
    };
    $scope.addNewSession = function() {

        $state.go('new-contact');

    };
    $scope.logout = function(chatid) {
        if (Chats.isAuthenticated()) {
            $http.post(Chats.getLogoutURL());
            Chats.setUnAuthenticated();
            $state.go("logout");
        }
    };

    $scope.checkInvites = function() {
        $state.go("new-invites");
    };
})

.controller('NewContactCtrl', function($scope, $http, $state, $ionicPopup, $ionicScrollDelegate, Chats) {
    var userCancel;
    var popupConnectDiag = null;
    $scope.user = {};

    $scope.requestChat = function() {
        console.log("requestChat, scope id=" + $scope.$id);
        userCancel = false;

        doNewContactRequest();

        popupConnectDiag = $ionicPopup.show({
            title: 'Send request ...',
            template: '<div align="center"><i class="center fa fa-spinner fa-spin fa-3x"></i></div>',
            buttons: [{
                text: 'Cancel'
            }]
        });
        popupConnectDiag.then(function(res) {

            console.log('user Cancel it');
            userCancel = true;

        });
    };
    $scope.requestCancel = function() {
        console.log("requestCancel, scope id=" + $scope.$id);
        $state.go("tab.contacts");
    };

    var doNewContactRequest = function() {

        var credentials = "invitedname=" + $scope.user.invitedName +
            "&greeting=" + $scope.user.greeting;

        $http.post(Chats.getNewContactReqURL(), credentials, {
                //headers: { 'Content-Type': 'application/json'}
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
                    'Accept': 'application/json'
                }
            })
            .success(function(data, status, headers, config) {
                afterReqHandler(data);
                $state.go("tab.contacts");

            }).error(function(data) {
                afterReqHandler(false);
            });

    };

    var afterReqHandler = function(success) {
        if (!userCancel) {
            closePopupDialog();
        }

        if (success == 0) {
            var alertPopup = $ionicPopup.alert({
                title: 'Chat request sent Successfully!',
                template: 'She/he will look at it!'
            });
        } else {
            var alertPopup = $ionicPopup.alert({
                title: 'Request failed!',
                template: 'Please change your username!'
            });
        }

    };

    var closePopupDialog = function() {
        if (popupConnectDiag == null) return;
        popupConnectDiag.close();
        popupConnectDiag = null;
    };
})

.controller('NewInvitesCtrl', function($scope, $http, $state, $ionicScrollDelegate, Chats) {
    $scope.chatInvites = Chats.getChatInvites();

    $scope.how2show = function(invt) {
        if (invt.inviteeId == Chats.getLoginId()) {
            if (invt.resp == 0) {
                return 1;
            }
            return -1;
        }
        if (invt.initatorId == Chats.getLoginId()) {
            if (invt.resp == 0) {
                return 2;
            }
            return 3;
        }

        return -1;
    };

    $scope.acceptChat = function(inviteId) {
        responseInvites(inviteId, 1);
    };

    $scope.refuseChat = function(inviteId) {
        responseInvites(inviteId, -1);
    };

    $scope.cancelChatRequest = function(inviteId) {
        responseInvites(inviteId, -2);
    };

    $scope.sendAckMsg = function(inviteId) {
        responseInvites(inviteId, 0);
    };

    var responseInvites = function(id, response) {
        var responseInfo = "inviteId=" + id + "&response=" + response;

        $http.post(Chats.getInviteRespURL(), responseInfo, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
                    'Accept': 'application/json'
                }
            })
            .success(function(data, status, headers, config) {

                console.log("Success in updating chat status");

            }).error(function(data) {
                console.log("Error in updating chat status");
            });
    };
})

.controller('ChatDetailCtrl', function($scope, $http, $ionicScrollDelegate, Chats) {
    console.log("Show chat detail, scope id=" + $scope.$id);

    $scope.serverUrl = Chats.getServerURL();
    $scope.data = {};

    $scope.pickupFile = function() {
	    
	};
	
    $scope.getBubbleClass = function(userid) {
        var classname = 'from-them';
        if ($scope.messageIsMine(userid)) {
            classname = 'from-me';
        }
        console.log("getBubbleClass=" + classname)
        return classname;
    };
    $scope.messageIsMine = function(userid) {
        var b = userid === Chats.getLoginId();
        //console.log("messageIsMine="+b);
        return userid === Chats.getLoginId();
    };

    function escape(key, val) {
        if (typeof(val) != "string") return val;
        return val
            .replace(/[\\]/g, '\\\\')
            .replace(/[\/]/g, '\\/')
            .replace(/[\b]/g, '\\b')
            .replace(/[\f]/g, '\\f')
            .replace(/[\n]/g, '\\n')
            .replace(/[\r]/g, '\\r')
            .replace(/[\t]/g, '\\t')
            .replace(/[\"]/g, '\\"')
            .replace(/\\'/g, "\\'");
    };

    $scope.sendMessage = function(chatId) {
        var loginid = Chats.getLoginId();
        var msg = JSON.stringify($scope.data.messageToSend, escape);
        var chatId = Chats.getChatId();
        var jsonText = '{"sender":"' + loginid + '","chatId":"' + chatId +
            '","message":' + msg + '}';

        $http.post(Chats.getSendMessageURL(), jsonText, {
                //headers: { 'Content-Type': 'application/json'}
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
            .success(function(data, status, headers, config) {
                /*if (data == true) {
                    var str = "{\"chatId\":\"" + chatId +
                        "\", \"speakerId\":\"" + loginid +
                        "\", \"speaker\":\"" + Chats.getUsername() +
                        "\", \"message\":\"" + msg +
                        "\", \"time\":\"" + new Date().getTime() + "\"}";
                    //console.log(str);
                    data = JSON.parse(str);
                }
                $scope.newChat.push(data);
                Chats.addHistory($scope.newChat);*/
                if (data == true) {
                    $scope.data.messageToSend = "";
                }

            }).error(function(data) {

            });
        //Chats.sendMessage(msg);
        $scope.data.message = "";
    };

    var onNewChatMsgComing = function(msg) {
        $scope.newChat.push(msg);
        $scope.$apply();
        console.log("onNewChatMsgComing, scope id=" + $scope.$id);
    };

    var loginid = Chats.getLoginId();
    var chatid = Chats.getChatId();


    var initializeChatHistoryFromServer = function() {

        var jsonText = '{"sender":"' + loginid + '","chatsessionid":"' + chatid + '"}';

        $http.post(Chats.getLaunchChatURL(), jsonText, {
                //headers: { 'Content-Type': 'application/json'}
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
            .success(function(data, status, headers, config) {

                console.log("Received the number of chat records=" + data.chatMessages.length);

                var chatHistory = data; //($stateParams.chatId);
                $scope.chatTitle = chatHistory.title;
                $scope.chatId = chatHistory.chatId;
                $scope.newChat = chatHistory.chatMessages;
                $scope.chatParticipants = chatHistory.participants;
                //console.log("typeof chatMessages=" + typeof chatMessages);
                $scope.Chats = Chats;
                Chats.setNewMsgCallback(onNewChatMsgComing);
                Chats.addHistory($scope.newChat);
                Chats.setTitle(chatHistory.title);

            }).error(function(data) {

            });
    };

    $scope.reportReadMsgStatus = function(msgs) {
        var max = 0;
        for (var i = 0; i < msgs.length; i++) {
            var m = msgs[i];
            if (m.speakerId != loginid) {
                if (m.msgId > max) {
                    max = m.msgId;
                }
            }
        }

        var jsonText = '{"uid":"' + loginid + '","msgId":"' + max + '","chatId":"' + chatid + '"}';

        $http.post(Chats.getUpdateChatURL(), jsonText, {
                //headers: { 'Content-Type': 'application/json'}
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            })
            .success(function(data, status, headers, config) {

                console.log("Success in updating chat status");

            }).error(function(data) {
                console.log("Error in updating chat status");
            });

    };

    $scope.$on('$ionicView.beforeEnter', function(e) {

        console.log("ChatDetailCtrl beforeEnter scope id=" + $scope.$id);
        if (Chats.needUpdate(chatid)) {
            initializeChatHistoryFromServer();
        } else {
            Chats.enterChatId(chatid);
            $scope.chatTitle = Chats.getTitle();
            $scope.chatId = Chats.getChatId();
            $scope.newChat = Chats.getHistory();
            $scope.Chats = Chats;
            Chats.setNewMsgCallback(onNewChatMsgComing);
            //$scope.$apply();
        }

    });

    $scope.$on('$ionicView.leave', function(e) {

        console.log("ChatDetailCtrl onLeave scope id=" + $scope.$id);
        Chats.leaveChatId();
    });
})

.directive('calcDiffTime', function($timeout) {
    var getTimeDiff = function(datetime) {

        var now = Date.now();

        var milisec_diff = now - datetime;

        //console.log(datetime + " " + now);
        //console.log(milisec_diff + " ");
        var days = Math.floor(milisec_diff / 1000 / 60 / (60 * 24));

        var diff = {};

        if (days != 0) {
            milisec_diff = milisec_diff - days * 1000 * 60 * 60 * 24;
            var hours = Math.floor(milisec_diff / 1000 / 60 / 60);
            diff['difftime'] = days + " Days " + hours + " Hrs ";
            diff['period'] = 1000 * 60 * 60;
            return diff;
        }

        var hours = Math.floor(milisec_diff / 1000 / 60 / 60);
        if (hours != 0) {
            milisec_diff = milisec_diff - hours * 1000 * 60 * 60;
            var mins = Math.floor(milisec_diff / 1000 / 60);
            diff['difftime'] = hours + " Hours " + mins + " Mins ago";
            diff['period'] = 1000 * 60 * 5;
            return diff;
        }

        var min = Math.floor(milisec_diff / 1000 / 60);
        if (min != 0) {
            milisec_diff = milisec_diff - min * 1000 * 60;
            var sec = Math.floor(milisec_diff / 1000);
            diff['difftime'] = min + " min ago";
            diff['period'] = 1000 * 60;
            return diff;
        }

        diff['difftime'] = "just now";
        diff['period'] = 1000 * 30;
        return diff;
    };

    var linker = function($scope, $el, $attrs) {

        var stopTimer = null;
        console.log("calcDiffTime directive link function");
        var updateTime = function() {
            var d = getTimeDiff($attrs.calcDiffTime);
            $el.text(d['difftime']);
            stopTimer = $timeout(updateTime, d['period']);
        };

        $el.on('$destroy', function() {
            $timeout.cancel(stopTimer);
        });
        updateTime();
    };

    return {
        link: linker,
    };
})

.directive('formatMessage', function() {
    //var Autolinker = require('autolinker');

    var linker = function($scope, $el, $attrs) {

        //if (typeof str == 'undefined' || str == null) return;
        str = $attrs.formatMessage;

        str = str.replace(/(\r|\n)/g, '<br>');
        str = Autolinker.link(str, {
            newWindow: true,
            //className: "auto-link",
            twitter: false,
            hashtag: false
        });
        $el.html(str);
    };

    return {
        link: linker,
    };
})

.directive('newComingMsg', function($compile, $templateRequest, $interpolate, $ionicScrollDelegate) {
    console.log("newChatMsg directive");

    var chatMessageHtml =
        '<li class="clearfix" ng-if="messageIsMine({{message.speakerId}})"> ' +
        '<div class="message-data align-right"> ' +
        '<span class="message-data-time" calc-diff-time="{{message.time}}"></span> &nbsp; &nbsp; <span class="message-data-name">{{message.speaker}}</span> ' +
        '<i class="fa fa-circle me"></i> ' +
        '</div> ' +
        '<div class="from-me-message" format-message="{{message.message}}"></div> ' +
        '</li> ' +
        '<li class="clearfix" ng-if="!messageIsMine({{message.speakerId}})"> ' +
        '<div class="message-data"> ' +
        '<span class="message-data-name"><i  ' +
        ' class="fa fa-circle online"></i>{{message.speaker}}</span> ' +
        ' <span class="message-data-time" calc-diff-time="{{message.time}}"></span> ' +
        '</div> ' +
        '<div class="from-them-message" format-message="{{message.message}}"></div> ' +
        '</li>';

    var linker = function($scope, $el, $attrs) {
        $scope.$watch(function() {
                //console.log("newComingMsg directive watch scope id=" + $scope.$id);
                return $scope.$parent.newChat;
            },
            function(newMsgs) {
                if (typeof newMsgs == 'undefined' || newMsgs == null || newMsgs.length == 0) return;

                console.log("newComingMsg directive scope id=" + $scope.$id);
                //console.log("newChat has changed! newChat.length=" + newMsgs.length);

                var liHtml = "";
                var newChats = $scope.$parent.newChat;

                for (var i = 0; i < newChats.length; i++) {
                    newChats[i].message = newChats[i].message.replace(/\\n/g, "<br />");
                    var msg = {
                        message: newChats[i],
                    };

                    var htmlTemp = $interpolate(chatMessageHtml)(msg);
                    liHtml += htmlTemp;
                }

                //var templateElement = angular.element(htmlTemp);
                //$scope.message = message;
                var chatR = $compile(liHtml)($scope.$parent);
                $el.append(chatR);

                //$scope.$parent.Chats.addHistory(newChats);

                $ionicScrollDelegate.$getByHandle('chatDetailScroll').scrollBottom(true);

                $scope.$parent.reportReadMsgStatus($scope.$parent.newChat);
                $scope.$parent.newChat = [];
            }, true
        );
    };

    return {
        link: linker,
        scope: false,

    };
})

.controller('AccountCtrl', function($scope) {
    $scope.settings = {
        enableFriends: true
    };
})

.controller('LogoutCtrl', function($scope, $state) {
    $scope.loginAgain = function(){
    	window.location.reload(true)
    };
})

.controller('FileOpenerController', function($scope, $cordovaFileOpener2, $ionicPlatform) {
	$scope.pickupFile = function() {
	    $cordovaFileOpener2.open(
	        '/sdcard', 
	        '*.png'
	    ).then(function() {
	        console.log('Success');
	    }, function(err) {
	        console.log('An error occurred: ' + JSON.stringify(err));
	    });
	};
});