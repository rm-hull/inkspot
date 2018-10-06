(ns inkspot.macros
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [inkspot.util :as util]))

(defn ^:private load-json [json-str]
  (json/read-str json-str :key-fn keyword))

(defn ^:private entry [{:keys [name colors]}]
  [(util/name->kword name) colors])

(defmacro ui-gradients [json-uri]
  (let [g (->>
           (io/resource json-uri)
           (slurp)
           (load-json)
           (map entry)
           (into {}))]
    `~g))
