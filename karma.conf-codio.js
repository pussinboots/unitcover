// Karma configuration
// Generated on Wed Aug 14 2013 22:33:43 GMT+0200 (CEST)

var hostname = require('os').hostname() + '.codio.io';
var proxyHost = 'http://' + hostname + ':9000/';
var proxyHostCoverage = proxyHost + 'js/bankapp/coverage';
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
            'public/test/e2e/*.js',
            'public/js/bankapp/**.js'
        ],

        // list of files to exclude
        exclude: [
        ],

        // test results reporter to use
        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['coverage', 'threshold'],

		/*thresholdReporter: {
          statements: 90,   
          branches: 60,     
          functions: 85,    
          lines: 90         
        },*/                                                                                                                                       
        coverageReporter: { 
          reporters:[       
            {type: 'lcov', dir:'coverage/'},    
            {type: 'text'}                      
          ],                                    
        }, 

        // web server port
        port: 9002,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_DEBUG,

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
        browsers: ['PhantomJS'],
        
        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,
        browserNoActivityTimeout: 30000,

        hostname: hostname,
        
        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: false,

        proxies: {
            '/js/bankapp': proxyHostCoverage,
            '/': proxyHost
        },

        urlRoot: '/'
    });
};
