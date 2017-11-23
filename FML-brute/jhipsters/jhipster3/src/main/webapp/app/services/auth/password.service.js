(function() {
    'use strict';

    angular
        .module('jhipsterApp')
        .factory('Password', Password);

    Password.$inject = ['$resource'];

    function Password($resource) {
        var service = $resource('uaa/api/account/change-password', {}, {});

        return service;
    }
})();
