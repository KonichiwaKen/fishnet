app = angular.module('fishNet', []);

app.controller('FriendCtrl', ['$scope', 'friends', function($scope, friends) {
  $scope.searchResults = friends.searchResults;
  $scope.friends = friends.friends;

  friends.getFriends();

  $scope.searchUsers = function() {
    friends.searchUsers($scope.searchQuery);
  };
}]);

app.factory('friends', ['$http', function($http) {
  o = {
    friends: [],
    searchResults: []
  };

  o.getFriends = function() {
    $http.get('/friendsList').success(function(data) {
      o.friends.push.apply(o.friends, data);
    });
  };

  o.searchUsers = function(searchQuery) {
    $http.get('/search/users?query=' + searchQuery).success(function(data) {
      o.searchResults.length = 0;
      o.searchResults.push.apply(o.searchResults, data);
    });
  };

  return o;
}]);
