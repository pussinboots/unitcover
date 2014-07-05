"use strict"

describe "builds", ->
  it "should redirect /index.html to /index.html/#/builds", ->
    browser().navigateTo "/index.html"
    expect(browser().location().url()).toBe "/builds/"

  describe "build with eleven test suites", ->
    beforeEach -> browser().navigateTo "index.html#/builds/pussinboots/bankapp/builds"

    it "ten builds are display", -> expect(repeater("li.build").count()).toBe 10

    #todo added test of correct link to testsuite, link to travis and correct error
    it "the latest build with buildNumber 11 show as first", ->
      expect(repeater("li.build:eq(0)").column("build.buildNumber")).toEqual ["11"]
      expect(element("li.build:eq(0) > span.status").attr("class")).toEqual "status red"

    it "the latest ten builds should be display", ->
      expect(repeater("li.build").column("build.buildNumber")).toEqual ["11","10","9","8","7","6","5","4","3","2"]
