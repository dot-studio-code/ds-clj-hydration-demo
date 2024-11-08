(ns sfdb.app
  (:require [rum.core :as rum :refer [defc reactive react]]
            #?(:cljs [cljs.reader :as re :refer [read-string]])))

;; we make some state, that will hold the symbol of 
;;  the react component along with the react component.
;;  this will let the component call itself during hydration
(def component-register (atom {}))

;; plumbing function to update the component-register atom
(defn register-hydrator [component-sym]
  (->> (assoc @component-register (str component-sym) component-sym)
       (reset! component-register)))

  ;; most clojure data is by default serializable so we can just drop it in the html data. 
   ;; but some datatypes might want special treatment in the future
(defn serialize [args] args)

(defn de-serialize [args] (read-string args))

(defc dry
  "freeze-dries a components arguments into div [data] attributes, along with the symbol
   of the component needed to hydrate it."
  [component-sym & args]
  [:div {:data-hydrate (serialize args)
         :data-component (serialize component-sym)}
   (apply component-sym args)])

;; only in js, hydrate an DOM element with data frozen in it's attributes
;;  and a component from the component register
(defn hydrate-component [js-el]
  #?(:cljs
     (let [comp-sym (.getAttribute js-el "data-component")
           component (@component-register comp-sym)
           args (clj->js (de-serialize (.getAttribute js-el "data-hydrate")))]
       (rum/hydrate (apply  component args) js-el))))

;; only in js, hydrate dried components from their freeze-dried args
(defn hydrate-dried-components! []
  #?(:cljs
     ;; any DOM item that has freeze-dried data get's reified
     (let [elemants (js/document.querySelectorAll "[data-hydrate]")]
       (doall
        (for [el elemants]
          (hydrate-component el))))))

;; now the application!

(def initial-count (atom 0))

(defc counter < reactive [counter-name]
  [:div
   counter-name
   [:div {:on-click (fn [_] (swap! initial-count inc))}
    "Clicks: " (react initial-count)]])

(register-hydrator #'counter)

(defc app [name]
  [:html
   [:body
    "hello " name
    [:div (dry #'counter "First counter")]
    [:div (dry #'counter "Second Counter")]
    [:div (dry #'counter "Third counter")]]
   [:script {:type "application/javascript"
             :src "/assets/js/main.js"}]])

;; init function for the js applications
(defn init []
  (println "whoa! initializing the application")
  (hydrate-dried-components!))

