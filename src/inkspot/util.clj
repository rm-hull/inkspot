(ns inkspot.util
  (:require [clojure.string :as str]))

(defn name->kword
  "Converts strings into punctuation-free keywords"
  [s]
  (->
    (name s)
    (str/replace #"\W" " ")
    (str/trim)
    (str/replace #" +" "-")
    (str/lower-case)
    (keyword)))
