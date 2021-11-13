(ns pmatiello.tty.internal.ansi.erase
  (:require [pmatiello.tty.internal.ansi.support :refer [ansi-seq]]))

(def below
  (ansi-seq "0J"))

(def above
  (ansi-seq "1J"))

(def all
  (ansi-seq "2J"))
