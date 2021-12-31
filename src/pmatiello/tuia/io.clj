(ns pmatiello.tuia.io
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tuia.internal.ansi.cursor :as cursor]
            [pmatiello.tuia.internal.ansi.erase :as erase]
            [pmatiello.tuia.internal.io :as internal.io]
            [pmatiello.tuia.internal.text :as internal.txt]
            [pmatiello.tuia.text :as txt])
  (:import (clojure.lang Atom)))

(s/def ::output-buf
  (s/and #(instance? Atom %)
         #(s/valid? ::internal.io/output-buf @%)))

(s/def ::window
  (s/keys :req [::row ::column ::width ::height]))

(s/def ::coordinates
  (s/keys :req [::row ::column]))

(s/def ::row int?)
(s/def ::column int?)
(s/def ::width int?)
(s/def ::height int?)

(defn- into-output-buf!
  "Appends the given string to the output buffer."
  [output-buf payload]
  (swap! output-buf conj payload))

(s/fdef into-output-buf!
  :args (s/cat :output-buf ::output-buf :payload string?))

(defn print!
  "Prints the given text to the output buffer at the given window."
  [output-buf text window]
  (let [{::keys [row column width height]} window
        render-settings #::internal.txt{:width width :height height}
        page (-> text
                 internal.txt/loose-text->text
                 (internal.txt/text->page render-settings))
        indexed-lines (map vector (range) page)]
    (doseq [[offset ^String line] indexed-lines]
      (into-output-buf! output-buf (str (cursor/position (+ row offset) column) line)))))

(s/fdef print!
  :args (s/cat :output-buf ::output-buf :text ::txt/loose-text :window ::window))

(defn clear-screen!
  "Clears the screen completely."
  [output-buf]
  (into-output-buf! output-buf (erase/all))
  (into-output-buf! output-buf (cursor/position 1 1)))

(s/fdef clear-screen!
  :args (s/cat :output-buf ::output-buf))

(defn show-cursor!
  "Makes the cursor visible."
  [output-buf]
  (into-output-buf! output-buf (cursor/show)))

(s/fdef show-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn hide-cursor!
  "Makes the cursor invisible."
  [output-buf]
  (into-output-buf! output-buf (cursor/hide)))

(s/fdef hide-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn place-cursor!
  "Places the cursor at the given coordinates."
  [output-buf coordinates]
  (let [{::keys [column row]} coordinates]
    (into-output-buf! output-buf (cursor/position row column))))

(s/fdef place-cursor!
  :args (s/cat :output-buf ::output-buf :coordinates ::coordinates))
