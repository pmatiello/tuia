(ns pmatiello.terminus.stty
  (:require [clojure.java.shell :as shell]
            [clojure.string :as string]))

(defn ^:private sh! [& args]
  (let [command  (string/join " " args)
        result   (shell/sh "/bin/sh" "-c" command)
        success? (-> result :exit zero?)]
    (when-not success?
      (throw (ex-info "Shell command execution failed"
                      {:command command :result result})))
    (:out result)))

(defn ^:private stty-part->map [part]
  (let [[k v] (string/split part #"=")]
    {(keyword k) v}))

(defn current []
  (let [stty-str   (-> (sh! "stty -g < /dev/tty") string/trim-newline)
        stty-parts (string/split stty-str #":")
        stty-maps  (map stty-part->map stty-parts)]
    (apply merge stty-maps)))

(defn apply! [settings]
  (let [stty-args (->> settings
                       (map identity)
                       (map #(remove nil? %))
                       (map #(mapv name %))
                       (map #(string/join "=" %))
                       (string/join ":"))]
    (sh! "stty" stty-args "< /dev/tty")))

(defn set-flags! [& flags]
  (let [stty-args (->> flags (map name) (string/join " "))]
    (sh! "stty" stty-args "< /dev/tty")))

(defn unset-flags! [& flags]
  (let [stty-args (->> flags
                       (map name)
                       (map #(string/replace % #"^" "-"))
                       (string/join " "))]
    (sh! "stty" stty-args "< /dev/tty")))
