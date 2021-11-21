(ns pmatiello.tty.input-demo
  (:require [pmatiello.tty :as tty]
            [pmatiello.tty.io :as tty.io])
  (:import (clojure.lang ExceptionInfo)))

(def ^:private state
  (atom {:events '()}))

(def ^:private header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn- full-render? [old new]
  (or (nil? (::tty/init old))
      (not= (::tty/size old) (::tty/size new))))

(defn- render [output old-state new-state]
  (when (full-render? old-state new-state)
    (tty.io/hide-cursor! output)
    (tty.io/clear-screen! output)
    (tty.io/print! output header
                   #::tty.io{:row 1 :column 1 :width 23 :height 3}))

  (when (::tty/halt new-state)
    (tty.io/show-cursor! output))

  (tty.io/print! output (:events new-state)
                 #::tty.io{:row 5 :column 1 :width 45 :height 6})
  (tty.io/place-cursor! output #::tty.io{:row 10 :column 1}))

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
