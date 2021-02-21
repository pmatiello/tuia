(ns pmatiello.terminus.tty.stty-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.matchers]
            [pmatiello.terminus.tty.stty :as stty]
            [clojure.java.shell :refer [sh]])
  (:import (clojure.lang ExceptionInfo)))

(mfn/deftest current-test
  (mfn/testing "returns current line terminal settings"
    (is (= {:gfmt1 nil :cflag "4b00" :iflag "2306"}
           (stty/current)))
    (mfn/providing
      (sh "/bin/sh" "-c" "stty -g < /dev/tty")
      {:exit 0 :out "gfmt1:cflag=4b00:iflag=2306\n"}))

  (mfn/testing "throws exception on error"
    (is (thrown? ExceptionInfo (stty/current)))
    (mfn/providing
      (sh "/bin/sh" "-c" "stty -g < /dev/tty") {:exit 1})))

(mfn/deftest apply!-test
  (mfn/testing "applies line terminal settings"
    (stty/apply! {:gfmt1 nil :cflag "4b00" :iflag "2306"})
    (mfn/verifying
      (sh "/bin/sh" "-c" "stty gfmt1:cflag=4b00:iflag=2306 < /dev/tty")
      {:exit 0} (mfn.matchers/exactly 1)))

  (mfn/testing "throws exception on error"
    (is (thrown? ExceptionInfo (stty/apply! {:gfmt1 nil})))
    (mfn/providing
      (sh "/bin/sh" "-c" "stty gfmt1 < /dev/tty") {:exit 2})))

(mfn/deftest set-flags!-test
  (mfn/testing "enables specific line terminal configuration flags"
    (stty/set-flags! :icanon :echo)
    (mfn/verifying
      (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty")
      {:exit 0} (mfn.matchers/exactly 1)))

  (mfn/testing "throws exception on error"
    (is (thrown? ExceptionInfo (stty/set-flags! :icanon)))
    (mfn/providing
      (sh "/bin/sh" "-c" "stty icanon < /dev/tty") {:exit 3})))

(mfn/deftest unset-flags!-test
  (mfn/testing "disables specific line terminal configuration flags"
    (stty/unset-flags! :icanon :echo)
    (mfn/verifying
      (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
      {:exit 0} (mfn.matchers/exactly 1)))

  (mfn/testing "throws exception on error"
    (is (thrown? ExceptionInfo (stty/unset-flags! :echo)))
    (mfn/providing
      (sh "/bin/sh" "-c" "stty -echo < /dev/tty") {:exit 4})))
