(ns clj-ansi.cursor
  (:require [clj-ansi.shared :refer [ansi-seq]]))

(defn cursor-up [lines]
  (ansi-seq lines "A"))

(defn cursor-down [lines]
  (ansi-seq lines "B"))

(defn cursor-forward [columns]
  (ansi-seq columns "C"))

(defn cursor-back [columns]
  (ansi-seq columns "D"))

(defn cursor-next-line [lines]
  (ansi-seq lines "E"))

(defn cursor-previous-line [lines]
  (ansi-seq lines "F"))

(defn cursor-column [column]
  (ansi-seq column "G"))

