(ns pmatiello.tty.io-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.io :as tty.io]
            [pmatiello.tty.internal.fixtures :as fixtures]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]))

(use-fixtures :each fixtures/with-readable-csi)

(defn- new-output []
  (atom []))

(deftest print!-test
  (testing "prints buffer at location"
    (let [output (new-output)]
      (tty.io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "prints only the given height"
    (let [output (new-output)]
      (tty.io/print! output ["line1" "line2" "ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "prints only the given width"
    (let [output (new-output)]
      (tty.io/print! output ["line1-ignored" "line2-ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "fills missing width in buffer with blank space"
    (let [output (new-output)]
      (tty.io/print! output ["line1" "line2"] {:x 3 :y 4 :w 8 :h 2})
      (is (= [(str (cursor/position 4 3) "line1   ")
              (str (cursor/position 5 3) "line2   ")]
             @output))))

  (testing "fills missing height in buffer with blank space"
    (let [output (new-output)]
      (tty.io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 3})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")
              (str (cursor/position 6 3) "     ")]
             @output)))))

(deftest clear-screen!-test
  (testing "clears screen"
    (let [output (new-output)]
      (tty.io/clear-screen! output)
      (is (some #{erase/all} @output))))

  (testing "moves cursor to top left"
    (let [output (new-output)]
      (tty.io/clear-screen! output)
      (is (some #{(cursor/position 1 1)} @output)))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output (new-output)]
      (tty.io/show-cursor! output)
      (is (= [cursor/show] @output)))))

(deftest hide-cursor!-test
  (testing "hide input cursor"
    (let [output (new-output)]
      (tty.io/hide-cursor! output)
      (is (= [cursor/hide] @output)))))

(deftest place-cursor!-test
  (testing "moves cursor to given position"
    (let [output (new-output)]
      (tty.io/place-cursor! output {:x 10 :y 5})
      (is (= [(cursor/position 5 10)] @output)))))
