(ns pmatiello.tuia.internal.text-test
  (:require [clojure.test :refer :all]
            [pmatiello.tuia.internal.ansi.graphics :as ansi.graphics]
            [pmatiello.tuia.internal.fixtures :as fixtures]
            [pmatiello.tuia.internal.text :as internal.txt]
            [pmatiello.tuia.text :as txt]))

(use-fixtures :each fixtures/with-readable-csi fixtures/with-spec-instrumentation)

(deftest loose-text->text-test
  (testing "already strict texts are unchanged"
    (is (= [[#::txt{:style [::txt/bold] :body "bold"}]
            [#::txt{:style [::txt/fg-blue] :body "blue"}]]
           (internal.txt/loose-text->text
             [[#::txt{:style [::txt/bold] :body "bold"}]
              [#::txt{:style [::txt/fg-blue] :body "blue"}]]))))

  (testing "segments are converted into strict text"
    (is (= [[#::txt{:style [::txt/bold] :body "bold"}]
            [#::txt{:style [::txt/fg-blue] :body "blue"}]]
           (internal.txt/loose-text->text
             [#::txt{:style [::txt/bold] :body "bold"}
              #::txt{:style [::txt/fg-blue] :body "blue"}]))))

  (testing "strings are converted into strict plain text"
    (is (= [[#::txt{:style [] :body "plain"}]
            [#::txt{:style [] :body "text!"}]]
           (internal.txt/loose-text->text
             ["plain" "text!"]))))

  (testing "mixed texts are made strict"
    (is (= [[#::txt{:style [] :body "plain"}]
            [#::txt{:style [] :body "text!"}]]
           (internal.txt/loose-text->text
             [#::txt{:style [] :body "plain"} "text!"])))))

(deftest render-test
  (testing "renders plain text"
    (is (= ["plain" "text!"]
           (internal.txt/render [[#::txt{:style [] :body "plain"}]
                                 [#::txt{:style [] :body "text!"}]]
                                #::internal.txt{:width 5 :height 2}))))

  (testing "restricts text to the given width"
    (is (= ["plain" "text!"]
           (internal.txt/render [[#::txt{:style [] :body "plainCROPPED"}]
                                 [#::txt{:style [] :body "text!CROPPED"}]]
                                #::internal.txt{:width 5 :height 2}))))

  (testing "fills missing width in text with blank space"
    (is (= ["plain   " "text!   "]
           (internal.txt/render [[#::txt{:style [] :body "plain"}]
                                 [#::txt{:style [] :body "text!"}]]
                                #::internal.txt{:width 8 :height 2}))))

  (testing "restricts text to the given height"
    (is (= ["plain" "text!"]
           (internal.txt/render [[#::txt{:style [] :body "plain"}]
                                 [#::txt{:style [] :body "text!"}]
                                 [#::txt{:style [] :body "CROP!"}]]
                                #::internal.txt{:width 5 :height 2}))))

  (testing "fills missing height in text with blank space"
    (is (= ["plain" "text!" "     "]
           (internal.txt/render [[#::txt{:style [] :body "plain"}]
                                 [#::txt{:style [] :body "text!"}]]
                                #::internal.txt{:width 5 :height 3}))))

  (testing "renders with emphasis"
    (is (= [(str (ansi.graphics/bold) (ansi.graphics/slow-blink) "bold blink" (ansi.graphics/reset))
            (str (ansi.graphics/underline) "underline" (ansi.graphics/reset) " ")]
           (internal.txt/render [[#::txt{:style [::txt/bold ::txt/blink] :body "bold blink"}]
                                 [#::txt{:style [::txt/underline] :body "underline"}]]
                                #::internal.txt{:width 10 :height 2}))))

  (testing "renders with foreground colors"
    (is (= [(str (ansi.graphics/fg-blue) "blue" (ansi.graphics/reset) " ")
            (str (ansi.graphics/fg-green) "green" (ansi.graphics/reset))]
           (internal.txt/render [[#::txt{:style [::txt/fg-blue] :body "blue"}]
                                 [#::txt{:style [::txt/fg-green] :body "green"}]]
                                #::internal.txt{:width 5 :height 2}))))

  (testing "renders with background colors"
    (is (= [(str (ansi.graphics/bg-white) "white" (ansi.graphics/reset) " ")
            (str (ansi.graphics/bg-yellow) "yellow" (ansi.graphics/reset))]
           (internal.txt/render [[#::txt{:style [::txt/bg-white] :body "white"}]
                                 [#::txt{:style [::txt/bg-yellow] :body "yellow"}]]
                                #::internal.txt{:width 6 :height 2}))))

  (testing "renders multiple styles in the same line"
    (is (= [(str (ansi.graphics/fg-blue) "blue" (ansi.graphics/reset) " "
                 (ansi.graphics/fg-green) "green" (ansi.graphics/reset) "  ")
            (str (ansi.graphics/bg-white) "white" (ansi.graphics/reset) " "
                 (ansi.graphics/bg-yellow) "yellow" (ansi.graphics/reset))]
           (internal.txt/render [[#::txt{:style [::txt/fg-blue] :body "blue"}
                                  #::txt{:style [] :body " "}
                                  #::txt{:style [::txt/fg-green] :body "green"}]
                                 [#::txt{:style [::txt/bg-white] :body "white"}
                                  #::txt{:style [] :body " "}
                                  #::txt{:style [::txt/bg-yellow] :body "yellow"}]]
                                #::internal.txt{:width 12 :height 2})))))
