'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('testsuites', function() {

  it('should redirect / to /builds', function() {
    browser().navigateTo('/index.html');
    expect(browser().location().url()).toBe('/builds');
  });

  describe('build with eleven test suites', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/bankapp/testsuites/11');
    });

    it('ten test suites are display', function() {
      expect(repeater('li.build').count()).toBe(1);
    });
    
    it('the latest testsuite show as first', function() {
      expect(repeater('li.build:eq(0)').column('build.name')).toEqual(["testsuite 11"]);
      expect(element('li.build:eq(0) > span.status').attr('class')).toEqual("status green");
    });

    it('the latest ten test suites should be display started with the eleven test suite', function() {
      expect(repeater('li.build').column('build.name')).
          toEqual(["testsuite 11"]);
    });
  });
});
