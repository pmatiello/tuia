(ns pmatiello.tuia.internal.ansi.cursor
  (:require [pmatiello.tuia.internal.ansi.support :refer [ansi-seq]]))

(defn up [lines]
  (ansi-seq lines "A"))

(defn down [lines]
  (ansi-seq lines "B"))

(defn forward [columns]
  (ansi-seq columns "C"))

(defn back [columns]
  (ansi-seq columns "D"))

(defn next-line [lines]
  (ansi-seq lines "E"))

(defn previous-line [lines]
  (ansi-seq lines "F"))

(defn column [column]
  (ansi-seq column "G"))

(defn position [line column]
  (ansi-seq line ";" column "H"))

(defn current-position []
  (ansi-seq "6n"))

(defn show []
  (ansi-seq "?25h"))

(defn hide []
  (ansi-seq "?25l"))
