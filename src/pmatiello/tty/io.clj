(ns pmatiello.tty.io
  (:require [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]))

(defn- cropped-height [height buffer]
  (let [blank (repeat height "")]
    (->> (concat buffer blank)
         (take height))))

(defn- cropped-width [width buffer-line]
  (let [blank (->> " " (repeat width) (apply str))]
    (-> buffer-line (str blank) (subs 0 width))))

(defn- cropped [buffer width height]
  (->> buffer
       (cropped-height height)
       (map #(cropped-width width %))))

(defn- into-output! [output each]
  (swap! output conj each))

(defn print! [output lines window]
  (let [{:keys [x y w h]} window
        cropped-lines (cropped lines w h)
        indexed-lines (map vector (range) cropped-lines)]
    (doseq [[offset ^String line] indexed-lines]
      (into-output! output (str (cursor/position (+ y offset) x) line)))))

(defn clear-screen! [output]
  (into-output! output erase/all)
  (into-output! output (cursor/position 1 1)))

(defn show-cursor! [output]
  (into-output! output cursor/show))

(defn hide-cursor! [output]
  (into-output! output cursor/hide))

(defn place-cursor! [output coords]
  (let [{:keys [x y]} coords]
    (into-output! output (cursor/position y x))))
