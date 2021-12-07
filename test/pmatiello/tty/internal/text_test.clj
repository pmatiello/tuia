(ns pmatiello.tty.internal.text-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.text :as txt]
            [pmatiello.tty.internal.text :as internal.txt]
            [pmatiello.tty.internal.fixtures :as fixtures]))

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
                                    #::internal.txt{:width 5 :height 2})))))
