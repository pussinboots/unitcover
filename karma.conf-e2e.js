// Karma configuration
// Generated on Wed Aug 14 2013 22:33:43 GMT+0200 (CEST)

module.exports = function (config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '',

        // frameworks to use
        frameworks: ['jasmine'],

        // list of files / patterns to load in the browser
        files: [
            './node_modules/karma-ng-scenario/lib/angular-scenario.js',
            './node_modules/karma-ng-scenario/lib/adapter.js',
            'public/test/e2e/*.js'
            //'public/test/e2e/builds.js'
            //'public/test/**/*.js'
        ],

        // list of files to exclude
        exclude: [
        ],

        preprocessors: { 'public/js/bankapp/*.js': ['coverage'],'public/test/e2e/*.js': ['coverage']  },


        // test results reporter to use
        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['progress', 'dots', 'junit', 'coverage', 'coveralls', 'threshold'],

        /*coverageReporter: {
          type : 'html',
          dir : 'coverage/'
        },*/
        thresholdEnd2EndTests: true,
        thresholdReporter: {
          statements: 90,
          branches: 60,
          functions: 85,
          lines: 90
        },

        coverageReporter: {
          reporters:[
            {type: 'lcov', dir:'coverage/'}
          ],
        },

        /*coverageReporter: {
            type: 'lcov', 'text', // lcov or lcovonly are required for generating lcov.info files
            dir: 'coverage/'
        },*/

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.ĹOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,
        usePolling : true,

        // Start these browsers, currently available:
        // - Chrome
        // - ChromeCanary
        // - Firefox
        // - Opera
        // - Safari (only Mac)
        // - IE (only Windows)
        //browsers: ['Chrome','Firefox','Opera','PhantomJS'],
        browsers: ['Firefox'/*, 'Opera'*/],

        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,
        browserNoActivityTimeout: 30000,

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false,

        proxies: {
            '/js/bankapp': 'http://localhost:9000/js/bankapp/coverage',
            '/': 'http://localhost:9000/'
        },

        urlRoot: '/'
    });
};
