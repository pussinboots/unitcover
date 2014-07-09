'use strict'
angular.scenario.dsl "expectStatus", ->
  (repeater, selector, transform) ->
    @addFutureAction "status check " + selector + '> span.status', ($window, $document, done) ->
      repeater.execute (value, array) ->
        console.log 'expect status ' + value + ' ' + array
        status = transform(array)
        tests = parseInt(status.tests)
        failures = parseInt(status.failures)
        errors = parseInt(status.errors)
        if errors > 0
          expectClass(selector + '> span.status').toContain "red"
        else if failures > 0
          expectClass(selector + '> span.status').toContain "yellow"
        else if tests > 0
          expectClass(selector + '> span.status').toContain "green"
        else
          expectClass(selector + '> span.status').toContain "gray"
        done null