sharedConfig = require('./karma-shared.conf')

module.exports = (config) ->
    sharedConfig(config)
    config.files.push('public/test/e2e/*.coffee')
    config.reporters.push('coverage')
    config.reporters.push('threshold')
