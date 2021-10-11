(ns pmatiello.terminus.internal.framework.mainloop
  (:require [pmatiello.terminus.internal.ansi.input :as input]))

(defn with-mainloop
  [handle-fn render-fn state input]
  (try
    (add-watch state ::state-changed (fn [_ _ old-state new-state]
                                       (render-fn old-state new-state)))
    (swap! state assoc ::init true)
    (->> input input/reader->event-seq (mapv handle-fn))
    (finally
      (remove-watch state ::state-changed))))
