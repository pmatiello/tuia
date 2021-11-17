(ns pmatiello.tty.internal.signal
  (:require [clojure.string :as str])
  (:import (sun.misc Signal SignalHandler)))

(defn trap [signal callback-fn]
  (Signal/handle
    (->> signal name str/upper-case Signal.)
    (reify SignalHandler
      (handle [_this _signal] (callback-fn signal)))))
