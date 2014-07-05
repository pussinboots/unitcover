var sharedConfig = require('./karma-shared.conf');

module.exports = function (config) {
    sharedConfig(config);
    config.files.push('public/test/live/*.coffee')
    config.set({
        junitReporter: {
	      outputFile: 'test-results-live.xml',
	      suite: 'https://unitcover.herokuapp.com/'
	},
        proxies: {
            '/': 'https://unitcover.herokuapp.com/'
        }
    });
};
