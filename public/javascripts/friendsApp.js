app = angular.module('fishNet', []);

app.controller('FriendCtrl', ['$scope', 'search', function($scope, search) {
  $scope.searchResults = search.searchResults;

  $scope.searchUsers = function() {
    search.searchUsers($scope.searchQuery);
  }
}]);

app.factory('search', ['$http', function($http) {
  o = {
    searchResults: []
  };

  o.searchUsers = function(searchQuery) {
    $http.get('/search/users?query=' + searchQuery).success(function(data) {
      o.searchResults.length = 0;
      o.searchResults.push.apply(o.searchResults, data);
    });
  };

  return o;
}]);
