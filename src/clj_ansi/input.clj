(ns clj-ansi.input
  (:require [clj-ansi.internal.input :as internal.input])
  (:import (java.io Reader)))

(defn ^:private each->key [acc key]
  (if (:has-next? key)
    (do (vswap! acc conj key) nil)
    (let [result (conj @acc key)]
      (vreset! acc [])
      result)))

(defn ^:private input-seq->key-seq [input-seq]
  (let [acc (volatile! [])]
    (->> input-seq
         (map (partial each->key acc))
         (remove nil?))))

(defn ^:private key->char [key]
  (let [char-codes (map :char-code key)]
    (cond
      (contains? internal.input/special-chars char-codes)
      (get internal.input/special-chars char-codes)

      (= (count key) 1)
      (-> key first :char-code char str)

      :else
      :unknown)))

(defn input-seq->char-seq [input-seq]
  (->> input-seq
       input-seq->key-seq
       (map key->char)))

(defn reader->input-seq [^Reader reader]
  (lazy-seq
    (let [char-code (.read reader)
          has-next? (do (Thread/sleep 0) (.ready reader))]
      (cons {:char-code char-code :has-next? has-next?}
            (reader->input-seq reader)))))
