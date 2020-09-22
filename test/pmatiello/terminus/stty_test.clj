(ns pmatiello.terminus.stty-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.stty :as stty]
            [clojure.java.shell :refer [sh]]))

(mfn/deftest current-test
  (is (= {:gfmt1 nil :cflag "4b00" :iflag "2306"}
         (stty/current)))
  (mfn/providing
    (sh "/bin/sh" "-c" "stty -g < /dev/tty")
    {:out "gfmt1:cflag=4b00:iflag=2306\n"}))
