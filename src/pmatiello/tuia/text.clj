(ns pmatiello.tuia.text
  (:require [clojure.spec.alpha :as s]))

(s/def ::text
  (s/and sequential? (s/coll-of ::paragraph)))

(s/def ::loose-text
  (s/and sequential? (s/coll-of ::loose-paragraph)))

(s/def ::paragraph
  (s/keys :req [::style ::body]))

(s/def ::style
  (s/coll-of
    (s/or :emphasis ::emphasis
          :fg-color ::fg-color
          :bg-color ::bg-color)))

(s/def ::body string?)

(s/def ::loose-paragraph
  (s/or :string string? :paragraph ::paragraph))

(s/def ::emphasis
  #{::bold ::underline ::blink})

(s/def ::fg-color
  #{::fg-black ::fg-red ::fg-green ::fg-yellow
    ::fg-blue ::fg-purple ::fg-cyan ::fg-white
    ::fg-default})

(s/def ::bg-color
  #{::bg-black ::bg-red ::bg-green ::bg-yellow
    ::bg-blue ::bg-purple ::bg-cyan ::bg-white
    ::bg-default})
