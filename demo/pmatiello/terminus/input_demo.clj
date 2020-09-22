(ns pmatiello.terminus.input-demo
  (:require [clojure.java.shell :refer [sh]]
            [pmatiello.terminus.ansi.input :as input]
            [pmatiello.terminus.ansi.cursor :as cursor]
            [pmatiello.terminus.stty :as stty]))

(defn handle [event]
  (println event)
  (when (-> event :value #{:f12})
    (print cursor/current-position)
    (flush))
  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {}))))

(defn -main []
  (println "Start typing.")
  (println "Enter Ctrl+D to quit.")
  (let [initial-stty (stty/current)]
    (println "Initial line settings:" (stty/current))
    (try
      (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
      (println "Current line settings:" (stty/current))
      (->> *in*
           input/reader->event-seq
           (mapv handle))
      (catch Exception e
        (println (.getMessage e)))
      (finally
        (stty/apply! initial-stty)
        (println "Final line settings:" (stty/current))))))
