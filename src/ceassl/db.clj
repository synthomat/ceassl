(ns ceassl.db
  (:require [clojure.java.jdbc :as j]
            [migratus.core :as migratus]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log])
  (:import [java.time LocalDate LocalTime OffsetDateTime ZoneOffset]
           (java.util UUID)))


(def db-uri
  {:connection-uri (str "jdbc:" (or (env :db) "h2:./ceassl"))})

(log/info "Connecting to database " db-uri)

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

(def generate-id #(str (UUID/randomUUID)))

(defn- h2-timestamp-with-time-zone->offset-date-time
  "Convert a h2 `TimestampWithTimeZone` to a `java.time.OffsetDateTime`.
  This preserves the offset information and is probably the cleanest
  way to map `org.h2.api.TimestampWithTimeZone` to a `java.time` data
  structure.
  As the `h2-timestamp` doesn't give us the time in (hours, minutes,
  seconds) but instead in nanos since midnight, we have to create a
  temporary `LocalTime` instance to convert this to a usable time
  first. That in turn causes us to have to create a `LocalDate` and
  `ZoneOffset` instance to be passed into the constructor of
  `OffsetDateTime`. It could be interesting whether this becomes
  problematic on a larger scale because these temporary instances will
  have to be GC'ed."
  [^org.h2.api.TimestampWithTimeZone h2-timestamp]
  (OffsetDateTime/of
    (LocalDate/of (.getYear h2-timestamp)
                  (.getMonth h2-timestamp)
                  (.getDay h2-timestamp))
    (LocalTime/ofNanoOfDay (.getNanosSinceMidnight h2-timestamp))
    (ZoneOffset/ofTotalSeconds (* 60 (.getTimeZoneOffsetMins h2-timestamp)))))

(extend-protocol j/IResultSetReadColumn
  org.h2.api.TimestampWithTimeZone
  (result-set-read-column [v _2 _3]
    (h2-timestamp-with-time-zone->offset-date-time v)))

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

(defn calculate-percentage
  [target]

  (try
    (Math/round (* 100 (float (/ (- (.toEpochSecond (OffsetDateTime/now)) (.toEpochSecond (:valid_after target)))
                                 (- (.toEpochSecond (:valid_until target)) (.toEpochSecond (:valid_after target)))))))
    (catch Exception e nil)))


(defn list-targets
  "Fetches a list of target"
  []
  (query ["SELECT * FROM targets ORDER BY valid_until ASC, host ASC"]
         {:row-fn #(assoc % :validity_percent (calculate-percentage %))}))

