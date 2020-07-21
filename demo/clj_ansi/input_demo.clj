(ns clj-ansi.input-demo
  (:require [clojure.java.shell :refer [sh]])
  (:import (java.io Reader)))

(defn stdin->char-code [& _]
  (let [char-code (.read *in*)
        more?     (do (Thread/sleep 0) (.ready ^Reader *in*))]
    {:char-code char-code :escape? more?}))

(defn -main []
  (println "Start typing.")
  (println "Enter Ctrl+C to quit.")
  (try
    (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
    (->> (iterate stdin->char-code nil)
         (remove nil?)
         (mapv println))
    (println "Done.")
    (finally
      (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty"))))
