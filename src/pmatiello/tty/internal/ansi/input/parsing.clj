(ns pmatiello.tty.internal.ansi.input.parsing
  (:require [pmatiello.tty.internal.ansi.input.event :as input.event]
            [clojure.spec.alpha :as s])
  (:import (java.io Reader)))

(s/def ::char-group (s/coll-of ::char))
(s/def ::char
  (s/keys :req [::char-code ::has-next?]))

(s/def ::char-code int?)
(s/def ::has-next boolean?)

(defn ^:private starting-escape-sequence? [char]
  (and (::has-next? char) (= (::char-code char) 27)))

(s/fdef starting-escape-sequence?
  :args (s/cat :char ::char)
  :ret any?)

(defn ^:private continuing-escape-sequence? [acc char]
  (and (not-empty @acc) (::has-next? char)))

(s/fdef continuing-escape-sequence?
  :args (s/cat :acc volatile? :char ::char))

(def ^:private escape-seq-timeout-ms 10)

(defn ^:private char->char-group [acc char]
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
  :args (s/cat :acc volatile? :char ::char))

(defn ^:private in-char-groups [char-seq]
  (let [acc (volatile! [])]
    (->> char-seq
         (map (partial char->char-group acc))
         (remove empty?))))

(s/fdef in-char-groups
  :args (s/cat :char-seq sequential?)
  :ret sequential?)

(def ^:private char-group->char-codes
  (partial map ::char-code))

(s/fdef char-group->char-codes
  :args (s/cat :key ::char-group)
  :ret (s/coll-of ::char-code))

(defn char-seq->event-seq [char-seq]
  (->> char-seq
       in-char-groups
       (map char-group->char-codes)
       (map input.event/char-codes->event)))

(s/fdef char-seq->event-seq
  :args (s/cat :char-seq sequential?)
  :ret sequential?)

(defn reader->char-seq [^Reader reader]
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
