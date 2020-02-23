(ns ceassl.views.base
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [ceassl.monitor :as m]))
(use 'ring.util.anti-forgery)

(defn layout
  "docstring"
  [& contents]
  (html
    [:html
     [:head
      [:title "Ceassl"]
      [:link {:rel "stylesheet" :href "/css/spectre/spectre.min.css"}]
      [:link {:rel "stylesheet" :href "/css/spectre/spectre-icons.min.css"}]
      [:link {:rel "stylesheet" :href "/css/app.css"}]
      [:script (str "var csrfToken='" *anti-forgery-token* "';")]
      [:script {:src "/js/app.js"}]

      ]
     [:body
      [:header.navbar
       [:section.navbar-section
        [:a.navbar-brand.mr-2 {:href "/"} "Ceassl"]]
       [:section.navbar-section
        [:form.form {:action "/targets/create" :method "post"}
         (anti-forgery-field)
         [:div.input-group.input-inline
          [:input.form-input {:name "target-host" :type "text" :placeholder "example.com"}]
          [:button.btn.btn-primary.input-group-btn "Create"]]]]]
      [:div.container
       contents]
      [:div.footer {:style "margin-top: 80px"}
       [:div.container
        [:div.columns
         [:div.column.col-12.col-mr-auto.text-center.text-secondary
          [:p "Ceassl © 2020 | contribute @ " [:a.text-gray {:href "https://github.com/synthomat/ceassl"} "Github"]]]]]]]]))


(defn dashboard
  "docstring"
  [targets]

  (let [row (fn [n] [:tr
                     [:td (:host n) ]
                     [:td (:valid_until n)]
                     [:td
                      (when-let [perc (:validity_percent n)]
                        [:div.bar {:style "width: 100px"}
                         [:div.bar-item {:style (str "width:" perc "%;")}]])
                      ]
                     [:td [:a {:href "#" :onclick (str "return deleteTarget('" (:id n) "')") :class "text-error"} [:i.icon.icon-delete]]]])
        target-list [:table.table [:thread [:tr
                                            [:th "Host"]
                                            [:th "Valid until"]
                                            [:th "Validity"]
                                            [:th "Action"]]] [:tbody (map row targets)]]]
    (layout
      [:div [:strong "last check: "] @m/last-check]
      [:div
       target-list
       ])))