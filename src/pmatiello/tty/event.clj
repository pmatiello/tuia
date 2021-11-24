(ns pmatiello.tty.event
  (:require [clojure.spec.alpha :as s]))

(s/def ::event
  (s/keys :req [::type ::value]))

(s/def ::type keyword?)

(s/def ::value any?)
