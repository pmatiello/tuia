(ns pmatiello.tty.internal.io
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tty.internal.stty :as stty])
  (:import (java.io Writer)))

(s/def ::output-buf (s/and sequential? (s/coll-of string?)))

(defn write!
  "Writes the output buffer into given writer."
  [^Writer writer output-buf]
  (doseq [^String each output-buf]
    (.append writer each))
  (.flush writer))

(s/fdef write!
  :args (s/cat :writer #(instance? Writer %) :rendered-document ::output-buf))

(defn with-raw-tty
  "Executes func with the terminal set to raw mode.
  Returns tty to original state on termination."
  [func]
  (let [initial-stty (stty/current)]
    (try
      (stty/unset-flags! :icanon :echo)
      (func)
      (finally
        (stty/apply! initial-stty)))))

(s/fdef with-raw-tty
  :args (s/cat :func fn?))
