(ns pmatiello.tuia.text
  (:require [clojure.spec.alpha :as s]))

(s/def ::text
  (s/and sequential? (s/coll-of ::line)))

(s/def ::loose-text
  (s/and sequential? (s/coll-of ::loose-line)))

(s/def ::line
  (s/and sequential? (s/coll-of ::segment)))

(s/def ::segment
  (s/keys :req-un [::style ::body]))

(s/def ::style
  (s/coll-of
    (s/or :emphasis ::emphasis
          :fg-color ::fg-color
          :bg-color ::bg-color)))

(s/def ::body string?)

(s/def ::loose-line
  (s/or :string string?
        :segment ::segment
        :line ::line))

(s/def ::emphasis
  #{:bold :bold-off
    :underline :underline-off
    :blink :blink-off})

(s/def ::fg-color
  #{:fg-black :fg-red :fg-green :fg-yellow
    :fg-blue :fg-purple :fg-cyan :fg-white
    :fg-default})

(s/def ::bg-color
  #{:bg-black :bg-red :bg-green :bg-yellow
    :bg-blue :bg-purple :bg-cyan :bg-white
    :bg-default})
