(ns clj-ansi.input-test
  (:require [clojure.test :refer :all]
            [clj-ansi.input :as input]))

(deftest parse-test
  (testing "decodes regular characters"
    (is (= ["A" "B" "C"]
           (input/parse [{:char-code 65 :escape? false}
                         {:char-code 66 :escape? false}
                         {:char-code 67 :escape? false}]))))

  (testing "decodes control characters"
    (is (= [:nul]
           (input/parse [{:char-code 0 :escape? false}])))
    (is (= [:eot]
           (input/parse [{:char-code 4 :escape? false}])))
    (is (= [:lf]
           (input/parse [{:char-code 10 :escape? false}])))
    (is (= [:esc]
           (input/parse [{:char-code 27 :escape? false}]))))

  (testing "decodes escape sequences"
    (is (= [:up]
           (input/parse [{:char-code 27, :escape? true}
                         {:char-code 91, :escape? true}
                         {:char-code 65, :escape? false}])))
    (is (= [:right]
           (input/parse [{:char-code 27, :escape? true}
                         {:char-code 91, :escape? true}
                         {:char-code 67, :escape? false}])))
    (is (= [:f1]
           (input/parse [{:char-code 27, :escape? true}
                         {:char-code 79, :escape? true}
                         {:char-code 80, :escape? false}])))
    (is (= [:f5]
           (input/parse [{:char-code 27, :escape? true}
                         {:char-code 91, :escape? true}
                         {:char-code 49, :escape? true}
                         {:char-code 53, :escape? true}
                         {:char-code 126, :escape? false}]))))

  (testing "returns :unknown when escape sequence is not recognised"
    (is (= [:unknown]
           (input/parse [{:char-code 27, :escape? true}
                         {:char-code 99, :escape? true}
                         {:char-code 99, :escape? false}])))))
