(ns pmatiello.tuia.internal.text
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [pmatiello.tuia.internal.ansi.graphics :as graphics]
            [pmatiello.tuia.text :as txt]))

(s/def ::rendered-text
  string?)

(s/def ::render-settings
  (s/keys :req [::width ::height]
          :opt [::style]))

(s/def ::width int?)
(s/def ::height int?)
(s/def ::style (s/or :nil nil? :style ::txt/style))

(defn ^:private loose-line->line
  "Converts a loose line into a strict line."
  [loose-line]
  (cond
    (string? loose-line) [{::txt/style [] ::txt/body loose-line}]
    (s/valid? ::txt/segment loose-line) [loose-line]
    (s/valid? ::txt/line loose-line) loose-line))

(s/fdef loose-line->line
  :args (s/cat :loose-line ::txt/loose-line)
  :ret ::txt/line)

(defn loose-text->text
  "Converts a loose text into a strict text."
  [loose-text]
  (map loose-line->line loose-text))

(s/fdef loose-text->text
  :args (s/cat :loose-text ::txt/loose-text)
  :ret ::txt/text)

(defn ^:private with-height
  "Crops the text at the given height.
  Completes remaining rows with blank characters."
  [height text]
  (let [blank (repeat height [])]
    (->> (concat text blank)
         (take height))))

(s/fdef with-height
  :args (s/cat :height ::height :text ::txt/text)
  :ret ::txt/text)

(defn ^:private into-line
  "Accumulates segments into a line of a maximum width.
  Crops all chars beyond the maximum width."
  [max-width line {:keys [::txt/style ::txt/body]}]
  (let [curr-width      (->> line (map ::txt/body) (map count) (reduce +))
        remaining-width (- max-width curr-width)
        selected-size   (min remaining-width (count body))
        selected-text   (subs body 0 selected-size)
        new-segment     #::txt{:style (or style []) :body selected-text}]
    (conj line new-segment)))

(s/fdef into-line
  :args (s/cat :width int? :line ::txt/line :segment ::txt/segment)
  :ret ::txt/line)

(defn ^:private with-width*
  "Crops the line at the given width.
  Completes remaining columns with blank characters."
  [width line]
  (let [padding     #::txt{:style [] :body (->> " " (repeat width) (apply str))}
        line+pading (conj line padding)]
    (reduce (partial into-line width) [] line+pading)))

(s/fdef with-width*
  :args (s/cat :width ::width :line ::txt/line)
  :ret ::txt/line)

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
  {::txt/bold          graphics/bold
   ::txt/bold-off      graphics/weight-off
   ::txt/underline     graphics/underline
   ::txt/underline-off graphics/underline-off
   ::txt/blink         graphics/slow-blink
   ::txt/blink-off     graphics/blink-off
   ::txt/fg-black      graphics/fg-black
   ::txt/fg-red        graphics/fg-red
   ::txt/fg-green      graphics/fg-green
   ::txt/fg-yellow     graphics/fg-yellow
   ::txt/fg-blue       graphics/fg-blue
   ::txt/fg-purple     graphics/fg-purple
   ::txt/fg-cyan       graphics/fg-cyan
   ::txt/fg-white      graphics/fg-white
   ::txt/fg-default    graphics/fg-default
   ::txt/bg-black      graphics/bg-black
   ::txt/bg-red        graphics/bg-red
   ::txt/bg-green      graphics/bg-green
   ::txt/bg-yellow     graphics/bg-yellow
   ::txt/bg-blue       graphics/bg-blue
   ::txt/bg-purple     graphics/bg-purple
   ::txt/bg-cyan       graphics/bg-cyan
   ::txt/bg-white      graphics/bg-white
   ::txt/bg-default    graphics/bg-default})

(defn ^:private style->string
  "Renders the ANSI codes for the given style"
  [style]
  (->> style
       (map style->string*)
       (map #(apply % nil))
       string/join))

(s/fdef style->string
  :args (s/cat :style ::txt/style)
  :ret string?)

(defn ^:private segment->string
  "Renders a segment into a printable string."
  [base-style {:keys [::txt/style ::txt/body]}]
  (if (not-empty body)
    (str (graphics/reset) (style->string base-style) (style->string style) body)))

(s/fdef segment->string
  :args (s/cat :base-style ::txt/style :segment ::txt/segment)
  :ret string?)

(defn ^:private line->string
  "Renders a line into a printable string."
  [base-style line]
  (->> line
       (map #(segment->string base-style %))
       (apply str)))

(s/fdef line->string
  :args (s/cat :base-style ::txt/style :line ::txt/line)
  :ret string?)

(defn render
  "Renders text into a printable string."
  [text {::keys [width height style]}]
  (let [base-style (or style [])]
    (->> text
         (with-height height)
         (with-width width)
         (map #(line->string base-style %)))))

(s/fdef render
  :args (s/cat :text ::txt/text :settings ::render-settings)
  :ret ::rendered-text)
