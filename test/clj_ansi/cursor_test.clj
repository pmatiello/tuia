(ns clj-ansi.cursor-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [clj-ansi.cursor]))

(deftest cursor-test
  (doseq [each (-> 'clj-ansi.cursor ns-publics keys)]
    (testing (str each " starts with CSI")
      (let [fn (->> each str (symbol "clj-ansi.cursor") resolve var-get)]
        (is (true? (str/starts-with? (fn 1) "\u001b[")))))))
