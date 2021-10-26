(ns pmatiello.terminus.framework
  (:require [pmatiello.terminus.internal.ansi.input :as input]
            [pmatiello.terminus.internal.framework.raw-tty :as raw-tty]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop]))

(defn new-tty-app
  [handle-fn render-fn state]
  (let [input (input/reader->event-seq *in*)]
    (raw-tty/with-raw-tty
      #(mainloop/with-mainloop handle-fn render-fn state input))))
