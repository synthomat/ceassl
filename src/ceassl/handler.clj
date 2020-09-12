(ns ceassl.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ceassl.routes :as r]
            [ceassl.monitor :as m]
            [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))


(defroutes app-routes
           (GET "/" [] r/dashboard)
           (context "/api" req
             (context "/targets" []
               (GET "/" [] (response (r/list-targets req)))
               (DELETE "/:id" [id] (r/delete-target id))))
           
           (context "/targets" []
             (POST "/create" [] r/create-target))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      wrap-json-response
      (wrap-json-body)
      (wrap-defaults site-defaults)))

(m/start-monitor)