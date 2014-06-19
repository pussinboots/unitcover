'use strict';

/* Services */

angular.module('productServices', ['ngResource'], function ($provide) {

    $provide.factory('Builds', function ($resource) {
        return $resource('/api/:owner/:project/builds', {}, {
            get: {method: 'GET', isArray: false}
        });
    });

    $provide.factory('TestSuites', function ($resource) {
        return $resource('/api/:owner/:project/:buildnumber', {}, {
            get: {method: 'GET', isArray: false}
        });
    });

    $provide.factory('TestCases', function ($resource) {
        return $resource('/api/:owner/:project/testcases/:testsuiteid', {}, {
            get: {method: 'GET', isArray: false}
        });
    });
});
