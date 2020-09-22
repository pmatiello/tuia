(ns clj-ansi.input-test
  (:require [clojure.test :refer :all]
            [clj-ansi.input :as input])
  (:import (java.io StringReader)))

(deftest input-seq->event-seq-test
  (testing "decodes regular characters"
    (is (= [{:event :keypress :value "A"}
            {:event :keypress :value "B"}
            {:event :keypress :value "C"}]
           (input/input-seq->event-seq [{:char-code 65 :has-next? false}
                                        {:char-code 66 :has-next? false}
                                        {:char-code 67 :has-next? false}]))))

  (testing "decodes control characters"
    (is (= [{:event :keypress :value :nul}]
           (input/input-seq->event-seq [{:char-code 0 :has-next? false}]))))

  (testing "decodes escape sequences"
    (is (= [{:event :keypress :value :up}]
           (input/input-seq->event-seq [{:char-code 27, :has-next? true}
                                        {:char-code 91, :has-next? true}
                                        {:char-code 65, :has-next? false}]))))

  (testing "returns :unknown when escape sequence is not recognised"
    (is (= [{:event :unknown}]
           (input/input-seq->event-seq [{:char-code 27, :has-next? true}
                                        {:char-code 99, :has-next? true}
                                        {:char-code 99, :has-next? false}]))))

  (testing "splits multiple buffered escape sequences"
    (is (= [{:event :keypress :value :up}
            {:event :keypress :value :esc}
            {:event :keypress :value :down}]
           (input/input-seq->event-seq [{:char-code 27, :has-next? true}
                                        {:char-code 91, :has-next? true}
                                        {:char-code 65, :has-next? true}
                                        {:char-code 27, :has-next? true}
                                        {:char-code 27, :has-next? true}
                                        {:char-code 91, :has-next? true}
                                        {:char-code 66, :has-next? false}])))))

(deftest reader->input-seq-test
  (let [test-string "input"
        reader      (StringReader. test-string)]
    (is (= [{:char-code 105, :has-next? true}
            {:char-code 110, :has-next? true}
            {:char-code 112, :has-next? true}
            {:char-code 117, :has-next? true}
            {:char-code 116, :has-next? true}]
           (->> reader
                input/reader->input-seq
                (take (count test-string)))))))
