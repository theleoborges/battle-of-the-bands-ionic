function onError() {
  alert('onError!');
};

var GRAVITY= 9.8;

function absAcceleration(x, y, z){
  return Math.sqrt(x*x + y*y + z*z) - GRAVITY;
}


angular.module('starter.controllers', [])

  .controller('DashCtrl', function($scope) {
    $scope.acceleration = "0";
    var options = { frequency: 500 };
    var watchID = navigator.accelerometer.watchAcceleration(function(acceleration){
      $scope.acceleration = absAcceleration(acceleration.x, acceleration.y, acceleration.z);
      console.log( "AbsAccel: ->( " + $scope.acceleration +" )<-");

// "X: " + acceleration.x + " Y: "
//         + acceleration.y + "Z: " + acceleration.z;
      $scope.$apply();
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
