(ns pmatiello.terminus.internal.ansi.screen-test
  (:require [clojure.test :refer :all])
  (:require [pmatiello.terminus.internal.ansi.screen :as screen]))

(deftest normal-bufer-test
  (is (= "\u001b[?1049l" screen/normal-buffer)))

(deftest alternate-bufer-test
  (is (= "\u001b[?1049h" screen/alternate-buffer)))
