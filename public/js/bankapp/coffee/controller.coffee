"use strict"
@OverviewCtrl = ['$scope', '$rootScope', 'Builds',($rootScope, $scope, Builds) ->
  initTable($scope, 10, 'date', 'desc')
  $scope.setItems = (rootScope, scope) -> loadLatestBuilds(rootScope, scope, Builds)
  $scope.statusClass=(build) -> css = if build.errors > 0 then "red" else if build.failures then "yellow" else if build.tests > 0 then "green" else "gray"     
  $scope.setItems($rootScope, $scope)
]
loadLatestBuilds = (rootScope, scope, Builds) ->
  scope.builds = Builds.get {owner:'all', project:'all'}, (response) -> scope.totalItems = response.count
