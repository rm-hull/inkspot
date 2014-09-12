(ns inkspot.macros
  (:require
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [inkspot.util :as util]))

(defn ^:private load-json [json-str]
  (json/read-str json-str :key-fn keyword))

(defn ^:private entry [{:keys [name colour1 colour2]}]
  [(util/name->kword name) [colour1 colour2]])

(defmacro ui-gradients [json-uri]
  (let [g (->>
            (io/resource json-uri)
            (slurp)
            (load-json)
            (map entry)
            (into {}))]
    `~g))
