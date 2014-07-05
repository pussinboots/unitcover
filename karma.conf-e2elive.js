// Karma configuration
// Generated on Wed Aug 14 2013 22:33:43 GMT+0200 (CEST)

module.exports = function (config) {
    config.set({
        // base path, that will be used to resolve files and exclude
        basePath: '',

        // frameworks to use
        frameworks: ['jasmine'],

        preprocessors: {
	    'public/test/live/*.coffee': ['coffee']
	},

        // list of files / patterns to load in the browser
        files: [
            './node_modules/karma-ng-scenario/lib/angular-scenario.js',
            './node_modules/karma-ng-scenario/lib/adapter.js',
            'public/test/live/*.coffee'
        ],

	coffeePreprocessor: {
	   // options passed to the coffee compiler
	   options: {
	     bare: true,
	     sourceMap: false
	   },
	   // transforming the filenames
	   transformPath: function(path) {
	     return path.replace(/\.coffee$/, '.js');
	   }
	},

        // list of files to exclude
        exclude: [
        ],

        // test results reporter to use
        // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
        reporters: ['progress', 'dots', 'junit'],

	junitReporter: {
	      outputFile: 'test-results-live.xml',
	      suite: 'https://unitcover.herokuapp.com/'
	},

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_WARN,

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
            '/': 'https://unitcover.herokuapp.com/'
        },

        urlRoot: '/'
    });
};
