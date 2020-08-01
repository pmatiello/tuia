(ns clj-ansi.input
  (:require [clj-ansi.internal.input :as internal.input])
  (:import (java.io Reader)))

(defn ^:private key->escape-seq [state key]
  (cond
    (-> key map? not) key
    (-> key :has-next?) (do (swap! state conj key) nil)
    (-> @state empty?) key
    :else (let [escape-seq-keys  (conj @state key)
                escape-seq-codes (map :char-code escape-seq-keys)
                escape-seq       (get internal.input/escape-seqs escape-seq-codes)]
            (reset! state [])
            (or escape-seq :unknown))))

(def ^:private is-control-char?
  (-> internal.input/control-chars keys set))

(defn ^:private key->control-char [key]
  (if (and (map? key) (-> key :char-code is-control-char?))
    (-> key :char-code internal.input/control-chars)
    key))

(defn ^:private key->regular-char [key]
  (if (map? key)
    (-> key :char-code char str)
    key))

(defn parse-each [state key]
  (->> key
       (key->escape-seq state)
       key->control-char
       key->regular-char))

(defn input-seq->char-seq [input-seq]
  (let [state (atom [])]
    (->> input-seq
         (map (partial parse-each state))
         (remove nil?))))

(defn reader->input-seq [^Reader reader]
  (lazy-seq
    (let [char-code (.read reader)
          has-next? (do (Thread/sleep 0) (.ready reader))]
      (cons {:char-code char-code :has-next? has-next?}
            (reader->input-seq reader)))))
