module.exports = function (config) {
    config.set({
        basePath: '../',

        frameworks: ['jasmine'],

        files: [
            'node_modules/karma-ng-scenario/lib/angular-scenario.js',
            'node_modules/karma-ng-scenario/lib/adapter.js',
            'public/test/live/scenario.coffee',
        ],


        exclude: [
        ],

        preprocessors: { 'public/js/bankapp/*.js': ['coverage'],
			 'public/test/e2e/*.coffee': ['coffee'],
			 'public/test/live/*.coffee': ['coffee'],
			 //'public/test/live/*.litcoffee': ['coffee'],    
			 'public/js/bankapp/coffee/*.js': ['coverage']  },


        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['progress', 'dots', 'junit'],

        thresholdReporter: {
          statements: 90,
          branches: 60,
          functions: 85,
          lines: 90
        },

        coverageReporter: {
          reporters:[
            {type: 'lcov', dir:'coverage/'},
            {type: 'text'}
          ],
        },

        port: 9876,

        colors: true,

        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_WARN,

        autoWatch: true,
        usePolling : true,

        //browsers: ['Chrome','Firefox','Opera','PhantomJS'],
        browsers: ['Firefox'],

        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,
        browserNoActivityTimeout: 30000,

        singleRun: false,

        proxies: {
            '/js/bankapp': 'http://localhost:9000/js/bankapp/coverage',
            '/': 'http://localhost:9000/'
        },

        urlRoot: '/'
    });
};
