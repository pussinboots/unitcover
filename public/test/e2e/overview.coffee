"use strict"

describe "overview", ->
  describe "build with eleven test suites", ->
    beforeEach -> browser().navigateTo "index.html#/builds/"

    it "ten builds are display", -> expect(repeater("li.build").count()).toBe 10
      
    it "the latest build with buildNumber 11 show as first", ->
      expect(repeater("li.build:eq(0)").column("build.buildNumber")).toEqual ["1"]
      expect(element("li.build:eq(0) > span:eq(0) > a").attr("href")).toEqual "https://otherowner.github.io"
      expect(element("li.build:eq(0) > span:eq(1) > a").attr("href")).toEqual "https://github.com/otherowner/otherproject"
      expect(element("li.build:eq(0) > span:eq(3) > a").attr("href")).toEqual "#/builds/otherowner/otherproject/testsuites/1"
      expect(repeater("li.build:eq(0)").column("build.project")).toEqual ["otherproject"]
      expect(repeater("li.build:eq(0)").column("build.owner")).toEqual ["otherowner"]
      expect(element("li.build:eq(0) > span.status").attr("class")).toEqual "status red"

    it "the latest ten builds should be display", ->
      expect(repeater("li.build").column("build.buildNumber")).toEqual ["1","11","10","9","8","7","6","5","4","3"]
