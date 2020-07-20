(ns clj-ansi.cursor-test
  (:require [clojure.test :refer :all]
            [clj-ansi.cursor :as cursor]))

(deftest cursor-up-test
  (is (= "\u001b[1A") (cursor/cursor-up 1)))

(deftest cursor-down-test
  (is (= "\u001b[1B") (cursor/cursor-down 1)))

(deftest cursor-forward-test
  (is (= "\u001b[1C") (cursor/cursor-forward 1)))

(deftest cursor-back-test
  (is (= "\u001b[1D") (cursor/cursor-back 1)))

(deftest cursor-next-test
  (is (= "\u001b[1E") (cursor/cursor-next-line 1)))

(deftest cursor-previous-test
  (is (= "\u001b[1F") (cursor/cursor-previous-line 1)))

(deftest cursor-column-test
  (is (= "\u001b[1G") (cursor/cursor-column 1)))

