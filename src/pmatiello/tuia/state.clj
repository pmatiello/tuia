(ns pmatiello.tuia.state
  (:require [clojure.spec.alpha :as s])
  (:import (clojure.lang Atom)))

(s/def ::state
  (s/and #(instance? Atom %)
         #(-> % deref map?)))
