(ns pmatiello.tty.internal.ansi.input.parsing-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.ansi.input.parsing :as input.parsing])
  (:import (java.io StringReader)))

(deftest input-seq->event-seq-test
  (testing "decodes regular characters"
    (is (= [{:event :keypress :value "A"}
            {:event :keypress :value "B"}
            {:event :keypress :value "C"}]
           (input.parsing/input-seq->event-seq
             [{:char-code 65 :has-next? false}
              {:char-code 66 :has-next? false}
              {:char-code 67 :has-next? false}]))))

  (testing "decodes control characters"
    (is (= [{:event :keypress :value :nul}]
           (input.parsing/input-seq->event-seq
             [{:char-code 0 :has-next? false}]))))

  (testing "decodes escape sequences"
    (is (= [{:event :keypress :value :up}]
           (input.parsing/input-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 65, :has-next? false}]))))

  (testing "returns :unknown when escape sequence is not recognised"
    (is (= [{:event :unknown :value [27 99 99]}]
           (input.parsing/input-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 99, :has-next? true}
              {:char-code 99, :has-next? false}]))))

  (testing "splits multiple buffered escape sequences"
    (is (= [{:event :keypress :value :up}
            {:event :keypress :value :esc}
            {:event :keypress :value :down}]
           (input.parsing/input-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 65, :has-next? true}
              {:char-code 27, :has-next? true}
              {:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 66, :has-next? false}])))))

(deftest reader->input-seq-test
  (is (= [{:char-code 105, :has-next? true}
          {:char-code 110, :has-next? true}
          {:char-code 112, :has-next? true}
          {:char-code 117, :has-next? true}
          {:char-code 116, :has-next? true}]
         (-> "input" StringReader. input.parsing/reader->input-seq))))
