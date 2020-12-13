(ns drums.handler
  (:require [clojure.string :as string]
            [compojure.core :refer [GET POST routes]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.page :refer [html5 include-css]]
            [ring.util.response :refer [redirect]]))

(defn- app-routes [{:keys [] :as config}]
  (routes
    (GET "/" [all] (html5 [:head [:title "drums"]]
                          [:body
                           [:div#cljs-target]
                           [:div.description
                            [:div.text
                             [:p "A drum machine is used compose drum patterns. You can use these patterns
                                 example when producing a new piece of electronic music, or as a complement if
                                 your band lacks a drummer."]

                             [:p "The basic layout of a drum machine is a sequence of buttons (mostly 16) laid
                                 out in a row. Each button represents a *step* (or a 16th note), 4 steps makes
                                 up one  *beat* and four beats make up one *bar*. When you press *play* on the
                                 drum machine, playback starts. This is visualized by each step-buttons lighting
                                 up shortly at the corresponding 16th note in the playback loop."]

                             [:p "You won't hear anything yet though, a drum machine only plays the corresponding
                                 note if you press one of the step-buttons to *activate* it.  A typical drum
                                 machine gives you access to multiple tracks. Each track can have a different
                                 instrument associated to it, so that you can program Kick, snare and high-hat
                                 at the same time, for example."]]]
                           [:div#footer-javascripts
                            [:script {:src "client/goog/base.js"}]
                            [:script {:src "main.js"}]
                            [:script "goog.require('drums.core')"]]
                           (apply include-css ["index.css"])]))
    (route/resources "/public")))

(defn new-handler
  [config]
  (-> (app-routes config)
      (wrap-json-params)
      (wrap-keyword-params)
      (wrap-params)
      (wrap-defaults
        (-> site-defaults (assoc-in [:security :anti-forgery] false)))))
