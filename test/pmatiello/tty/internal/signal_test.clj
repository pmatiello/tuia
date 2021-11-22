(ns pmatiello.tty.internal.signal-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.signal :as signal]
            [pmatiello.tty.internal.fixtures :as fixtures])
  (:import (sun.misc Signal)))

(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest trap-test
  (testing "executes the given function when the signal is raised"
    (let [recvd (promise)]
      (signal/trap :xfsz (fn [signal] (deliver recvd signal)))
      (Signal/raise (Signal. "XFSZ"))
      (is (= :xfsz (deref recvd 1000 :failed))))))
