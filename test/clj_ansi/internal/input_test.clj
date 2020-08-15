(ns clj-ansi.internal.input-test
  (:require [clojure.test :refer :all])
  (:require [clj-ansi.internal.input :as internal.input]))

(deftest key-codes->char
  (testing "maps regular key to char"
    (is (= "A" (internal.input/key-codes->char [65])))
    (is (= "B" (internal.input/key-codes->char [66])))
    (is (= "C" (internal.input/key-codes->char [67]))))

  (testing "maps special key to char"
    (is (= :nul (internal.input/key-codes->char [0])))
    (is (= :eot (internal.input/key-codes->char [4])))
    (is (= :lf (internal.input/key-codes->char [10])))
    (is (= :esc (internal.input/key-codes->char [27]))))

  (testing "maps escape sequences"
    (is (= :up (internal.input/key-codes->char [27 91 65])))
    (is (= :right (internal.input/key-codes->char [27 91 67])))
    (is (= :f1 (internal.input/key-codes->char [27 79 80])))
    (is (= :f5 (internal.input/key-codes->char [27 91 49 53 126]))))

  (testing "maps unknown keys to :unknown"
    (is (= :unknown (internal.input/key-codes->char [27 99 99])))))
