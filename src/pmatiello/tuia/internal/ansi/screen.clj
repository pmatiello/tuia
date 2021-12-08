(ns pmatiello.tuia.internal.ansi.screen
  (:require [pmatiello.tuia.internal.ansi.support :refer [ansi-seq]]))

(def normal-buffer
  (ansi-seq "?1049l"))

(def alternate-buffer
  (ansi-seq "?1049h"))
