function onError() {
  alert('onError!');
};

angular.module('starter.controllers', [])

  .controller('DashCtrl', function($scope) {
    $scope.acceleration = "0,0,0";
    var options = { frequency: 500 };
    var watchID = navigator.accelerometer.watchAcceleration(function(acceleration){
      $scope.acceleration = "X: " + acceleration.x + " Y: " + acceleration.y + "Z: " + acceleration.z;
      $scope.$apply();
      console.log("Got acc: " + $scope.acceleration);
      console.log("(X: " + acceleration.x + " Y: " + acceleration.y + "Z: " + acceleration.z + ")");
    }, onError, options);
  })

  .controller('FriendsCtrl', function($scope, Friends) {
    $scope.friends = Friends.all();
  })

  .controller('FriendDetailCtrl', function($scope, $stateParams, Friends) {
    $scope.friend = Friends.get($stateParams.friendId);
  })

  .controller('AccountCtrl', function($scope) {
  });
