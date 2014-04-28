(ns inkspot.common
  (:require [clojure.string :as str]))

(defn parse-int
  "Wrapper around integer parsing, but cross-compatible
   across Clojure and ClojureScript"
  ([s] (parse-int s 10))
  ([s r]
  (when-not (empty? s)
    ^{:cljs (js/parseInt s r)}
    (Integer/parseInt s r))))

(defn parse-double [s]
  "Wrapper around floating point number parsing, but
   cross-compatible across Clojure and ClojureScript"
  (when-not (empty? s)
    ^{:cljs (js/parseFloat s)}
    (Double/parseDouble s)))

