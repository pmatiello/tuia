(ns pmatiello.tty.internal.io
  (:require [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]
            [pmatiello.tty.internal.stty :as stty])
  (:import (java.io Writer)))

(defn write! [^Writer writer payload]
  (doseq [^String each payload]
    (.append writer each))
  (.flush writer))

(defn with-raw-tty [func]
  (let [initial-stty (stty/current)]
    (try
      (stty/unset-flags! :icanon :echo)
      (func)
      (finally
        (stty/apply! initial-stty)))))

