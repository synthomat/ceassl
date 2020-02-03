(ns ceassl.db
  (:require [clojure.java.jdbc :as j]
            [migratus.core :as migratus]
            [environ.core :refer [env]]))

(def db-uri
  {:connection-uri (str "jdbc:" (env :db))})

(def migratus-config {:store                :database
                      :migration-dir        "migrations/"
                      :init-script          "init.sql"      ;script should be located in the :migration-dir path
                      :init-in-transaction? true
                      :db                   db-uri})

(migratus/migrate migratus-config)
(migratus/init migratus-config)


(def execute! (partial j/execute! db-uri))
(def query (partial j/query db-uri))
(def delete! (partial j/delete! db-uri))
(def insert! (partial j/insert! db-uri))
(def update! (partial j/update! db-uri))

(def generate-id #(java.util.UUID/randomUUID))

(defn create-target
  [host]
  (insert! :targets {:id (generate-id)
                     :host host}))

(defn list-targets
  "docstring"
  []
  (query ["SELECT * FROM targets"]))