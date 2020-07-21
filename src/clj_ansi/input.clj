(ns clj-ansi.input)

(def ^:private control-chars
  {0  :nul
   1  :soh
   2  :stx
   3  :etx
   4  :eot
   5  :enq
   6  :ack
   7  :bel
   8  :bs
   9  :ht
   10 :lf
   11 :vt
   12 :ff
   13 :cr
   14 :so
   15 :si
   16 :dle
   17 :dc1
   18 :dc2
   19 :dc3
   20 :dc4
   21 :nak
   22 :syn
   23 :etb
   24 :can
   25 :em
   26 :sub
   27 :esc
   28 :fs
   29 :gs
   30 :rs
   31 :us})

(def is-control-char?
  (-> control-chars keys set))

(defn ^:private key->control-char [key]
  (if (and (map? key) (-> key :char-code is-control-char?))
    (-> key :char-code control-chars)
    key))

(defn ^:private key->regular-char [key]
  (if (map? key)
    (-> key :char-code char str)
    key))

(defn parse-one [key]
  (-> key
      key->control-char
      key->regular-char))

(defn parse [input-seq]
  (map parse-one input-seq))
