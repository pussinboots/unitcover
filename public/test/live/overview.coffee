"use strict"

describe "overview live test", ->
  describe "the overview page (index.html#/builds/) the unitcover project should", ->
    beforeEach -> browser().navigateTo "index.html#/builds/"

    it "display max 10 builds", ->
      expect(repeater("li.build").count()).toBe 10