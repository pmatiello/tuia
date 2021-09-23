(ns pmatiello.terminus.framework
  (:require [pmatiello.terminus.internal.framework.raw-tty :as raw-tty]
            [pmatiello.terminus.internal.framework.mainloop :as mainloop]))

(def ^:macro with-raw-tty
  #'raw-tty/with-raw-tty)

(def with-mainloop
  mainloop/with-mainloop)
