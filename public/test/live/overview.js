'use strict';

/* http://docs.angularjs.org/guide/dev_guide.e2e-testing */

describe('overview live test', function() {

  describe('the overview page should', function() {

    beforeEach(function() {
      browser().navigateTo('index.html#/builds/');
    });

    it('display max 10 builds', function() {
      expect(repeater('li.build').count()).toBe(10);
    }); 
  });
});
