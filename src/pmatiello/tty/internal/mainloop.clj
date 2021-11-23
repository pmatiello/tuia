(ns pmatiello.tty.internal.mainloop
  (:require [pmatiello.tty.lifecycle :as tty.lifecycle]
            [pmatiello.tty.state :as tty.state]
            [pmatiello.tty.event :as tty.event]
            [pmatiello.tty.internal.signal :as signal]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [clojure.spec.alpha :as s]))

(defn- watch-fn
  "Produces a watch function for an atom such that render-fn is invoked with the arguments:
    - ::tty.io/output-buf, old-state, new-state.

  After render-fn finishes, writes the output-buf (calling output!)."
  [render-fn output!]
  (fn [_ _ old-state new-state]
    (let [output (atom [])]
      (render-fn output old-state new-state)
      (output! @output))))

(s/fdef watch-fn
  :args (s/cat :render-fn fn? :output fn?)
  :ret fn?)

(defn- call-sync!
  "Invokes func with event as argument, serially."
  [func & args]
  (locking func
    (apply func args)))

(s/fdef call-sync!
  :args (s/cat :func fn? :args (s/* any?)))

(defn- notify!
  "Notify functions of an event."
  [handle-fn state event value]
  (call-sync! handle-fn {:event event :value value})
  (swap! state assoc event value))

(s/fdef notify!
  :args (s/cat :handle-fn fn? :state ::tty.state/state :event ::tty.event/event :value any?))

(defn- tty-size?!
  "Requests tty size."
  [output! _signal]
  (output! [(cursor/position 9999 9999) cursor/current-position]))

(s/fdef tty-size?!
  :args (s/cat :output! fn? :_signal any?))

(defn with-mainloop
  "Starts the main loop.

  handle-fn: input event handler function.
  render-fn: output rendering function.
  state: mutable application state
  input: sequence of input events
  output!: function for writing to the output stream."
  [handle-fn render-fn state input output!]
  (try
    (add-watch state ::state-changed (watch-fn render-fn output!))
    (notify! handle-fn state ::tty.lifecycle/init true)

    (signal/trap :winch (partial tty-size?! output!))
    (tty-size?! output! nil)

    (doseq [event input]
      (case (:event event)
        :cursor-position
        (notify! handle-fn state ::tty.lifecycle/size (:value event))

        (call-sync! handle-fn event)))

    (finally
      (notify! handle-fn state ::tty.lifecycle/halt true)
      (remove-watch state ::state-changed))))

(s/fdef with-mainloop
  :args (s/cat :handle-fn fn? :render-fn fn? :state ::tty.state/state
               :input sequential? :output! fn?))
