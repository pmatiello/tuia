(ns pmatiello.terminus.internal.io-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.internal.io :as io]
            [pmatiello.terminus.internal.stty :as stty])
  (:import (java.io StringWriter)))

(defn- new-writer []
  (StringWriter.))

(defn func [])

(deftest write!-test
  (testing "writes entire payload to writer"
    (let [writer (new-writer)]
      (io/write! writer [":str1" ":str2"])
      (is (= ":str1:str2" (str writer))))))

(mfn/deftest with-raw-tty-test
  (mfn/testing "runs given body"
    (io/with-raw-tty func)
    (mfn/verifying
      (func) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "starts raw mode"
    (io/with-raw-tty func)
    (mfn/verifying
      (stty/unset-flags! :icanon :echo) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "returns to original terminal settings"
    (io/with-raw-tty func)
    (mfn/providing
      (stty/current) 'stty-current)
    (mfn/verifying
      (stty/apply! 'stty-current) nil (mockfn.matchers/exactly 1)))

  (mfn/providing
    (stty/apply! (mockfn.matchers/any)) nil))

