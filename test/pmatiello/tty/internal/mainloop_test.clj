(ns pmatiello.tty.internal.mainloop-test
  (:require [clojure.test :refer :all]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.m]
            [pmatiello.tty.event :as event]
            [pmatiello.tty.lifecycle :as tty.lifecycle]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [pmatiello.tty.internal.signal :as signal]
            [pmatiello.tty.internal.fixtures :as fixtures])
  (:import (clojure.lang Atom)))

(use-fixtures :each fixtures/with-readable-csi)
(use-fixtures :each fixtures/with-spec-instrumentation)

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
        (handle-fn #::event{:type ::tty.lifecycle/init :value true}) nil (mfn.m/exactly 1)
        (handle-fn #::event{:type ::tty.lifecycle/halt :value true}) nil (mfn.m/exactly 1)))

    (mfn/testing "produce events to render function"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (render-fn output-buffer {} {::tty.lifecycle/init true}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::tty.lifecycle/init true ::tty.lifecycle/halt true}) nil (mfn.m/exactly 1))))

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
        (render-fn output-buffer (mfn.m/any) {::tty.lifecycle/init true :last-event 'ev1}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::tty.lifecycle/init true :last-event 'ev2}) nil (mfn.m/exactly 1)
        (render-fn mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "no longer invokes render function when mainloop is over"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!)
        (swap! state assoc :x :y))
      (mfn/verifying
        (render-fn output-buffer (mfn.m/any) {::tty.lifecycle/init true}) nil (mfn.m/any)
        (render-fn output-buffer (mfn.m/any) {::tty.lifecycle/init true ::tty.lifecycle/halt true}) nil (mfn.m/any))))

  (mfn/testing "on terminal dimensions"
    (mfn/testing "request dimensions on initialization"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (signal/trap :winch fn?) nil (mfn.m/exactly 1)
        (output! [(cursor/position 9999 9999) cursor/current-position]) nil (mfn.m/exactly 1)
        (output! mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "request dimensions on resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (signal/trap :winch function?!) nil (mfn.m/exactly 1)
        (output! [(cursor/position 9999 9999) cursor/current-position]) nil (mfn.m/exactly 2)
        (output! mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "notifies the handler function on init/resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [#::event{:type ::event/cursor-position :value [10 20]}] output!))
      (mfn/verifying
        (handle-fn #::event{:type ::tty.lifecycle/size :value [10 20]}) nil (mfn.m/exactly 1)
        (handle-fn mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "updates the state atom on init/resize"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [#::event{:type ::event/cursor-position :value [10 20]}] output!)
        (is (= [10 20] (::tty.lifecycle/size @state))))))

  (mfn/providing
    (render-fn mfn.m/any-args?) nil
    (handle-fn mfn.m/any-args?) nil
    (output! mfn.m/any-args?) nil))
