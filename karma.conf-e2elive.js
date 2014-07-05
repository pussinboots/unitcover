module.exports = function (config) {
    config.set({
        basePath: '',

        frameworks: ['jasmine'],

        preprocessors: {
	    'public/test/live/*.coffee': ['coffee']
	},

        files: [
            './node_modules/karma-ng-scenario/lib/angular-scenario.js',
            './node_modules/karma-ng-scenario/lib/adapter.js',
            'public/test/live/*.coffee'
        ],

	coffeePreprocessor: {
	   options: {
	     bare: true,
	     sourceMap: false
	   },
	   // transforming the filenames
	   transformPath: function(path) {
	     return path.replace(/\.coffee$/, '.js');
	   }
	},

        exclude: [
        ],

        reporters: ['progress', 'dots', 'junit'],

	junitReporter: {
	      outputFile: 'test-results-live.xml',
	      suite: 'https://unitcover.herokuapp.com/'
	},

        port: 9876,

        colors: true,

        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_WARN,

        autoWatch: true,
        usePolling : true,

        browsers: ['Firefox'/*, 'Opera'*/],

        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,
        browserNoActivityTimeout: 30000,

        singleRun: false,

        proxies: {
            '/': 'https://unitcover.herokuapp.com/'
        },

        urlRoot: '/'
    });
};
