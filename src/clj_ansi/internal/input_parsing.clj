(ns clj-ansi.internal.input-parsing
  (:require [clj-ansi.internal.input-event :as input-event])
  (:import (java.io Reader)))

(defn ^:private each->key [acc key]
  (cond
    (and (:has-next? key) (= (:char-code key) 27))
    (let [result @acc]
      (vreset! acc [key])
      result)

    (and (not-empty @acc) (:has-next? key))
    (do (vswap! acc conj key)
        nil)

    :else
    (let [result (conj @acc key)]
      (vreset! acc [])
      result)))

(defn ^:private with-grouped-escape-seqs [input-seq]
  (let [acc (volatile! [])]
    (->> input-seq
         (map (partial each->key acc))
         (remove empty?))))

(def ^:private key->key-codes
  (partial map :char-code))

(defn input-seq->event-seq [input-seq]
  (->> input-seq
       with-grouped-escape-seqs
       (map key->key-codes)
       (map input-event/key-codes->event)
       (remove #{::input-event/omit})))

(defn reader->input-seq [^Reader reader]
  (lazy-seq
    (let [char-code (.read reader)
          _         (if (= 27 char-code)
                      (Thread/sleep 10))
          has-next? (.ready reader)]
      (when (not= char-code -1)
        (cons {:char-code char-code :has-next? has-next?}
              (reader->input-seq reader))))))
