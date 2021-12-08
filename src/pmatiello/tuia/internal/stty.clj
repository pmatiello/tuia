(ns pmatiello.tuia.internal.stty
  (:require [clojure.java.shell :as shell]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]))

(s/def ::settings (s/map-of ::flag ::value))
(s/def ::flag keyword?)
(s/def ::value (s/or :nil nil? :string string?))

(defn ^:private sh!
  "Executes the given shell command."
  [& args]
  (let [command (string/join " " args)
        result (shell/sh "/bin/sh" "-c" command)
        success? (-> result :exit zero?)]
    (when-not success?
      (throw (ex-info "Shell command execution failed"
                      {:command command :result result})))
    (:out result)))

(s/fdef sh!
  :args (s/cat :args (s/* string?))
  :ret string?)

(defn ^:private stty-part->map
  "Parses a single tty setting."
  [part]
  (let [[k v] (string/split part #"=")]
    {(keyword k) v}))

(s/fdef stty-part->map
  :args (s/cat :part string?)
  :ret ::settings)

(defn current
  "Returns current tty settings."
  []
  (let [stty-str (-> (sh! "stty -g < /dev/tty") string/trim-newline)
        stty-parts (string/split stty-str #":")
        stty-maps (map stty-part->map stty-parts)]
    (apply merge stty-maps)))

(s/fdef current
  :ret ::settings)

(defn apply!
  "Applies the given tty settings."
  [settings]
  (let [stty-args (->> settings
                       (map identity)
                       (map #(remove nil? %))
                       (map #(mapv name %))
                       (map #(string/join "=" %))
                       (string/join ":"))]
    (sh! "stty" stty-args "< /dev/tty")))

(s/fdef apply!
  :args (s/cat :settings ::settings))

(defn set-flags!
  "Sets the given tty flags."
  [& flags]
  (let [stty-args (->> flags (map name) (string/join " "))]
    (sh! "stty" stty-args "< /dev/tty")))

(s/fdef set-flags!
  :args (s/cat :flags (s/* ::flag)))

(defn unset-flags!
  "Unsets the given tty flags."
  [& flags]
  (let [stty-args (->> flags
                       (map name)
                       (map #(string/replace % #"^" "-"))
                       (string/join " "))]
    (sh! "stty" stty-args "< /dev/tty")))

(s/fdef unset-flags!
  :args (s/cat :flags (s/* ::flag)))
