"use strict"

navigateTo = (url) -> browser().navigateTo url
navigateToBuilds = () -> browser().navigateTo 'index.html#/builds/pussinboots/unitcover/builds'
expectClass = (expr) -> expect(element(expr).attr("class"))
expectClassAsync = (condition, expr) ->

  expect(element(expr).attr("class"))
valueInt = (repeater) -> 
  repeater.execute(()->)
  console.log(repeater.value);
  parseInt(repeater.value[0])

describe "live test", ->
  it "should redirect /index.html to /index.html/#/builds", ->
    navigateTo "/index.html"
    expect(browser().location().url()).toBe "/builds/"

  describe "the build page (index.html#/builds/pussinboots/unitcover/builds) of the unitcover project should", ->
    beforeEach -> navigateTo('index.html#/builds/pussinboots/unitcover/builds')

    it "display the latest ten builds", ->
      expect(repeater("li.build").count()).toBe 10

    it "display the latest build with properly status", ->
      expectStatus repeater("li.build").row(0), "li.build:eq(0)", (array)-> {errors:array[5], failures:[4], tests:array[3]}

  describe "the testsuite page (index.html#/builds/pussinboots/unitcover/testsuites/(id)) of the unitcover project should", ->
    beforeEach -> 
      navigateTo('index.html#/builds/pussinboots/unitcover/builds')
      element("li.build:eq(0) > span:eq(1) > a").click()

    it "display the first testsuite with status green", ->
      expectStatus repeater("li.suite").row(0), "li.suite:eq(0)", (array)-> {errors:array[3], failures:[2], tests:array[1]}

  describe "the testcase page (index.html#/builds/pussinboots/unitcover/testcases/(id)) of the unitcover project should", ->
    beforeEach -> 
      navigateTo('index.html#/builds/pussinboots/unitcover/builds')
      element("li.build:eq(0) > span:eq(1) > a").click()
      element("li.suite:eq(0) > span:eq(0) > a").click()
 
    it "display the testcase ", ->
      expect(repeater("li.testcase").count()).toBeGreaterThan 0