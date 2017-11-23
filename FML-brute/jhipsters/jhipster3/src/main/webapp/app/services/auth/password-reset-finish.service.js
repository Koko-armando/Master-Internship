(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .factory('PasswordResetFinish', PasswordResetFinish);

    PasswordResetFinish.$inject = ['$resource'];

    function PasswordResetFinish($resource) {
        var service = $resource('uaa/api/account/reset-password/finish', {}, {});

        return service;
    }
})();
