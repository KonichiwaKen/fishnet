app = angular.module('playCal', []);

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
      console.log($scope.email);
      console.log($scope.password);
      console.log($scope.firstName);
      console.log($scope.lastName);
      login.register({
        email: $scope.email,
        password: $scope.password,
        firstName: $scope.firstName,
        lastName: $scope.lastName
      });
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
