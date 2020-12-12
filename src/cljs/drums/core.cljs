(ns drums.core
  (:require-macros [cljs.core.async :refer (go)])
  (:require [clojure.string :as str]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs.core.async :refer (chan put! <!)]
            [cljs.pprint :refer (pprint)]
            [drums.app :as app]))

(def event-channel (chan))

(defn- bpm-to-interval [bpm] (* (/ 60.0 4.0 bpm) 1000))

(defonce app-state
  (r/atom
    {:tick 0
     :bpm 130
     :interval nil
     :snares {}
     :kicks {}}))

(defn generate-audio [instrument]
  (case instrument
    :kicks (js/Audio. "media/tr909/wav/BT3A0D0.WAV")
    :snares (let [audio (js/Audio. "media/tr909/wav/HHOD2.WAV")]
              (set! audio -volume "0.1")
              audio)))

(def events {:toggle (fn [{:keys [bid sid instrument]}]
                       (let [audio (get-in @app-state [instrument (app/bar-id bid sid) :sound])]
                         (if audio
                           (swap! app-state assoc-in [instrument (app/bar-id bid sid) :sound] nil)
                           (do
                             (let [audio (generate-audio instrument)]
                               (swap! app-state assoc-in [instrument (app/bar-id bid sid) :sound] audio))))))
             :reset (fn [data]
                      (swap! app-state assoc :snares {})
                      (swap! app-state assoc :kicks {}))
             :stop (fn [data]
                     )
             :toggle-start (fn [data]
                             (if (:interval @app-state)
                               (do
                                 (js/clearInterval (:interval @app-state))
                                 (swap! app-state assoc :interval nil))
                              (swap! app-state assoc :interval (js/setInterval (fn [] (put! event-channel [:tick {}])) (bpm-to-interval (:bpm @app-state))))))
             :change-speed (fn [{:keys [amount op]}]
                             (swap! app-state update :bpm #((case op :inc + -) % amount))
                             (when (:interval @app-state)
                               (put! event-channel [:toggle-start {}])
                               (put! event-channel [:toggle-start {}])))
             :tick (fn []
                     (swap! app-state update :tick inc)
                     (let [id (mod (:tick @app-state) 16)
                           kick (get-in @app-state [:kicks id :sound])
                           snare (get-in @app-state [:snares id :sound])]
                       (when kick
                         (. kick play))
                       (when snare
                         (. snare play))))})

(defn app []
  (pprint @app-state)
  [:div.main
   [:div.header (str "BPM: " (:bpm @app-state))]
   (app/four-beat :kicks event-channel (:kicks @app-state) (:tick @app-state))
   (app/four-beat :snares event-channel (:snares @app-state) (:tick @app-state))
   [:div.separator]
   [:div.buttons
    [:button
     {:on-click #(put! event-channel [:toggle-start {}])}
     (if (:interval @app-state) "Stop" "Start")]
    [:button {:on-click #(put! event-channel [:change-speed {:amount 10 :op :inc}])} "Speed up"]
    [:button {:on-click #(put! event-channel [:change-speed {:amount 10 :op :dec}])} "Slow down"]
    [:button {:on-click #(put! event-channel [:reset {}])} "Reset"]]])

;; Start event loop
(go
  (while true
    (let [[event-name event-data] (<! event-channel)]
      ((event-name events) event-data))))

;; Render app
(rdom/render [app] (js/document.getElementById "cljs-target"))
