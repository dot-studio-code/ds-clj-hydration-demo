(ns hydrationtest.build
  (:require [rum.core :as rum]
            [hydrationtest.app :as app]))

(println "hello")

(spit "./public/index.html" (rum/render-html (app/app "helloooo")))