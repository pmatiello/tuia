(ns pmatiello.terminus.stty-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.matchers]
            [pmatiello.terminus.stty :as stty]
            [clojure.java.shell :refer [sh]]))

(mfn/deftest current-test
  (is (= {:gfmt1 nil :cflag "4b00" :iflag "2306"}
         (stty/current)))
  (mfn/providing
    (sh "/bin/sh" "-c" "stty -g < /dev/tty")
    {:out "gfmt1:cflag=4b00:iflag=2306\n"}))

(mfn/deftest apply!-test
  (stty/apply! {:gfmt1 nil :cflag "4b00" :iflag "2306"})
  (mfn/verifying
    (sh "/bin/sh" "-c" "stty gfmt1:cflag=4b00:iflag=2306 < /dev/tty")
    :irrelevant (mfn.matchers/exactly 1)))

(mfn/deftest set-flags!-test
  (stty/set-flags! :icanon :echo)
  (mfn/verifying
    (sh "/bin/sh" "-c" "stty icanon echo < /dev/tty")
    :irrelevant (mfn.matchers/exactly 1)))

(mfn/deftest unset-flags!-test
  (stty/unset-flags! :icanon :echo)
  (mfn/verifying
    (sh "/bin/sh" "-c" "stty -icanon -echo < /dev/tty")
    :irrelevant (mfn.matchers/exactly 1)))
