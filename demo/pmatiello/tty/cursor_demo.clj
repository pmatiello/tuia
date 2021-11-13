(ns pmatiello.tty.cursor-demo
  (:require [pmatiello.tty.internal.ansi.cursor :as cursor]))

(defn ^:deprecated -main []
  (print "First line")
  (print (cursor/next-line 2))
  (print "Two lines from the first")
  (print (cursor/previous-line 1))
  (print "Up one line from below")
  (print (cursor/next-line 2))
  (print "This will be overwritten")
  (print (cursor/column 1))
  (print "This overwrittes something")
  (println))
