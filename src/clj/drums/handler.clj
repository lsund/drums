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
