(ns pmatiello.tty.internal.mainloop-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [pmatiello.tty :as tty]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.m]
            [pmatiello.tty.internal.signal :as signal]
            [pmatiello.tty.internal.ansi.cursor :as cursor]
            [pmatiello.tty.internal.fixtures :as fixtures])
  (:import (clojure.lang Atom)))

(use-fixtures :each fixtures/with-readable-csi)

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
        (handle-fn {:event ::tty/init :value true}) nil (mfn.m/exactly 1)
        (handle-fn {:event ::tty/halt :value true}) nil (mfn.m/exactly 1)))

    (mfn/testing "produce events to render function"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (render-fn output-buffer {} {::tty/init true}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::tty/init true ::tty/halt true}) nil (mfn.m/exactly 1))))

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
        (render-fn output-buffer (mfn.m/any) {::tty/init true :last-event 'ev1}) nil (mfn.m/exactly 1)
        (render-fn output-buffer (mfn.m/any) {::tty/init true :last-event 'ev2}) nil (mfn.m/exactly 1)
        (render-fn mfn.m/any-args?) nil (mfn.m/any)))

    (mfn/testing "no longer invokes render function when mainloop is over"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!)
        (swap! state assoc :x :y))
      (mfn/verifying
        (render-fn output-buffer (mfn.m/any) {::tty/init true}) nil (mfn.m/any)
        (render-fn output-buffer (mfn.m/any) {::tty/init true ::tty/halt true}) nil (mfn.m/any))))

  (mfn/testing "on window resize events"
    (mfn/testing "request window dimensions"
      (let [state (atom {})]
        (mainloop/with-mainloop handle-fn render-fn state [] output!))
      (mfn/verifying
        (signal/trap :winch function?!) nil (mfn.m/exactly 1)
        (output! [(cursor/position 9999 9999) cursor/current-position]) nil (mfn.m/exactly 1)
        (output! mfn.m/any-args?) nil (mfn.m/any))))

  (mfn/providing
    (render-fn mfn.m/any-args?) nil
    (handle-fn mfn.m/any-args?) nil
    (output! mfn.m/any-args?) nil))
