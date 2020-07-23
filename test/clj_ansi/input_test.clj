(ns clj-ansi.input-test
  (:require [clojure.test :refer :all]
            [clj-ansi.input :as input])
  (:import (java.io StringReader)))

(deftest parse-test
  (testing "decodes regular characters"
    (is (= ["A" "B" "C"]
           (input/parse [{:char-code 65 :has-next? false}
                         {:char-code 66 :has-next? false}
                         {:char-code 67 :has-next? false}]))))

  (testing "decodes control characters"
    (is (= [:nul]
           (input/parse [{:char-code 0 :has-next? false}])))
    (is (= [:eot]
           (input/parse [{:char-code 4 :has-next? false}])))
    (is (= [:lf]
           (input/parse [{:char-code 10 :has-next? false}])))
    (is (= [:esc]
           (input/parse [{:char-code 27 :has-next? false}]))))

  (testing "decodes escape sequences"
    (is (= [:up]
           (input/parse [{:char-code 27, :has-next? true}
                         {:char-code 91, :has-next? true}
                         {:char-code 65, :has-next? false}])))
    (is (= [:right]
           (input/parse [{:char-code 27, :has-next? true}
                         {:char-code 91, :has-next? true}
                         {:char-code 67, :has-next? false}])))
    (is (= [:f1]
           (input/parse [{:char-code 27, :has-next? true}
                         {:char-code 79, :has-next? true}
                         {:char-code 80, :has-next? false}])))
    (is (= [:f5]
           (input/parse [{:char-code 27, :has-next? true}
                         {:char-code 91, :has-next? true}
                         {:char-code 49, :has-next? true}
                         {:char-code 53, :has-next? true}
                         {:char-code 126, :has-next? false}]))))

  (testing "returns :unknown when escape sequence is not recognised"
    (is (= [:unknown]
           (input/parse [{:char-code 27, :has-next? true}
                         {:char-code 99, :has-next? true}
                         {:char-code 99, :has-next? false}])))))

(deftest reader->char-seq
  (let [test-string "input"
        reader      (StringReader. test-string)]
    (is (= [{:char-code 105, :has-next? true}
            {:char-code 110, :has-next? true}
            {:char-code 112, :has-next? true}
            {:char-code 117, :has-next? true}
            {:char-code 116, :has-next? true}]
           (->> reader
                input/reader->char-seq
                (take (count test-string)))))))
