(ns ceassl.views.base
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ceassl.monitor :as m]
            [java-time :as t]))
(use 'ring.util.anti-forgery)

(defn html-head
  "docstring"
  []
  [:head
   [:title "Ceassl"]
   [:link {:rel "stylesheet" :href "/css/spectre/spectre.min.css"}]
   [:link {:rel "stylesheet" :href "/css/app.css"}]
   [:script (str "var csrfToken='" *anti-forgery-token* "';")]])


(def includes
  ["/js/mithril.min.js"
   "/js/microdux.js"
   "/js/app.js"])

(defn layout
  "docstring"
  [& contents]
  (html
    (html-head)
    [:body
     (for  [x includes]
       [:script {:src x}])]))


(defn dashboard
  "docstring"
  [targets]
  (layout))