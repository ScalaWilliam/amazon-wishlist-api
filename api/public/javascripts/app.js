angular.module('yay', [])
    .controller('Sygments', function($scope, $http) {

    $http.get("get").success(function (res) {
        $scope.wishlist = res;
        res.items.filter(function(x) { return x.title.indexOf("HTC") != -1; }).forEach(function(x) {
            $scope.selected = x;
        });
    });


    $scope.hover = function(x) {
        $scope.hovered = x;
    };

    $scope.select = function(x) {
        $scope.selected = x;
    };

        $scope.unhover = function(x) {
            if ( $scope.hovered  && $scope.hovered.id == x.id) {
                $scope.hovered = null;
            }
        };

});

