(ns pmatiello.terminus.input-demo
  (:require [clojure.java.shell :refer [sh]]
            [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.ansi.erase :as erase]
            [pmatiello.terminus.framework :as framework]
            [clojure.string :as str])
  (:import (clojure.lang ExceptionInfo)))

(def state (atom {:events '()}))

(def header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."
   ""])

(defn println-all [lines]
  (println (str/join "\n" lines)))

(defn render [_old-state new-state]
  (print erase/all (cursor/position 1 1))
  (println-all header)
  (println-all (:events new-state))
  (flush))

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event :value #{:f12})
    (print cursor/current-position)
    (flush))

  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (try
    (framework/new-tty-app handle render state *in*)
    (catch ExceptionInfo ex
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
