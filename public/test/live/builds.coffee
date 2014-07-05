"use strict"

navigateTo = (url) -> browser().navigateTo url
navigateToBuilds = () -> browser().navigateTo 'index.html#/builds/pussinboots/unitcover/builds'
expectClass = (expr) -> expect(element(expr).attr("class"))
describe "live test", ->
  it "should redirect /index.html to /index.html/#/builds", ->
    navigateTo "/index.html"
    expect(browser().location().url()).toBe "/builds/"

  describe "the build page (index.html#/builds/pussinboots/unitcover/builds) of the unitcover project should", ->
    beforeEach -> navigateTo('index.html#/builds/pussinboots/unitcover/builds')

    it "display the latest ten builds", ->
      expect(repeater("li.build").count()).toBe 10

    it "display the latest build with status green", ->
      expectClass("li.build:eq(0) > span.status").toEqual "status green"

  describe "the testsuite page (index.html#/builds/pussinboots/unitcover/testsuites/(id)) of the unitcover project should", ->
    beforeEach -> navigateTo('index.html#/builds/pussinboots/unitcover/builds')
    
    it "display the first testsuite with status green", ->
      element("li.build:eq(0) > span:eq(1) > a").click()
      expectClass("li.suite:eq(0) > span.status").toEqual "status green"
      element("li.suite:eq(0) > span:eq(0) > a").click()
      expectClass("li.testcase:eq(0) > span.status").toEqual "status ng-binding green"

  describe "the testcase page (index.html#/builds/pussinboots/unitcover/testcases/(id)) of the unitcover project should", ->
    beforeEach -> navigateTo('index.html#/builds/pussinboots/unitcover/builds')
 
    it "display the first testcase with status green", ->
      element("li.build:eq(0) > span:eq(1) > a").click()
      element("li.suite:eq(0) > span:eq(0) > a").click()
      expectClass("li.testcase:eq(0) > span.status").toEqual "status ng-binding green"
