(ns pmatiello.tty.io
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]
            [pmatiello.tty.internal.io :as internal.io])
  (:import (clojure.lang Atom)))

(s/def ::output-buf
  (s/and #(instance? Atom %)
         #(s/valid? ::internal.io/output-buf @%)))

(s/def ::document (s/and sequential? (s/coll-of ::paragraph)))
(s/def ::paragraph string?)

(s/def ::window
  (s/keys :req [::row ::column ::width ::height]))

(s/def ::coordinates
  (s/keys :req [::row ::column]))

(s/def ::row int?)
(s/def ::column int?)
(s/def ::width int?)
(s/def ::height int?)

(defn- cropped-height
  "Crops the document at the given height.
  Completes remaining rows with blank characters."
  [document height]
  (let [blank (repeat height "")]
    (->> (concat document blank)
         (take height))))

(s/fdef cropped-height
  :args (s/cat :document ::document :height ::height)
  :ret ::document)

(defn- cropped-width
  "Crops the document at the given width.
  Completes remaining columns with blank characters."
  [line width]
  (let [blank (->> " " (repeat width) (apply str))]
    (-> line (str blank) (subs 0 width))))

(s/fdef cropped-width
  :args (s/cat :line ::paragraph :width ::width)
  :ret ::paragraph)

(defn- cropped
  "Crops the document at the given width and height.
  Completes remaining rows and columns with blank characters."
  [document width height]
  (->> (cropped-height document height)
       (map #(cropped-width % width))))

(s/fdef cropped
  :args (s/cat :document ::document :width ::width :height ::height)
  :ret ::document)

(defn- into-output-buf!
  "Appends the given string to the output buffer."
  [output-buf payload]
  (swap! output-buf conj payload))

(s/fdef into-output-buf!
  :args (s/cat :output-buf ::output-buf :payload string?))

(defn print!
  "Prints the given document to the output buffer at the given window."
  [output-buf document window]
  (let [{::keys [row column width height]} window
        cropped-lines (cropped document width height)
        indexed-lines (map vector (range) cropped-lines)]
    (doseq [[offset ^String line] indexed-lines]
      (into-output-buf! output-buf (str (cursor/position (+ row offset) column) line)))))

(s/fdef print!
  :args (s/cat :output-buf ::output-buf :document ::document :window ::window))

(defn clear-screen!
  "Clears the screen completely."
  [output-buf]
  (into-output-buf! output-buf erase/all)
  (into-output-buf! output-buf (cursor/position 1 1)))

(s/fdef clear-screen!
  :args (s/cat :output-buf ::output-buf))

(defn show-cursor!
  "Makes the cursor visible."
  [output-buf]
  (into-output-buf! output-buf cursor/show))

(s/fdef show-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn hide-cursor!
  "Makes the cursor invisible."
  [output-buf]
  (into-output-buf! output-buf cursor/hide))

(s/fdef hide-cursor!
  :args (s/cat :output-buf ::output-buf))

(defn place-cursor!
  "Places the cursor at the given coordinates."
  [output-buf coordinates]
  (let [{::keys [column row]} coordinates]
    (into-output-buf! output-buf (cursor/position row column))))

(s/fdef place-cursor!
  :args (s/cat :output-buf ::output-buf :coordinates ::coordinates)
  :ret ::document)
