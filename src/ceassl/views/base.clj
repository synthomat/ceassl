(ns ceassl.views.base
  (:require [hiccup.core :refer [html]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]))
(use 'ring.util.anti-forgery)

(defn layout
  "docstring"
  [content]
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
       content]]]))


(defn hosts-table
  "docstring"
  [hosts])

(defn create-form
  "docstring"
  []
  [:div
   [:button#create-btn.btn.btn-primary "Create"]])

(defn dashboard
  "docstring"
  [targets]

  (let [row (fn [n] [:tr
                     [:td (:host n)]
                     [:td (:created_at n)]
                     [:td (:last_check n)]
                     [:td [:a {:href "#" :onclick (str "return deleteTarget('" (:id n) "')") :class "text-error"} [:i.icon.icon-delete]]]])
        target-list [:table.table [:thread [:tr
                                            [:th "Host"]
                                            [:th "Created At"]
                                            [:th "Last checked"]
                                            [:th "Action"]]] [:tbody (map row targets)]]]
    (layout
      [:div
       target-list

       [:div.modal
        [:a.modal-overlay {:href "#close" :aria-label "Close"}]
        [:div.modal-container
         [:div.modal-header
          [:a.btn.btn-clear.float-right {:href "#close" :aria-label "Close"}]
          [:div.modal-title.h5 "New Host"]]
         [:div.modal-body
          [:div.content
           "Create a new host for tracking certificates"
           [:div.form-group
            [:label.form-label {:for "input-example-1"} "Host Name"]
            [:input#input-example-1.form-input {:type "text" :placeholder "e.g. example.com"}]]]]
         [:div.modal-footer
          [:button.btn.btn-primary.ok "Create Host"]]]]])))