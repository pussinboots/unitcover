'use strict';

/* Header CDontroller */

function HeaderController($scope, $location) {
    $scope.isActive = function (viewLocation) {
        return viewLocation === $location.path();
    };
}

/* Controllers */
function BuildsCtrl($rootScope, $scope, $routeParams, Builds) {
    initTable($scope, 10, 'date', 'desc')
    $scope.owner = $routeParams.owner
    $scope.project = $routeParams.project
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadBuilds(rootScope, scope, routeParams, Builds)
    };
    $scope.setItems($rootScope, $scope, $routeParams)
}

function loadBuilds(rootScope, scope, routeParams, Builds) {
    scope.builds = Builds.get({owner:routeParams.owner, project:routeParams.project}, function (response) {
        scope.totalItems = response.count;
    });
}

function TestSuitesCtrl($rootScope, $scope, $routeParams, TestSuites) {
    initTable($scope, 10, 'date', 'desc')
    $scope.owner = $routeParams.owner
    $scope.project = $routeParams.project
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadTestSuites(rootScope, scope, routeParams, TestSuites)
    };
    $scope.setItems($rootScope, $scope, $routeParams)
}

function loadTestSuites(rootScope, scope, routeParams, TestSuites) {
    scope.builds = TestSuites.get({owner:routeParams.owner, project:routeParams.project, buildnumber:routeParams.buildnumber}, function (response) {
        scope.totalItems = response.count;
    });
}

function TestCasesCtrl($rootScope, $scope, $routeParams, TestCases) {
    initTable($scope, 10, 'date', 'desc')
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadTestCases(rootScope, scope, routeParams, TestCases)
    };
    $scope.setItems($rootScope, $scope, $routeParams)
}

function loadTestCases(rootScope, scope, routeParams, TestCases) {
    scope.testsuite = TestCases.get({owner:routeParams.owner, project:routeParams.project ,testsuiteid:routeParams.testsuiteid}, function (response) {
        scope.totalItems = response.count;
    });
}

//TODO move to data table directive
function setSort(rootScope, scope, routeParams, sort) {
    var oldSort = angular.copy(scope.sortColumn);
    if (scope.multiselect) scope.sortColumn = scope.sortColumn + " " + sort; else scope.sortColumn = sort;
    if (oldSort == sort) scope.sortDirection = scope.sortDirection == 'desc' ? 'asc' : 'desc'; else scope.sortDirection = 'desc';
    scope.setItems(rootScope, scope, routeParams)
};
