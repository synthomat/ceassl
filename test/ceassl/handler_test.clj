(ns ceassl.handler-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [ceassl.handler :refer :all]
            [ceassl.db :refer :all]
            [environ.core :refer [env]]))


#_(facts "test-app"

       (fact "main route"
             (let [response (app (mock/request :get "/"))]
               (:status response) => 200
               (:body response) => "Hello World"))

       (fact "not-found route"
             (let [response (app (mock/request :get "/invalid"))]
               (:status response) => 404)))