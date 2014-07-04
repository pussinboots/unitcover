// Generated by CoffeeScript 1.7.1
(function() {
  var loadLatestBuilds;

  this.OverviewCtrl = [
    '$scope', '$rootScope', 'Builds', function($rootScope, $scope, Builds) {
      initTable($scope, 10, 'date', 'desc');
      $scope.setItems = function(rootScope, scope) {
        return loadLatestBuilds(rootScope, scope, Builds);
      };
      $scope.statusClass = function(build) {
        var css;
        return css = build.errors > 0 ? "red" : build.failures ? "yellow" : build.tests > 0 ? "green" : "gray";
      };
      return $scope.setItems($rootScope, $scope);
    }
  ];

  loadLatestBuilds = function(rootScope, scope, Builds) {
    return scope.builds = Builds.get({
      owner: 'all',
      project: 'all'
    }, function(response) {
      return scope.totalItems = response.count;
    });
  };

}).call(this);