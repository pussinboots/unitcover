'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('UnitCover', function() {

  it('should redirect products.html to products.html#/builds', function() {
    browser().navigateTo('products.html');
    expect(browser().location().url()).toBe('/builds');
  });

  describe('build with eleven test suites', function() {

    beforeEach(function() {
      browser().navigateTo('products.html#/builds/pussinboots/bankapp');
    });

    it('ten test suites are display', function() {
      expect(repeater('li.build').count()).toBe(10);
    });

    //problem that field name_enc is empty for that expect here but than setted right
    it('the latest ten test suites should be display started with the eleven test suite', function() {
      expect(repeater('li.build').column('build.name')).
          toEqual(["testsuite 11", "testsuite 10", "testsuite 9","testsuite 8", "testsuite 7","testsuite 6", "testsuite 5","testsuite 4", "testsuite 3", "testsuite 2"]);
    });

    describe('display test cases of complete test suite', function() {

      beforeEach(function() {
        browser().navigateTo('products.html#/builds/pussinboots/bankapp/1');
      });

      it('ten test cases are display', function() {
        expect(repeater('li.tescase').count()).toBe(10);
      });

      //problem that field name_enc is empty for that expect here but than setted right
      it('display all test cases', function() {
        expect(repeater('li.testcase').column('testcase.name')).
            toEqual(["testsuite 11", "testsuite 10", "testsuite 9","testsuite 8", "testsuite 7","testsuite 6", "testsuite 5","testsuite 4", "testsuite 3", "testsuite 2"]);
      });
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
