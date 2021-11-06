(ns pmatiello.terminus.internal.framework.io-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.ansi.erase :as erase]
            [pmatiello.terminus.internal.framework.io :as io]
            [pmatiello.terminus.internal.tty.stty :as stty]
            [pmatiello.terminus.internal.fixtures :as fixtures]
            [clojure.string :as string])
  (:import (java.io StringWriter)))

(use-fixtures :each fixtures/with-readable-csi)

(defn- new-writer []
  (StringWriter.))

(defn func [])

(deftest print!-test
  (testing "prints buffer at location"
    (let [output (new-writer)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 2})
      (is (= (str (cursor/position 4 3) "line1"
                  (cursor/position 5 3) "line2")
             (str output)))))

  (testing "prints only the given height"
    (let [output (new-writer)]
      (io/print! output ["line1" "line2" "ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= (str (cursor/position 4 3) "line1"
                  (cursor/position 5 3) "line2")
             (str output)))))

  (testing "prints only the given width"
    (let [output (new-writer)]
      (io/print! output ["line1-ignored" "line2-ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= (str (cursor/position 4 3) "line1"
                  (cursor/position 5 3) "line2")
             (str output)))))

  (testing "fills missing width in buffer with blank space"
    (let [output (new-writer)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 8 :h 2})
      (is (= (str (cursor/position 4 3) "line1   "
                  (cursor/position 5 3) "line2   ")
             (str output)))))

  (testing "fills missing height in buffer with blank space"
    (let [output (new-writer)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 3})
      (is (= (str (cursor/position 4 3) "line1"
                  (cursor/position 5 3) "line2"
                  (cursor/position 6 3) "     ")
             (str output))))))

(deftest clear-screen!-test
  (testing "clears screen"
    (let [output (new-writer)]
      (io/clear-screen! output)
      (is (string/includes? (str output) erase/all))))

  (testing "moves cursor to top left"
    (let [output (new-writer)]
      (io/clear-screen! output)
      (is (string/includes? (str output) (cursor/position 1 1))))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output (new-writer)]
      (io/show-cursor! output)
      (is (= cursor/show (str output))))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output (new-writer)]
      (io/hide-cursor! output)
      (is (= cursor/hide (str output))))))

(deftest place-cursor!-test
  (testing "moves cursor to given position"
    (let [output (new-writer)]
      (io/place-cursor! output 5 10)
      (is (= (str output) (cursor/position 5 10))))))

(mfn/deftest with-raw-tty-test
  (mfn/testing "runs given body"
    (io/with-raw-tty func)
    (mfn/verifying
      (func) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "starts raw mode"
    (io/with-raw-tty func)
    (mfn/verifying
      (stty/unset-flags! :icanon :echo) nil (mockfn.matchers/exactly 1)))

  (mfn/testing "returns to original terminal settings"
    (io/with-raw-tty func)
    (mfn/providing
      (stty/current) 'stty-current)
    (mfn/verifying
      (stty/apply! 'stty-current) nil (mockfn.matchers/exactly 1)))

  (mfn/providing
    (stty/apply! (mockfn.matchers/any)) nil))
