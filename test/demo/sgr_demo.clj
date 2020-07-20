(ns demo.sgr-demo
  (:require [clojure.test :refer :all]
            [clj-ansi.sgr :as sgr]))

(deftest sgr
  (testing "bold"
    (println (str sgr/bold "bold" sgr/weight-off)))

  (testing "underline"
    (println (str sgr/underline "underline" sgr/underline-off)))

  (testing "blink"
    (println (str sgr/slow-blink "blink" sgr/blink-off)))

  (testing "reverse"
    (println (str sgr/reverse-video "reverse" sgr/reverse-video-off)))

  (testing "conceal"
    (println (str sgr/conceal "conceal " sgr/conceal-off "reveal")))

  (testing "foreground colors"
    (println (str sgr/fg-red "red " sgr/fg-blue "blue " sgr/fg-default "default")))

  (testing "intense foreground colors"
    (println (str sgr/bold sgr/fg-red "red! " sgr/fg-blue "blue! " sgr/fg-default "default! " sgr/weight-off "default")))

  (testing "background colors"
    (println (str sgr/bg-cyan "cyan" sgr/bg-default " "
                  sgr/bg-yellow "yellow" sgr/bg-default " default"))))
