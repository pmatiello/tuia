(ns clj-ansi.input-demo
  (:require [clojure.java.shell :refer [sh]]
            [clj-ansi.input :as input]
            [clj-ansi.cursor :as cursor]))

(defn handle [char]
  (println char)
  (when (= char :f12)
    (print cursor/current-position)
    (flush)))

(defn -main []
  (println "Start typing.")
  (println "Enter Ctrl+C to quit.")
  (try
    (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
    (->> *in*
         input/reader->input-seq
         input/input-seq->char-seq
         (mapv handle))
    (println "Done.")
    (finally
      (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty"))))
