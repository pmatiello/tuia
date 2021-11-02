(ns pmatiello.terminus.input-demo
  (:require [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.framework :as framework]
            [pmatiello.terminus.internal.framework.io :as io]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop]
            [clojure.string :as str])
  (:import (clojure.lang ExceptionInfo)))

(def state (atom {:events '()}))

(def header
  ["input-demo ------------"
   "Type to produce events."
   "Enter Ctrl+D to quit."])

(defn render [old-state new-state]
  (when-not (::mainloop/init old-state)
    (io/clear-screen! *out*)
    (io/hide-cursor! *out*)
    (io/print! *out* header {:x 1 :y 1 :w 23 :h 3}))

  (io/print! *out* (:events new-state) {:x 1 :y 5 :w 40 :h 6}))

(defn handle [event]
  (swap! state assoc :events
         (->> event (conj (:events @state)) (take 5)))

  (when (-> event :value #{:f12})
    (.append *out* cursor/current-position)
    (.flush *out*))

  (when (-> event :value #{:eot})
    (throw (ex-info "Interrupted" {:cause :interrupted}))))

(defn -main []
  (try
    (framework/new-tty-app handle render state)
    (catch ExceptionInfo ex
      (io/show-cursor! *out*)
      (if (-> ex ex-data :cause #{:interrupted})
        (System/exit 0)
        (throw ex)))))
