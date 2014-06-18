'use strict';

/* Services */

angular.module('productServices', ['ngResource'], function ($provide) {

    $provide.factory('Builds', function ($resource) {
        return $resource('/api/:owner/:project', {}, {
            get: {method: 'GET', isArray: false}
        });
    });

    $provide.factory('TestSuite', function ($resource) {
        return $resource('/api/:owner/:project/:testsuiteid', {}, {
            get: {method: 'GET', isArray: false}
        });
    });
});
