(defproject drums "0.1.0-SNAPSHOT"
  :plugins [[lein-figwheel "0.5.20"]
            [lein-cljsbuild "1.1.8"]
            [lein-ring "0.8.7"]]
  :min-lein-version "2.0.0"
  :description "Drum machine"
  :url "https://github.com/lsund/drums"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :profiles {:uberjar {:aot :all}
             :prep-tasks ["compile" ["cljsbuild" "once"]]}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.764"]
                 [org.clojure/core.async "1.3.610"]
                 [com.stuartsierra/component "1.0.0"]
                 [compojure "1.6.2"]
                 [environ "1.0.0"]
                 [hiccup "1.0.5"]
                 [ring/ring-defaults "0.3.0"]
                 [ring/ring-json "0.4.0"]
                 [http-kit "2.5.0"]
                 [reagent "1.0.0-rc1"]
                 [hiccup "1.0.5"]]
  :resource-paths ["resources"]
  :clean-targets ^{:protect false} [:target-path]

  :figwheel {:repl false
             :css-dirs ["resources/public"]
             :http-server-root "public"}

  :uberjar-name "drums-stanalone.jar"

  :source-paths ["src/clj"]
  :main drums.main
  :ring {:handler drums.core/new-handler}

  :cljsbuild {:builds {:client
                       {:source-paths ["src"]
                        :figwheel {:on-jsload "drums.core/run"}
                        :compiler {:output-dir "resources/public/client"
                                   :output-to "resources/public/main.js"
                                   :main "drums.core"
                                   :asset-path "client"}}}})

