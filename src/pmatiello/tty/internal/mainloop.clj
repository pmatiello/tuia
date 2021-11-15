(ns pmatiello.tty.internal.mainloop)

(defn- watch-fn [render-fn output!]
  (fn [_ _ old-state new-state]
    (let [output (atom [])]
      (render-fn output old-state new-state)
      (output! @output))))

(defn- notify! [state event]
  (swap! state assoc event true))

(defn with-mainloop
  [handle-fn render-fn state input output!]
  (try
    (add-watch state ::state-changed (watch-fn render-fn output!))
    (notify! state :pmatiello.tty/init)

    (doseq [event input]
      (handle-fn event))

    (finally
      (notify! state :pmatiello.tty/halt)
      (remove-watch state ::state-changed))))
