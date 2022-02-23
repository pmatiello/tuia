(ns pmatiello.tuia.core-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.m]
            [pmatiello.tuia.core :as core]
            [pmatiello.tuia.internal.ansi.input :as input]
            [pmatiello.tuia.internal.io :as io]
            [pmatiello.tuia.internal.mainloop :as mainloop]
            [pmatiello.tuia.internal.stty :as stty])
  (:import (java.io StringWriter)))

(defn output-fn? [writer out-fn!]
  (out-fn! ["/ok"])
  (-> writer str (str/ends-with? "/ok")))

(mfn/deftest init!-test
  (mfn/testing "initialises the terminal in raw mode"
    (core/init! 'handle-fn 'render-fn 'state)
    (mfn/verifying
      (io/with-raw-tty fn?) 'irrelevant (mfn.m/exactly 1)))

  (let [writer     (StringWriter.)
        output-fn? (partial output-fn? writer)]
    (mfn/testing "starts the application main loop"
      (with-redefs [*out* writer]
        (core/init! 'handle-fn 'render-fn 'state))
      (mfn/verifying
        (mainloop/with-mainloop 'handle-fn 'render-fn 'state 'input output-fn?)
        'irrelevant (mfn.m/exactly 1))))

  (mfn/providing
    (stty/unset-flags! mfn.m/any-args?) 'irrelevant
    (stty/apply! (mfn.m/any)) 'irrelevant
    (input/reader->event-seq *in*) 'input))
