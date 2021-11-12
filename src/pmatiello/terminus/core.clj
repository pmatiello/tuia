(ns pmatiello.terminus.core
  (:require [pmatiello.terminus.internal.ansi.input :as input]
            [pmatiello.terminus.internal.framework.io :as io]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop])
  (:import (java.io Writer)))

(defn new-tty-app
  [handle-fn render-fn state]
  (let [input (input/reader->event-seq *in*)
        output! (partial io/write! *out*)]
    (with-redefs
      [*out* (Writer/nullWriter)]
      (io/with-raw-tty
        #(mainloop/with-mainloop handle-fn render-fn state input output!)))))
