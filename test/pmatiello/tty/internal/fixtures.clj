(ns pmatiello.tty.internal.fixtures
  (:require [pmatiello.tty.internal.ansi.support :as support]
            [clojure.spec.test.alpha :as stest]))

(defn with-spec-instrumentation [f]
  (stest/instrument)
  (f)
  (stest/unstrument))

(defn with-readable-csi [f]
  (with-redefs
    [support/csi "\\u001B["]
    (f)))
