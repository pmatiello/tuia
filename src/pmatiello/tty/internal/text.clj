(ns pmatiello.tty.internal.text
  (:require [clojure.spec.alpha :as s]
            [pmatiello.tty.text :as txt]
            [pmatiello.tty.internal.ansi.graphics :as ansi.graphics]
            [clojure.string :as string]))

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
    (string? loose-paragraph) {::txt/style [] ::txt/body loose-paragraph}
    :else loose-paragraph))

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
  {::txt/style [] ::txt/body ""})

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

(defn ^:private with-width*
  "Crops the paragraph at the given width.
  Completes remaining columns with blank characters."
  [width paragraph]
  (let [body (::txt/body paragraph)
        blank (->> " " (repeat width) (apply str))]
    (assoc paragraph ::txt/body (-> body (str blank) (subs 0 width)))))

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
  {::txt/bold      ansi.graphics/bold
   ::txt/underline ansi.graphics/underline
   ::txt/blink     ansi.graphics/slow-blink})

(defn ^:private style->string
  "Renders the ANSI codes for the given style"
  [style]
  (->> style
       (map style->string*)
       string/join))

(s/fdef style->string
  :args (s/cat :style ::txt/style)
  :ret string?)

(defn ^:private paragraph->string
  "Renders a paragraph into a printable string."
  [{:keys [::txt/style ::txt/body]}]
  (if (empty? style)
    body
    (str (style->string style) body ansi.graphics/reset)))

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
