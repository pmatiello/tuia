(ns pmatiello.tty.internal.mainloop-test
  (:require [clojure.test :refer :all]
            [pmatiello.tty.internal.mainloop :as mainloop]
            [pmatiello.tty :as tty]
            [mockfn.clj-test :as mfn]
            [mockfn.matchers :as mfn.matchers]
            [clojure.set :as set])
  (:import (clojure.lang Atom)))

(declare handle-fn)
(declare render-fn)

(def output-buffer
  (mfn.matchers/pred
    #(and (instance? Atom %) (vector? @%) (empty? @%))))

(def anything?
  mfn.matchers/any)

(defn embeds? [submap]
  (mfn.matchers/pred
    #(set/subset? (set submap) (set %))))

(mfn/deftest with-mainloop-test
  (mfn/testing "calls render function on initialisation and termination"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state [] 'output!))
    (mfn/verifying
      (render-fn output-buffer {} {::tty/init true}) 'irrelevant (mfn.matchers/exactly 1)
      (render-fn output-buffer (anything?) (embeds? {::tty/halt true})) 'irrelevant (mfn.matchers/exactly 1)))

  (mfn/testing "invokes the handler function for each event in the input reader"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] 'output!))
    (mfn/verifying
      (handle-fn 'ev1) nil (mfn.matchers/exactly 1)
      (handle-fn 'ev2) nil (mfn.matchers/exactly 1))
    (mfn/providing
      (render-fn (anything?) (anything?) (anything?)) 'irrelevant))

  (mfn/testing "invokes the render function on each change to the state atom"
    (let [state (atom {})
          handle-fn (fn [event] (swap! state assoc :last-event event))]
      (mainloop/with-mainloop handle-fn render-fn state ['ev1 'ev2] 'output!))
    (mfn/verifying
      (render-fn output-buffer (anything?) {::tty/init true}) 'irrelevant (anything?)
      (render-fn output-buffer (anything?) (embeds? {::tty/halt true})) 'irrelevant (anything?)
      (render-fn output-buffer (anything?) (embeds? {:last-event 'ev1})) nil (mfn.matchers/exactly 1)
      (render-fn output-buffer (anything?) (embeds? {:last-event 'ev2})) nil (mfn.matchers/exactly 1)))

  (mfn/testing "no longer invokes render function when mainloop is over"
    (let [state (atom {})]
      (mainloop/with-mainloop handle-fn render-fn state [] 'output!)
      (swap! state assoc :x :y))
    (mfn/verifying
      (render-fn output-buffer (anything?) {::tty/init true}) 'irrelevant (anything?)
      (render-fn output-buffer (anything?) {::tty/init true ::tty/halt true}) 'irrelevant (anything?))))
