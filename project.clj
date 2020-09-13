(defproject ceassl "0.3"
  :description "SSL/TLS Certificate Expiration Monitoring"
  :url "https://github.com/synthomat/ceassl"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [seancorfield/next.jdbc "1.0.13"]
                 [migratus "1.2.7"]
                 [com.h2database/h2 "1.4.200"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]
                 [cheshire "5.10.0"]
                 [ring/ring-json "0.5.0"]
                 [org.clojure/tools.logging "0.6.0"]
                 [ch.qos.logback/logback-classic "1.3.0-alpha5"]
                 [clojure.java-time "0.3.2"]]

  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/slf4j-factory"]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler ceassl.handler/app
         :nrepl   {:start? true}
         :reload-paths ["src"]}
  :profiles {:dev  {:dependencies [[javax.servlet/servlet-api "2.5"]
                                   [ring/ring-mock "0.3.2"]
                                   [ring/ring-devel "1.8.0"]
                                   [midje "1.9.9"]
                                   [ring/ring-jetty-adapter "1.6.3"]]
                    :plugins      [[lein-midje "3.2.1"]
                                   [lein-environ "1.1.0"]]
                    :env {:db "h2:tcp://localhost/./ceassl;USER=sa;PASSWORD=sa"}}
             :test {:env {:db "h2:mem:test_db;DB_CLOSE_DELAY=-1"}}}
  :aliases {"midje" ["with-profile" "+test" "midje"]})
