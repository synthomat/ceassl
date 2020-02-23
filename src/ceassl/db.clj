(ns ceassl.db
  (:require [clojure.java.jdbc :as j]
            [migratus.core :as migratus]
            [environ.core :refer [env]]))

(def db-uri
  {:connection-uri (str "jdbc:" (env :db))})

(println "connecting to " db-uri)

(def migratus-config {:store                :database
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
(def get-by-id (partial j/get-by-id db-uri))

(def generate-id #(str (java.util.UUID/randomUUID)))

(defn create-target!
  [host]
  (let [id (generate-id)
        target {:id   id
                :host host}]
    (when (insert! :targets target)
      target)))

(defn get-target-by-id
  [target-id]
  (get-by-id :targets target-id))

(defn delete-all-targets!
  "Delete all targets from the database"
  []
  (execute! "truncate table targets"))

(defn delete-target-by-id!
  "docstring"
  [target-id]
  (delete! :targets ["id = ?" target-id]))

(defn list-targets
  "Fetches a list of target"
  []
  (query ["SELECT * FROM targets ORDER BY valid_until ASC, host ASC"]))

