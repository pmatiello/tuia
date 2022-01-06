(ns pmatiello.tuia.internal.text
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [pmatiello.tuia.internal.ansi.graphics :as ansi.graphics]
            [pmatiello.tuia.text :as txt]))

(s/def ::page
  string?)

(s/def ::render-settings
  (s/keys :req [::width ::height]
          :opt [::default-style]))

(s/def ::width int?)
(s/def ::height int?)

(defn ^:private loose-paragraph->paragraph
  "Converts a loose paragraph into a strict paragraph."
  [loose-paragraph]
  (cond
    (string? loose-paragraph) [{::txt/style [] ::txt/body loose-paragraph}]
    (s/valid? ::txt/segment loose-paragraph) [loose-paragraph]
    (s/valid? ::txt/paragraph loose-paragraph) loose-paragraph))

(s/fdef loose-paragraph->paragraph
  :args (s/cat :loose-paragraph ::txt/loose-paragraph)
  :ret ::txt/paragraph)

(defn loose-text->text
  "Converts a loose text into a strict text."
  [loose-text]
  (map loose-paragraph->paragraph loose-text))

(s/fdef loose-text->text
  :args (s/cat :loose-text ::txt/loose-text)
  :ret ::txt/text)

(def ^:private blank-paragraph
  [{::txt/style [] ::txt/body ""}])

(defn ^:private with-height
  "Crops the text at the given height.
  Completes remaining rows with blank characters."
  [height text]
  (let [blank (repeat height blank-paragraph)]
    (->> (concat text blank)
         (take height))))

(s/fdef with-height
  :args (s/cat :height ::height :text ::txt/text)
  :ret ::txt/text)

(defn ^:private into-paragraph
  "Accumulates segments into a paragraph of a maximum width.
  Crops all chars beyond the maximum width."
  [max-width paragraph {:keys [::txt/style ::txt/body]}]
  (let [curr-width      (->> paragraph (map ::txt/body) (map count) (reduce +))
        remaining-width (- max-width curr-width)
        selected-size   (min remaining-width (count body))
        selected-text   (subs body 0 selected-size)
        new-segment     #::txt{:style (or style []) :body selected-text}]
    (conj paragraph new-segment)))

(s/fdef into-paragraph
  :args (s/cat :width int? :paragraph ::txt/paragraph :segment ::txt/segment)
  :ret ::txt/paragraph)

(defn ^:private with-width*
  "Crops the paragraph at the given width.
  Completes remaining columns with blank characters."
  [width paragraph]
  (let [padding          #::txt{:style [] :body (->> " " (repeat width) (apply str))}
        paragraph+pading (conj paragraph padding)]
    (reduce (partial into-paragraph width) [] paragraph+pading)))

(s/fdef with-width*
  :args (s/cat :width ::width :paragraph ::txt/paragraph)
  :ret ::txt/paragraph)

(defn ^:private with-width
  "Crops the text at the given width.
  Completes remaining columns with blank characters."
  [width text]
  (let [with-width-fn (partial with-width* width)]
    (map with-width-fn text)))

(s/fdef with-width
  :args (s/cat :width ::width :text ::txt/text)
  :ret ::txt/text)

(def ^:private style->string*
  {::txt/bold       (ansi.graphics/bold)
   ::txt/underline  (ansi.graphics/underline)
   ::txt/blink      (ansi.graphics/slow-blink)
   ::txt/fg-black   (ansi.graphics/fg-black)
   ::txt/fg-red     (ansi.graphics/fg-red)
   ::txt/fg-green   (ansi.graphics/fg-green)
   ::txt/fg-yellow  (ansi.graphics/fg-yellow)
   ::txt/fg-blue    (ansi.graphics/fg-blue)
   ::txt/fg-purple  (ansi.graphics/fg-purple)
   ::txt/fg-cyan    (ansi.graphics/fg-cyan)
   ::txt/fg-white   (ansi.graphics/fg-white)
   ::txt/fg-default (ansi.graphics/fg-default)
   ::txt/bg-black   (ansi.graphics/bg-black)
   ::txt/bg-red     (ansi.graphics/bg-red)
   ::txt/bg-green   (ansi.graphics/bg-green)
   ::txt/bg-yellow  (ansi.graphics/bg-yellow)
   ::txt/bg-blue    (ansi.graphics/bg-blue)
   ::txt/bg-purple  (ansi.graphics/bg-purple)
   ::txt/bg-cyan    (ansi.graphics/bg-cyan)
   ::txt/bg-white   (ansi.graphics/bg-white)
   ::txt/bg-default (ansi.graphics/bg-default)})

(defn ^:private style->string
  "Renders the ANSI codes for the given style"
  [style]
  (->> style
       (map style->string*)
       string/join))

(s/fdef style->string
  :args (s/cat :style ::txt/style)
  :ret string?)

(defn ^:private segment->string
  "Renders a segment into a printable string."
  [{:keys [::txt/style ::txt/body]}]
  (if (empty? style)
    body
    (str (style->string style) body (ansi.graphics/reset))))

(s/fdef segment->string
  :args (s/cat :segment ::txt/segment)
  :ret string?)

(defn ^:private paragraph->string
  "Renders a paragraph into a printable string."
  [paragraph]
  (->> paragraph
       (map segment->string)
       (apply str)))

(s/fdef paragraph->string
  :args (s/cat :paragraph ::txt/paragraph)
  :ret string?)

(defn text->page
  "Renders text into a printable string."
  [text {::keys [width height]}]
  (->> text
       (with-height height)
       (with-width width)
       (map paragraph->string)))

(s/fdef text->page
  :args (s/cat :text ::txt/text :settings ::render-settings)
  :ret ::page)
