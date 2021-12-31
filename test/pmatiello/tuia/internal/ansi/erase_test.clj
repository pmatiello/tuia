(ns pmatiello.tuia.internal.ansi.erase-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.erase :as erase]
            [pmatiello.tuia.internal.fixtures :as fixtures]))

(use-fixtures :each fixtures/with-readable-csi)

(deftest below-test
  (is (= "\\u001b[0J" (erase/below))))

(deftest above-test
  (is (= "\\u001b[1J" (erase/above))))

(deftest all-test
  (is (= "\\u001b[2J" (erase/all))))
