(ns pmatiello.tuia.internal.ansi.input-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.input :as input]
            [pmatiello.tuia.internal.fixtures :as fixtures])
  (:import (java.io StringReader)))

(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest reader->event-seq-test
  (is (= [{:type :keypress :value "i"}
          {:type :keypress :value "n"}
          {:type :keypress :value "p"}
          {:type :keypress :value "u"}
          {:type :keypress :value "t"}]
         (-> "input" StringReader. input/reader->event-seq))))
