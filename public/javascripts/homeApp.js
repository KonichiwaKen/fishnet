app = angular.module('playCal', []);

app.controller('HomeCtrl', [
  '$scope',
  'login',
  function($scope, login) {
    $scope.login = function() {
      login.login({
        email: $scope.email,
        password: $scope.password
      });

      $scope.email = '';
      $scope.password = '';
    }

    $scope.register = function() {
      login.register({
        email: $scope.email,
        password: $scope.password,
        firstName: $scope.firstName,
        lastName: $scope.lastName
      });

      $scope.email = '';
      $scope.password = '';
      $scope.firstName = '';
      $scope.lastName = '';
    }
  }
]);

app.factory('login', ['$http', function($http){
  var o = {};

  o.login = function(user) {
    return $http.post('/login', user).success(function(data) {
      console.log(data);
    });
  };

  o.register = function(user) {
    return $http.post('/register', user).success(function(data) {
      console.log(data);
    });
  };

  return o;
}]);
