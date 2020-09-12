(ns ceassl.routes
  (:require [ceassl.views.base :as view]
            [ceassl.db :as db]
            [ring.util.response :refer [redirect]]
            [ceassl.monitor :as m]
            [java-time :as t]))

(defn create-target
  "docstring"
  [req]
  (let [target (-> req :form-params (get "target-host"))]
    (when (not (empty? (clojure.string/trim target)))
      (-> (db/create-target! target)
          m/check-target))
    (redirect "/")))

(defn delete-target
  "docstring"
  [id]
  (db/delete-target-by-id! id))


(defn days-diff
  "docstring"
  [date]
  (when date
    (t/time-between (t/local-date) date :days)))

(defn list-targets
  "docstring"
  [req]
  (let [targets (db/list-targets)]
    (map #(-> (select-keys % [:id :host])
              (assoc :expires_in (days-diff (:valid_until %)))
              (assoc :level (when-let [diff (days-diff (:valid_until %))]
                              (condp >= diff
                                40 "error"
                                100 "warning"
                                "success"
                                ))))
         targets)))


(defn dashboard
  "docstring"
  [req]
  (view/dashboard req)
  )