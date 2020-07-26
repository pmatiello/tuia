(ns clj-ansi.erase
  (:require [clj-ansi.shared :refer [ansi-seq]]))

(def below
  (ansi-seq "0J"))

(def above
  (ansi-seq "1J"))

(def all
  (ansi-seq "2J"))
