(ns pmatiello.tty.core
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tty.internal.ansi.input :as input]
            [pmatiello.tty.internal.io :as io]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [pmatiello.tty.state :as state])
  (:import (java.io Writer)))

(defn init!
  "Initializes the application main loop.

  handle-fn: function invoked for each input event. Args:
    - event

  render-fn: function invoked for each change in state. Args:
    - output: a ::tty.io/output-buf object
    - old-state: previous ::tty.state/state
    - new-state: updated ::tty.state/state

  state: mutable application state"
  [handle-fn render-fn state]
  (let [input (input/reader->event-seq *in*)
        output! (partial io/write! *out*)]
    (with-redefs
      [*out* (Writer/nullWriter)]
      (io/with-raw-tty
        #(mainloop/with-mainloop handle-fn render-fn state input output!)))))

(s/fdef init!
  :args (s/cat :handle-fn fn? :render-fn fn? :state ::state/state))
