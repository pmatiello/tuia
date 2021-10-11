(ns pmatiello.terminus.internal.ansi.screen
  (:require [pmatiello.terminus.internal.ansi.support :refer [ansi-seq]]))

(def normal-buffer
  (ansi-seq "?1049l"))

(def alternate-buffer
  (ansi-seq "?1049h"))
