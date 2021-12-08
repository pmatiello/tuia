(ns pmatiello.tty.input-demo
  (:require [clojure.spec.test.alpha :as stest]
            [pmatiello.tty.core :as tty.core]
            [pmatiello.tty.event :as tty.event]
            [pmatiello.tty.text :as txt]
            [pmatiello.tty.io :as tty.io])
  (:import (clojure.lang ExceptionInfo)))

(def ^:private state
  (atom {:events '()}))

(def ^:private header
  [#::txt {:style [::txt/bold] :body "input-demo"}
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn- full-render? [old new]
  (or (nil? (::tty.event/init old))
      (not= (::tty.event/size old) (::tty.event/size new))))

(defn- render [output old-state new-state]
  (when (full-render? old-state new-state)
    (tty.io/hide-cursor! output)
    (tty.io/clear-screen! output)
    (tty.io/print! output header
                   #::tty.io{:row 1 :column 1 :width 23 :height 3}))

  (when (::tty.event/halt new-state)
    (tty.io/show-cursor! output))

  (tty.io/print! output (map str (:events new-state))
                 #::tty.io{:row 5 :column 1 :width 80 :height 6})
  (tty.io/place-cursor! output #::tty.io{:row 10 :column 1}))

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event ::tty.event/value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (stest/instrument)
  (try
    (tty.core/init! handle render state)
    (catch ExceptionInfo ex
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
