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
  (s/keys :req-un [::row ::column ::width ::height]
          :opt-un [::style]))

(s/def ::coordinates
  (s/keys :req-un [::row ::column]))

(s/def ::row int?)
(s/def ::column int?)
(s/def ::width int?)
(s/def ::height int?)
(s/def ::style ::txt/style)

(defn- into-output-buf!
  "Appends the given string to the output buffer."
  [output-buf payload]
  (swap! output-buf conj payload))

(s/fdef into-output-buf!
  :args (s/cat :output-buf ::output-buf :payload string?))

(defn print!
  "Prints the given text to the output buffer at the given window.

  output-buf: mutable buffer accumulating writes to the output.

  text: text to be printed.

  window: a map describing a rectangle in the screen where the text will be printed
  with the following entries:
    - row: the row of the window's top-most row in the screen.
    - column: the column of the window's left-most column in the screen.
    - width: the window's width.
    - height: the window's height.
    - style: base style for the window (optional)."
  [output-buf text window]
  (let [{:keys [row column width height style]} window
        render-settings {:width width :height height :style style}
        rendered-text   (-> text
                            internal.txt/loose-text->text
                            (internal.txt/render render-settings))
        indexed-lines   (map vector (range) rendered-text)]
    (doseq [[offset ^String line] indexed-lines]
      (into-output-buf! output-buf (str (cursor/position (+ row offset) column) line)))))

(s/fdef print!
  :args (s/cat :output-buf ::output-buf :text ::txt/loose-text :window ::window))

(defn clear-screen!
  "Clears the screen completely.

  output-buf: mutable buffer accumulating writes to the output."
  [output-buf]
  (into-output-buf! output-buf (erase/all))
  (into-output-buf! output-buf (cursor/position 1 1)))

(s/fdef clear-screen!
  :args (s/cat :output-buf ::output-buf))

(defn show-cursor!
  "Makes the cursor visible.

  output-buf: mutable buffer accumulating writes to the output."
  [output-buf]
  (into-output-buf! output-buf (cursor/show)))

(s/fdef show-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn hide-cursor!
  "Makes the cursor invisible.

  output-buf: mutable buffer accumulating writes to the output."
  [output-buf]
  (into-output-buf! output-buf (cursor/hide)))

(s/fdef hide-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn place-cursor!
  "Places the cursor at the given coordinates.

  output-buf: mutable buffer accumulating writes to the output.

  coordinates: a map with the coordinates for placing the cursor
  with the following entries:
    - row.
    - column."
  [output-buf coordinates]
  (let [{:keys [column row]} coordinates]
    (into-output-buf! output-buf (cursor/position row column))))

(s/fdef place-cursor!
  :args (s/cat :output-buf ::output-buf :coordinates ::coordinates))
