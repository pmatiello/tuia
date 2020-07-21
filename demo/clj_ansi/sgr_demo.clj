(ns clj-ansi.sgr-demo
  (:require [clojure.test :refer :all]
            [clj-ansi.sgr :as sgr]))

(defn -main []
  (println (str sgr/bold "bold" sgr/weight-off))

  (println (str sgr/underline "underline" sgr/underline-off))

  (println (str sgr/slow-blink "blink" sgr/blink-off))

  (println (str sgr/reverse-video "reverse" sgr/reverse-video-off))

  (println (str sgr/conceal "conceal " sgr/conceal-off "reveal"))

  (println (str sgr/fg-red "red " sgr/fg-blue "blue " sgr/fg-default "default"))

  (println (str sgr/bold sgr/fg-red "red! " sgr/fg-blue "blue! " sgr/fg-default "default! " sgr/weight-off "default"))

  (println (str sgr/bg-cyan "cyan" sgr/bg-default " "
                sgr/bg-yellow "yellow" sgr/bg-default " default")))
