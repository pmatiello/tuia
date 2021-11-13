(ns pmatiello.tty.internal.mainloop-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.matchers])
  (:import (clojure.lang Atom)))

(declare handle-fn)
(declare render-fn)

(def output-buffer
  (reify mfn.matchers/Matcher
    (matches? [this actual]
      (and (instance? Atom actual)
           (vector? @actual)
           (empty? @actual)))
    (description [this] "output buffer")))

(mfn/deftest with-mainloop-test
  (mfn/testing "calls render function on initialisation"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state [] 'output!))
    (mfn/verifying
      (render-fn output-buffer {} {::mainloop/init true}) 'irrelevant (mfn.matchers/exactly 1)))

  (mfn/testing "invokes the handler function for each event in the input reader"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] 'output!))
    (mfn/verifying
      (handle-fn 'ev1) nil (mfn.matchers/exactly 1)
      (handle-fn 'ev2) nil (mfn.matchers/exactly 1))
    (mfn/providing
      (render-fn (mfn.matchers/any) (mfn.matchers/any) (mfn.matchers/any)) 'irrelevant))

  (mfn/testing "invokes the render function on each change to the state atom"
    (let [state (atom {})
          handle-fn (fn [event] (swap! state assoc :last-event event))]
      (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] 'output!))
    (mfn/verifying
      (render-fn output-buffer (mfn.matchers/any) {::mainloop/init true}) 'irrelevant (mfn.matchers/any)
      (render-fn output-buffer (mfn.matchers/any) {::mainloop/init true :last-event 'ev1}) nil (mfn.matchers/exactly 1)
      (render-fn output-buffer (mfn.matchers/any) {::mainloop/init true :last-event 'ev2}) nil (mfn.matchers/exactly 1)))

  (mfn/testing "no longer invokes render function when mainloop is over"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state [] 'output!)
      (swap! state assoc :x :y))
    (mfn/verifying
      (render-fn output-buffer {} {::mainloop/init true}) 'irrelevant (mfn.matchers/any))))
