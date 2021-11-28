(ns pmatiello.tty.text
  (:require [clojure.spec.alpha :as s]))

(s/def ::text
  (s/and sequential? (s/coll-of ::paragraph)))

(s/def ::loose-text
  (s/and sequential? (s/coll-of ::loose-paragraph)))

(s/def ::paragraph
  (s/keys :req [::style ::body]))

(s/def ::style (s/coll-of keyword?))
(s/def ::body string?)

(s/def ::loose-paragraph
  (s/or :string string? :paragraph ::paragraph))
