(ns pmatiello.terminus.internal.framework.raw-tty-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.internal.framework.raw-tty :as raw-tty]
            [pmatiello.terminus.internal.tty.stty :as stty]))

(defn func [])

(mfn/deftest with-raw-tty-test
  (mfn/testing "runs given body"
    (raw-tty/with-raw-tty (func))
    (mfn/verifying
      (func) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "starts raw mode"
    (raw-tty/with-raw-tty (func))
    (mfn/verifying
      (stty/unset-flags! :icanon :echo) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "returns to original terminal settings"
    (raw-tty/with-raw-tty (func))
    (mfn/providing
      (stty/current) 'stty-current)
    (mfn/verifying
      (stty/apply! 'stty-current) nil (mockfn.matchers/exactly 1))))
