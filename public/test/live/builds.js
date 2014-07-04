'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('live test', function() {

  it('should redirect /index.html to /index.html/#/builds', function() {
    browser().navigateTo('/index.html');
    expect(browser().location().url()).toBe('/builds/');
  });

  describe('the build page of the unitcover project should', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/unitcover/builds');
    });

    it('display the latest ten builds', function() {
      expect(repeater('li.build').count()).toBe(10);
    });

    it('display the latest build with status green', function() {
      expect(element('li.build:eq(0) > span.status').attr('class')).toEqual("status green");
    });
  });
  describe('the testsuite page of the unitcover project should', function() {
    
    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/unitcover/builds');
    });
    
    it('display the first testsuite with status green', function() {
      element('li.build:eq(0) > span:eq(1) > a').click();
      expect(element('li.suite:eq(0) > span.status').attr('class')).toEqual("status green");
      element('li.suite:eq(0) > span:eq(0) > a').click();
      expect(element('li.testcase:eq(0) > span.status').attr('class')).toEqual("status ng-binding green");
    });
  });
  describe('the testcase page of the unitcover project should', function() {
    
    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/unitcover/builds');
    });
    
    it('display the first testcase with status green', function() {
      element('li.build:eq(0) > span:eq(1) > a').click();
      element('li.suite:eq(0) > span:eq(0) > a').click();
      expect(element('li.testcase:eq(0) > span.status').attr('class')).toEqual("status ng-binding green");
    });
  });
});
