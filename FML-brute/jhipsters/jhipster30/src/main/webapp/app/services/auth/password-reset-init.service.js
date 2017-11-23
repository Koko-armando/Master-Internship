(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .factory('PasswordResetInit', PasswordResetInit);

    PasswordResetInit.$inject = ['$resource'];

    function PasswordResetInit($resource) {
        var service = $resource('uaa/api/account/reset-password/init', {}, {});

        return service;
    }
})();
