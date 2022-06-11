(ns pmatiello.tuia.internal.ansi.input.event
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            [pmatiello.tuia.event :as event]))

(s/def ::char-code-group (s/coll-of ::char-code))
(s/def ::char-code int?)

(def ^:private control-keys
  "Registry of control keys."
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
  "Dictionary of escaped keys."
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
  "Dictionary of special keys."
  (merge (into {} (map (fn [[k v]] [[k] v]) control-keys))
         escaped-keys))

(defn ^:private special-key
  "Returns keypress event for special keys (up, down, f1, esc, etc.)."
  [char-codes]
  (if-let [key (get special-keys char-codes)]
    {:type :keypress :value key}))

(s/fdef special-key
  :args (s/cat :char-codes ::char-code-group)
  :ret ::event/event)

(defn ^:private regular-key
  "Returns keypress event for regular keys (A, B, 1, 2, etc.)."
  [char-codes]
  (when (= (count char-codes) 1)
    {:type  :keypress
     :value (-> char-codes first char str)}))

(s/fdef regular-key
  :args (s/cat :char-codes ::char-code-group)
  :ret ::event/event)

(defn ^:private device-status-report
  "Returns current-position event when consuming device-status-report char codes."
  [char-codes]
  (when (and (= (take 2 char-codes) [27 91]) (= (last char-codes) 82))
    (let [pos-chars (->> char-codes (drop 2) drop-last)
          line      (->> pos-chars (take-while #(not= % 59)) (map char) str/join Integer/parseInt)
          column    (->> pos-chars (drop-while #(not= % 59)) (drop 1) (map char) str/join Integer/parseInt)]
      {:type :cursor-position :value [line column]})))

(s/fdef device-status-report
  :args (s/cat :char-codes ::char-code-group)
  :ret ::event/event)

(defn char-codes->event
  "Converts the given char codes into an ::event/event."
  [char-codes]
  (or (special-key char-codes)
      (regular-key char-codes)
      (device-status-report char-codes)
      {:type :unknown :value char-codes}))

(s/fdef char-codes->event
  :args (s/cat :char-codes ::char-code-group)
  :ret ::event/event)
