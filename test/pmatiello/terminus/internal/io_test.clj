(ns pmatiello.terminus.internal.io-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [pmatiello.terminus.internal.ansi.cursor :as cursor]
            [pmatiello.terminus.internal.ansi.erase :as erase]
            [pmatiello.terminus.internal.io :as io]
            [pmatiello.terminus.internal.stty :as stty]
            [pmatiello.terminus.internal.fixtures :as fixtures]
            [clojure.string :as string])
  (:import (java.io StringWriter)))

(use-fixtures :each fixtures/with-readable-csi)

(defn- new-writer []
  (StringWriter.))

(defn- new-output []
  (atom []))

(defn func [])

(deftest write!-test
  (testing "writes entire payload to writer"
    (let [writer (new-writer)]
      (io/write! writer [":str1" ":str2"])
      (is (= ":str1:str2" (str writer))))))

(deftest print!-test
  (testing "prints buffer at location"
    (let [output (new-output)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "prints only the given height"
    (let [output (new-output)]
      (io/print! output ["line1" "line2" "ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "prints only the given width"
    (let [output (new-output)]
      (io/print! output ["line1-ignored" "line2-ignored"] {:x 3 :y 4 :w 5 :h 2})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")]
             @output))))

  (testing "fills missing width in buffer with blank space"
    (let [output (new-output)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 8 :h 2})
      (is (= [(str (cursor/position 4 3) "line1   ")
              (str (cursor/position 5 3) "line2   ")]
             @output))))

  (testing "fills missing height in buffer with blank space"
    (let [output (new-output)]
      (io/print! output ["line1" "line2"] {:x 3 :y 4 :w 5 :h 3})
      (is (= [(str (cursor/position 4 3) "line1")
              (str (cursor/position 5 3) "line2")
              (str (cursor/position 6 3) "     ")]
             @output)))))

(deftest clear-screen!-test
  (testing "clears screen"
    (let [output (new-output)]
      (io/clear-screen! output)
      (is (some #{erase/all} @output))))

  (testing "moves cursor to top left"
    (let [output (new-output)]
      (io/clear-screen! output)
      (is (some #{(cursor/position 1 1)} @output)))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output (new-output)]
      (io/show-cursor! output)
      (is (= [cursor/show] @output)))))

(deftest hide-cursor!-test
  (testing "hide input cursor"
    (let [output (new-output)]
      (io/hide-cursor! output)
      (is (= [cursor/hide] @output)))))

(deftest place-cursor!-test
  (testing "moves cursor to given position"
    (let [output (new-output)]
      (io/place-cursor! output {:x 10 :y 5})
      (is (= [(cursor/position 5 10)] @output)))))

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

