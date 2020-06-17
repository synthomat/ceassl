(ns ceassl.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :as resp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ceassl.routes :as r]
            [ceassl.monitor :as m]))


(defroutes app-routes
           (GET "/" [] r/dashboard)
           (context "/targets" []
             (POST "/create" [] r/create-target)
             (DELETE "/:id" [id] (r/delete-target id)))
           (GET "/settings" [] (resp/redirect "/settings/general"))
           (GET "/settings/:sub" [sub] (r/settings sub))
           (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)))

(m/start-monitor)