'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('builds', function() {

  it('should redirect products.html to products.html#/builds', function() {
    browser().navigateTo('products.html');
    expect(browser().location().url()).toBe('/builds');
  });

  describe('build with eleven test suites', function() {

    beforeEach(function() {
      browser().navigateTo('products.html#/builds/pussinboots/bankapp/builds');
    });

    it('ten builds are display', function() {
      expect(repeater('li.build').count()).toBe(10);
    });
    
    it('the latest build with buildNumber 11 show as first', function() {
      expect(repeater('li.build:eq(0)').column('build.buildNumber')).toEqual(["11"]);
    });

    it('the latest ten builds should be display', function() {
      expect(repeater('li.build').column('build.buildNumber')).
          toEqual(["11", "10", "9","8", "7","6", "5","4", "3", "2"]);
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
