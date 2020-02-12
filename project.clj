(defproject ceassl "0.1"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [org.clojure/java.jdbc "0.7.11"]
                 [migratus "1.2.7"]
                 [com.h2database/h2 "1.4.200"]
                 [environ "1.1.0"]
                 [hiccup "1.0.5"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler ceassl.handler/app
         :nrepl   {:start? true}
         :reload-paths ["src"]}
  :profiles {:dev  {:dependencies [[javax.servlet/servlet-api "2.5"]
                                   [ring/ring-mock "0.3.2"]
                                   [ring/ring-devel "1.8.0"]
                                   [midje "1.9.9"]]
                    :plugins      [[lein-midje "3.2.1"]
                                   [lein-environ "1.1.0"]]
                    :env {:db "h2:tcp://localhost/~/test_db;USER=sa"}}
             :test {:env {:db "h2:mem:test_db;DB_CLOSE_DELAY=-1"}}}
  :aliases {"midje" ["with-profile" "+test" "midje"]})
