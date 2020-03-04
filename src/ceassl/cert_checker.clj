(ns ceassl.cert-checker
  (:require [clojure.java.io :as io])
  (:import (javax.net.ssl HttpsURLConnection)
           (java.net URL)
           (java.security.cert X509Certificate)))

(defn fetch-certs
  "Fetches certificates from a specified URL which must start with 'https'"
  [url-str]
  (let [url ^URL (io/as-url url-str)
        conn ^HttpsURLConnection (.openConnection url)]
    (.connect conn)
    (.getServerCertificates conn)))

(defn extract-cert-info
  [^X509Certificate cert ]
  {:not-after         (.getNotAfter cert)
   :not-before        (.getNotBefore cert)
   :principal         (-> cert .getIssuerX500Principal .getName)
   :subject           (-> cert .getSubjectX500Principal .getName)
   :alternative-names (->> cert .getSubjectAlternativeNames (map second))})



(defn get-cert-info
  "docstring"
  [url]
  (-> (fetch-certs url)
      first
      extract-cert-info))