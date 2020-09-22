(ns pmatiello.terminus.ansi.screen-test
  (:require [clojure.test :refer :all])
  (:require [pmatiello.terminus.ansi.screen :as screen]))

(deftest normal-bufer-test
  (is (= "\u001b[?1049l" screen/normal-buffer)))

(deftest alternate-bufer-test
  (is (= "\u001b[?1049h" screen/alternate-buffer)))
