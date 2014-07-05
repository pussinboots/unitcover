var sharedConfig = require('./karma-shared.conf');
var hostname = require('os').hostname() + '.codio.io';
var proxyHost = 'http://' + hostname + ':9000/';
var proxyHostCoverage = proxyHost + 'js/bankapp/coverage';

module.exports = function (config) {
    sharedConfig(config);
    config.files.push('public/test/e2e/*.coffee');
    config.set({
        port: 9002,
	browsers: ['PhantomJS'],
        hostname: hostname,
        proxies: {
            '/js/bankapp': proxyHostCoverage,
            '/': proxyHost
        }
    });
};
