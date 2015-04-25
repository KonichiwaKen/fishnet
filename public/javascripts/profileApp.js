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

      for(var i=0; i<$scope.eventsAttending.length;i++){
        $scope.eventsAttending[i].startTimeDisplay = convertDate($scope.eventsAttending[i].startTime);
        $scope.eventsAttending[i].endTime = convertDate($scope.eventsAttending[i].endTime);
      }
      for(var i=0; i<$scope.eventsHosting.length;i++){
        $scope.eventsHosting[i].startTimeDisplay = convertDate($scope.eventsHosting[i].startTime);
        $scope.eventsHosting[i].endTime = convertDate($scope.eventsHosting[i].endTime);
      }

    }

    $scope.addFriend = function(user) {
      requests.addFriend(user);
    }

    $scope.acceptRequest = function(user) {
      requests.acceptRequest(user);
    }

    $scope.declineRequest = function(user) {
      requests.declineRequest(user);
    }

    convertDate = function(dateString) {
      var year = parseInt(dateString.substring(0, 4));
      var month = parseInt(dateString.substring(5, 7));
      var day = parseInt(dateString.substring(8, 10));
      var hour = parseInt(dateString.substring(11, 13));
      var minute = dateString.substring(14, 16);
      var ampm;

      if (hour >= 12) {
        ampm = "PM";
      } else {
        ampm = "AM";
      }
      if (hour > 12) {
        hour = hour - 12;
      } else if (hour == 0) {
        hour = 12;
      }
      var str = hour + ":" + minute + " " + ampm + " on " + month + "/" + day + "/" + year;
      return str;
    }
}]);

app.factory('requests', ['$http', '$window', function($http, $window) {
  var o = {};

  o.addFriend = function(user) {
    return $http.post('/friendRequest/create', {'user': user}).success(function(data) {
      $window.location.href = '/profile?user=' + user;
    })
  };

  o.acceptRequest = function(user) {
    return $http.post('/friendRequest/accept', {'user': user}).success(function(data) {
      $window.location.href = '/profile?user=' + user;
    })
  };

  o.declineRequest = function(user) {
    return $http.post('/friendRequest/decline', {'user': user}).success(function(data) {
      $window.location.href = '/profile?user=' + user;
    })
  };

}]);
