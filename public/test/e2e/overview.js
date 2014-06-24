'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('overview', function() {

  describe('build with eleven test suites', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/');
    });

    it('ten builds are display', function() {
      expect(repeater('li.build').count()).toBe(10);
    });
    
    it('the latest build with buildNumber 11 show as first', function() {
      expect(repeater('li.build:eq(0)').column('build.buildNumber')).toEqual(["1"]);
      expect(element('li.build:eq(0) > span:eq(0) > a').attr('href')).toEqual("https://github.com/otherowner/otherproject");
      expect(element('li.build:eq(0) > span:eq(2) > a').attr('href')).toEqual("#builds/otherowner/otherproject/testsuites/1");
      expect(repeater('li.build:eq(0)').column('build.project')).toEqual(["otherproject"]);
      expect(repeater('li.build:eq(0)').column('build.owner')).toEqual(["otherowner"]);
      expect(element('li.build:eq(0) > span.status').attr('class')).toEqual("status red");
    });

    it('the latest ten builds should be display', function() {
      expect(repeater('li.build').column('build.buildNumber')).
          toEqual(["1", "11", "10", "9","8", "7","6", "5","4", "3"]);
    });    
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
