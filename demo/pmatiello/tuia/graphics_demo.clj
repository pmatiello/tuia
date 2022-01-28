(ns pmatiello.tuia.graphics-demo
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.graphics :as graphics]))

(defn ^:deprecated -main []
  (println (str (graphics/bold) "bold" (graphics/weight-off)))

  (println (str (graphics/underline) "underline" (graphics/underline-off)))

  (println (str (graphics/slow-blink) "blink" (graphics/blink-off)))

  (println (str (graphics/reverse-video) "reverse" (graphics/reverse-video-off)))

  (println (str (graphics/conceal) "conceal " (graphics/conceal-off) "reveal"))

  (println (str (graphics/fg-red) "red " (graphics/fg-blue) "blue " (graphics/fg-default) "default"))

  (println (str (graphics/bold) (graphics/fg-red) "red! " (graphics/fg-blue) "blue! " (graphics/fg-default) "default! " (graphics/weight-off) "default"))

  (println (str (graphics/bg-cyan) "cyan" (graphics/bg-default) " "
                (graphics/bg-yellow) "yellow" (graphics/bg-default) " default")))
