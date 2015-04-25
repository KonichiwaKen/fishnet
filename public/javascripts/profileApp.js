app = angular.module('fishNet', []);

app.controller('ProfileCtrl', [
  '$scope',
  function($scope) {
    $scope.init = function(json) {
      var jsonString = angular.fromJson(json);

      $scope.friendStatus = jsonString.friendStatus;
      $scope.eventsAttending = jsonString.eventsAttending;
      $scope.eventsHosting = jsonString.eventsHosting;
    }
}]);
