(ns clj-ansi.erase-test
  (:require [clojure.test :refer :all]
            [clj-ansi.erase :as erase]))

(deftest from-cursor-test
  (is (= "\u001b[0J" erase/from-cursor)))

(deftest to-cursor-test
  (is (= "\u001b[1J" erase/to-cursor)))

(deftest full-screen-test
  (is (= "\u001b[2J" erase/full-screen)))
