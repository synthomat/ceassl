(ns ceassl.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ceassl.routes :as r]
            [ceassl.monitor :as m]))


(defroutes app-routes
           (GET "/" [] r/dashboard)
           (POST "/targets/create" [] r/create-target)
           (DELETE "/targets/:id" [id] (r/delete-target id))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)))

(m/start-monitor)