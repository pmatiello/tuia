(ns clj-ansi.internal.input-event
  (:require [clojure.string :as str]))

(def ^:private control-keys
  {0   :nul
   1   :soh
   2   :stx
   3   :etx
   4   :eot
   5   :enq
   6   :ack
   7   :bel
   8   :bs
   9   :ht
   10  :lf
   11  :vt
   12  :ff
   13  :cr
   14  :so
   15  :si
   16  :dle
   17  :dc1
   18  :dc2
   19  :dc3
   20  :dc4
   21  :nak
   22  :syn
   23  :etb
   24  :can
   25  :em
   26  :sub
   27  :esc
   28  :fs
   29  :gs
   30  :rs
   31  :us
   127 :del})

(def ^:private escaped-keys
  {[27 91 65]        :up
   [27 91 66]        :down
   [27 91 67]        :right
   [27 91 68]        :left
   [27 91 70]        :end
   [27 91 71]        :keypad-5
   [27 91 72]        :home
   [27 91 90]        :cbt
   [27 91 51 126]    :kdch1
   [27 79 80]        :f1
   [27 79 81]        :f2
   [27 79 82]        :f3
   [27 79 83]        :f4
   [27 91 49 53 126] :f5
   [27 91 49 55 126] :f6
   [27 91 49 56 126] :f7
   [27 91 49 57 126] :f8
   [27 91 50 48 126] :f9
   [27 91 50 49 126] :f10
   [27 91 50 51 126] :f11
   [27 91 50 52 126] :f12})

(def ^:private special-keys
  (merge (into {} (map (fn [[k v]] [[k] v]) control-keys))
         escaped-keys))

(defn ^:private special-key [key-codes]
  (if-let [key (get special-keys key-codes)]
    {:event :keypress :value key}))

(defn ^:private regular-key [key-codes]
  (when (= (count key-codes) 1)
    {:event :keypress
     :value (-> key-codes first char str)}))

(defn ^:private device-status-report [key-codes]
  (when (and (= (take 2 key-codes) [27 91]) (= (last key-codes) 82))
    (let [pos-chars (->> key-codes (drop 2) drop-last)
          line      (->> pos-chars (take-while #(not= % 59)) (map char) str/join Integer/parseInt)
          column    (->> pos-chars (drop-while #(not= % 59)) (drop 1) (map char) str/join Integer/parseInt)]
      {:event :cursor-position :value [line column]})))

(defn key-codes->event [key-codes]
  (or (special-key key-codes)
      (regular-key key-codes)
      (device-status-report key-codes)
      {:event :unknown :value key-codes}))
