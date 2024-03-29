(ns pmatiello.tuia.input-demo
  (:require [clojure.spec.test.alpha :as stest]
            [pmatiello.tuia.core :as tuia.core]
            [pmatiello.tuia.event :as tuia.event]
            [pmatiello.tuia.io :as tuia.io]
            [pmatiello.tuia.text :as txt])
  (:import (clojure.lang ExceptionInfo)))

(def ^:private state
  (atom {:events '()}))

(def ^:private header
  [#::txt {:style [::txt/bold] :body "input-demo"}
   #::txt {:style [::txt/fg-blue] :body "Type to produce events."}
   "Enter Ctrl+D to quit."])

(defn- full-render?
  [old new]
  (or (nil? (::tuia.event/init old))
      (not= (::tuia.event/size old) (::tuia.event/size new))))

(defn event->text
  [{:keys [::tuia.event/type ::tuia.event/value]}]
  [#::txt{:style [::txt/fg-blue] :body (str type)}
   #::txt{:style [::txt/fg-blue] :body ": "}
   #::txt{:style [::txt/bold-off] :body (str value)}])

(defn- render
  [output old-state new-state]
  (when (full-render? old-state new-state)
    (tuia.io/hide-cursor! output)
    (tuia.io/clear-screen! output)
    (tuia.io/print! output header
                    #::tuia.io{:row 1 :column 1 :width 23 :height 3}))

  (when (::tuia.event/halt new-state)
    (tuia.io/show-cursor! output))

  (tuia.io/print! output (map event->text (:events new-state))
                  #::tuia.io{:row 5 :column 1 :width 80 :height 5 :style [::txt/bold ::txt/bg-white]})
  (tuia.io/place-cursor! output #::tuia.io{:row 10 :column 1}))

(defn handle
  [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event ::tuia.event/value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main
  []
  (stest/instrument)
  (try
    (tuia.core/init! handle render state)
    (catch ExceptionInfo ex
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
