(ns clj-ansi.cursor-demo
  (:require [clj-ansi.cursor :as cursor]))

(defn -main []
  (print "First line")
  (print (cursor/cursor-next-line 2))
  (print "Two lines from the first")
  (print (cursor/cursor-previous-line 1))
  (print "Up one line from below")
  (print (cursor/cursor-next-line 2))
  (print "This will be overwritten")
  (print (cursor/cursor-column 1))
  (print "This overwrittes something")
  (println))
