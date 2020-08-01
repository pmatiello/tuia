(ns clj-ansi.internal.output)

(def ^:private csi "\u001b[")

(defn ansi-seq [& code]
  (apply str csi code))
