(ns pmatiello.terminus.input-demo
  (:require [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.framework :as framework]
            [pmatiello.terminus.internal.framework.io :as io]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop]
            [clojure.string :as str])
  (:import (clojure.lang ExceptionInfo)))

(def state
  (atom {:events    '()
         :curr-pos? 0}))

(def header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn render [output old-state new-state]
  (when-not (::mainloop/init old-state)
    (io/clear-screen! output)
    (io/print! output header {:x 1 :y 1 :w 23 :h 3}))

  (when (not= (:curr-pos? old-state) (:curr-pos? new-state))
    (io/print! output [cursor/current-position] {:x 1 :y 10 :w 4 :h 5}))

  (io/print! output (:events new-state) {:x 1 :y 5 :w 40 :h 6})
  (io/place-cursor! output {:x 1 :y 10}))

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event :value #{:f12})
    (swap! state update-in [:curr-pos?] inc))

  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (try
    (->> (atom []) io/hide-cursor! (io/write! *out*))
    (framework/new-tty-app handle render state)
    (catch ExceptionInfo ex
      (->> (atom []) io/show-cursor! (io/write! *out*))
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
