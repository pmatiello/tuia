(ns pmatiello.tty.event
  (:require [clojure.spec.alpha :as s]))

(s/def ::event
  (s/keys :req [::type ::value]))

(s/def ::type
  #{::init ::halt ::size ::keypress ::cursor-position ::unknown})

(s/def ::value any?)
