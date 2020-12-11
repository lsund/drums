(ns drums.core
  (:require-macros [cljs.core.async :refer (go)])
  (:require [clojure.string :as str]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs.core.async :refer (chan put! <!)]
            [drums.app :as app]))

(def event-channel (chan))

(defonce app-state
  (r/atom
    {:tick 0
     :bpm 130
     :snares {}
     :kicks {}}))

(defn generate-audio [instrument]
  (case instrument
    :kicks (js/Audio. "media/tr909/wav/BT3A0D0.WAV")
    :snares (js/Audio. "media/tr909/wav/HHOD2.WAV")))

(defn- bpm-to-interval [bpm] (* (/ 60.0 bpm) 1000))

(def events {:toggle (fn [{:keys [id instrument]}]
                       (let [audio (get-in @app-state [instrument id :sound])]
                         (if audio
                           (swap! app-state assoc-in [instrument id :sound] nil)
                           (do
                             (let [audio (generate-audio instrument)]
                               (swap! app-state assoc-in [instrument id :sound] audio))))))
             :reset (fn [data]
                      (swap! app-state assoc :snares {})
                      (swap! app-state assoc :kicks {}))
             :tick (fn [data]
                     (swap! app-state update :tick inc)
                     (let [id (mod (:tick @app-state) 8)
                           kick (get-in @app-state [:kicks id :sound])
                           snare (get-in @app-state [:snares id :sound])]
                       (when kick
                         (. kick play))
                       (when snare
                         (. snare play))))})

(defn app []
  [:div
   [:h1 "Kick"]
   (app/sub-component :kicks event-channel (:kicks @app-state) (:tick @app-state))
   [:h1 "Snare"]
   (app/sub-component :snares event-channel (:snares @app-state) (:tick @app-state))
   [:h1 "Reset"]
   [:button {:on-click #(put! event-channel [:reset {}])} "Reset"]])

(go
  (while true
    (let [[event-name event-data] (<! event-channel)]
      ((event-name events) event-data))))

(defonce set-interval
    (js/setInterval (fn [] (put! event-channel [:tick {}])) (bpm-to-interval (:bpm @app-state))))

(rdom/render [app] (js/document.getElementById "cljs-target"))
