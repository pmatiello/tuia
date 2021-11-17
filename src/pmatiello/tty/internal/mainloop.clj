(ns pmatiello.tty.internal.mainloop
  (:require [pmatiello.tty.internal.signal :as signal]
            [pmatiello.tty.internal.ansi.cursor :as cursor]))

(defn- watch-fn [render-fn output!]
  (fn [_ _ old-state new-state]
    (let [output (atom [])]
      (render-fn output old-state new-state)
      (output! @output))))

(defn- call-sync! [handle-fn event]
  (locking handle-fn
    (handle-fn event)))

(defn- notify! [handle-fn state event value]
  (call-sync! handle-fn {:event event :value value})
  (swap! state assoc event value))

(defn- tty-size?! [output! _signal]
  (output! [(cursor/position 9999 9999) cursor/current-position]))

(defn with-mainloop
  [handle-fn render-fn state input output!]
  (try
    (add-watch state ::state-changed (watch-fn render-fn output!))
    (notify! handle-fn state :pmatiello.tty/init true)

    (signal/trap :winch (partial tty-size?! output!))
    (tty-size?! output! nil)

    (doseq [event input]
      (case (:event event)
        :cursor-position
        (notify! handle-fn state :pmatiello.tty/size (:value event))

        (call-sync! handle-fn event)))

    (finally
      (notify! handle-fn state :pmatiello.tty/halt true)
      (remove-watch state ::state-changed))))
