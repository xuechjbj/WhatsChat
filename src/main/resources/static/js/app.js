// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'ngCookies'])

.run(function($ionicPlatform, $state) {
    $ionicPlatform.ready(function() {
        // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
        // for form inputs)
        if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
            cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            cordova.plugins.Keyboard.disableScroll(true);

        }
        if (window.StatusBar) {
            // org.apache.cordova.statusbar required
            StatusBar.styleDefault();
        }

        console.log("start to run");
        $state.go("login");

    });


})

.factory('XSRFInterceptor', function($q, $cookies) {
    function readCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    var XSRFInterceptor = {
        request: function(config) {
        	var AuthToken = $cookies.get('XSRF-TOKEN');
        	//var AuthTokens = $cookies.getAll();
            //var AuthToken = readCookie('XSRF-TOKEN');
            if (AuthToken!= null) {
                config.headers['X-XSRF-TOKEN'] = AuthToken;
                console.log("set header X-XSRF-TOKEN="+AuthToken);
            }
            return config;
        },
    };
    return XSRFInterceptor;
})

.config(['$httpProvider', function($httpProvider) {
    $httpProvider.defaults.withCredentials = true;
    $httpProvider.interceptors.push('XSRFInterceptor');
}])

.config(function($stateProvider, $urlRouterProvider) {

    // Ionic uses AngularUI Router which uses the concept of states
    // Learn more here: https://github.com/angular-ui/ui-router
    // Set up the various states which the app can be in.
    // Each state's controller can be found in controllers.js
    $stateProvider

        .state('login', {
        url: '/',

        templateUrl: 'templates/login.html',
        controller: 'LoginCtrl'
    })

    $stateProvider
        .state('logout', {
        url: '/logout',

        templateUrl: 'templates/logout.html',
        controller: 'LogoutCtrl'
    })
    
    .state('new-contact', {
        url: '/newcontact',

        templateUrl: 'templates/new-contact.html',
        controller: 'NewContactCtrl'
    })

    .state('new-invites', {
        url: '/newinvites',
        templateUrl: 'templates/new-invites.html',
        controller: 'NewInvitesCtrl'
    })

    // setup an abstract state for the tabs directive
    .state('tab', {
        url: '/tab',
        abstract: true,
        templateUrl: 'templates/tabs.html'
    })

    // Each tab has its own nav history stack:

    .state('tab.contacts', {
            url: '/contacts',
            views: {
                'tab-contacts': {
                    templateUrl: 'templates/tab-contacts.html',
                    controller: 'ChatsCtrl'
                }
            }
        })
        .state('tab.chat-detail', {
            url: '/chats/:chatId',
            views: {
                'tab-contacts': {
                    templateUrl: 'templates/chat-detail.html',
                    controller: 'ChatDetailCtrl'
                }
            }
        })

    .state('tab.account', {
        url: '/account',
        views: {
            'tab-account': {
                templateUrl: 'templates/tab-account.html',
                controller: 'AccountCtrl'
            }
        }
    });

    // if none of the above states are matched, use this as the fallback
    $urlRouterProvider.otherwise('/');

});