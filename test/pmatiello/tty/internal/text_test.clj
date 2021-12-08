(ns pmatiello.tty.internal.text-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.text :as txt]
            [pmatiello.tty.internal.text :as internal.txt]
            [pmatiello.tty.internal.ansi.graphics :as ansi.graphics]
            [pmatiello.tty.internal.fixtures :as fixtures]))

(use-fixtures :each fixtures/with-readable-csi)
(use-fixtures :each fixtures/with-spec-instrumentation)

(deftest loose-text->text-test
  (testing "already strict texts are unchanged"
    (is (= [#::txt{:style [::txt/bold] :body "bold"}
            #::txt{:style [::txt/fg-blue] :body "blue"}]
           (internal.txt/loose-text->text
             [#::txt{:style [::txt/bold] :body "bold"}
              #::txt{:style [::txt/fg-blue] :body "blue"}]))))

  (testing "strings are converted into strict plain text"
    (is (= [#::txt{:style [] :body "plain"}
            #::txt{:style [] :body "text!"}]
           (internal.txt/loose-text->text
             ["plain" "text!"]))))

  (testing "mixed texts are made strict"
    (is (= [#::txt{:style [] :body "plain"}
            #::txt{:style [] :body "text!"}]
           (internal.txt/loose-text->text
             [#::txt{:style [] :body "plain"} "text!"])))))

(deftest text->page-test
  (testing "renders plain text"
    (is (= ["plain" "text!"]
           (internal.txt/text->page [#::txt{:style [] :body "plain"}
                                     #::txt{:style [] :body "text!"}]
                                    #::internal.txt{:width 5 :height 2}))))

  (testing "restricts text to the given width"
    (is (= ["plain" "text!"]
           (internal.txt/text->page [#::txt{:style [] :body "plainCROPPED"}
                                     #::txt{:style [] :body "text!CROPPED"}]
                                    #::internal.txt{:width 5 :height 2}))))

  (testing "restricts text to the given height"
    (is (= ["plain" "text!"]
           (internal.txt/text->page [#::txt{:style [] :body "plain"}
                                     #::txt{:style [] :body "text!"}
                                     #::txt{:style [] :body "CROP!"}]
                                    #::internal.txt{:width 5 :height 2}))))

  (testing "renders with emphasis"
    (is (= [(str ansi.graphics/bold ansi.graphics/slow-blink "bold blink" ansi.graphics/reset)
            (str ansi.graphics/underline "underline " ansi.graphics/reset)]
           (internal.txt/text->page [#::txt{:style [::txt/bold ::txt/blink] :body "bold blink"}
                                     #::txt{:style [::txt/underline] :body "underline"}]
                                    #::internal.txt{:width 10 :height 2}))))

  (testing "renders with foreground colour"
    (is (= [(str ansi.graphics/fg-blue "blue " ansi.graphics/reset)
            (str ansi.graphics/fg-green "green" ansi.graphics/reset)]
           (internal.txt/text->page [#::txt{:style [::txt/fg-blue] :body "blue"}
                                     #::txt{:style [::txt/fg-green] :body "green"}]
                                    #::internal.txt{:width 5 :height 2})))))