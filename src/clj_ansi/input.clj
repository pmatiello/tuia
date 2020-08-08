(ns clj-ansi.input
  (:require [clj-ansi.internal.input :as internal.input]
            [clojure.string :as str])
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

      (and (= (take 2 char-codes) [27 91]) (= (last char-codes) 82))
      (let [pos-chars (->> char-codes (drop 2) drop-last)
            line      (->> pos-chars (take-while #(not= % 59)) (map char) str/join Integer/parseInt)
            column    (->> pos-chars (drop-while #(not= % 59)) (drop 1) (map char) str/join Integer/parseInt)]
        (swap! internal.input/state assoc :cursor-position [line column])
        nil)

      :else
      :unknown)))

(defn input-seq->char-seq [input-seq]
  (->> input-seq
       input-seq->key-seq
       (map key->char)
       (remove nil?)))

(defn reader->input-seq [^Reader reader]
  (lazy-seq
    (let [char-code (.read reader)
          has-next? (do (Thread/sleep 0) (.ready reader))]
      (cons {:char-code char-code :has-next? has-next?}
            (reader->input-seq reader)))))
