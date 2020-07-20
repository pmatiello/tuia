(ns clj-ansi.sgr-test
  (:require [clojure.test :refer :all])
  (:require [clj-ansi.sgr :as sgr]
            [clojure.string :as str]))

(deftest sgr-test
  (doseq [each (-> 'clj-ansi.sgr ns-publics keys)]
    (testing (str each " starts with CSI")
      (let [value (->> each str (symbol "clj-ansi.sgr") resolve var-get)]
        (is (true? (str/starts-with? value "\u001B[")))))))

(deftest ^:integration graphical-test
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
    (println (str sgr/bold sgr/fg-red "red! " sgr/fg-blue "blue! " sgr/fg-default "default! " sgr/weight-off "default"))))
