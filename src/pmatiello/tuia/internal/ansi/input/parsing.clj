(ns pmatiello.tuia.internal.ansi.input.parsing
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tuia.internal.ansi.input.event :as input.event])
  (:import (java.io Reader)))

(s/def ::char-group
  (s/coll-of ::char))

(s/def ::char-group-builder
  (s/and volatile? #(s/valid? ::char-group @%)))

(s/def ::char
  (s/keys :req [::char-code ::has-next?]))

(s/def ::char-code int?)
(s/def ::has-next boolean?)

(def ^:private escape-seq-timeout-ms
  "Time, in msec, given to the input buffer to fill between characters before deciding whether
  an ESC character is an ESC keypress or the initiation of an escape sequence."
  10)

(defn ^:private starting-escape-sequence?
  "Returns whether the given ::char starts an escape sequence."
  [char]
  (and (::has-next? char) (= (::char-code char) 27)))

(s/fdef starting-escape-sequence?
  :args (s/cat :char ::char)
  :ret any?)

(defn ^:private continuing-escape-sequence?
  "Returns whether the given state and ::char continue an escape sequence."
  [acc char]
  (and (not-empty @acc) (::has-next? char)))

(s/fdef continuing-escape-sequence?
  :args (s/cat :acc ::char-group-builder :char ::char)
  :ret any?)

(defn ^:private char->char-group
  "Consumes given ::char until a ::char-group is formed."
  [acc char]
  (cond
    (starting-escape-sequence? char)
    (let [result @acc]
      (vreset! acc [char])
      result)

    (continuing-escape-sequence? acc char)
    (do (vswap! acc conj char)
        nil)

    :else
    (let [result (conj @acc char)]
      (vreset! acc [])
      result)))

(s/fdef char->char-group
  :args (s/cat :acc ::char-group-builder :char ::char)
  :ret ::char-group)

(defn ^:private in-char-groups
  "Converts a sequence of ::char into a sequence of ::char-group.
  Groups ::char forming an ANSI escape sequence into a single ::char-group."
  [char-seq]
  (let [acc (volatile! [])]
    (->> char-seq
         (map (partial char->char-group acc))
         (remove empty?))))

(s/fdef in-char-groups
  :args (s/cat :char-seq sequential?)
  :ret sequential?)

(def ^:private char-group->char-codes
  "Converts a ::char-group into a sequence of ::char-code."
  (partial map ::char-code))

(s/fdef char-group->char-codes
  :args (s/cat :key ::char-group)
  :ret (s/coll-of ::char-code))

(defn char-seq->event-seq
  "Converts a sequence of ::char into a sequence of ::event/event.
  Preserves the laziness of the given sequence."
  [char-seq]
  (->> char-seq
       in-char-groups
       (map char-group->char-codes)
       (map input.event/char-codes->event)))

(s/fdef char-seq->event-seq
  :args (s/cat :char-seq sequential?)
  :ret sequential?)

(defn reader->char-seq
  "Converts input from reader into a lazy sequence of ::char"
  [^Reader reader]
  (lazy-seq
    (let [char-code (.read reader)
          _ (if (= 27 char-code)
              (Thread/sleep escape-seq-timeout-ms))
          has-next? (.ready reader)]
      (when (not= char-code -1)
        (cons {::char-code char-code ::has-next? has-next?}
              (reader->char-seq reader))))))

(s/fdef reader->char-seq
  :args (s/cat :reader #(instance? Reader %))
  :ret sequential?)
