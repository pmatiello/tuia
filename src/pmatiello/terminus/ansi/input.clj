(ns pmatiello.terminus.ansi.input
  (:require [pmatiello.terminus.ansi.internal.input-parsing :as input-parsing])
  (:import (java.io Reader)))

(defn reader->event-seq [^Reader reader]
  (->> reader
       input-parsing/reader->input-seq
       input-parsing/input-seq->event-seq))
