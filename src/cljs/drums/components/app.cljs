(ns drums.app
  (:require [clojure.string :as str]
            [cljs.core.async :refer (put!)]
            [reagent.core :as r]))

;; One bar = four beats
;; one beat = four steps
;;
;; bid = Bar ID
;; sid = Step Id

(defn bar-id [beat-id sid]
  (+ (* beat-id 4) sid))

(defn active-cell? [tick bid sid]
  (= (mod tick 16) (bar-id bid sid)))

(defn beat [instrument event-channel cells tick bid]
  [:div.beat
   {:key (str "beat-" bid)}
   (for [sid (range 0 4)]
     (let [cell (get-in cells [:data (+ (* bid 4) sid)])]
       [:div.cell
        {:key (str "step-" sid)}
        [:div {:class (cond
                        (and (:sound cell) (active-cell? tick bid sid)) "cell loaded active"
                        (:sound cell) "cell loaded"
                        (active-cell? tick bid sid) "cell active"
                        :otherwise "cell")
               :on-click (fn [event] (put! event-channel [:toggle {:bid bid :sid sid :instrument instrument}]))}]]))])

(defn four-beat [instrument event-channel cells tick]
  [:div.bar
    (for [bid (range 0 4)]
      (beat instrument event-channel cells tick bid))])

