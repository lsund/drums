(ns drums.core
  (:require
   [com.stuartsierra.component :as c]
   [drums.app :as app]
   [drums.server :as server]))

(defn new-system
  [config]
  (c/system-map :server (c/using (server/new-server (:server config))
                                 [:app])
                :app (c/using (app/new-app (:app config))
                              [])
                ))
