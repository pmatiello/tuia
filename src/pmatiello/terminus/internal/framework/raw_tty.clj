(ns pmatiello.terminus.internal.framework.raw-tty
  (:require [pmatiello.terminus.internal.tty.stty :as stty]))

(defmacro with-raw-tty [& body]
  `(let [initial-stty# (stty/current)]
     (try
       (stty/unset-flags! :icanon :echo)
       (do ~@body)
       (finally
         (stty/apply! initial-stty#)))))
