(ns drums.app
  (:require
   [com.stuartsierra.component :as c]
   [compojure.handler :refer [site]]
   [drums.handler :as handler]))

(defrecord App [handler app-config]
  c/Lifecycle

  (start [component]
    (if handler
      component
      (do
        (println ";; [App] Starting, attaching handler")
        (println ";; comp: " component)
        (assoc component
               :handler
               (handler/new-handler (merge app-config))))))

  (stop [component]
    (println ";; [App] Stopping")
    (println ";; comp: " component)
    (assoc component :handler nil)))

(defn new-app
  [config]
  (map->App {:app-config config}))
