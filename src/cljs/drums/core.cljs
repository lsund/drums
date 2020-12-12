(ns drums.core
  (:require-macros [cljs.core.async :refer (go)])
  (:require [clojure.string :as str]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs.core.async :refer (chan put! <!)]
            [cljs.pprint :refer (pprint)]
            [drums.app :as app]))

(def event-channel (chan))


;; The :instruments have one static part and one dynamic part.
;; [:instruments instrument :config] is static and should not be changed during runtime
;; [:instruments instrument :data] holds information on which steps that are active for
;; the instrument.
(defonce app-state
  (r/atom
    {:tick 0
     :bpm 130
     :interval nil
     :instruments {:kick {:config {:url "./media/kick.wav" :volume "1.0"} :data {}}
                   :hh-open {:config {:url "./media/highhat-closed.wav" :volume "0.1"} :data {}}
                   :hh-closed {:config {:url "./media/highhat-open.wav" :volume "0.1"} :data {}}}}))

(defn generate-audio [instrument bar-id]
  (let [audio (js/Audio. (get-in @app-state [:instruments instrument :config :url]))]
    (set! audio -volume (get-in @app-state [:instruments instrument  :config :volume]))
    (. audio addEventListener "canplaythrough" (fn [] (swap! app-state assoc-in [instrument bar-id :sound :ready] true)))
    audio))


(defn- bpm-to-interval [bpm] (* (/ 60.0 4.0 bpm) 1000))

(defn- play-audio [audio]
  (. audio play))

(def events {:toggle (fn [{:keys [bid sid instrument]}]
                       (let [audio (get-in @app-state [:instruments instrument :data (app/bar-id bid sid) :sound])]
                         (if audio
                           (swap! app-state assoc-in [:instruments instrument :data (app/bar-id bid sid) :sound] nil)
                           (do
                             (let [audio (generate-audio instrument (app/bar-id bid sid))]
                               (swap! app-state assoc-in [:instruments instrument :data (app/bar-id bid sid) :sound] {:source audio :ready true}))))))
             :reset (fn [data]
                      (doseq [instrument (keys (:instruments :app-state))]
                        (swap! app-state assoc-in [:instruments instrument :data] {})))
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
                     (let [id (mod (:tick @app-state) 16)]
                       (doseq [instrument (keys (:instruments @app-state))]
                         (let [{:keys [source ready]} (get-in @app-state [:instruments instrument :data id :sound])]
                           (when (and source ready)
                             (play-audio source))))))})

(defn app []
  ; (pprint @app-state)
  [:div.main
   [:div.header (str "BPM: " (:bpm @app-state))]
   (app/four-beat :kick event-channel (get-in @app-state [:instruments :kick]) (:tick @app-state))
   (app/four-beat :hh-open event-channel (get-in @app-state [:instruments :hh-open]) (:tick @app-state))
   (app/four-beat :hh-closed event-channel (get-in @app-state [:instruments :hh-closed]) (:tick @app-state))
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
