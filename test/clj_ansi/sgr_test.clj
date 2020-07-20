(ns clj-ansi.sgr-test
  (:require [clojure.test :refer :all]
            [clj-ansi.sgr :as sgr]))

(deftest reset-test
  (is (= "\u001b[0m" sgr/reset)))

(deftest bold-test
  (is (= "\u001b[1m" sgr/bold)))

(deftest faint-test
  (is (= "\u001b[2m" sgr/faint)))

(deftest italic-test
  (is (= "\u001b[3m" sgr/italic)))

(deftest underline-test
  (is (= "\u001b[4m" sgr/underline)))

(deftest slow-blink-test
  (is (= "\u001b[5m" sgr/slow-blink)))

(deftest fast-blink-test
  (is (= "\u001b[6m" sgr/fast-blink)))

(deftest reverse-video-test
  (is (= "\u001b[7m") sgr/reverse-video))

(deftest conceal-test
  (is (= "\u001b[8m") sgr/conceal))

(deftest strike-test
  (is (= "\u001b[9m") sgr/strike))

(deftest weight-off-test
  (is (= "\u001b[22") sgr/weight-off))

(deftest italic-off-test
  (is (= "\u001b[23") sgr/italic-off))

(deftest underline-off-test
  (is (= "\u001b[24") sgr/underline-off))

(deftest blink-off-test
  (is (= "\u001b[25") sgr/blink-off))

(deftest reverse-video-off-test
  (is (= "\u001b[27") sgr/reverse-video-off))

(deftest conceal-off-test
  (is (= "\u001b[28") sgr/conceal-off))

(deftest strike-off-test
  (is (= "\u001b[29") sgr/strike-off))

(deftest fg-black-test
  (is (= "\u001b[30m") sgr/fg-black))

(deftest fg-red-test
  (is (= "\u001b[31m") sgr/fg-red))

(deftest fg-green-test
  (is (= "\u001b[32m") sgr/fg-green))

(deftest fg-yellow-test
  (is (= "\u001b[33m") sgr/fg-yellow))

(deftest fg-blue-test
  (is (= "\u001b[34m") sgr/fg-blue))

(deftest fg-purple-test
  (is (= "\u001b[35m") sgr/fg-purple))

(deftest fg-cyan-test
  (is (= "\u001b[36m") sgr/fg-cyan))

(deftest fg-white-test
  (is (= "\u001b[37m") sgr/fg-white))

(deftest fg-default-test
  (is (= "\u001b[39m") sgr/fg-default))

(deftest bg-black-test
  (is (= "\u001b[40m") sgr/bg-black))

(deftest bg-red-test
  (is (= "\u001b[41m") sgr/bg-red))

(deftest bg-green-test
  (is (= "\u001b[42m") sgr/bg-green))

(deftest bg-yellow-test
  (is (= "\u001b[43m") sgr/bg-yellow))

(deftest bg-blue-test
  (is (= "\u001b[44m") sgr/bg-blue))

(deftest bg-purple-test
  (is (= "\u001b[45m") sgr/bg-purple))

(deftest bg-cyan-test
  (is (= "\u001b[46m") sgr/bg-cyan))

(deftest bg-white-test
  (is (= "\u001b[47m") sgr/bg-white))

(deftest bg-default-test
  (is (= "\u001b[49m") sgr/bg-default))
