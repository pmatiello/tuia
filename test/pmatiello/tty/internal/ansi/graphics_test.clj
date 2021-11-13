(ns pmatiello.tty.internal.ansi.graphics-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.ansi.graphics :as graphics]))

(deftest reset-test
  (is (= "\u001b[0m" graphics/reset)))

(deftest bold-test
  (is (= "\u001b[1m" graphics/bold)))

(deftest faint-test
  (is (= "\u001b[2m" graphics/faint)))

(deftest italic-test
  (is (= "\u001b[3m" graphics/italic)))

(deftest underline-test
  (is (= "\u001b[4m" graphics/underline)))

(deftest slow-blink-test
  (is (= "\u001b[5m" graphics/slow-blink)))

(deftest fast-blink-test
  (is (= "\u001b[6m" graphics/fast-blink)))

(deftest reverse-video-test
  (is (= "\u001b[7m") graphics/reverse-video))

(deftest conceal-test
  (is (= "\u001b[8m") graphics/conceal))

(deftest strike-test
  (is (= "\u001b[9m") graphics/strike))

(deftest weight-off-test
  (is (= "\u001b[22") graphics/weight-off))

(deftest italic-off-test
  (is (= "\u001b[23") graphics/italic-off))

(deftest underline-off-test
  (is (= "\u001b[24") graphics/underline-off))

(deftest blink-off-test
  (is (= "\u001b[25") graphics/blink-off))

(deftest reverse-video-off-test
  (is (= "\u001b[27") graphics/reverse-video-off))

(deftest conceal-off-test
  (is (= "\u001b[28") graphics/conceal-off))

(deftest strike-off-test
  (is (= "\u001b[29") graphics/strike-off))

(deftest fg-black-test
  (is (= "\u001b[30m") graphics/fg-black))

(deftest fg-red-test
  (is (= "\u001b[31m") graphics/fg-red))

(deftest fg-green-test
  (is (= "\u001b[32m") graphics/fg-green))

(deftest fg-yellow-test
  (is (= "\u001b[33m") graphics/fg-yellow))

(deftest fg-blue-test
  (is (= "\u001b[34m") graphics/fg-blue))

(deftest fg-purple-test
  (is (= "\u001b[35m") graphics/fg-purple))

(deftest fg-cyan-test
  (is (= "\u001b[36m") graphics/fg-cyan))

(deftest fg-white-test
  (is (= "\u001b[37m") graphics/fg-white))

(deftest fg-default-test
  (is (= "\u001b[39m") graphics/fg-default))

(deftest bg-black-test
  (is (= "\u001b[40m") graphics/bg-black))

(deftest bg-red-test
  (is (= "\u001b[41m") graphics/bg-red))

(deftest bg-green-test
  (is (= "\u001b[42m") graphics/bg-green))

(deftest bg-yellow-test
  (is (= "\u001b[43m") graphics/bg-yellow))

(deftest bg-blue-test
  (is (= "\u001b[44m") graphics/bg-blue))

(deftest bg-purple-test
  (is (= "\u001b[45m") graphics/bg-purple))

(deftest bg-cyan-test
  (is (= "\u001b[46m") graphics/bg-cyan))

(deftest bg-white-test
  (is (= "\u001b[47m") graphics/bg-white))

(deftest bg-default-test
  (is (= "\u001b[49m") graphics/bg-default))
