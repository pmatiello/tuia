(ns clj-ansi.input)

(def ^:private control-chars
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

(def ^:private escape-seqs
  {[27 91 65]        :up
   [27 91 66]        :down
   [27 91 67]        :right
   [27 91 68]        :left
   [27 91 70]        :end
   [27 91 71]        :keypad-5
   [27 91 72]        :home
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

(defn ^:private key->escape-seq [state key]
  (cond
    (-> key map? not) key
    (-> key :escape?) (do (swap! state conj key) nil)
    (-> @state empty?) key
    :else (let [escape-seq-keys  (conj @state key)
                escape-seq-codes (map :char-code escape-seq-keys)
                escape-seq       (get escape-seqs escape-seq-codes)]
            (reset! state [])
            (or escape-seq :unknown))))

(def ^:private is-control-char?
  (-> control-chars keys set))

(defn ^:private key->control-char [key]
  (if (and (map? key) (-> key :char-code is-control-char?))
    (-> key :char-code control-chars)
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

(defn parse [input-seq]
  (let [state (atom [])]
    (->> input-seq
         (map (partial parse-each state))
         (remove nil?))))
