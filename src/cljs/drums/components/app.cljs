(ns drums.app
  (:require [clojure.string :as str]
            [cljs.core.async :refer (put!)]
            [reagent.core :as r]))

(def button-names {:toggle "Toggle"
                   :unload-sound "Unload sound"
                   :play "Play"})

(defn sub-component [instrument event-channel cells tick]
  (for [id (range 0 8)]
    (let [cell (get cells id)]
      [:div
       {:key (str "item-" id)
        :class (cond
                 (and (:sound cell) (= (mod tick 8) id)) "item loaded active"
                 (:sound cell) "item loaded"
                 (= (mod tick 8) id) "item active"
                 :otherwise "item")}
       [:p (str "Cell " id)]
       [:button {:on-click (fn [event] (put! event-channel [:toggle {:id id :instrument instrument}]))} (:toggle button-names)]])))

