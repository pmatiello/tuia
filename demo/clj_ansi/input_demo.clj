(ns clj-ansi.input-demo
  (:require [clojure.java.shell :refer [sh]]
            [clj-ansi.input :as input]
            [clj-ansi.cursor :as cursor]))

(defn handle [event]
  (println event)
  (when (-> event :value #{:f12})
    (print cursor/current-position)
    (flush)))

(defn -main []
  (println "Start typing.")
  (println "Enter Ctrl+C to quit.")
  (try
    (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
    (->> *in*
         input/reader->event-seq
         (mapv handle))
    (println "Done.")
    (finally
      (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty"))))
