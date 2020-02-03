(ns ceassl.handler-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [ceassl.handler :refer :all]
            [ceassl.db :refer :all]
            [environ.core :refer [env]]))



(facts "target generation"
       (fact "creating target"
             (let [host "example.com"
                   target (create-target host)]
               (:host target) => host

               (fact "fetch existing target by id"
                     (let [target-id (:id target)
                           target (get-target-by-id target-id)]
                       target => truthy))))
       (fact "fetch non-existing target by id"
             (let [target-id "non-existing"
                   target (get-target-by-id target-id)]
               target => falsey)))



#_(facts "test-app"

       (fact "main route"
             (let [response (app (mock/request :get "/"))]
               (:status response) => 200
               (:body response) => "Hello World"))

       (fact "not-found route"
             (let [response (app (mock/request :get "/invalid"))]
               (:status response) => 404)))