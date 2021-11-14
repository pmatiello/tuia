(ns pmatiello.tty.internal.mainloop)

(defn- render-output! [render-fn output! old-state new-state]
  (let [output (atom [])]
    (render-fn output old-state new-state)
    (output! @output)))

(defn- notify-init! [state]
  (swap! state assoc :pmatiello.tty/init true))

(defn- notify-halt! [state]
  (swap! state assoc :pmatiello.tty/halt true))

(defn with-mainloop
  [handle-fn render-fn state input output!]
  (try
    (add-watch state
               ::state-changed
               (fn [_ _ old-state new-state]
                 (render-output! render-fn output! old-state new-state)))

    (notify-init! state)
    (mapv handle-fn input)
    (finally
      (notify-halt! state)
      (remove-watch state ::state-changed))))
