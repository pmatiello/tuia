(ns pmatiello.terminus.internal.fixtures
  (:require [pmatiello.terminus.internal.ansi.support :as support]))

(defn with-readable-csi [f]
  (with-redefs
    [support/csi "\\u001B["]
    (f)))
