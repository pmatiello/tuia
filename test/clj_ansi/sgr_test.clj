(ns clj-ansi.sgr-test
  (:require [clojure.test :refer :all])
  (:require [clj-ansi.sgr :as sgr]
            [clojure.string :as str]))

(deftest sgr-test
  (doseq [each (-> 'clj-ansi.sgr ns-publics keys)]
    (testing (str each " starts with CSI")
      (let [value (->> each str (symbol "clj-ansi.sgr") resolve var-get)]
        (is (true? (str/starts-with? value "\u001B[")))))))
