(ns pmatiello.terminus.internal.ansi.support)

(def ^:private csi "\u001b[")

(defn ^String ansi-seq [& code]
  (apply str csi code))
