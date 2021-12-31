(ns pmatiello.tuia.internal.ansi.screen
  (:require [pmatiello.tuia.internal.ansi.support :refer [ansi-seq]]))

(defn normal-buffer []
  (ansi-seq "?1049l"))

(defn alternate-buffer []
  (ansi-seq "?1049h"))
