(ns pmatiello.tuia.internal.ansi.screen-test
  (:require [clojure.test :refer :all])
  (:require [pmatiello.tuia.internal.ansi.screen :as screen]
            [pmatiello.tuia.internal.fixtures :as fixtures]))

(use-fixtures :each fixtures/with-readable-csi)

(deftest normal-bufer-test
  (is (= "\\u001b[?1049l" (screen/normal-buffer))))

(deftest alternate-bufer-test
  (is (= "\\u001b[?1049h" (screen/alternate-buffer))))
