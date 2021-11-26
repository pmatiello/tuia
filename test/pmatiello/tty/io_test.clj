(ns pmatiello.tty.io-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.io :as tty.io]
            [pmatiello.tty.internal.fixtures :as fixtures]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.ansi.erase :as erase]))

(use-fixtures :each fixtures/with-readable-csi)
(use-fixtures :each fixtures/with-spec-instrumentation)

(defn- new-output-buf []
  (atom []))

(deftest print!-test
  (testing "prints buffer at location"
    (let [output-buf (new-output-buf)]
      (tty.io/print! output-buf
                     ["line1" "line2"]
                     #::tty.io{:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output-buf))))

  (testing "prints only the given height"
    (let [output-buf (new-output-buf)]
      (tty.io/print! output-buf
                     ["line1" "line2" "ignored"]
                     #::tty.io{:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output-buf))))

  (testing "prints only the given width"
    (let [output-buf (new-output-buf)]
      (tty.io/print! output-buf
                     ["line1-ignored" "line2-ignored"]
                     #::tty.io{:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output-buf))))

  (testing "fills missing width in buffer with blank space"
    (let [output-buf (new-output-buf)]
      (tty.io/print! output-buf
                     ["line1" "line2"]
                     #::tty.io{:row 4 :column 3 :width 8 :height 2})
      (is (= [(str (cursor/position 4 3) "line1   ")
              (str (cursor/position 5 3) "line2   ")]
             @output-buf))))

  (testing "fills missing height in buffer with blank space"
    (let [output-buf (new-output-buf)]
      (tty.io/print! output-buf
                     ["line1" "line2"]
                     #::tty.io{:row 4 :column 3 :width 5 :height 3})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")
              (str (cursor/position 6 3) "     ")]
             @output-buf)))))

(deftest clear-screen!-test
  (testing "clears screen"
    (let [output-buf (new-output-buf)]
      (tty.io/clear-screen! output-buf)
      (is (some #{erase/all} @output-buf))))

  (testing "moves cursor to top left"
    (let [output-buf (new-output-buf)]
      (tty.io/clear-screen! output-buf)
      (is (some #{(cursor/position 1 1)} @output-buf)))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output-buf (new-output-buf)]
      (tty.io/show-cursor! output-buf)
      (is (= [cursor/show] @output-buf)))))

(deftest hide-cursor!-test
  (testing "hide input cursor"
    (let [output-buf (new-output-buf)]
      (tty.io/hide-cursor! output-buf)
      (is (= [cursor/hide] @output-buf)))))

(deftest place-cursor!-test
  (testing "moves cursor to given position"
    (let [output-buf (new-output-buf)]
      (tty.io/place-cursor! output-buf #::tty.io{:row 5 :column 10})
      (is (= [(cursor/position 5 10)] @output-buf)))))
