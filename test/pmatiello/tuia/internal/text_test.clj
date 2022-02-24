(ns pmatiello.tuia.internal.text-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.graphics :as graphics]
            [pmatiello.tuia.internal.fixtures :as fixtures]
            [pmatiello.tuia.internal.text :as internal.txt]))

(use-fixtures :each fixtures/with-readable-csi fixtures/with-spec-instrumentation)

(deftest loose-text->text-test
  (testing "already strict texts are unchanged"
    (is (= [[{:style [:bold] :body "bold"}]
            [{:style [:fg-blue] :body "blue"}]]
           (internal.txt/loose-text->text
             [[{:style [:bold] :body "bold"}]
              [{:style [:fg-blue] :body "blue"}]]))))

  (testing "segments are converted into strict text"
    (is (= [[{:style [:bold] :body "bold"}]
            [{:style [:fg-blue] :body "blue"}]]
           (internal.txt/loose-text->text
             [{:style [:bold] :body "bold"}
              {:style [:fg-blue] :body "blue"}]))))

  (testing "strings are converted into strict plain text"
    (is (= [[{:style [] :body "plain"}]
            [{:style [] :body "text!"}]]
           (internal.txt/loose-text->text
             ["plain" "text!"]))))

  (testing "mixed texts are made strict"
    (is (= [[{:style [] :body "plain"}]
            [{:style [] :body "text!"}]]
           (internal.txt/loose-text->text
             [{:style [] :body "plain"} "text!"])))))

(deftest render-test
  (testing "renders plain text"
    (is (= [(str (graphics/reset) "plain")
            (str (graphics/reset) "text!")]
           (internal.txt/render [[{:style [] :body "plain"}]
                                 [{:style [] :body "text!"}]]
                                {:width 5 :height 2}))))

  (testing "restricts text to the given width"
    (is (= [(str (graphics/reset) "plain")
            (str (graphics/reset) "text!")]
           (internal.txt/render [[{:style [] :body "plainCROPPED"}]
                                 [{:style [] :body "text!CROPPED"}]]
                                {:width 5 :height 2}))))

  (testing "fills missing width in text with blank space"
    (is (= [(str (graphics/reset) "plain" (graphics/reset) "   ")
            (str (graphics/reset) "text!" (graphics/reset) "   ")]
           (internal.txt/render [[{:style [] :body "plain"}]
                                 [{:style [] :body "text!"}]]
                                {:width 8 :height 2}))))

  (testing "restricts text to the given height"
    (is (= [(str (graphics/reset) "plain")
            (str (graphics/reset) "text!")]
           (internal.txt/render [[{:style [] :body "plain"}]
                                 [{:style [] :body "text!"}]
                                 [{:style [] :body "CROP!"}]]
                                {:width 5 :height 2}))))

  (testing "fills missing height in text with blank space"
    (is (= [(str (graphics/reset) "plain")
            (str (graphics/reset) "text!")
            (str (graphics/reset) "     ")]
           (internal.txt/render [[{:style [] :body "plain"}]
                                 [{:style [] :body "text!"}]]
                                {:width 5 :height 3}))))

  (testing "renders with emphasis"
    (is (= [(str (graphics/reset) (graphics/bold) (graphics/slow-blink) "bold blink")
            (str (graphics/reset) (graphics/underline) "underline" (graphics/reset) " ")]
           (internal.txt/render [[{:style [:bold :blink] :body "bold blink"}]
                                 [{:style [:underline] :body "underline"}]]
                                {:width 10 :height 2}))))

  (testing "renders with foreground colors"
    (is (= [(str (graphics/reset) (graphics/fg-blue) "blue" (graphics/reset) " ")
            (str (graphics/reset) (graphics/fg-green) "green")]
           (internal.txt/render [[{:style [:fg-blue] :body "blue"}]
                                 [{:style [:fg-green] :body "green"}]]
                                {:width 5 :height 2}))))

  (testing "renders with background colors"
    (is (= [(str (graphics/reset) (graphics/bg-white) "white" (graphics/reset) " ")
            (str (graphics/reset) (graphics/bg-yellow) "yellow")]
           (internal.txt/render [[{:style [:bg-white] :body "white"}]
                                 [{:style [:bg-yellow] :body "yellow"}]]
                                {:width 6 :height 2}))))

  (testing "renders multiple styles in the same line"
    (is (= [(str (graphics/reset) (graphics/fg-blue) "blue" (graphics/reset) " "
                 (graphics/reset) (graphics/fg-green) "green" (graphics/reset) "  ")
            (str (graphics/reset) (graphics/bg-white) "white" (graphics/reset) " "
                 (graphics/reset) (graphics/bg-yellow) "yellow")]
           (internal.txt/render [[{:style [:fg-blue] :body "blue"}
                                  {:style [] :body " "}
                                  {:style [:fg-green] :body "green"}]
                                 [{:style [:bg-white] :body "white"}
                                  {:style [] :body " "}
                                  {:style [:bg-yellow] :body "yellow"}]]
                                {:width 12 :height 2}))))

  (testing "renders with a custom base style"
    (is (= [(str (graphics/reset) (graphics/bg-white) (graphics/fg-blue) "blue"
                 (graphics/reset) (graphics/bg-white) "!")
            (str (graphics/reset) (graphics/bg-white) "     ")]
           (internal.txt/render [[{:style [:fg-blue] :body "blue"}
                                  {:style [] :body "!"}]]
                                {:width 5 :height 2 :style [:bg-white]})))))
