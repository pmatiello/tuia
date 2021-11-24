(ns pmatiello.tty.internal.ansi.input
  (:require [pmatiello.tty.internal.ansi.input.parsing :as input.parsing]
            [clojure.spec.alpha :as s])
  (:import (java.io Reader)))

(s/def ::event-seq sequential?)

(defn reader->event-seq
  "Converts input from reader into a lazy sequence of events"
  [^Reader reader]
  (->> reader
       input.parsing/reader->input-seq
       input.parsing/input-seq->event-seq))

(s/fdef reader->event-seq
  :args (s/cat :reader #(instance? Reader %))
  :ret ::event-seq)
