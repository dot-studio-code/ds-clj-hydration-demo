(ns sfdb.build
  (:require [rum.core :as rum]
            [sfdb.app :as app]))

(println "hello")

(spit "./public/index.html" (rum/render-html (app/app "helloooo")))