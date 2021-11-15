(ns pmatiello.tty.internal.mainloop)

(defn- watch-fn [render-fn output!]
  (fn [_ _ old-state new-state]
    (let [output (atom [])]
      (render-fn output old-state new-state)
      (output! @output))))

(defn- call-sync! [handle-fn event]
  (locking handle-fn
    (handle-fn event)))

(defn- notify! [handle-fn state event]
  (call-sync! handle-fn {:event event :value true})
  (swap! state assoc event true))

(defn with-mainloop
  [handle-fn render-fn state input output!]
  (try
    (add-watch state ::state-changed (watch-fn render-fn output!))
    (notify! handle-fn state :pmatiello.tty/init)

    (doseq [event input]
      (call-sync! handle-fn event))

    (finally
      (notify! handle-fn state :pmatiello.tty/halt)
      (remove-watch state ::state-changed))))
