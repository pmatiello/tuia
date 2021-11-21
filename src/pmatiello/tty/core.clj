(ns pmatiello.tty.core
  (:require [pmatiello.tty.internal.ansi.input :as input]
            [pmatiello.tty.internal.io :as io]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [clojure.spec.alpha :as s])
  (:import (java.io Writer)
           (clojure.lang Atom)))

(s/def ::state
  (s/and #(instance? Atom %)
         #(-> % deref map?)))

(defn init!
  "Initializes the application main loop.

  handle-fn: function invoked for each input event. Args:
    - event

  render-fn: function invoked for each change in state. Args:
    - output: a #::tty.io/output object
    - old-state: previous ::state
    - new-state: updated ::state

  state: mutable application state"
  [handle-fn render-fn state]
  (let [input (input/reader->event-seq *in*)
        output! (partial io/write! *out*)]
    (with-redefs
      [*out* (Writer/nullWriter)]
      (io/with-raw-tty
        #(mainloop/with-mainloop handle-fn render-fn state input output!)))))

(s/fdef init!
  :args (s/cat :handle-fn fn? :render-fn fn? :state ::state))
