angular.module('TestData', []).directive('testData', function () {
    return {
        restrict: 'E',
        templateUrl: 'partials/bankapp/directive/data-table.html',
        replace: false,
        transclude: true
    };
})


function TableCtrl($rootScope, $scope, $routeParams) {
    $scope.currentPage = 1;
    $scope.filter = {}
}

function initTable(scope, items, sortColumn, sortDirection) {
    scope.filter = {}
    scope.items = items
    scope.sortColumn = sortColumn;
    scope.sortDirection = sortDirection
    scope.dateFormat = 'yyyy-MM-dd HH:mm:ss Z'
}
