(ns pmatiello.terminus.internal.ansi.erase
  (:require [pmatiello.terminus.internal.ansi.support :refer [ansi-seq]]))

(def below
  (ansi-seq "0J"))

(def above
  (ansi-seq "1J"))

(def all
  (ansi-seq "2J"))
