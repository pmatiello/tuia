(ns clj-ansi.cursor-test
  (:require [clojure.test :refer :all]
            [clj-ansi.cursor :as cursor]))

(deftest up-test
  (is (= "\u001b[1A") (cursor/up 1)))

(deftest down-test
  (is (= "\u001b[1B") (cursor/down 1)))

(deftest forward-test
  (is (= "\u001b[1C") (cursor/forward 1)))

(deftest back-test
  (is (= "\u001b[1D") (cursor/back 1)))

(deftest next-test
  (is (= "\u001b[1E") (cursor/next-line 1)))

(deftest previous-test
  (is (= "\u001b[1F") (cursor/previous-line 1)))

(deftest column-test
  (is (= "\u001b[1G") (cursor/column 1)))

(deftest position-test
  (is (= "\u001b[2;3H" (cursor/position 2 3))))
