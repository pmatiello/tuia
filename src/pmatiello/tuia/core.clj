(ns pmatiello.tuia.core
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tuia.internal.ansi.input :as input]
            [pmatiello.tuia.internal.io :as io]
            [pmatiello.tuia.internal.mainloop :as mainloop]
            [pmatiello.tuia.state :as state])
  (:import (java.io Writer)))

(defn init!
  "Initializes the application main loop.

  handle-fn: function invoked for each input event. Args:
    - event

  render-fn: function invoked for each change in state. Args:
    - output: a ::tuia.io/output-buf object
    - old-state: previous ::tuia.state/state
    - new-state: updated ::tuia.state/state

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
