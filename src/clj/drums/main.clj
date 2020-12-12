(ns drums.main
  "Main entry point"
  (:require
   [drums.core :refer [new-system]]
   [com.stuartsierra.component :as c])
  (:gen-class))

(defn -main [& args]
  (c/start (new-system {}))
  (println "Server up and running"))
