'use strict';

/* App Module */
var myModule = angular.module('bankapp', 
                ['TestData', 'MobileTable', 'angular-loading-bar', 'ngRoute', 
                'ui.bootstrap', 'ngSanitize', 'productFilters', 
                'productServices', 'ngCookies'])
myModule.config(function ($routeProvider) {
    $routeProvider
    .when('/builds/:owner/:project/builds', {  templateUrl: 'partials/bankapp/build.html',
                                        controller: BuildsCtrl })
	.when('/builds/:owner/:project/testsuites/:buildnumber', {  templateUrl: 'partials/bankapp/testsuite.html',
                                        controller: TestSuitesCtrl })
	.when('/builds/:owner/:project/testcases/:testsuiteid', {  templateUrl: 'partials/bankapp/testcase.html',
                                        controller: TestCasesCtrl })
    .otherwise({ redirectTo: '/builds' });
})

String.prototype.contains = function(it) { return this.indexOf(it) != -1; };