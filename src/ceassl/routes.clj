(ns ceassl.routes
  (:require [ceassl.views.base :as b]
            [ceassl.db :as db]
            [ring.util.response :refer [redirect]]
            [ceassl.monitor :as m]))


(defn dashboard
  "docstring"
  [req]
  (let [targets (db/list-targets)]
    (b/dashboard targets)))


(defn create-target
  "docstring"
  [req]
  (let [target (-> req :form-params (get "target-host"))]
    (when (not (empty? (clojure.string/trim target)))
      (db/create-target! target)
      (m/check-target target))
    (redirect "/")))

(defn delete-target
  "docstring"
  [id]
  (db/delete-target-by-id! id))
