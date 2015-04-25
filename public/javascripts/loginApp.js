app = angular.module('fishNet', []);

app.controller('LoginCtrl', [
  '$scope',
  'login',
  function($scope, login) {
    $scope.view = false;

    $scope.login = function() {
      login.login({
        email: $scope.email,
        password: $scope.password
      });
    }

    $scope.register = function() {
      login.register({
        email: $scope.email,
        password: $scope.password,
        firstName: $scope.firstName,
        lastName: $scope.lastName
      });
    }
  }
]);

app.factory('login', ['$http', '$window', function($http, $window){
  var o = {};

  o.login = function(user) {
    return $http.post('/login', user).success(function(data) {
      $window.location.href = '/home';
    })
    .error(function(data) {
      alert('Incorrect username/password');
    });
  };

  o.register = function(user) {
    return $http.post('/register', user).success(function(data) {
      $window.location.href = '/home';
    })
    .error(function(data) {
      alert('Email already in use');
    });
  };

  return o;
}]);
