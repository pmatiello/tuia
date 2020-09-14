(ns clj-ansi.screen
  (:require [clj-ansi.internal.output :refer [ansi-seq]]))

(def normal-buffer
  (ansi-seq "?1049l"))

(def alternate-buffer
  (ansi-seq "?1049h"))
