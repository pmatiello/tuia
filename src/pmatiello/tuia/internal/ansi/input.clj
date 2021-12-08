(ns pmatiello.tuia.internal.ansi.input
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tuia.internal.ansi.input.parsing :as input.parsing])
  (:import (java.io Reader)))

(s/def ::event-seq sequential?)

(defn reader->event-seq
  "Converts input from reader into a lazy sequence of events"
  [^Reader reader]
  (->> reader
       input.parsing/reader->char-seq
       input.parsing/char-seq->event-seq))

(s/fdef reader->event-seq
  :args (s/cat :reader #(instance? Reader %))
  :ret ::event-seq)
