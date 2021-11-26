(ns pmatiello.tty.internal.ansi.input-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.ansi.input :as input]
            [pmatiello.tty.event :as event]
            [pmatiello.tty.internal.fixtures :as fixtures])
  (:import (java.io StringReader)))

(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest reader->event-seq-test
  (is (= [#::event{:type ::event/keypress :value "i"}
          #::event{:type ::event/keypress :value "n"}
          #::event{:type ::event/keypress :value "p"}
          #::event{:type ::event/keypress :value "u"}
          #::event{:type ::event/keypress :value "t"}]
         (-> "input" StringReader. input/reader->event-seq))))
