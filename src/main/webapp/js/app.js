var app = angular.module('myApp', [
    'ngRoute',
    'ngSanitize',
    'ui.bootstrap',
    'angularFileUpload'
]);
    
app.config(function ($routeProvider) {
    $routeProvider.
    when('/search', {
        templateUrl: 'search.html',
        controller: 'searchCtrl'
    }).
    when('/home', {
        templateUrl: 'docManagement.html',
        controller: 'docManagementCtrl'
    }).
    otherwise({
        redirectTo: '/search'
    });
});