(ns clj-ansi.input-test
  (:require [clojure.test :refer :all]
            [clj-ansi.input :as input])
  (:import (java.io StringReader)))

(deftest reader->event-seq-test
  (is (= [{:event :keypress :value "i"}
          {:event :keypress :value "n"}
          {:event :keypress :value "p"}
          {:event :keypress :value "u"}
          {:event :keypress :value "t"}]
         (-> "input" StringReader. input/reader->event-seq))))
