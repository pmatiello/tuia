(ns pmatiello.tty.input-demo
  (:require [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.core :as tty]
            [pmatiello.tty.io :as tty.io]
            [pmatiello.tty.internal.io :as tty.internal.io]
            [pmatiello.tty.internal.mainloop :as mainloop])
  (:import (clojure.lang ExceptionInfo)))

(def state
  (atom {:events    '()
         :curr-pos? 0}))

(def header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn render [output old-state new-state]
  (when-not (::mainloop/init old-state)
    (tty.io/clear-screen! output)
    (tty.io/print! output header {:x 1 :y 1 :w 23 :h 3}))

  (when (not= (:curr-pos? old-state) (:curr-pos? new-state))
    (tty.io/print! output [cursor/current-position] {:x 1 :y 10 :w 4 :h 5}))

  (tty.io/print! output (:events new-state) {:x 1 :y 5 :w 40 :h 6})
  (tty.io/place-cursor! output {:x 1 :y 10})
  nil)

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event :value #{:f12})
    (swap! state update-in [:curr-pos?] inc))

  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (try
    (->> (atom []) tty.io/hide-cursor! (tty.internal.io/write! *out*))
    (tty/init! handle render state)
    (catch ExceptionInfo ex
      (->> (atom []) tty.io/show-cursor! (tty.internal.io/write! *out*))
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
