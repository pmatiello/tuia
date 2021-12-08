(ns pmatiello.tuia.internal.ansi.input-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.event :as event]
            [pmatiello.tuia.internal.ansi.input :as input]
            [pmatiello.tuia.internal.fixtures :as fixtures])
  (:import (java.io StringReader)))

(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest reader->event-seq-test
  (is (= [#::event{:type ::event/keypress :value "i"}
          #::event{:type ::event/keypress :value "n"}
          #::event{:type ::event/keypress :value "p"}
          #::event{:type ::event/keypress :value "u"}
          #::event{:type ::event/keypress :value "t"}]
         (-> "input" StringReader. input/reader->event-seq))))
