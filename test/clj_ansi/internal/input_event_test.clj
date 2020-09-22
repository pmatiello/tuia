(ns clj-ansi.internal.input-event-test
  (:require [clojure.test :refer :all])
  (:require [clj-ansi.internal.input-event :as input-event]))

(deftest key-codes->event
  (testing "maps regular key to char"
    (is (= {:event :keypress :value "A"}
           (input-event/key-codes->event [65])))
    (is (= {:event :keypress :value "B"}
           (input-event/key-codes->event [66])))
    (is (= {:event :keypress :value "C"}
           (input-event/key-codes->event [67]))))

  (testing "maps special key to char"
    (is (= {:event :keypress :value :nul}
           (input-event/key-codes->event [0])))
    (is (= {:event :keypress :value :eot}
           (input-event/key-codes->event [4])))
    (is (= {:event :keypress :value :lf}
           (input-event/key-codes->event [10])))
    (is (= {:event :keypress :value :esc}
           (input-event/key-codes->event [27]))))

  (testing "maps escape sequences"
    (is (= {:event :keypress :value :up}
           (input-event/key-codes->event [27 91 65])))
    (is (= {:event :keypress :value :right}
           (input-event/key-codes->event [27 91 67])))
    (is (= {:event :keypress :value :f1}
           (input-event/key-codes->event [27 79 80])))
    (is (= {:event :keypress :value :f5}
           (input-event/key-codes->event [27 91 49 53 126]))))

  (testing "decodes and stores current cursor position"
    (is (= {:event :cursor-position :value [12 34]}
           (input-event/key-codes->event [27 91 49 50 59 51 52 82]))))

  (testing "maps unknown keys to :unknown"
    (is (= {:event :unknown :value [27 99 99]}
           (input-event/key-codes->event [27 99 99])))))
