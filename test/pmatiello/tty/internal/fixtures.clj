(ns pmatiello.tty.internal.fixtures
  (:require [pmatiello.tty.internal.ansi.support :as support]))

(defn with-readable-csi [f]
  (with-redefs
    [support/csi "\\u001B["]
    (f)))
