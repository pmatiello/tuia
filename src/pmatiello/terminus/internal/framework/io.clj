(ns pmatiello.terminus.internal.framework.io
  (:require [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.tty.stty :as stty])
  (:import (java.io Writer)))

(defn render [^Writer output window buffer]
  (let [{:keys [x y w h]} window
        indexed-buffer (map vector (range) buffer)]
    (doseq [[offset ^String line] indexed-buffer]
      (.append output (str (cursor/position (+ y offset) x) line)))
    (.flush output)))

(defn with-raw-tty [func]
  (let [initial-stty (stty/current)]
    (try
      (stty/unset-flags! :icanon :echo)
      (func)
      (finally
        (stty/apply! initial-stty)))))
