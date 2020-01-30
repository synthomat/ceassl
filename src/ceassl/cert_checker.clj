(ns ceassl.cert-checker
  (:require [clojure.java.io :as io])
  (:import (javax.net.ssl HttpsURLConnection)
           (java.net URL)
           (java.security.cert X509Certificate)))

(defn fetch-certs-from-url
  [url-str]
  (let [url ^URL (io/as-url url-str)
        conn ^HttpsURLConnection (.openConnection url)]
    (with-open [_ (.getInputStream conn)]
      (.getServerCertificates conn)))
  )

(defn get-cert-info
  [url]
  (let [certs (fetch-certs-from-url url)
        cert  ^X509Certificate (first certs)]
    {:not-after  (.getNotAfter cert)
     :not-before (.getNotBefore cert)
     :principal  (-> cert .getIssuerX500Principal .getName)
     :subject  (-> cert .getSubjectX500Principal .getName)
     :alternative-names (->> cert .getSubjectAlternativeNames (map second))}))


(def hosts
  ["https://google.com"
   "https://synthomat.de"
   "https://app.sparklebase.com"
   "https://uberspace.de"
   "https://lostprofile.de"
   "https://amazon.de"
   "https://clojure.org"
   "https://slack.com"])

(defn -main
  "docstring"
  []
  (let [ress (map #(future [% (get-cert-info %)]) hosts)]
    (doseq [res ress]
      (println @res )))

  (shutdown-agents)
  )
