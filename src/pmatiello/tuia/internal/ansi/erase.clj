(ns pmatiello.tuia.internal.ansi.erase
  (:require [pmatiello.tuia.internal.ansi.support :refer [ansi-seq]]))

(def below
  (ansi-seq "0J"))

(def above
  (ansi-seq "1J"))

(def all
  (ansi-seq "2J"))
