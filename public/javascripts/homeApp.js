app = angular.module('fishNet', []);

app.controller('HomeCtrl', [
  '$scope',
  'events',
  'friends',
  function($scope, events, friends) {
    events.getEvents();
    friends.getFriends();
    $scope.events = events.events;

    $scope.displayModal = function(event) {
      // add friends to event, but not those who have been invited/accepted/declined
      var friendsToDisplay = [];

      for (var i = 0; i < friends.friends.length; i++) {
        if (event.invitedUsers.indexOf(friends.friends[i]) == -1 &&
            event.acceptedUsers.indexOf(friends.friends[i]) == -1 &&
            event.declinedUsers.indexOf(friends.friends[i]) == -1) {
          friendsToDisplay.push(friends.friends[i]);
        }
      }

      event.friends = friendsToDisplay;
      $scope.eventToDisplay = event;
    };

    $scope.addEvent = function(userId) {
      $scope.title = $scope.title.replace('\'', '');
      $scope.location = $scope.location.replace('\'', '');

      if ($scope.description != null) {
        $scope.description = $scope.description.replace('\'', '');
      }

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
    };

    $scope.inviteFriend = function(user, event) {
      friends.inviteFriend(user, event);
    }
}]);

app.factory('events', ['$http', '$window', '$sce', function($http, $window, $sce) {
  var o = {
    events: []
  };

  o.addEvent = function(event, userId) {
    return $http.post('/events', event).success(function(data) {
      event.owner = userId;
      event.id = data;
      event.startTimeDisplay = convertDate(event.startTime);
      event.endTime = convertDate(event.endTime);
      event.link = $sce.trustAsResourceUrl('https://www.google.com/maps/embed/v1/place?key=AIzaSyCpYioRQ0epUsEnIz2YlzbVYNKpUXWnYqg&q=' + convertTextString(event.location) + '&zoom=14');
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
        o.events[i].link = $sce.trustAsResourceUrl('https://www.google.com/maps/embed/v1/place?key=AIzaSyCpYioRQ0epUsEnIz2YlzbVYNKpUXWnYqg&q=' + convertTextString(o.events[i].location) + '&zoom=14');
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

  convertTextString = function(location) {
    var convertedString = location.split(' ').join('+');
    return convertedString;
  }

  return o;
}]);

app.factory('friends', ['$http', function($http) {
  var o = {
    friends: []
  };

  o.getFriends = function() {
    $http.get('/friendsList').success(function(data) {
      o.friends.push.apply(o.friends, data);
    });
  };

  o.inviteFriend = function(user, event) {
    $http.post('/event/invite?user=' + user + '&event=' + event).success(function(data) {
    });
  };

  return o;
}])
