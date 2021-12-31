(ns pmatiello.tuia.internal.fixtures
  (:require [clojure.spec.test.alpha :as stest]
            [pmatiello.tuia.internal.ansi.support :as support]))

(defn with-spec-instrumentation [f]
  (stest/instrument)
  (f)
  (stest/unstrument))

(defn with-readable-csi [f]
  (with-redefs
    [support/csi "\\u001b["]
    (f)))
