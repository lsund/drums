(defproject drums "0.1.0-SNAPSHOT"
  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-figwheel "0.5.20"]]
  :description "Drum machine"
  :url "https://github.com/lsund/drums"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.764"]
                 [org.clojure/core.async "1.3.610"]
                 [reagent "1.0.0-rc1"]
                 [hiccup "1.0.5"]
                 [lein-cljsbuild "1.1.8"]]
  :repl-options {:init-ns drums.core}

  :resource-paths ["resources"]
  :clean-targets ^{:protect false} [:target-path]

  :profiles {:dev {:cljsbuild
                   {:builds {:client
                             {:figwheel {:on-jsload "drums.core/run"}
                              :compiler {:main "drums.core"
                                         :optimizations :none}}}}}

             :prod {:cljsbuild
                    {:builds {:client
                              {:compiler {:optimizations :advanced
                                          :elide-asserts true
                                          :pretty-print false}}}}}}

  :figwheel {:repl false
             :css-dirs ["resources/public"]
             :http-server-root "public"}

  :cljsbuild {:builds {:client
                       {:source-paths ["src"]
                        :compiler {:output-dir "resources/public/client"
                                   :asset-path "client"}}}})

