(ns ceassl.handler-test
  (:use midje.sweet)
  (:require [ring.mock.request :as mock]
            [ceassl.handler :refer :all]
            [ceassl.db :refer :all]
            [environ.core :refer [env]]))


(fact "creating and reading target"
      (let [host "example.com"
            target (create-target! host)]
        (:host target) => host

        (fact "fetch existing target by id"
              (let [target-id (:id target)
                    target (get-target-by-id target-id)]
                target => truthy))))


(fact "fetch non-existing target by id"
      (let [target-id "non-existing"
            target (get-target-by-id target-id)]
        target => falsey))


(facts "list targets"
       (fact "after creating targets list-targets should return a non-empty list"
             (create-target! "example.com") => truthy
             (list-targets) => not-empty)

       (fact "after deleting all targets list-targets should return an empty list"
             (delete-all-targets!)
             (list-targets) => empty?))


(fact "deleting targets by id"
      (let [target (create-target! "example.com")]
        target => truthy

        (fact "deleted target should not exist in the db anymore"
              (delete-target-by-id! (:id target))
              (get-target-by-id (:id target)) => falsey)))


#_(facts "test-app"

         (fact "main route"
               (let [response (app (mock/request :get "/"))]
                 (:status response) => 200
                 (:body response) => "Hello World"))

         (fact "not-found route"
               (let [response (app (mock/request :get "/invalid"))]
                 (:status response) => 404)))