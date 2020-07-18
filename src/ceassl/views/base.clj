(ns ceassl.views.base
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ceassl.monitor :as m]
            [java-time :as t]))
(use 'ring.util.anti-forgery)

(defn footer
  []
  [:div.footer {:style "margin-top: 80px"}
   [:div.container
    [:div.columns
     [:div.column.col-12.col-mr-auto.text-center.text-secondary
      [:p "Ceassl © 2020 | contribute @ " [:a.text-gray {:href "https://github.com/synthomat/ceassl"} "Github"]]]]]])

(defn html-head
  "docstring"
  []
  [:head
   [:title "Ceassl"]
   [:link {:rel "stylesheet" :href "/css/spectre/spectre.min.css"}]
   [:link {:rel "stylesheet" :href "/css/spectre/spectre-icons.min.css"}]
   [:link {:rel "stylesheet" :href "/css/app.css"}]
   [:script (str "var csrfToken='" *anti-forgery-token* "';")]
   [:script {:src "/js/app.js"}]])


(defn add-target-form
  []
  [:form.form {:action "/targets/create" :method "post"}
   (anti-forgery-field)
   [:div.input-group.input-inline
    [:input.form-input {:name "target-host" :type "text" :placeholder "example.com"}]
    [:button.btn.btn-primary.input-group-btn "Add"]]])

(defn layout
  "docstring"
  [& contents]
  (html
    (html-head)
    [:body
     [:header.navbar
      [:section.navbar-section
       [:a.navbar-brand.mr-2 {:href "/"} "Ceassl"]]
      [:section.navbar-section
       (add-target-form)]
      ;[:a {:href "/settings"} "Settings"]
      ]
     [:div.container
      contents]
     (footer)]))


(defn target-row
  "docstring"
  [target]
  [:tr
   [:td [:a {:href (str "https://" (:host target)) :target "_blank"} (:host target)]]
   [:td [:span {:title (str "expires on " (:valid_until target))} (when-let [ein (:valid_until target)] (t/time-between (t/local-date) ein :days)) " days"]]
   [:td
    [:a {:href "#" :onclick (format "return deleteTarget('%s')" (:id target)) :class "text-error"}
     [:i.icon.icon-delete]]]])

(defn targets-table
  "docstring"
  [targets]
  [:table.table
   [:thead
    [:tr
     [:th "Host"]
     [:th "Expires in"]
     [:th "Action"]]]
   [:tbody
    (map target-row targets)]])

(defn dashboard
  "docstring"
  [targets]
  (layout
    [:div [:strong "last check: "] @m/last-check]
    [:div
     (targets-table targets)]))