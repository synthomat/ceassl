(ns ceassl.monitor
  (:require [ceassl.db :refer :all]
            [ceassl.cert-checker :refer [get-cert-info]]
            [clojure.tools.logging :as log])
  (:import (java.time Instant)))


(defn check-all-targets
  "docstring"
  []
  (doseq [target (list-targets)]
    (let [url (str "https://" (:host target))]
      (log/debug "checking " url)
      (try
        (when-let [cert-info (get-cert-info url)]
          (let [updated-target (merge target {:valid_until (:not-after cert-info)
                                              :last_check  (Instant/now)})]
            (update! :targets updated-target ["id = ?" (:id target)])))
        (catch Exception e
          (println e))))))