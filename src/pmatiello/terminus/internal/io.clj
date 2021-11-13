(ns pmatiello.terminus.internal.io
  (:require [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.ansi.erase :as erase]
            [pmatiello.terminus.internal.stty :as stty])
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

