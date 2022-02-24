(ns pmatiello.tuia.io-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.cursor :as cursor]
            [pmatiello.tuia.internal.ansi.erase :as erase]
            [pmatiello.tuia.internal.ansi.graphics :as graphics]
            [pmatiello.tuia.internal.fixtures :as fixtures]
            [pmatiello.tuia.io :as io]
            [pmatiello.tuia.text :as txt]))

(use-fixtures :each fixtures/with-readable-csi fixtures/with-spec-instrumentation)

(defn- new-output-buf []
  (atom []))

(deftest print!-test
  (testing "prints loose text at location"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 ["line1" "line2"]
                 {:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1")
              (str (cursor/position 5 3) (graphics/reset) "line2")]
             @output-buf))))

  (testing "prints strict text at location"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 [[#::txt{:style [] :body "line1"}]
                  [#::txt{:style [] :body "line2"}]]
                 {:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1")
              (str (cursor/position 5 3) (graphics/reset) "line2")]
             @output-buf))))

  (testing "prints only the given height"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 ["line1" "line2" "ignored"]
                 {:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1")
              (str (cursor/position 5 3) (graphics/reset) "line2")]
             @output-buf))))

  (testing "fills missing height in text with blank space"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 ["line1" "line2"]
                 {:row 4 :column 3 :width 5 :height 3})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1")
              (str (cursor/position 5 3) (graphics/reset) "line2")
              (str (cursor/position 6 3) (graphics/reset) "     ")]
             @output-buf))))

  (testing "prints only the given width"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 ["line1-ignored" "line2-ignored"]
                 {:row 4 :column 3 :width 5 :height 2})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1")
              (str (cursor/position 5 3) (graphics/reset) "line2")]
             @output-buf))))

  (testing "fills missing width in text with blank space"
    (let [output-buf (new-output-buf)]
      (io/print! output-buf
                 ["line1" "line2"]
                 {:row 4 :column 3 :width 8 :height 2})
      (is (= [(str (cursor/position 4 3) (graphics/reset) "line1" (graphics/reset) "   ")
              (str (cursor/position 5 3) (graphics/reset) "line2" (graphics/reset) "   ")]
             @output-buf)))))

(deftest clear-screen!-test
  (testing "clears screen"
    (let [output-buf (new-output-buf)]
      (io/clear-screen! output-buf)
      (is (some #{(erase/all)} @output-buf))))

  (testing "moves cursor to top left"
    (let [output-buf (new-output-buf)]
      (io/clear-screen! output-buf)
      (is (some #{(cursor/position 1 1)} @output-buf)))))

(deftest show-cursor!-test
  (testing "shows input cursor"
    (let [output-buf (new-output-buf)]
      (io/show-cursor! output-buf)
      (is (= [(cursor/show)] @output-buf)))))

(deftest hide-cursor!-test
  (testing "hide input cursor"
    (let [output-buf (new-output-buf)]
      (io/hide-cursor! output-buf)
      (is (= [(cursor/hide)] @output-buf)))))

(deftest place-cursor!-test
  (testing "moves cursor to given position"
    (let [output-buf (new-output-buf)]
      (io/place-cursor! output-buf {:row 5 :column 10})
      (is (= [(cursor/position 5 10)] @output-buf)))))
