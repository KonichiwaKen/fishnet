app = angular.module('fishNet', []);

app.controller('ProfileCtrl', [
  '$scope',
  'requests',
  function($scope, requests) {
    $scope.init = function(json) {
      var jsonString = angular.fromJson(json);

      $scope.friendStatus = jsonString.friendStatus;
      $scope.eventsAttending = jsonString.eventsAttending;
      $scope.eventsHosting = jsonString.eventsHosting;
    }

    $scope.addFriend = function(user) {
      requests.addFriend(user);
    }
}]);

app.factory('requests', ['$http', '$window', function($http, $window) {
  var o = {};

  o.addFriend = function(user) {
    return $http.post('/friendRequest/create', {'user': user}).success(function(data) {
      $window.location.href = '/profile?user=' + user;
    })
  };

  return o;
}]);
