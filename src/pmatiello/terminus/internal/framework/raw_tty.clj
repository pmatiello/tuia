(ns pmatiello.terminus.internal.framework.raw-tty
  (:require [pmatiello.terminus.internal.tty.stty :as stty]))

(defn with-raw-tty [func]
  (let [initial-stty (stty/current)]
    (try
      (stty/unset-flags! :icanon :echo)
      (func)
      (finally
        (stty/apply! initial-stty)))))
