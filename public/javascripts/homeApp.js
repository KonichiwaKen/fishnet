app = angular.module('fishNet', []);

app.controller('HomeCtrl', [
  '$scope',
  'events',
  function($scope, events) {
    $scope.addEvent = function() {
      events.addEvent({
        title: $scope.title,
        description: $scope.description,
        startTime: $scope.startTime,
        endTime: $scope.endTime,
        isPublic: $scope.isPublic
      });
    }
  }
]);

app.factory('events', ['$http', '$window', function($http, $window) {
  var o = {};

  o.addEvent = function(event) {
    $http.post('/events', event);
  };

  return o;
}]);
