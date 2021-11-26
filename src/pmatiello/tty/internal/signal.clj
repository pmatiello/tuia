(ns pmatiello.tty.internal.signal
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s])
  (:import (sun.misc Signal SignalHandler)))

(s/def ::signal keyword?)

(defn trap
  "Registers a signal handler.

  signal: signal name
  handler-fn: handler function. Receives the signal name as argument."
  [signal handler-fn]
  (Signal/handle
    (->> signal name str/upper-case Signal.)
    (reify SignalHandler
      (handle [_this _signal] (handler-fn signal)))))

(s/fdef trap
  :args (s/cat :signal ::signal :handler-fn fn?))
