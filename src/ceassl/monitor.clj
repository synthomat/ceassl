(ns ceassl.monitor
  (:require [ceassl.db :refer :all]
            [ceassl.cert-checker :refer [get-cert-info]]
            [clojure.tools.logging :as log])
  (:import (java.time Instant)))


(defn check-target
  "docstring"
  [target]
  (let [url (str "https://" (:host target))]
    (log/debug "checking " url)
    (try
      (when-let [cert-info (get-cert-info url)]
        (let [updated-target (-> (merge target {:valid_until (:not-after cert-info)
                                                :valid_after (:not-before cert-info)
                                                :last_check  (Instant/now)})
                                 (dissoc :validity_percent))]
          (update! :targets updated-target ["id = ?" (:id target)])))
      (catch Exception e
        (println e)))))

(def last-check (atom nil))

(defn check-all-targets
  "docstring"
  []
  (doseq [target (list-targets)]
    (check-target target))
  (reset! last-check (Instant/now)))


(def running (atom true))

(defn stop-monitor
  []
  (log/debug "Stopping Monitor")
  (reset! running false))

(defn start-monitor
  ([] (start-monitor 60))
  ([sleep-minutes]
   (when @running
     (log/debug "Stopping old monitor…")
     (stop-monitor))

   (log/debug (str "Starting Monitor with " sleep-minutes "min interval"))
   (reset! running true)
   (future (loop []
             (check-all-targets)

             (Thread/sleep (* sleep-minutes 60 1000))
             (if @running
               (recur)
               (log/debug "Monitor stopped"))))))