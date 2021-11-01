(ns pmatiello.terminus.internal.framework.io-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.framework.io :as io]
            [pmatiello.terminus.internal.tty.stty :as stty])
  (:import (java.io StringWriter)))

(defn func [])

(mfn/deftest render-test
  (let [output (StringWriter.)]
    (io/render output {:x 3 :y 4 :w 5 :h 2} ["line1" "line2"])
    (is (= (str (cursor/position 4 3) "line1"
                (cursor/position 5 3) "line2")
           (str output)))))

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
