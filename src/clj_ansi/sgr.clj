(ns clj-ansi.sgr)

(def ^:private csi "\u001B[")

(defn ^:private ansi-seq [code]
  (str csi code))

(def reset (ansi-seq "0m"))
(def bold (ansi-seq "1m"))
