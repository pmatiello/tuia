(ns pmatiello.tty.lifecycle
  (:require [clojure.spec.alpha :as s]))

(s/def ::lifecycle-events
  #{::init ::halt ::size})
