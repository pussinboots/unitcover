var sharedConfig = require('./karma-shared.conf');

module.exports = function (config) {
    sharedConfig(config);
    config.files.push('public/test/e2e/*.coffee')
    config.set({
        proxies: {
            '/': 'http://localhost:9000/'
        }
    });
};
