(ns pmatiello.tuia.internal.mainloop-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.m]
            [pmatiello.tuia.event :as event]
            [pmatiello.tuia.internal.ansi.cursor :as cursor]
            [pmatiello.tuia.internal.fixtures :as fixtures]
            [pmatiello.tuia.internal.mainloop :as mainloop]
            [pmatiello.tuia.internal.signal :as signal])
  (:import (clojure.lang Atom)))

(use-fixtures :each fixtures/with-readable-csi fixtures/with-spec-instrumentation)

(declare handle-fn)
(declare render-fn)
(declare output!)

(def output-buffer
  (mfn.m/pred
    #(and (instance? Atom %) (vector? @%) (empty? @%))))

(def function?!
  (mfn.m/pred
    (memoize
      (fn [func] (func nil) (fn? func)))))

(mfn/deftest with-mainloop-test
  (mfn/testing "on initialization and termination"
    (mfn/testing "produce events to handler function"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (handle-fn #::event{:type ::event/init :value true}) nil (mfn.m/exactly 1)
        (handle-fn #::event{:type ::event/halt :value true}) nil (mfn.m/exactly 1)))

    (mfn/testing "produce events to render function"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (render-fn output-buffer {} {::event/init true}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::event/init true ::event/halt true}) nil (mfn.m/exactly 1))))

  (mfn/testing "on input reader events"
    (mfn/testing "invokes the handler function for each event"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] output!))
      (mfn/verifying
        (handle-fn 'ev1) nil (mfn.m/exactly 1)
        (handle-fn 'ev2) nil (mfn.m/exactly 1)
        (handle-fn mfn.m/any-args?) nil (mfn.m/any))))

  (mfn/testing "on changes to the state atom"
    (mfn/testing "invokes the render function on each change"
      (let [state (atom {})
            handle-fn (fn [event] (if (symbol? event) (swap! state assoc :last-event event)))]
        (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] output!))
      (mfn/verifying
        (render-fn output-buffer (mfn.m/any) {::event/init true :last-event 'ev1}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::event/init true :last-event 'ev2}) nil (mfn.m/exactly 1)
        (render-fn mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "no longer invokes render function when mainloop is over"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!)
        (swap! state assoc :x :y))
      (mfn/verifying
        (render-fn output-buffer (mfn.m/any) {::event/init true}) nil (mfn.m/any)
        (render-fn output-buffer (mfn.m/any) {::event/init true ::event/halt true}) nil (mfn.m/any))))

  (mfn/testing "on terminal dimensions"
    (mfn/testing "request dimensions on initialization"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (signal/trap :winch fn?) nil (mfn.m/exactly 1)
        (output! [(cursor/position 9999 9999) (cursor/current-position)]) nil (mfn.m/exactly 1)
        (output! mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "request dimensions on resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (signal/trap :winch function?!) nil (mfn.m/exactly 1)
        (output! [(cursor/position 9999 9999) (cursor/current-position)]) nil (mfn.m/exactly 2)
        (output! mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "notifies the handler function on init/resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [#::event{:type ::event/cursor-position :value [10 20]}] output!))
      (mfn/verifying
        (handle-fn #::event{:type ::event/size :value [10 20]}) nil (mfn.m/exactly 1)
        (handle-fn mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "updates the state atom on init/resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [#::event{:type ::event/cursor-position :value [10 20]}] output!)
        (is (= [10 20] (::event/size @state))))))

  (mfn/providing
    (render-fn mfn.m/any-args?) nil
    (handle-fn mfn.m/any-args?) nil
    (output! mfn.m/any-args?) nil))
