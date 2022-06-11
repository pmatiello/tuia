(ns pmatiello.tuia.event
  (:require [clojure.spec.alpha :as s]))

(s/def ::event                                              ; RENAME?
  (s/keys :req-un [::type ::value]))

(s/def ::type
  #{:init :halt :size :keypress :cursor-position :unknown})

(s/def ::value any?)
