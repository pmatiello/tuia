(ns pmatiello.terminus.internal.framework.mainloop)

(defn with-mainloop
  [handle-fn render-fn state input]
  (try
    (add-watch state ::state-changed (fn [_ _ old-state new-state]
                                       (render-fn old-state new-state)))
    (swap! state assoc ::init true)
    (mapv handle-fn input)
    (finally
      (remove-watch state ::state-changed))))
