(ns pmatiello.tuia.internal.ansi.erase
  (:require [pmatiello.tuia.internal.ansi.support :refer [ansi-seq]]))

(defn below []
  (ansi-seq "0J"))

(defn above []
  (ansi-seq "1J"))

(defn all []
  (ansi-seq "2J"))
