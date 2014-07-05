"use strict"

describe "testcase", ->
  describe "three test cases one passed, one failed and one error", ->
    beforeEach -> browser().navigateTo "index.html#/builds/pussinboots/unitcover/testcases/11"
   
    it "five testcases should be display 3 plus 1 for failure detail message and 1 for error detail message", ->
      expect(repeater("li.testcase").count()).toBe 5

    it "first test case has error", ->
      expect(repeater("li.testcase:eq(0)").column("testcase.name")).toEqual ["testcase 11 testsuite 11 error"]
      expect(repeater("li.testcase:eq(0)").column("testcase.className")).toEqual ["testclass"]
      expect(repeater("li.testcase:eq(0)").column("testcase.errorMessage")).toEqual ["errorMessage"]
      expect(element("li.testcase:eq(0) > span.status").attr("class")).toEqual "status ng-binding red"

    it "second test case has failures", ->
      expect(repeater("li.testcase:eq(1)").column("testcase.name")).toEqual ["testcase 11 testsuite 11 failure"]
      expect(repeater("li.testcase:eq(1)").column("testcase.className")).toEqual ["testclass"]
      expect(repeater("li.testcase:eq(1)").column("testcase.failureMessage")).toEqual ["failureMessage"]
      expect(element("li.testcase:eq(1) > span.status").attr("class")).toEqual "status ng-binding yellow"

    it "third test case is passed", ->
      expect(repeater("li.testcase:eq(2)").column("testcase.name")).toEqual ["testcase 11 testsuite 11"]
      expect(repeater("li.testcase:eq(2)").column("testcase.className")).toEqual ["testclass"]
      expect(repeater("li.testcase:eq(2)").column("testcase.failureMessage")).toEqual []
      expect(repeater("li.testcase:eq(2)").column("testcase.errorMessage")).toEqual []
      expect(element("li.testcase:eq(2) > span.status").attr("class")).toEqual "status ng-binding green"

    it "fourth test case display detail error message", ->
      element(".errorToggle").click()
      expect(repeater("li.testcase:eq(3)").column("testcase.name")).toEqual ["testcase 11 testsuite 11 error"]
      expect(repeater("li.testcase:eq(3)").column("testcase.detailErrorMessage")).toEqual ["error message 11"]
      expect(element("li.testcase:eq(3) > span.status").attr("class")).toEqual "status ng-binding red"

    it "fourth test case display detail error message", ->
      element(".failureToggle").click()
      expect(repeater("li.testcase:eq(4)").column("testcase.name")).toEqual ["testcase 11 testsuite 11 failure"]
      expect(repeater("li.testcase:eq(4)").column("testcase.detailFailureMessage")).toEqual ["failure message 11"]
      expect(element("li.testcase:eq(4) > span.status").attr("class")).toEqual "status ng-binding yellow"
