(ns pmatiello.terminus.internal.framework.mainloop-test
  (:require [clojure.test :refer :all]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.matchers])
  (:import (java.io StringReader)))

(declare handle-fn)
(declare render-fn)

(mfn/deftest with-mainloop-test
  (mfn/testing "calls render function on initialisation"
    (let [input (StringReader. "")
          state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state input))
    (mfn/verifying
      (render-fn {} {::mainloop/init true}) 'irrelevant (mfn.matchers/exactly 1)))

  (mfn/testing "invokes the handler function for each event in the input reader"
    (let [input (StringReader. "x!")
          state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state input))
    (mfn/verifying
      (handle-fn {:event :keypress, :value "x"}) nil (mfn.matchers/exactly 1)
      (handle-fn {:event :keypress, :value "!"}) nil (mfn.matchers/exactly 1))
    (mfn/providing
      (render-fn (mfn.matchers/any) (mfn.matchers/any)) 'irrelevant))

  (mfn/testing "invokes the render function on each change to the state atom"
    (let [input (StringReader. "x!")
          state (atom {})
          handle-fn (fn [event] (swap! state assoc :last-event (:value event)))]
      (mainloop/with-mainloop handle-fn render-fn state input))
    (mfn/verifying
      (render-fn (mfn.matchers/any) {::mainloop/init true}) 'irrelevant (mfn.matchers/any)
      (render-fn (mfn.matchers/any) {::mainloop/init true :last-event "x"}) nil (mfn.matchers/exactly 1)
      (render-fn (mfn.matchers/any) {::mainloop/init true :last-event "!"}) nil (mfn.matchers/exactly 1)))

  (mfn/testing "no longer invokes render function when mainloop is over"
    (let [input (StringReader. "")
          state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state input)
      (swap! state assoc :x :y))
    (mfn/verifying
      (render-fn {} {::mainloop/init true}) 'irrelevant (mfn.matchers/any))))
