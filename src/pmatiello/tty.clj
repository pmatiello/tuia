(ns pmatiello.tty
  (:require [pmatiello.tty.internal.ansi.input :as input]
            [pmatiello.tty.internal.io :as io]
            [pmatiello.tty.internal.mainloop :as mainloop])
  (:import (java.io Writer)))

(defn init!
  [handle-fn render-fn state]
  (let [input (input/reader->event-seq *in*)
        output! (partial io/write! *out*)]
    (with-redefs
      [*out* (Writer/nullWriter)]
      (io/with-raw-tty
        #(mainloop/with-mainloop handle-fn render-fn state input output!)))))
