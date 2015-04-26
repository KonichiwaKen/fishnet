app = angular.module('fishNet', []);

app.controller('HomeCtrl', [
  '$scope',
  'events',
  function($scope, events) {
    events.getEvents();
    $scope.events = events.events;

    $scope.displayModal = function(event){
      $scope.eventToDisplay = event;
    };

    $scope.addEvent = function(userId) {
      events.addEvent({
        title: $scope.title,
        description: $scope.description,
        location: $scope.location,
        startTime: $scope.startTime,
        endTime: $scope.endTime,
        isPublic: $scope.isPublic
      }, userId);

      $scope.title = '';
      $scope.description = '';
      $scope.location = '';
      $scope.startTime = '';
      $scope.endTime = '';
      $scope.isPublic = false;
    }
}]);

app.factory('events', ['$http', '$window', function($http, $window) {
  var o = {
    events: []
  };

  o.addEvent = function(event, userId) {
    return $http.post('/events', event).success(function(data){
      event.owner = userId;
      event.id = data;
      event.startTimeDisplay = convertDate(event.startTime);
      event.endTime = convertDate(event.endTime);
      o.events.push(event);
    })
    .error(function(data) {
      alert('Error creating event');
    });
  };

  o.getEvents = function() {
    return $http.get('/events').success(function(data) {
      o.events.push.apply(o.events, angular.fromJson(data));
      for(var i=0; i<o.events.length;i++){
        o.events[i].startTimeDisplay = convertDate(o.events[i].startTime);
        o.events[i].endTime = convertDate(o.events[i].endTime);
      }
    })
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

  return o;
}]);
