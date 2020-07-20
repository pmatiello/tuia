(ns clj-ansi.shared)

(def ^:private csi "\u001b[")

(defn ansi-seq [& code]
  (apply str csi code))
