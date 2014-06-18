'use strict';

/* App Module */
var myModule = angular.module('bankapp', 
                ['TestData', 'MobileTable', 'angular-loading-bar', 'ngRoute', 
                'ui.bootstrap', 'ngSanitize', 'productFilters', 
                'productServices', 'ngCookies'])
myModule.config(function ($routeProvider) {
    $routeProvider
	.when('/builds/:owner/:project', {  templateUrl: 'partials/bankapp/builds.html',
                                        controller: BuildsCtrl })
	.when('/builds/:owner/:project/:testsuiteid', {  templateUrl: 'partials/bankapp/testsuite.html',
                                        controller: TestSuiteCtrl })
    .otherwise({ redirectTo: '/builds' });
})

String.prototype.contains = function(it) { return this.indexOf(it) != -1; };