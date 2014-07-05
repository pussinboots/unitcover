"use strict"

describe "testsuites", ->
  it "should redirect /index.html to /index.html/#builds/", ->
    browser().navigateTo "/index.html"
    expect(browser().location().url()).toBe "/builds/"

  describe "passed testsuite", ->
    beforeEach -> browser().navigateTo "index.html#/builds/pussinboots/bankapp/testsuites/7"

    it "ten test suites are display", ->
      expect(repeater("li.suite").count()).toBe 1

    it "the latest testsuite show as first", ->
      expect(repeater("li.suite:eq(0)").column("suite.name")).toEqual ["testsuite 7"]
      expect(element("li.suite:eq(0) > span.status").attr("class")).toEqual "status green"

    it "the latest ten test suites should be display started with the eleven test suite", ->
      expect(repeater("li.suite").column("suite.name")).toEqual ["testsuite 7"]

  describe "failed testsuite", ->
    beforeEach ->
      browser().navigateTo "index.html#/builds/pussinboots/bankapp/testsuites/8"

    it "one test suite should be displayed", ->
      expect(repeater("li.suite").count()).toBe 1

    it "the latest testsuite show as first", ->
      expect(repeater("li.suite:eq(0)").column("suite.name")).toEqual ["testsuite 8"]
      expect(element("li.suite:eq(0) > span.status").attr("class")).toEqual "status yellow"

    it "the latest ten test suites should be display started with the eleven test suite", ->
      expect(repeater("li.suite").column("suite.name")).toEqual ["testsuite 8"]

  describe "error testsuite", ->
    beforeEach ->
      browser().navigateTo "index.html#/builds/pussinboots/bankapp/testsuites/10"

    it "one test suite should be displayed", ->
      expect(repeater("li.suite").count()).toBe 1

    it "the latest testsuite show as first", ->
      expect(repeater("li.suite:eq(0)").column("suite.name")).toEqual ["testsuite 10"]
      expect(element("li.suite:eq(0) > span.status").attr("class")).toEqual "status red"
      return

    it "the latest ten test suites should be display started with the eleven test suite", ->
      expect(repeater("li.suite").column("suite.name")).toEqual ["testsuite 10"]
