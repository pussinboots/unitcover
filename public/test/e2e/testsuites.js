'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('testsuites', function() {

  it('should redirect /index.html to /index.html/#builds/', function() {
    browser().navigateTo('/index.html');
    expect(browser().location().url()).toBe('/builds/');
  });

  describe('passed testsuite', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/bankapp/testsuites/7');
    });

    it('ten test suites are display', function() {
      expect(repeater('li.suite').count()).toBe(1);
    });
    
    it('the latest testsuite show as first', function() {
      expect(repeater('li.suite:eq(0)').column('suite.name')).toEqual(["testsuite 7"]);
      expect(element('li.suite:eq(0) > span.status').attr('class')).toEqual("status green");
    });

    it('the latest ten test suites should be display started with the eleven test suite', function() {
      expect(repeater('li.suite').column('suite.name')).
          toEqual(["testsuite 7"]);
    });
  });

  describe('failed testsuite', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/bankapp/testsuites/8');
    });

    it('one test suite should be displayed', function() {
      expect(repeater('li.suite').count()).toBe(1);
    });
    
    it('the latest testsuite show as first', function() {
      expect(repeater('li.suite:eq(0)').column('suite.name')).toEqual(["testsuite 8"]);
      expect(element('li.suite:eq(0) > span.status').attr('class')).toEqual("status yellow");
    });

    it('the latest ten test suites should be display started with the eleven test suite', function() {
      expect(repeater('li.suite').column('suite.name')).
          toEqual(["testsuite 8"]);
    });
  });

  describe('failed testsuite', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/bankapp/testsuites/10');
    });

    it('one test suite should be displayed', function() {
      expect(repeater('li.suite').count()).toBe(1);
    });
    
    it('the latest testsuite show as first', function() {
      expect(repeater('li.suite:eq(0)').column('suite.name')).toEqual(["testsuite 10"]);
      expect(element('li.suite:eq(0) > span.status').attr('class')).toEqual("status red");
    });

    it('the latest ten test suites should be display started with the eleven test suite', function() {
      expect(repeater('li.suite').column('suite.name')).
          toEqual(["testsuite 10"]);
    });
  });
});
