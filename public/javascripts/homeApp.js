app = angular.module('fishNet', []);

app.controller('HomeCtrl', [
  '$scope',
  'events',
  function($scope, events) {
    events.getEvents();

    $scope.addEvent = function() {
      events.addEvent({
        title: $scope.title,
        description: $scope.description,
        location: $scope.location,
        startTime: $scope.startTime,
        endTime: $scope.endTime,
        isPublic: $scope.isPublic
      });

      $scope.title = '';
      $scope.description = '';
      $scope.location = '';
      $scope.startTime = '';
      $scope.endTime = '';
      $scope.isPublic = false;
    }
  }
]);

app.factory('events', ['$http', '$window', function($http, $window) {
  var o = {};

  o.addEvent = function(event) {
    $http.post('/events', event);
  };

  o.getEvents = function() {
    $http.get('/events').success(function(data) {
      console.log(data);
    })
  }

  return o;
}]);
