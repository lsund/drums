(ns drums.core)

;; The core abstraction is a namespace, not a file

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn -main []
  (foo 12))
