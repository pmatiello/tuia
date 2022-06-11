(ns pmatiello.tuia.internal.ansi.input.parsing-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.input.parsing :as input.parsing]
            [pmatiello.tuia.internal.fixtures :as fixtures])
  (:import (java.io StringReader)))

(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest input-seq->event-seq-test
  (testing "decodes regular characters"
    (is (= [{:type :keypress :value "A"}
            {:type :keypress :value "B"}
            {:type :keypress :value "C"}]
           (input.parsing/char-seq->event-seq
             [{:char-code 65 :has-next? false}
              {:char-code 66 :has-next? false}
              {:char-code 67 :has-next? false}]))))

  (testing "decodes control characters"
    (is (= [{:type :keypress :value :nul}]
           (input.parsing/char-seq->event-seq
             [{:char-code 0 :has-next? false}]))))

  (testing "decodes escape sequences"
    (is (= [{:type :keypress :value :up}]
           (input.parsing/char-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 65, :has-next? false}]))))

  (testing "returns :unknown when escape sequence is not recognised"
    (is (= [{:type :unknown :value [27 99 99]}]
           (input.parsing/char-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 99, :has-next? true}
              {:char-code 99, :has-next? false}]))))

  (testing "splits multiple buffered escape sequences"
    (is (= [{:type :keypress :value :up}
            {:type :keypress :value :esc}
            {:type :keypress :value :down}]
           (input.parsing/char-seq->event-seq
             [{:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 65, :has-next? true}
              {:char-code 27, :has-next? true}
              {:char-code 27, :has-next? true}
              {:char-code 91, :has-next? true}
              {:char-code 66, :has-next? false}])))))

(deftest reader->char-seq
  (is (= [{:char-code 105, :has-next? true}
          {:char-code 110, :has-next? true}
          {:char-code 112, :has-next? true}
          {:char-code 117, :has-next? true}
          {:char-code 116, :has-next? true}]
         (-> "input" StringReader. input.parsing/reader->char-seq))))
