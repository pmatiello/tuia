(ns demo.cursor-demo
  (:require [clojure.test :refer :all]
            [clj-ansi.cursor :as output-seq]))

(deftest output-seq-test
  (print "First line")
  (print (output-seq/cursor-next-line 2))
  (print "Two lines from the first")
  (print (output-seq/cursor-previous-line 1))
  (print "Up one line from below")
  (print (output-seq/cursor-next-line 2))
  (print "This will be overwritten")
  (print (output-seq/cursor-column 1))
  (print "This overwrittes something")
  (println))
