(ns clj-ansi.erase
  (:require [clj-ansi.shared :refer [ansi-seq]]))

(def from-cursor
  (ansi-seq "0J"))

(def to-cursor
  (ansi-seq "1J"))

(def full-screen
  (ansi-seq "2J"))
