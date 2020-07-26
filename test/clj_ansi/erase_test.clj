(ns clj-ansi.erase-test
  (:require [clojure.test :refer :all]
            [clj-ansi.erase :as erase]))

(deftest below-test
  (is (= "\u001b[0J" erase/below)))

(deftest above-test
  (is (= "\u001b[1J" erase/above)))

(deftest all-test
  (is (= "\u001b[2J" erase/all)))
