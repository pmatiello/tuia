(ns pmatiello.tty.internal.signal-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.signal :as signal])
  (:import (sun.misc Signal)))

(deftest trap-test
  (testing "executes the given function when the signal is raised"
    (let [recvd (promise)]
      (signal/trap :xfsz (fn [signal] (deliver recvd signal)))
      (Signal/raise (Signal. "XFSZ"))
      (is (= :xfsz (deref recvd 1000 :failed))))))
