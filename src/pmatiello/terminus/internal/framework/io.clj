(ns pmatiello.terminus.internal.framework.io
  (:require [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.tty.stty :as stty])
  (:import (java.io Writer)))

(defn- cropped [buffer width height]
  (->> buffer (take height) (map #(subs % 0 width))))

(defn render [^Writer output window buffer]
  (let [{:keys [x y w h]} window
        cropped-buf (cropped buffer w h)
        indexed-buf (map vector (range) cropped-buf)]
    (doseq [[offset ^String line] indexed-buf]
      (.append output (str (cursor/position (+ y offset) x) line)))
    (.flush output)))

(defn with-raw-tty [func]
  (let [initial-stty (stty/current)]
    (try
      (stty/unset-flags! :icanon :echo)
      (func)
      (finally
        (stty/apply! initial-stty)))))
