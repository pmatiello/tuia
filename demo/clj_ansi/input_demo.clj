(ns clj-ansi.input-demo
  (:require [clojure.java.shell :refer [sh]]
            [clj-ansi.input :as input]))

(defn -main []
  (println "Start typing.")
  (println "Enter Ctrl+C to quit.")
  (try
    (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
    (->> *in*
         input/reader->char-seq
         input/parse
         (mapv println))
    (println "Done.")
    (finally
      (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty"))))
