(ns pmatiello.tty.input-demo
  (:require [pmatiello.tty :as tty]
            [pmatiello.tty.io :as tty.io])
  (:import (clojure.lang ExceptionInfo)))

(def state
  (atom {:events '()}))

(def header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn render [output old-state new-state]
  (when-not (::tty/init old-state)
    (tty.io/hide-cursor! output)
    (tty.io/clear-screen! output)
    (tty.io/print! output header {:x 1 :y 1 :w 23 :h 3}))

  (when (::tty/halt new-state)
    (tty.io/show-cursor! output))

  (tty.io/print! output (:events new-state) {:x 1 :y 5 :w 45 :h 6})
  (tty.io/place-cursor! output {:x 1 :y 10})
  nil)

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (try
    (tty/init! handle render state)
    (catch ExceptionInfo ex
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
