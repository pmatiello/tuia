(ns pmatiello.tty.io
  (:require [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]
            [clojure.spec.alpha :as s])
  (:import (clojure.lang Atom)))

(s/def ::output
  (s/and #(instance? Atom %)
         #(-> % deref sequential?)))

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

(defn- into-output!
  "Adds string to the list of output writes."
  [output string]
  (swap! output conj string))

(s/fdef into-output!
  :args (s/cat :output ::output :string string?))

(defn print!
  "Prints the given document to output at the given window."
  [output document window]
  (let [{::keys [row column width height]} window
        cropped-lines (cropped document width height)
        indexed-lines (map vector (range) cropped-lines)]
    (doseq [[offset ^String line] indexed-lines]
      (into-output! output (str (cursor/position (+ row offset) column) line)))))

(s/fdef print!
  :args (s/cat :output ::output :document ::document :window ::window))

(defn clear-screen!
  "Clears the screen completely."
  [output]
  (into-output! output erase/all)
  (into-output! output (cursor/position 1 1)))

(s/fdef clear-screen!
  :args (s/cat :output ::output))

(defn show-cursor!
  "Makes the cursor visible."
  [output]
  (into-output! output cursor/show))

(s/fdef show-cursor!
  :args (s/cat :output ::output))

(defn hide-cursor!
  "Makes the cursor invisible."
  [output]
  (into-output! output cursor/hide))

(s/fdef hide-cursor!
  :args (s/cat :output ::output))

(defn place-cursor!
  "Places the cursor at the given coordinates."
  [output coordinates]
  (let [{::keys [column row]} coordinates]
    (into-output! output (cursor/position row column))))

(s/fdef place-cursor!
  :args (s/cat :output ::output :coordinates ::coordinates)
  :ret ::document)
