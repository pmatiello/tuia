(ns pmatiello.terminus.stty
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as string]))

(defn ^:private stty-part->map [part]
  (let [[k v] (string/split part #"=")]
    {(keyword k) v}))

(defn current []
  (let [stty-str   (-> (sh "/bin/sh" "-c" "stty -g < /dev/tty") :out string/trim-newline)
        stty-parts (string/split stty-str #":")
        stty-maps  (map stty-part->map stty-parts)]
    (apply merge stty-maps)))

(defn apply! [settings]
  (let [stty-args (->> settings
                       (map identity)
                       (map #(remove nil? %))
                       (map #(mapv name %))
                       (map #(string/join "=" %))
                       (string/join ":"))]
    (sh "/bin/sh" "-c" (str "stty " stty-args " < /dev/tty"))))
