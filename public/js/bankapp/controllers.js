'use strict';

/* Controllers */
function OverviewCtrl($rootScope, $scope, Builds) {
    initTable($scope, 10, 'date', 'desc')
    $scope.setItems = function (rootScope, scope) {
        loadLatestBuilds(rootScope, scope, Builds)
    };
    $scope.statusClass=function(build) {
        if(build.errors > 0) return "red"
        if(build.failures > 0) return "yellow"
        if(build.tests > 0) return "green"
        return "gray"
    }
    $scope.setItems($rootScope, $scope)
}
function BuildsCtrl($rootScope, $scope, $routeParams, Builds) {
    initTable($scope, 10, 'date', 'desc')
    $rootScope.owner = $routeParams.owner
    $rootScope.project = $routeParams.project
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadBuilds(rootScope, scope, routeParams, Builds)
    };
    $scope.statusClass=function(build) {
        if(build.errors > 0) return "red"
        if(build.failures > 0) return "yellow"
        if(build.tests > 0) return "green"
        return "gray"
    }
  $scope.chartConfig = {
        options: {
          chart: {
            type: 'areaspline'
          },
          plotOptions: {
            series: {
              stacking: ''
            }
          }
        },
        //size:{height: 200, width:1024},
        credits: {
          enabled: false
        },
        loading: false,
        xAxis:{type:'category'}
      }
    $scope.setItems($rootScope, $scope, $routeParams)
}

function loadLatestBuilds(rootScope, scope, Builds) {
    scope.builds = Builds.get({owner:'all', project:'all'}, function (response) {
        scope.totalItems = response.count;
    });
}

function loadBuilds(rootScope, scope, routeParams, Builds) {
    scope.builds = Builds.get({owner:routeParams.owner, project:routeParams.project}, function (response) {
        var failures = [];
        var tests = [];
        var errors = [];
        angular.forEach(response.items, function(build) {
            tests.unshift([build.buildNumber, build.tests]);
            failures.unshift([build.buildNumber, build.failures]);
            errors.unshift([build.buildNumber, build.errors]);
        })
        var chartSeries = [
            {"name": "tests", "data": tests, "color":"darkgreen"},
            {"name": "failures", "data": failures, "color":"darkgoldenrod", connectNulls: true},
            {"name": "errors", "data": errors, "color":"darkred", connectNulls: true}
          ];
        scope.chartConfig.series = chartSeries;
        scope.totalItems = response.count;
        scope.$broadcast('highchartsng.reflow');
    });
}

function TestSuitesCtrl($rootScope, $scope, $routeParams, TestSuites) {
    initTable($scope, 10, 'date', 'desc')
    $rootScope.owner = $routeParams.owner
    $rootScope.project = $routeParams.project
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadTestSuites(rootScope, scope, routeParams, TestSuites)
    };
    $scope.setItems($rootScope, $scope, $routeParams)
    
    $scope.statusClass=function(build) {
        if(build.errors > 0) return "red"
        if(build.failures > 0) return "yellow"
        if(build.tests > 0) return "green"
        return "gray"
    }
}

function loadTestSuites(rootScope, scope, routeParams, TestSuites) {
    scope.builds = TestSuites.get({owner:routeParams.owner, project:routeParams.project, buildnumber:routeParams.buildnumber}, function (response) {
        scope.totalItems = response.count;
    });
}

function TestCasesCtrl($rootScope, $scope, $routeParams, TestCases) {
    $rootScope.owner = $routeParams.owner
    $rootScope.project = $routeParams.project
    initTable($scope, 10, 'date', 'desc')
    $scope.setItems = function (rootScope, scope, routeParams) {
        loadTestCases(rootScope, scope, routeParams, TestCases)
    };
    $scope.setItems($rootScope, $scope, $routeParams)

    $scope.statusClass=function(testCase) {
        if(typeof testCase.errorMessage === "string") return "red"
        if(typeof testCase.failureMessage === "string") return "yellow"
        return "green"
    }
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
