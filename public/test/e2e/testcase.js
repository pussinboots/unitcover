'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('testcase', function() {

  describe('three test cases one passed, one failed and one error', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/pussinboots/unitcover/testcases/11');
    });

    it('five testcases should be display 3 plus 1 for failure detail message and 1 for error detail message', function() {
      expect(repeater('li.testcase').count()).toBe(5);
    });
    
    it('first test case has error', function() {
      expect(repeater('li.testcase:eq(0)').column('testcase.name')).toEqual(["testcase 11 testsuite 11 error"]);
      expect(repeater('li.testcase:eq(0)').column('testcase.className')).toEqual(["testclass"]);
      expect(repeater('li.testcase:eq(0)').column('testcase.errorMessage')).toEqual(["errorMessage"]);
      expect(element('li.testcase:eq(0) > span.status').attr('class')).toEqual("status ng-binding red");
    });

    it('second test case has failures', function() {
      expect(repeater('li.testcase:eq(1)').column('testcase.name')).toEqual(["testcase 11 testsuite 12 failure"]);
      expect(repeater('li.testcase:eq(1)').column('testcase.className')).toEqual(["testclass"]);
      expect(repeater('li.testcase:eq(1)').column('testcase.failureMessage')).toEqual(["failureMessage"]);
      expect(element('li.testcase:eq(1) > span.status').attr('class')).toEqual("status ng-binding yellow");
    });

    it('third test case is passed', function() {
      expect(repeater('li.testcase:eq(2)').column('testcase.name')).toEqual(["testcase 11 testsuite 11"]);
      expect(repeater('li.testcase:eq(2)').column('testcase.className')).toEqual(["testclass"]);
      expect(repeater('li.testcase:eq(2)').column('testcase.failureMessage')).toEqual([]);
      expect(repeater('li.testcase:eq(2)').column('testcase.errorMessage')).toEqual(["failureMessage"]);
      expect(element('li.testcase:eq(2) > span.status').attr('class')).toEqual("status ng-binding green");
    });


    it('fourth test case display detail error message', function() {
      element('.errorToggle').click()
      expect(repeater('li.testcase:eq(3)').column('testcase.name')).toEqual(["testcase 11 testsuite 11 error"]);
      expect(repeater('li.testcase:eq(3)').column('testcase.detailErrorMessage')).toEqual(["error message 11"]);
      expect(element('li.testcase:eq(3) > span.status').attr('class')).toEqual("status ng-binding red");
    });

    it('fourth test case display detail error message', function() {
      element('.failureToggle').click()
      expect(repeater('li.testcase:eq(4)').column('testcase.name')).toEqual(["testcase 11 testsuite 11 failure"]);
      expect(repeater('li.testcase:eq(4)').column('testcase.detailFailureMessage')).toEqual(["failure message 11"]);
      expect(element('li.testcase:eq(4) > span.status').attr('class')).toEqual("status ng-binding yellow");
    });
    /*it('the latest ten builds should be display', function() {
      expect(repeater('li.build').column('build.buildNumber')).
          toEqual(["1", "11", "10", "9","8", "7","6", "5","4", "3"]);
    });*/
  });


  /*describe('Phone detail view', function() {

    beforeEach(function() {
      browser().navigateTo('../../app/index.html#/phones/nexus-s');
    });


    it('should display nexus-s page', function() {
      expect(binding('phone.name')).toBe('Nexus S');
    });


    it('should display the first phone image as the main phone image', function() {
      expect(element('img.phone').attr('src')).toBe('img/phones/nexus-s.0.jpg');
    });


    it('should swap main image if a thumbnail image is clicked on', function() {
      element('.phone-thumbs li:nth-child(3) img').click();
      expect(element('img.phone').attr('src')).toBe('img/phones/nexus-s.2.jpg');

      element('.phone-thumbs li:nth-child(1) img').click();
      expect(element('img.phone').attr('src')).toBe('img/phones/nexus-s.0.jpg');
    });
  });*/
});
