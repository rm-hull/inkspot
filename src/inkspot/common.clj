(ns inkspot.common
  (:require [clojure.string :as str]))

(defn parse-int
  ([s] (parse-int s 10))
  ([s r]
  (when-not (empty? s)
    ^{:cljs (js/parseInt s r)}
    (Integer/parseInt s r))))

(defn parse-double [s]
  (when-not (empty? s)
    ^{:cljs (js/parseFloat s)}
    (Double/parseDouble s)))

